package net.pubnative.serde

import net.pubnative.domain.{Click, EventReport, Impression, Recommendation}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait JsonProtocols extends DefaultJsonProtocol {

  implicit val clickFormat: RootJsonFormat[Click] = jsonFormat2(Click.apply)
  implicit val impFormat: RootJsonFormat[Impression] = jsonFormat4(Impression.apply)
  implicit val eventReportFormat: RootJsonFormat[EventReport] = jsonFormat5(EventReport.apply)
  implicit val recommendationFormat: RootJsonFormat[Recommendation] = jsonFormat3(Recommendation.apply)

}
