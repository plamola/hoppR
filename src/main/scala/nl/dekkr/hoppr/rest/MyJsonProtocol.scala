package nl.dekkr.hoppr.rest


import nl.dekkr.hoppr.model._
import org.joda.time.DateTime
import spray.json._

/**
 * Convert to and from JSON
 */
object MyJsonProtocol extends DefaultJsonProtocol {


//  implicit val FetchLogFormat = jsonFormat5(FetchLog)   // not possible, no support for Joda DateTime

  implicit object FetchLogJsonFormat extends RootJsonFormat[ FetchLog ] {

    def write(c: FetchLog) = JsObject(
      "id"  -> JsNumber(c.id.get),
      "level" -> JsString(c.level.toString),
      "uri" -> JsString(c.uri),
      "result" -> JsString(c.result.get),
      "date" -> JsString(c.logdate.toString)
    )

    def read(value: JsValue) : FetchLog = {
      value.asJsObject.getFields("id", "level", "uri", "result", "date") match {
        case Seq(JsNumber(id), JsString(level), JsString(uri), JsString(result), JsString(date)) =>
          new FetchLog(uri = uri, level = stringToLogLevel(level), result = Option(result),logdate = new DateTime(date))
        case _ => throw new DeserializationException("Fetch log expected")
      }
    }

  }


  def stringToLogLevel(s: String) : LogLevel = {
    s match {
      case "info" => Info
      case "debug" => Debug
      case "error" => Error
      case "warn" => Warning
      case "critical" => Critical
      case _ => Info
    }
  }



}
