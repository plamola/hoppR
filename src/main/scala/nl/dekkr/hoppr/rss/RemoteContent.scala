package nl.dekkr.hoppr.rss

import com.sun.syndication.feed.synd._
import com.sun.syndication.io._
import nl.dekkr.hoppr.model.FetchLogger

import scalaj.http.{Http, HttpException}

/**
 * Created by matthijs on 12/26/13.
 *
 * Get feed content
 */

case class RemoteContent() {


  def fetchSyndicationContent(feedurl: String): Option[SyndFeed] = {
    // No try-catch: caller will must handle the failure
    val responseBody = Http(feedurl).asString
    val sfi = new SyndFeedInput()
    Some(sfi.build(new XmlReader(new java.io.ByteArrayInputStream(responseBody.getBytes("UTF-8")))))
    }

}
