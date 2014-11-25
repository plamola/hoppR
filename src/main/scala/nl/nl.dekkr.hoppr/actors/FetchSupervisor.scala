package nl.dekkr.hoppr.actors

import com.sun.syndication.feed.synd.SyndFeed
import nl.dekkr.hoppr.actors.FetchSupervisor.Nudge
import nl.dekkr.hoppr.actors.SyndicationActor.GetFeed
import nl.dekkr.hoppr.db.{Tables, Schema}
import akka.event.Logging
import akka.routing.FromConfig
import nl.nl.dekkr.hoppr.model.{Info, Debug, Error, Syndication, LogLevel}

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
      // TODO Implement LinkedIn & Twitter
      //updateLinkedInSubscriptions()
      //updateTwitterSearches()

    case SyndicationActor.FeedContent(url, content: SyndFeed) =>
     Syndication.storeFeed(url, content) match {
      case 0 =>   LogDebug(url,s"Got ${content.getEntries.size()} existing articles")
      case count: Int =>  LogInfo(url,s"${count} new articles")
    }

    case SyndicationActor.FeedNoContentFound(url) =>
      LogError(url,"No content")
      Syndication.setNextUpdate(url)

    case SyndicationActor.FeedException(url, error: String) =>
      LogError(url,s"Feed exception: $error")
      Syndication.setNextUpdate(url)

    case _ =>
      log.error("unknown message received")
  }


  private def LogDebug(feedUri: String, fetchResult: String): Unit = writeToFetchLog(feedUri, fetchResult, Debug)
  private def LogInfo(feedUri: String, fetchResult: String): Int = writeToFetchLog(feedUri, fetchResult, Info)
  private def LogError(feedUri: String, fetchResult: String): Int = writeToFetchLog(feedUri, fetchResult, Error)

  private def writeToFetchLog(feedUri: String, fetchResult: String, level: LogLevel): Int = {
    log.debug(s"[$feedUri] $fetchResult")
    TableQuery[Tables.FetchLog] += Tables.FetchLogRow(uri = feedUri, result = Option(fetchResult), level = level)
  }


}
