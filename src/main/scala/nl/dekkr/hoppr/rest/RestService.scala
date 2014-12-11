package nl.dekkr.hoppr.rest

import nl.dekkr.hoppr.model.{Feed, Syndication, FetchLogger}
import spray.http.MediaTypes._
import spray.http.StatusCodes
import spray.httpx.marshalling._
import spray.routing.{Directives, HttpService}
import StatusCodes._
import spray.routing.HttpService

case class Url(uri: String)

/**
 * REST Service definition
 */
// this trait defines our service behavior independently from the service actor
trait RestService extends HttpService {
  import nl.dekkr.hoppr.rest.MyJsonProtocol._
  import spray.httpx.SprayJsonSupport._

  //TODO Add route for RSS feed of collected feed

  lazy val index =
    <html>
      <body>
        <h1>This is
          <i>hoppR</i>
          !</h1>
        <p>Defined resources:</p>
        <ul>
          <li>
            <a href="/api/log">/log</a>
          </li>
          <li>Add feed:
            <form action="/api/feed" method="post">
              <input type="text" name="url"></input>
              <input type="submit" name="Add feed"/>
            </form>
          </li>
        </ul>
      </body>
    </html>
  val myRoute = {
    get {
      pathSingleSlash {
        complete(index)
      }
    } ~
    pathPrefix("api") {
      get {
        path("log") {
          respondWithMediaType(`application/json`) {
            complete {
              marshal(FetchLogger.getLast100)
            }
          }
        }
      } ~
      post {
        path("feed") {
          entity(as[Url]) { url =>
            // transfer to newly spawned actor
            //detach() {
            respondWithMediaType(`application/json`) {
              Syndication.addNewFeed(url.uri) match {
                case feed: Feed => complete(Created, feed)
                case _ => complete(BadRequest, "Could not add feed")
              }
            }
            //}
          }
        }
      } ~
        delete {
          path("feed") {
            entity(as[Url]) { url =>
              // transfer to newly spawned actor
              //detach() {
              respondWithMediaType(`application/json`) {
                Syndication.removeFeed(url.uri) match {
                  case 1 => complete(OK)
                  case 0 => complete(NotFound)
                  case _ => complete(BadRequest, "Could not remove feed")
              }
            }
              //}
          }
          }
        } ~
        get {
          path("feed") {
            entity(as[Url]) { url =>
              // transfer to newly spawned actor
              //detach() {
              respondWithMediaType(`application/json`) {
                Syndication.getFeed(url.uri) match {
                  case Some(feed) => complete(OK, feed)
                  case None => complete(NotFound)
                }
              }
              //}
            }
          }
      }
    }
  }

}
