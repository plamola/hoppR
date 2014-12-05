package nl.dekkr.hoppr.rest

import nl.dekkr.hoppr.db.Tables.FetchLogRow
import spray.json._

/**
 * Convert to and from JSON
 */
object MyJsonProtocol extends DefaultJsonProtocol {
  implicit object FetchLogRowJsonFormat extends RootJsonFormat[ FetchLogRow ] {
    def write(c: FetchLogRow) = JsObject(
      "id"  -> JsNumber(c.id.get),
      "level" -> JsString(c.level.toString),
      "uri" -> JsString(c.uri),
      "result" -> JsString(c.result.get),
      "date" -> JsString(c.logdate.toString)
    )
    def read(value: JsValue) = {
      throw new DeserializationException("FetchLogRow demarshalling not implemented")
    }
  }
}
