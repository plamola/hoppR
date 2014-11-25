package nl.nl.dekkr.hoppr.rest

import nl.dekkr.hoppr.model.FetchLogger
import spray.http.MediaTypes._
import spray.httpx.marshalling._
import spray.routing.HttpService

/**
 * Created by Matthijs Dekker on 25/11/14.
 */
// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {
  import nl.nl.dekkr.hoppr.rest.MyJsonProtocol._
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
