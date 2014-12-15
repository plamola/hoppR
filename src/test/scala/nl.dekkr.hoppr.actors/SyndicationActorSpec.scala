package nl.dekkr.hoppr.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import nl.dekkr.hoppr.actors.SyndicationActor.{FeedContent, FeedException, GetFeed, FeedNoContentFound}
import org.specs2.mutable.SpecificationLike

class SyndicationActorSpec extends TestKit(ActorSystem()) with SpecificationLike with CoreActors with Core with ImplicitSender {


  sequential

  "Syndication should" >> {

    "reject invalid uri" in {
      syndication ! GetFeed("httpx://invalid.url")
      expectMsg(FeedException("httpx://invalid.url","unknown protocol: httpx"))
      success
    }

    "reject host not found" in {
      syndication ! GetFeed("http://host.unknown")
      expectMsg(FeedException("http://host.unknown","host.unknown"))
      success
    }

    "handle result with no content" in {
      syndication ! GetFeed("http://blog.dekkr.nl/thisdoesnotexists")
      expectMsg(FeedException("http://blog.dekkr.nl/thisdoesnotexists", "404: Not Found"))
      success
    }

    "get content from rss feed" in {
      syndication ! GetFeed("http://dekkr.nl/rss")
      expectMsgPF() {
        case FeedContent(url, content) =>
         content.getTitle equals "Dekkr Projects"
      }
    }
  }

}
