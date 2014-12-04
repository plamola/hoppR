package nl.dekkr.hoppr.actors

/**
 * Created by Matthijs Dekker on 25/11/14.
 */
import akka.actor.Actor
import nl.nl.dekkr.hoppr.rest.RestService



// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class RestServiceActor extends Actor with RestService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}


