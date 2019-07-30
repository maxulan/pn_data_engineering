package net.pubnative.actors

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

import akka.actor.{Actor, ActorLogging, Props}
import net.pubnative.commands.{Flush, RecommendationAggrResult, ReportAggrResult}
import net.pubnative.domain.{AppCountryReport, EventsAggregate, Recommendation}
import net.pubnative.serde.JsonProtocols

import scala.collection.mutable
import spray.json._

import scala.util.{Failure, Success}

object AggregatorActor {
  val name = "aggregator"

  def props: Props = Props[AggregatorActor]
}

class AggregatorActor extends Actor with ActorLogging with JsonProtocols {
  log.info(s"Created new EventReportActor: path = ${self.path}")

  private val aggregates = mutable.ListBuffer[EventsAggregate]()

  override def receive: Receive = {

    case aggregate: Seq[EventsAggregate] => {
      log.info(s"${self.path} received aggregate chunk")
      this.aggregates ++= aggregate
    }

    case Flush() => {
      log.info(s"${self.path} received Flush request")
      writeAppCountryReportJson()
      writeRecommendationsJson()
    }
  }

  private def writeRecommendationsJson(outputPath: String = "recommendations.json") = {
    try {
      val recommendations = Recommendation.fromAggregates(aggregates)
      writeJson(recommendations.toJson, outputPath)
      sender() ! RecommendationAggrResult(Success(outputPath))
    }
    catch {
      case t: Throwable => sender() ! RecommendationAggrResult(Failure(t))
    }

  }

  private def writeAppCountryReportJson(outputPath: String = "app-country-report.json") = {
    try {
      val report = AppCountryReport.fromAggregates(aggregates)
      writeJson(report.toJson, outputPath)
      sender() ! ReportAggrResult(Success(outputPath))
    }
    catch {
      case t: Throwable => sender() ! ReportAggrResult(Failure(t))
    }
  }

  private def writeJson(value: JsValue, outputPath: String) = {
    val json = value.prettyPrint
    Files.write(Paths.get(outputPath), json.getBytes(StandardCharsets.UTF_8))
  }

}