package nl.dekkr.hoppr.rss

import com.sun.syndication.feed.synd._
import com.sun.syndication.io._

import scalaj.http.{Http, HttpException}

/**
 * Created by matthijs on 12/26/13.
 *
 * Get feed content
 */

case class RemoteContent() {


  def fetchSyndicationContent(feedurl: String): Option[SyndFeed] = {

    try {
      val responseBody = Http(feedurl).asString
      //print(responseBody)

      try {
        val sfi = new SyndFeedInput()
        Some(sfi.build(new XmlReader(new java.io.ByteArrayInputStream(responseBody.getBytes("UTF-8")))))
      } catch {
        case e: ParsingFeedException =>
          //        Feed.findByFeedUrl(feedurl) match {
          //          case Some(feed) =>
          //            //Logger.error(s"Parsing exception: ${e.getMessage}")
          //            SyncLog.create(feed.id, DateTime.now, 0, 0, s"Parsing exception: ${e.getMessage}")
          //            // Mark as synced, to prevent retries every minute
          //            Feed.markFeedUpdated(feed.id)
          //          case None =>
          print(s"Parsing exception for url [$feedurl}]: ${e.getMessage}")
          //        }
          None
        case e: Throwable =>
          None
      }
      //None
    } catch {
      case e: HttpException =>
        print(s"Fetch exception for url [$feedurl}]: ${e.getMessage}")
        None
    }

    }

}
