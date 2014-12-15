package nl.dekkr.hoppr.model

import com.sun.syndication.feed.synd.{SyndContent, SyndEntry, SyndFeed}
import nl.dekkr.hoppr.db.{Schema, Tables}
import org.joda.time.DateTime

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.lifted.TableQuery


case class Feed(id: Option[Int] = None, feedurl: String, link: Option[String] = None, title: Option[String] = None, description: Option[String] = None, image: Option[String] = None, publisheddate: Option[DateTime] = None, updateddate: DateTime = DateTime.now(), updateInterval: Int = 60, nextupdate : Long = DateTime.now().getMillis, lastarticlecount: Int = 0, faviconfk: Int = 0)

case class Article(id: Option[Int] = None, feedid: Option[Int] = None, uri: String, link: Option[String] = None, title: Option[String] = None, content: Option[String] = None, author: Option[String] = None, publisheddate: Option[DateTime] = None, updateddate: Option[DateTime] = None, lastsynceddate: Option[DateTime] = None)


/**
 * Handles syndication processing
 */
object Syndication {

  implicit val session = Schema.getSession
  val feeds = TableQuery[Tables.FeedTable]

  def getFeedsForUpdate : List[String] = {
    for {c <- feeds.list if c.nextupdate < DateTime.now().getMillis } yield c.feedurl
  }

  def setNextUpdate(uri: String): Unit = {
    val query = feeds.filter(_.feedurl === uri)
    val feed = query.first
    val updatedFeed = feed.copy(
      nextupdate = DateTime.now().plusMinutes(feed.updateInterval).getMillis
    )
    query.update(updatedFeed)
  }

  def storeFeed(uri: String, content: SyndFeed): Int = {
    var newArticleCount : Int = 0
    // get the feed from the db
    for ( feed <- feeds if feed.feedurl === uri ) {
      Tables.feedTable.filter(_.id === feed.id.get).update(makeFeedRow(uri, feed.id, feed.faviconfk, feed.updateInterval, content))
      // iterate through articles
      val it = content.getEntries.iterator()
      while (it.hasNext) {
        val entry: SyndEntry = it.next().asInstanceOf[SyndEntry]
        // get existing article
        val q = TableQuery[Tables.ArticleTable].filter(a => a.feedid === feed.id.get && a.uri === entry.getUri)
        if (q.list.size > 0) {
          // update existing article
          val article = q.first
          Tables.articleTable.filter(_.id === article.id.get).update(makeArticleRow(article.id, article.feedid, entry))
        } else {
          // insert new article
          Tables.articleTable += makeArticleRow(None, feed.id, entry)
          newArticleCount += 1
        }
      }
    }
    newArticleCount
  }


  private def makeArticleRow(articleId : Option[Int], feedFk: Option[Int], entry : SyndEntry) : Article = {
    new Article(
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

  private def makeFeedRow(uri: String, feedId: Option[Int], faviconfk: Int, interval: Int, content: SyndFeed): Feed = {
    new Feed(
      id = feedId,
      faviconfk = faviconfk,
      feedurl = {
        if (content.getUri != null) content.getUri else uri
      },
      link = Some(content.getLink),
      title = Some(content.getTitle),
      description = Some(content.getDescription),
      publisheddate = toJodaDateTime(content.getPublishedDate),
      updateddate = DateTime.now(),
      image = {
        if (content.getImage != null) Some(content.getImage.getUrl) else None
      },
      nextupdate = DateTime.now().plusMinutes(interval).getMillis,
      updateInterval = interval,
      lastarticlecount = content.getEntries.size()
    )
  }

  // Needed because postgres didn't like the original date types
  private def toJodaDateTime(date: java.util.Date): Option[DateTime] = {
    if (date != null) Some(new DateTime(date)) else None
  }

  def addNewFeed(url: String): Feed = {
    if (feeds.filter(_.feedurl === url  ).list.size == 0)
      feeds += Feed( feedurl = url)
    getFeed(url).get
  }

  def getFeed(url: String): Option[Feed] = feeds.filter(_.feedurl === url).firstOption

  def removeFeed(url: String): Int = {
    feeds.filter(_.feedurl === url).delete
  }

}
