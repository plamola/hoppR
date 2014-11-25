package nl.nl.dekkr.hoppr.model

import com.sun.syndication.feed.synd.{SyndContent, SyndEntry, SyndFeed}
import nl.dekkr.hoppr.db.Tables.{ArticleRow, FeedRow}
import nl.dekkr.hoppr.db.{Schema, Tables}
import org.joda.time.DateTime

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.lifted.TableQuery


/**
 * Created by Matthijs Dekker on 24/11/14.
 */
object Syndication {

  implicit val session = Schema.getSession

  def getFeedsForUpdate : List[String] = {
    val feeds = TableQuery[Tables.Feeds]
    for {c <- feeds.list if c.nextupdate < DateTime.now().getMillis } yield c.feedurl
  }


  def setNextUpdate(uri: String): Unit = {
    val feeds = TableQuery[Tables.Feeds]
    val q = for { c <- feeds if c.feedurl === uri } yield c.nextupdate
    // TODO use the interval set in the feed, not the hard-coded value of 60
    q.update(DateTime.now().plusMinutes(60).getMillis)
    val statement = q.updateStatement
    val invoker = q.updateInvoker
  }


  def storeFeed(uri: String, content: SyndFeed): Int = {
    var newArticleCount : Int = 0
    val feeds = TableQuery[Tables.Feeds]
    // get the feed from the db
    for ( feed <- feeds if feed.feedurl === uri ) {
      Tables.Feeds.filter(_.id === feed.id.get).update(makeFeedRow(uri, feed.id, feed.faviconfk, feed.updateInterval, content))
      // iterate through articles
      val it = content.getEntries.iterator()
      while (it.hasNext) {
        val entry: SyndEntry = it.next().asInstanceOf[SyndEntry]
        // get existing article
        val q = TableQuery[Tables.Articles].filter(a => a.feedid === feed.id.get && a.uri === entry.getUri)
        if (q.list.size > 0) {
          // update existing article
          val article = q.first
          Tables.Articles.filter(_.id === article.id.get).update(makeArticleRow(article.id, article.feedid, entry))
        } else {
          // insert new article
          Tables.Articles += makeArticleRow(None, feed.id, entry)
          newArticleCount += 1
        }
      }
    }
    newArticleCount
  }


  private def makeArticleRow(articleId : Option[Int], feedFk: Option[Int], entry : SyndEntry) : ArticleRow = {
    new ArticleRow(
      id = articleId,
      feedid = feedFk,
      uri = entry.getUri,
      link = Some(entry.getLink),
      title = Some(entry.getTitle),
      content = Some(extractContent(entry)),
      author = Some(entry.getAuthor),
      publisheddate = toJodaDateTime(entry.getPublishedDate),
      updateddate = toJodaDateTime(entry.getUpdatedDate),
      lastsynceddate =Some(DateTime.now)
    )
  }


  private def makeFeedRow(uri: String, feedId : Option[Int], faviconfk : Int, interval : Int, content: SyndFeed) : FeedRow = {
    new FeedRow(
    id = feedId,
    faviconfk = faviconfk,
    feedurl = {if (content.getUri != null) content.getUri else uri },
    link = Some(content.getLink),
    title = Some(content.getTitle),
    description = Some(content.getDescription),
    publisheddate = toJodaDateTime(content.getPublishedDate),
    updateddate = DateTime.now(),
    image = { if (content.getImage != null ) Some(content.getImage.getUrl) else None },
    nextupdate = DateTime.now().plusMinutes(interval).getMillis,
    updateInterval = interval,
    lastarticlecount = content.getEntries.size()
    )
  }


  // Needed because postgres didn't like the original date types
  private def toJodaDateTime(date : java.util.Date) : Option[DateTime] = {
    if (date != null) Some(new DateTime(date)) else None
  }


  private def extractContent(entry: SyndEntry): String = {
    var content: String = ""
    if (entry.getDescription != null) {
      content = entry.getDescription.getValue
    } else {
      val it = entry.getContents.iterator()
      while (it.hasNext) {
        val contentPart: SyndContent = it.next().asInstanceOf[SyndContent]
        content = content + contentPart.getValue
      }
    }
    content
  }


}
