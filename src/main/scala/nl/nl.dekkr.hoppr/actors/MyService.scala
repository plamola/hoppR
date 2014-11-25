package nl.nl.dekkr.hoppr.actors

/**
 * Created by Matthijs Dekker on 25/11/14.
 */
import akka.actor.Actor
import nl.dekkr.hoppr.model.FetchLogger
import nl.dekkr.hoppr.db.Tables.FetchLogRow
import spray.json._
import spray.routing._
import spray.http._
import MediaTypes._
import spray.httpx.marshalling._

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


// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}




// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {
  import MyJsonProtocol._
  import spray.httpx.SprayJsonSupport._

  val myRoute =
    path("log") {
      get {
        respondWithMediaType(`application/json`) { // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            marshal(FetchLogger.getLast100)
          }
        }
      }
    }

}