package net.pubnative.processor

import net.pubnative.domain.{Click, Impression, ImpressionHits}
import net.pubnative.serde.JsonProtocols
import spray.json.JsValue

class ImpressionProcessor extends EventProcessor[Impression] with JsonProtocols{

  def extract(json: JsValue): Impression = json.convertTo[Impression]

  override def combineById(events: Iterable[Impression]): Map[String, ImpressionHits] = {
    events.groupBy(_.id).mapValues(ImpressionHits.convert)
  }

}
