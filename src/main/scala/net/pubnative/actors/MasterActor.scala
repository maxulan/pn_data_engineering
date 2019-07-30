package net.pubnative.actors

import java.nio.file.{Files, Paths}

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.routing.{Broadcast, ConsistentHashingPool, RoundRobinPool}
import net.pubnative.commands._

import scala.util.{Failure, Success, Try}

object MasterActor {
  val WORKERS_NUM = 8

  def props: Props = Props[MasterActor]
}

class MasterActor extends Actor with ActorLogging {
  log.info(s"Created new MasterActor: path = ${self.path}")

  private val parserActor: ActorRef =
    context.actorOf(RoundRobinPool(MasterActor.WORKERS_NUM).props(JsonFileParserActor.props), "parser")

  private val joinActor: ActorRef =
    context.actorOf(ConsistentHashingPool(MasterActor.WORKERS_NUM).props(ImpressionClickJoinActor.props), "join")

  private val aggregatorActor = context.actorOf(AggregatorActor.props, AggregatorActor.name)

  private var filesCount = 0
  private var processedFilesCount = 0

  private var joinWorkersFinished = 0
  // client is main App
  var client: ActorRef = Actor.noSender

  var recommendationResult: RecommendationAggrResult = null
  var reportResult: ReportAggrResult = null

  override def receive: Receive = {

    case fileNames: Array[String] => {
      validate(fileNames) match {
        case Success(true) => {
          client = sender()
          sendToWorkers(fileNames)
        }
        case failure => sender() ! failure
      }
    }

    case JsonFileParsingResult(result) => {
      processedFilesCount += 1
      result match {
        case Success(filename) => {
          log.info(s"Parsing of $filename is completed.")
          if (isAllFilesParsed) {
            joinActor ! Broadcast(Flush())
          }
        }
        case Failure(ex) => client ! Failure(ex)
      }
    }

    case ImpressionClickJoinResult() => {
      joinWorkersFinished += 1
      log.info(s"${self.path} received ImpressionClickJoinResult. Finished $joinWorkersFinished workers.")
      if (isAllJoinsFlushed) {
        aggregatorActor ! Flush()
      }
    }

    case res: RecommendationAggrResult => {
      recommendationResult = res
      if(areWeDone)  {
        client ! TotalProcessingResult(reportResult, recommendationResult)
      }
    }

    case res: ReportAggrResult => {
      reportResult = res
      if(areWeDone)  {
        client ! TotalProcessingResult(reportResult, recommendationResult)
      }
    }
  }

  private def areWeDone = {
    Option(reportResult).isDefined && Option(recommendationResult).isDefined
  }

  private def isAllFilesParsed = {
    filesCount == processedFilesCount
  }

  private def isAllJoinsFlushed = {
    joinWorkersFinished == MasterActor.WORKERS_NUM
  }

  private def sendToWorkers(fileNames: Iterable[String]): Unit = {
    fileNames.foreach { fileName =>
      parserActor ! fileName
      filesCount += 1
    }
  }

  private def validate(fileNames: Iterable[String]): Try[Boolean] = {
    val nonExisting = fileNames.filterNot(fileName => Files.exists(Paths.get(fileName)))

    if (nonExisting.size > 0) {
      val msg = "Invalid arguments. Following input files don't exist: " + nonExisting.mkString("\"", "\", \"", "\"")
      return Failure(new IllegalArgumentException(msg))
    }
    Success(true)
  }

}
