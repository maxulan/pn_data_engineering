package net.pubnative.processor

import net.pubnative.domain.{Click, ClickHits}
import net.pubnative.serde.JsonProtocols
import spray.json.JsValue

class ClickProcessor extends EventProcessor[Click] with JsonProtocols {

  def extract(json: JsValue): Click = json.convertTo[Click]

  override def combineById(events: Iterable[Click]): Map[String, ClickHits] = {
    events.groupBy(_.impression_id).mapValues(ClickHits.convert)
  }

}
