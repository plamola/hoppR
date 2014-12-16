package nl.dekkr.hoppr.rest

import com.sun.syndication.feed.synd._
import com.sun.syndication.io.SyndFeedOutput
import nl.dekkr.hoppr.model._
import org.joda.time.DateTime

import scala.collection.JavaConverters._


/**
 * Author: matthijs 
 * Created on: 15 Dec 2014.
 */
object FeedLogXml {

  def getAtomFeed : String ={
    val logEntries = FetchLogger.getLast100Errors
    val output: SyndFeedOutput = new SyndFeedOutput()
    output.outputString(wrapLog("atom_0.3", logEntries))
  }

  private def wrapLog(feedType: String, logEntries: List[FetchLog]) : SyndFeed = {
    val feedOut: SyndFeed = new SyndFeedImpl()
    feedOut.setFeedType(feedType)
    feedOut.setUri("hoppr.local")
    feedOut.setTitle("hoppR log entries")
    feedOut.setDescription("Latest 100 log entries")
    feedOut.setPublishedDate(DateTime.now().toDate)
    feedOut.setEntries(wrapEntries(logEntries).asJava)
    feedOut
  }


  private def wrapEntries(articles : List[FetchLog]) :List[SyndEntry] = {
    if (articles.isEmpty)
      List.empty
    else
      List(wrapFeedEntry(articles.head)) ++ wrapEntries(articles.tail)
  }

  private def wrapFeedEntry(feedItem : FetchLog) : SyndEntry = {
    val entry: SyndEntry = new SyndEntryImpl()
    entry.setTitle(s"${feedItem.id.get}-${feedItem.level}" )
    entry.setLink(feedItem.uri)
    entry.setPublishedDate(feedItem.logdate.toDate)
    val description: SyndContent = new SyndContentImpl()
    description.setType("text/html")
    description.setValue(feedItem.result.get)
    entry.setDescription(description)
    entry
  }


}
