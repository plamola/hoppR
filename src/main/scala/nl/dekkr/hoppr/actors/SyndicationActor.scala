package nl.dekkr.hoppr.actors

import akka.actor.Actor
import com.sun.syndication.feed.synd.SyndFeed
import nl.dekkr.hoppr.actors.SyndicationActor._
import nl.dekkr.hoppr.rss.RemoteContent

import scala.util.{Failure, Success, Try}

/**
 * Author: matthijs 
 * Created on: 14 Aug 2014.
 */

object SyndicationActor {

  case class GetFeed(url: String)

  case class GetFavico(url: String)

  case class FeedContent(url: String, content: SyndFeed)

  case class FeedNoContentFound(url: String)

  case class FeedException(url: String, error: String)

}


class SyndicationActor extends Actor {

  def receive: Receive = {
    case GetFeed(url) =>
      Try(RemoteContent().fetchSyndicationContent(url)) match {
        case Success(content) =>
          content match {
            case Some(feed) =>
              sender ! FeedContent(url, feed)
            case None =>
              sender ! FeedNoContentFound(url)
          }
        case Failure(e) =>
          sender ! FeedException(url, e.getMessage)
      }
    case GetFavico(url) =>
    // TODO implement this (copy from FeedR)

  }

}
