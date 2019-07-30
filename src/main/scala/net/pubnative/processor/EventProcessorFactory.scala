package net.pubnative.processor

import net.pubnative.domain.{Click, Event, Impression}
import net.pubnative.serde.JsonProtocols
import spray.json.JsValue

object EventProcessorFactory extends JsonProtocols {

  def fromJson(jsonAst: JsValue): EventProcessor[_] = {
    try {
      jsonAst.convertTo[Impression]
      new ImpressionProcessor
    }
    catch {
      case _ : Throwable => {
        jsonAst.convertTo[Click]
        new ClickProcessor
      }
    }
  }
}
