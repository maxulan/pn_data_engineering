package net.pubnative

import java.nio.file.{Files, Paths}

import net.pubnative.domain.{AppCountryReport, Recommendation}
import net.pubnative.serde.JsonProtocols
import spray.json._

trait JsonTestHelper extends JsonProtocols {

  private def readJson(filename: String): JsValue = {
    val source = new String(Files.readAllBytes(Paths.get(filename)))
    source.parseJson
  }

  def readRecommendations(filename: String): Set[Recommendation] = {
    val json = readJson(filename)
    json.convertTo[List[Recommendation]].toSet
  }


  def readReport(filename: String): Set[AppCountryReport] = {
    val json = readJson(filename)
    json.convertTo[List[AppCountryReport]].toSet
  }

  def compareRecommendations(filename1: String, filename2: String): Boolean = {
    readRecommendations(filename1) == readRecommendations(filename2)
  }

  def compareReports(filename1: String, filename2: String): Boolean = {
    readReport(filename1) == readReport(filename2)
  }


}
