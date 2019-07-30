package net.pubnative.serde

import net.pubnative.domain.{AppCountryReport, Click, Impression, Recommendation}
import spray.json.{DefaultJsonProtocol, NullOptions, RootJsonFormat}

trait JsonProtocols extends DefaultJsonProtocol with NullOptions{

  implicit val clickFormat: RootJsonFormat[Click] = jsonFormat2(Click.apply)
  implicit val impFormat: RootJsonFormat[Impression] = jsonFormat4(Impression.apply)
  implicit val reportFormat: RootJsonFormat[AppCountryReport] = jsonFormat5(AppCountryReport.apply)
  implicit val recommendationFormat: RootJsonFormat[Recommendation] = jsonFormat3(Recommendation.apply)

}
