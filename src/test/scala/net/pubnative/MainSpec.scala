package net.pubnative

import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import net.pubnative.actors.MasterActor
import net.pubnative.commands.{RecommendationAggrResult, ReportAggrResult, TotalProcessingResult}

import scala.concurrent.duration._
import scala.io.Source
import scala.language.postfixOps
import scala.util.Success

//#test-classes
class MainSpec(_system: ActorSystem)
  extends TestKit(_system)
    with ImplicitSender
    with Matchers
    with WordSpecLike
    with BeforeAndAfterAll
    with JsonTestHelper {

  def this() = this(ActorSystem("PubNativeSpec"))

  override def afterAll: Unit = {
    shutdown(system)
  }

  "A Case Study" should {
    "produce App/Country report and recommendation files" in {
      val eventFiles: Array[String] = Array(
        getClass.getResource("/events/t1_clicks.json").getFile,
        getClass.getResource("/events/t1_impressions.json").getFile
      )
      val master = system.actorOf(MasterActor.props, "master")

      master ! eventFiles
      val reportOutFile = "app-country-report.json"
      val recommendationsOutFile = "recommendations.json"
      expectMsg(10 seconds,
        TotalProcessingResult(
          ReportAggrResult(Success(reportOutFile)),
          RecommendationAggrResult(Success(recommendationsOutFile))
        )
      )
      val expectedReport = getClass.getResource("/events/t1_expected_report.json").getFile
      val expectedRecommendation = getClass.getResource("/events/t1_expected_recommendations.json").getFile

      assert(
        compareReports(expectedReport, reportOutFile) == true,
        s"Report '${reportOutFile}' should match '${expectedReport}'"
      )

      assert(
        compareRecommendations(expectedRecommendation, recommendationsOutFile) == true,
        s"Recommendations '${recommendationsOutFile}' should match '${expectedRecommendation}'"
      )
    }
  }
}