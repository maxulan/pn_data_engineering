package net.pubnative

import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import net.pubnative.actors.MasterActor
import net.pubnative.commands.TotalProcessingResult

import scala.concurrent.duration._
import scala.language.postfixOps

//#test-classes
class MainSpec(_system: ActorSystem)
  extends TestKit(_system)
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
        getClass.getResource("/clicks.json").getFile,
        getClass.getResource("/impressions.json").getFile
      )
      val master = system.actorOf(MasterActor.props, "master")

      master ! files
      expectMsgType[TotalProcessingResult](20 seconds)
    }
  }
}
