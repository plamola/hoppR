package nl.dekkr.hoppr.rest

import nl.dekkr.hoppr.model.{Feed, Syndication, FetchLogger}
import nl.dekkr.hoppr.rss.{FeedLogOutput, AtomOutput}
import spray.http.MediaTypes._
import spray.http.StatusCodes
import spray.httpx.marshalling._
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

  //TODO Actor per request ?

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
        path("feed") {
          post {
            entity(as[Url]) { url =>
              respondWithMediaType(`application/json`) {
                Syndication.addNewFeed(url.uri) match {
                  case feed: Feed => respondWithStatus(Created) {
                    complete(feed)
                  }
                  case _ => respondWithStatus(BadRequest) {
                    complete("Could not add feed")
                  }
                }
              }
            }
          } ~
            delete {
              entity(as[Url]) { url =>
                respondWithMediaType(`application/json`) {
                  Syndication.removeFeed(url.uri) match {
                    case 1 => complete(OK)
                    case 0 => complete(NotFound)
                    case _ => respondWithStatus(BadRequest) {
                      complete("Could not remove feed")
                    }
                  }
                }
              }
            } ~
            get {
              entity(as[Url]) { url =>
                respondWithMediaType(`application/json`) {
                  Syndication.getFeed(url.uri) match {
                    case Some(feed) => respondWithStatus(OK) {
                      complete(feed)
                    }
                    case None => complete(NotFound)
                  }
                }
              }
            }
        } ~
        path("rss" / IntNumber) { feedId =>
          get {
            respondWithMediaType(`application/xml`) {
              if (feedId == 0) {
                complete(FeedLogOutput.getAtomFeed(0).get)
              } else {
                AtomOutput.getAtomFeed(feedId) match {
                  case Some(output) => complete(output)
                  case None => complete(NotFound)
                }
              }
            }
            }
          }
        }
      }

}
