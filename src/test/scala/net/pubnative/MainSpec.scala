package net.pubnative

import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import net.pubnative.actors.MasterActor
import net.pubnative.commands.{RecommendationAggrResult, ReportAggrResult, TotalProcessingResult}

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Success

//#test-classes
class MainSpec(_system: ActorSystem)
  extends TestKit(_system)
    with ImplicitSender
    with Matchers
    with WordSpecLike
    with BeforeAndAfterAll {

  def this() = this(ActorSystem("PubNativeSpec"))

  override def afterAll: Unit = {
    shutdown(system)
  }

  "A Master Actor" should {
    "manage computation pipeline of App/Country report and recommendation" in {

      val files: Array[String] = Array(
        getClass.getResource("/events/clicks.json").getFile,
        getClass.getResource("/events/impressions.json").getFile
      )
      val master = system.actorOf(MasterActor.props, "master")

      master ! files

      expectMsg(10 seconds,
        TotalProcessingResult(
          ReportAggrResult(Success("app-country-report.json")),
          RecommendationAggrResult(Success("recommendations.json"))
        )
      )
    }
  }
}
