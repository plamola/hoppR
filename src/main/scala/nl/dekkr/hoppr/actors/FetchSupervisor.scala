package nl.dekkr.hoppr.actors

import com.sun.syndication.feed.synd.SyndFeed
import nl.dekkr.hoppr.actors.FetchSupervisor.Nudge
import nl.dekkr.hoppr.actors.SyndicationActor.GetFeed
import nl.dekkr.hoppr.db.Schema
import akka.event.Logging
import akka.routing.FromConfig
import nl.dekkr.hoppr.model.FetchLogger


import akka.actor.{Props, Actor}
import nl.nl.dekkr.hoppr.model.Syndication

/**
 * Author: matthijs 
 * Created on: 27 Dec 2013.
 */
object FetchSupervisor {

  case object Nudge

}

class FetchSupervisor extends Actor {

  val log = Logging(context.system, this)
  val roundRobinRouter = context.actorOf(Props[SyndicationActor].withRouter(FromConfig()), "syndication")

  implicit val session = Schema.getSession

  def receive = {
    case Nudge =>
      log.info("#### Looking for updatable content ####")
      for(url <- Syndication.getFeedsForUpdate) roundRobinRouter ! GetFeed(url)
      // TODO Implement LinkedIn & Twitter
      //updateLinkedInSubscriptions()
      //updateTwitterSearches()

    case SyndicationActor.FeedContent(url, content: SyndFeed) =>
     Syndication.storeFeed(url, content) match {
      case 0 =>   FetchLogger.LogDebug(url,"No new articles")
      case 1 =>  FetchLogger.LogInfo(url,"1 new article")
      case count: Int =>  FetchLogger.LogInfo(url,s"$count new articles")
    }

    case SyndicationActor.FeedNoContentFound(url) =>
      FetchLogger.LogError(url,"No content")
      Syndication.setNextUpdate(url)

    case SyndicationActor.FeedException(url, error: String) =>
      FetchLogger.LogError(url,s"Feed exception: $error")
      Syndication.setNextUpdate(url)

    case _ =>
      log.error("unknown message received")
  }

}
