package nl.dekkr.hoppr.rest


import nl.dekkr.hoppr.model._
import org.joda.time.DateTime
import spray.json._

/**
 * Convert to and from JSON
 */
object MyJsonProtocol extends DefaultJsonProtocol {

  implicit val UrlFormat = jsonFormat1(Url)

  def stringToLogLevel(s: String): LogLevel = {
    s match {
      case "Info" => Info
      case "Debug" => Debug
      case "Error" => Error
      case "Warn" => Warning
      case "Critical" => Critical
      case _ => throw new DeserializationException("Invalid log level in Json")
    }
  }


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
          new FetchLog(id = Option(id.toInt), uri = uri, level = stringToLogLevel(level), result = Option(result), logdate = new DateTime(date))
        case _ =>
          throw new DeserializationException("Fetch log expected")
      }
    }

  }

  implicit object FeedJsonFormat extends RootJsonFormat[Feed] {

    def write(c: Feed) = JsObject(
      "id" -> JsNumber(c.id.get),
      "feedurl" -> JsString(c.feedurl),
      "description" -> JsString(c.description.getOrElse("")),
      "link" -> JsString(c.link.getOrElse("")),
      "title" -> JsString(c.title.getOrElse(""))
    )

    def read(value: JsValue): Feed = {
      value.asJsObject.getFields("id", "feedurl", "description", "link", "title") match {
        case Seq(JsNumber(id), JsString(feedUrl), JsString(description), JsString(link), JsString(title),) =>
          new Feed(id = Option(id.toInt), feedurl = feedUrl, description = Option(description), link = Option(link), title = Option(title))
        case _ =>
          throw new DeserializationException("Feed expected")
      }
    }

  }

}
