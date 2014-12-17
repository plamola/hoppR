package nl.dekkr.hoppr.rss

import com.sun.syndication.io.SyndFeedOutput
import nl.dekkr.hoppr.model._
import org.joda.time.DateTime

/**
 * Author: matthijs 
 * Created on: 15 Dec 2014.
 */
object FeedLogOutput extends FeedToAtom {

  override def getAtomFeed(id: Int): Option[String] = {
    val output: SyndFeedOutput = new SyndFeedOutput()
    Some(output.outputString(wrapFeed(feedType, getAsFeed, convertFetchLogToArticles(FetchLogger.getLast100Errors))))
  }

  def getAsFeed: Feed =
    new Feed(
      title = Option("hoppR log entries"),
      feedurl = "hoppr.local",
      description = Option("Latest 100 log entries"),
      publisheddate = Option(DateTime.now()),
      updateddate = DateTime.now()
    )

  def convertFetchLogToArticles(logEntries: List[FetchLog]): List[Article] = {
    if (logEntries.isEmpty)
      List.empty
    else {
      val logEntry = logEntries.head
      List(
        new Article(
          uri = logEntry.uri,
          publisheddate = Option(logEntry.logdate),
          title = Option(s"[${logEntry.level}] ${logEntry.result.getOrElse("<no result logged>")}"),
          content = Option(s"[${logEntry.level}] - ${logEntry.id.get} - ${logEntry.result.getOrElse("<no result logged>")}")
        )
      ) ++ convertFetchLogToArticles(logEntries.tail)
    }
  }

}
