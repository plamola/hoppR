package nl.dekkr.hoppr.rest

import com.sun.syndication.feed.synd._
import com.sun.syndication.io.SyndFeedOutput
import nl.dekkr.hoppr.model.{Feed, Article, Syndication}
import scala.collection.JavaConverters._


/**
 * Author: matthijs 
 * Created on: 15 Dec 2014.
 */
object AtomXml {

  def getAtomFeed(id: Int) : Option[String] ={
    Syndication.getFeedById(id) match {
      case Some(feed) =>
        val articles = Syndication.getArticles(feed.id.get, feed.lastarticlecount)
        val output: SyndFeedOutput = new SyndFeedOutput()
        Some(output.outputString(wrapFeed("atom_0.3", feed, articles)))
      case None =>  None

    }
  }

  private def wrapFeed(feedType: String, feed: Feed, articles: List[Article]) : SyndFeed = {
    val feedOut: SyndFeed = new SyndFeedImpl()
    feedOut.setFeedType(feedType)
    feedOut.setUri(feed.feedurl)
    feedOut.setTitle(feed.title.getOrElse("Untitled feed"))
    if (feed.link != None) feedOut.setLink(feed.link.get)
    if (feed.description != None) feedOut.setDescription(feed.description.get)
    if (feed.copyright != None) feedOut.setCopyright(feed.copyright.get)
    if (feed.publisheddate != None) feedOut.setPublishedDate(feed.publisheddate.get.toDate)
    feedOut.setEntries(wrapEntries(articles).asJava)
    feedOut
  }


  private def wrapEntries(articles : List[Article]) :List[SyndEntry] = {
    if (articles.isEmpty)
      List.empty
    else
      List(wrapFeedEntry(articles.head)) ++ wrapEntries(articles.tail)
  }

  private def wrapFeedEntry(feedItem : Article) : SyndEntry = {
    val entry: SyndEntry = new SyndEntryImpl()
    entry.setTitle(feedItem.title.get)
    entry.setLink(feedItem.link.get)
    entry.setPublishedDate(feedItem.publisheddate.get.toDate)
    val description: SyndContent = new SyndContentImpl()
    description.setType("text/html")
    description.setValue(feedItem.content.get)
    entry.setDescription(description)
    entry
  }


}
