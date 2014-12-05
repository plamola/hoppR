package nl.dekkr.hoppr.actors

import akka.actor.Props
import akka.io.IO
import com.typesafe.config.ConfigFactory
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

/**
 * This trait contains the nl.dekkr.hoppr.actors that make up our application; it can be mixed in with
 * ``BootedCore`` for running code or ``TestKit`` for unit and integration tests.
 */
trait CoreActors {
  this: Core =>
  val syndication   = system.actorOf(Props[SyndicationActor], "syndication")
  val fetchsupervisor = system.actorOf(Props[FetchSupervisor],"fetch-supervisor")

  val service = system.actorOf(Props[RestServiceActor], "rest-service")

  implicit val timeout = Timeout(5.seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler
  var conf = ConfigFactory.load
  IO(Http) ? Http.Bind(
    service,
    interface = conf.getString("hoppr.api.interface"),
    port = conf.getInt("hoppr.api.port")
  )
}
