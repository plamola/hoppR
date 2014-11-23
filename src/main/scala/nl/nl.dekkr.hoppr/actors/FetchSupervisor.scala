package nl.dekkr.hoppr.actors

import com.sun.syndication.feed.synd.SyndFeed
import com.sun.xml.internal.bind.v2.TODO
import nl.dekkr.hoppr.actors.FetchSupervisor.Nudge
import nl.dekkr.hoppr.actors.SyndicationActor.GetFeed
import nl.dekkr.hoppr.db.{Tables, Schema}
import akka.event.Logging
import akka.routing.FromConfig

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
      updateSyndicationFeeds()
      updateLinkedInSubscriptions()
      updateTwitterSearches()
    case SyndicationActor.FeedContent(url, content: SyndFeed) =>
      println(s"answer from ${sender()}")
      println(s"Got content for $url - ${content.getEntries.size()} entries")
      // Todo Store content & write log
    case SyndicationActor.FeedNoContentFound(url) =>
      println("No content found")
      // Todo update lastupdated & write log
    case SyndicationActor.FeedException(url, error: String) =>
      println("Feed exception: " + error)
    // Todo update lastupdated & write log
    case _ =>
      log.info("received unknown message")
  }

  def updateSyndicationFeeds(): Unit = {
    log.info("##### Find updatable feeds")
    // TODO move this to a separate object
    val feeds = TableQuery[Tables.Feeds]
    implicit val session = Schema.getSession
    for {c <- feeds.list} roundRobinRouter ! GetFeed(c.feedurl)
    log.info("##### All feeds updates have been requested")
  }

  def updateLinkedInSubscriptions() = ???
  def updateTwitterSearches() = ???



}
