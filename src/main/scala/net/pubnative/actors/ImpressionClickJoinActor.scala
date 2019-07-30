package net.pubnative.actors

import akka.actor.{Actor, ActorLogging, Props}
import net.pubnative.commands.{Flush, ImpressionClickJoinResult}
import net.pubnative.domain._

import scala.collection.mutable

object ImpressionClickJoinActor {

  def props: Props = Props[ImpressionClickJoinActor]
}

/**
  * Join Impressions with Clicks by id
  */
class ImpressionClickJoinActor extends Actor with ActorLogging {
  log.info(s"Created new ImpressionClickJoinActor: path = ${self.path}")

  val reportActor = context.actorSelection(s"/user/master/${AggregatorActor.name}")

  val join = mutable.HashMap[String, EventHits]()
  var msgCount = 0

  def incrementMsgCount(): Unit = {
    msgCount += 1
    if(msgCount % 10 == 0) {
      log.info(s"${self.path} received ${msgCount} msgs")
    }
  }

  override def receive: Receive = {
    // ImpressionHits
    case imp: ImpressionHits => {
      if (join contains imp.id) {
        join(imp.id) = join(imp.id).merge(imp)
      }
      else {
        join(imp.id) = imp
      }
      incrementMsgCount()
    }
    // ClickHits
    case click: ClickHits => {
      if (join contains click.impression_id) {
        join(click.impression_id) = join(click.impression_id).merge(click)
      }
      else {
        join(click.impression_id) = click
      }
      incrementMsgCount()
    }

    case Flush() => {
      log.info(s"${self.path} received Flush request")

      try {
        val reportChunk = aggregate()
        reportActor.tell(reportChunk, sender())
        sender ! ImpressionClickJoinResult()
      }
      catch {
        case t: Throwable => sender ! ImpressionClickJoinResult()
      }
    }

  }

  def aggregate(): Seq[EventsAggregate] = {
    join.values.groupBy(events => {
      events match {
        case imp: ImpressionHits => {
          (imp.country_code, imp.app_id, imp.advertiser_id)
        }
        case click: ClickHits => {
          //This is an error: all clicks should have an imp
          (None, -1, -1)
        }
        case _ => throw new IllegalStateException("Failed to aggregate joined clicks/impressions into report: unexpected content in 'join' map.")
      }
    }).map {
      case ((countryCode, appId, avertiserId), hits) => mapHitsToReport(countryCode, appId, avertiserId, hits)
    }.toSeq
  }

  def mapHitsToReport(countryCode: Option[String], appId: Int, avertiserId: Int, hits: Iterable[EventHits]): EventsAggregate = {
    var clicks = 0L
    var imps = 0L
    var revenue = 0D
    hits.foreach(_ match {
        case imp: ImpressionHits => {
          clicks += imp.clicks
          imps += imp.impressions
          revenue += imp.revenue
        }
        case click: ClickHits => {
          clicks += click.clicks
          revenue += click.revenue
        }
        case _ => throw new IllegalStateException("Failed to map hits to Report: unexpected EventHits type.")
      }
    )
    EventsAggregate(appId, avertiserId, countryCode, imps, clicks, revenue)
  }
}
