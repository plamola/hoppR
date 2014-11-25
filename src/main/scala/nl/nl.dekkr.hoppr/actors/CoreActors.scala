package nl.dekkr.actors

import nl.dekkr.hoppr.actors.{SyndicationActor, FetchSupervisor}
import akka.actor.Props
import akka.io.IO
import nl.nl.dekkr.hoppr.actors.MyServiceActor
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

/**
 * This trait contains the actors that make up our application; it can be mixed in with
 * ``BootedCore`` for running code or ``TestKit`` for unit and integration tests.
 */
trait CoreActors {
  this: Core =>
  val syndication   = system.actorOf(Props[SyndicationActor], "syndication")
  val fetchsupervisor = system.actorOf(Props[FetchSupervisor],"FetchSupervisor")

  val service = system.actorOf(Props[MyServiceActor], "rest-service")

  implicit val timeout = Timeout(5.seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler
  //TODO move interface & port to config
  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)

}
