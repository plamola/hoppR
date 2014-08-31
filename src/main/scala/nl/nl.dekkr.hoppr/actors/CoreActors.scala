package nl.dekkr.actors

import akka.actor.Props
import nl.dekkr.hoppr.actors.{SyndicationActor, FetchSupervisor}


/**
 * This trait contains the actors that make up our application; it can be mixed in with
 * ``BootedCore`` for running code or ``TestKit`` for unit and integration tests.
 */
trait CoreActors {
  this: Core =>
  val syndication   = system.actorOf(Props[SyndicationActor], "syndication")
  val fetchsupervisor = system.actorOf(Props[FetchSupervisor],"FetchSupervisor")

}
