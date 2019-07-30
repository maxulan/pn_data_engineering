package net.pubnative

import akka.actor.{ActorRef, ActorSystem}
import net.pubnative.actors.MasterActor
import org.slf4j.{Logger, LoggerFactory}
import akka.pattern._
import akka.util.Timeout
import net.pubnative.commands.TotalProcessingResult

import scala.concurrent.duration.{Duration, _}
import scala.util.{Failure, Success}

object Main extends App {
  val log: Logger = LoggerFactory.getLogger("Main")

  if (args.length == 0) {
    log.error("Missing input arguments: at least one filename should be supplied. Aborting program...")
    System.exit(1)
  }

  if (args.length > 200) {
    log.error("Too many input arguments: 200 filenames is max. Aborting program...")
    System.exit(1)
  }

  implicit val timeout: Timeout = 30.minutes
  val system: ActorSystem = ActorSystem("pubnative")

  import system.dispatcher

  val master: ActorRef = system.actorOf(MasterActor.props, "master")

  def stop = {
    system.stop(master); system.terminate()
  }

  master ? args onComplete {
    case Success(TotalProcessingResult(reportResult, recommendationResult)) => {
      log.info(s"Processing is finished: ${args.length} files processed.")
      recommendationResult.output match {
        case Success(filename) => {
          log.info(s"App/Country recommendation created successfully! Output file: $filename")
        }
        case Failure(ex) => log.error(s"App/Country recommendation processing has failed: ${ex}")
      }

      reportResult.output match {
        case Success(filename) => {
          log.info(s"App/Country report created successfully! Output file: $filename")
        }
        case Failure(ex) => log.error(s"App/Country report processing has failed: ${ex}")
      }
      stop
    }
    case Failure(ex) => {
      log.error(s"Processing has failed: ${ex}")
      stop
    }
    case Success(ok) => {
      log.info(s"Processing is finished: ${args.length} files processed. Details: $ok")
      stop
    }
  }


}
