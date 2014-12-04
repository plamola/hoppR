package nl.nl.dekkr.hoppr.rest

import nl.dekkr.hoppr.model.FetchLogger
import nl.nl.dekkr.hoppr.model.Syndication
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

  val myRoute_old = {
    get {
      pathSingleSlash {
        complete(index)
      } ~
        path("api" / "log") {
          respondWithMediaType(`application/json`) {
            complete {
              marshal(FetchLogger.getLast100)
            }
          }
        }
    } ~
    post {
        path("api" / "feed") {
          respondWithMediaType(`application/json`) {
            complete("{result: feed}")
          }
        }
    }
  }


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
          respondWithMediaType(`application/json`) {
            Syndication.addNewFeed("test4") match {
              case 0 => complete("{result: failure}")
              case i : Int  => complete(s"{feed : $i}")
              }
            }
        }
      }
    }
  }


  lazy val index =
    <html>
      <body>
        <h1>This is <i>hoppR</i>!</h1>
        <p>Defined resources:</p>
        <ul>
          <li><a href="/api/log">/log</a></li>
          <li> Add feed:
          <form action="/api/feed" method="post">
            <input type="text" name="url"></input>
            <input type="submit" name="Add feed" />
          </form>
          </li>
        </ul>
      </body>
    </html>

}
