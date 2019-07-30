package net.pubnative.processor

import net.pubnative.domain.{Event, EventHits}
import net.pubnative.serde.JsonProtocols
import spray.json.JsValue

trait EventProcessor[E <: Event] {

  def extract(json: JsValue): E

  def combineById(events: Iterable[E]): Map[String, EventHits]

  def extractCombineById(jsonArray: Iterable[JsValue]): Map[String, EventHits] = combineById(jsonArray.map(extract))
}
