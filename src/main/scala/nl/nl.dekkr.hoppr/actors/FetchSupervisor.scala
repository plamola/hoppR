package nl.dekkr.hoppr.actors

import com.sun.syndication.feed.synd.SyndFeed
import nl.dekkr.hoppr.actors.FetchSupervisor.Nudge
import nl.dekkr.hoppr.actors.SyndicationActor.GetFeed
import nl.dekkr.hoppr.db.{Tables, Schema}
import akka.event.Logging
import akka.routing.FromConfig
import nl.nl.dekkr.hoppr.db.Syndication

import scala.slick.driver.PostgresDriver.simple._


import scala.slick.lifted.TableQuery
import akka.actor.{Props, Actor}

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
      log.info("##### Find updatable content")
      for(url <- Syndication.getFeedsForUpdate) roundRobinRouter ! GetFeed(url)
      // TODO
      //updateLinkedInSubscriptions()
      //updateTwitterSearches()

    case SyndicationActor.FeedContent(url, content: SyndFeed) =>
      Syndication.updateFeedLastUpdated(url)
      // Todo Store content
      Syndication.storeFeed(url, content)
      insertFetchLog(url,s"Got ${content.getEntries.size()} entries")

    case SyndicationActor.FeedNoContentFound(url) =>
      Syndication.updateFeedLastUpdated(url)
      insertFetchLog(url,"No content")

    case SyndicationActor.FeedException(url, error: String) =>
      Syndication.updateFeedLastUpdated(url)
      insertFetchLog(url,s"Feed exception: $error")

    case _ =>
      log.error("unknown message received")
  }

  def updateLinkedInSubscriptions() = ??? // TODO implement
  def updateTwitterSearches() = ???       // TODO implement


  def insertFetchLog(feedUri: String, fetchResult: String): Int = {
    log.debug(s"[$feedUri] $fetchResult")
    TableQuery[Tables.FetchLog] += Tables.FetchLogRow(uri = feedUri, result = Option(fetchResult))
  }

}
