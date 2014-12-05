package nl.dekkr.hoppr.rest

import nl.dekkr.hoppr.model.{Syndication, FetchLogger}
import spray.http.MediaTypes._
import spray.httpx.marshalling._
import spray.routing.HttpService

/**
 * REST Service definition
 */
// this trait defines our service behavior independently from the service actor
trait RestService extends HttpService {
  import nl.dekkr.hoppr.rest.MyJsonProtocol._
  import spray.httpx.SprayJsonSupport._


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
            // TODO Use supplied URL instead of current hard-coded value
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
