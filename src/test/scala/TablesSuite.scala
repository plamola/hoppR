
import nl.dekkr.hoppr.db.{Tables, Schema}
import nl.dekkr.hoppr.model.{FetchLog, Article, Feed}
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.meta._


class TablesSuite extends HopprTestBase {

  val feedsTable = TableQuery[Tables.FeedTable]
  val articlesTable = TableQuery[Tables.ArticleTable]
  val fetchlogTable = TableQuery[Tables.FetchLogTable]


  val testFeedUrl = "http://url"

  //def createSchema() = Tables.ddl.create

  def insertFeed(): Int = feedsTable += Feed( feedurl = testFeedUrl, link = Option("link"), title = Option("feed title"))

  def insertArticle(): Int = articlesTable += Article(feedid = Option(1), uri = "uri", title = Option("article title"))

  def insertFetchLog(): Int = fetchlogTable += FetchLog(uri = "log-uri", result = Option("result"))


  def before() = {
    session = Schema.getSession
  }

   "TablesSuite" should {
     "Recreate the schema" in {
       cleanDB()
       val tables = MTable.getTables.list
       tables.count(_.name.name.equalsIgnoreCase("fetchlog")) should be equalTo 1
       tables.count(_.name.name.equalsIgnoreCase("article")) should be equalTo 1
       tables.count(_.name.name.equalsIgnoreCase("feed")) should be equalTo 1
     }

     "Insert a feed" in  {
       insertFeed() should be equalTo 1
     }

     "Query feeds" in {
       val results = feedsTable.filter(_.feedurl === testFeedUrl ).list
       results.size should be equalTo 1
       results.head.feedurl must be equalTo testFeedUrl
       results.head.link must be equalTo Option("link")
     }

     "Insert an article" in {
       insertArticle() must be equalTo 1
     }

     "Query articles" in {
       val results = articlesTable.filter (_.title === "article title").list
       results.size must be equalTo 1
       results.head.id must be equalTo Option(1)
       results.head.title must be equalTo Option("article title")
     }

     "Join feed with article" in {
       val exisitingFeed = feedsTable.filter(_.feedurl === testFeedUrl ).list.head
       val articleUri = "uri2"
       articlesTable += Article(feedid = exisitingFeed.id, uri = articleUri , title = Option("article 2"))
       val joinQuery: Query[(Column[String], Column[Option[String]]), (String, Option[String]), Seq] = for {
         a <- articlesTable if a.feedid === exisitingFeed.id
         f <- a.feed
       } yield (a.uri, f.title)
       val joinedResults = joinQuery.filter(_._1 === articleUri ).list
       joinedResults.size must be equalTo 1
       joinedResults.head._1 must be equalTo articleUri
       joinedResults.head._2 must be equalTo Option("feed title")
     }

     "Insert a fetchlog" in {
       insertFetchLog() must be equalTo 1
     }

     "Query fetchlogs" in {
       val results = fetchlogTable.list
       results.size must be equalTo 1
       results.head.uri must be equalTo "log-uri"
       results.head.result must be equalTo Option("result")
     }

   }

  def after() = session.close()


}