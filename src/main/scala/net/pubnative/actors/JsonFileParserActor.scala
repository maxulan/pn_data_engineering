package net.pubnative.actors

import java.io.File
import java.nio.file.{Files, Paths}
import java.util.Scanner
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection, InvalidActorNameException, Props}
import spray.json._
import DefaultJsonProtocol._
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import akka.routing.{ActorRefRoutee, ConsistentHashingRoutingLogic, Router}
import akka.util.Timeout
import net.pubnative.commands.JsonFileParsingResult
import net.pubnative.domain.{Click, Event, Impression}
import net.pubnative.processor.EventProcessorFactory
import net.pubnative.serde.JsonProtocols

import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}


case class FileParsingException(filename: String, t: Throwable) extends Exception(t)

object JsonFileParserActor {

  def props: Props = Props[JsonFileParserActor]
}

class JsonFileParserActor extends Actor with ActorLogging with JsonProtocols {
  log.info(s"Created new JsonFileParserActor: path = ${self.path}")

  val join = context.actorSelection("/user/master/join")

  def receive: Receive = {

    case filename: String => {
      log.info(s"Received $filename parsing request")
      try {
        val source = new String(Files.readAllBytes(Paths.get(filename)))
        val jsonAst: JsValue = source.parseJson

        if (jsonAst.isInstanceOf[JsArray]) {
          val jsonArray = jsonAst.asInstanceOf[JsArray].elements

          if (!jsonArray.isEmpty) {

            val processor = EventProcessorFactory.fromJson(jsonArray.head)

            val eventsById = processor.extractCombineById(jsonArray)

            for ((id, eventHits) <- eventsById) {
              val msg = ConsistentHashableEnvelope(eventHits, id.hashCode)
              join.tell(msg, sender())
            }

          }

          sender ! JsonFileParsingResult(Success(filename))
        }
        else {
          val msg = s"Failed to parse $filename: expected json array but was ${jsonAst.getClass}"
          log.error(msg)
          val ex = new FileParsingException(filename, new Exception(msg))
          sender ! JsonFileParsingResult(Failure(ex))
        }
      }
      catch {
        case t: Throwable => sender ! JsonFileParsingResult(Failure(new FileParsingException(filename, t)))
      }

    }
  }

}
