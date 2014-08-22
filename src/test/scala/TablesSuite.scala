import org.scalatest._
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.meta._


class TablesSuite extends FunSuite with BeforeAndAfter {

  //  val suppliers = TableQuery[Tables.Suppliers]
  //  val coffees = TableQuery[Tables.Coffees]
  val feeds = TableQuery[Tables.Feeds]
  val articles = TableQuery[Tables.Articles]

  implicit var session: Session = _

  def createSchema() = Tables.ddl.create

  //def insertSupplier(): Int = suppliers += (101, "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199")
  def insertFeed(): Int = feeds += Tables.FeedRow(id = 1, feedurl = "url", link = Option("link"), title = Option("feed title"))

  def insertArticle(): Int = articles += Tables.ArticleRow(feedid = Option(1), uri = "uri", title = Option("article title"))


  before {
    //session = Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver").createSession()
    session = Database.forURL(url = "jdbc:postgresql://localhost/feedrtest",
      user = "feedr", password = "narcoticflowerelecticgrey",
      driver = "org.postgresql.Driver").createSession()
  }

  test("Recreating the schema") {
    val existingTables = MTable.getTables.list
    if (existingTables.exists(_.name.name.equalsIgnoreCase("coffees"))) {
      Tables.Coffees.ddl.drop
    }
    if (existingTables.exists(_.name.name.equalsIgnoreCase("suppliers"))) {
      Tables.Suppliers.ddl.drop
    }
    if (existingTables.exists(_.name.name.equalsIgnoreCase("article"))) {
      Tables.Articles.ddl.drop
    }
    if (existingTables.exists(_.name.name.equalsIgnoreCase("feed"))) {
      Tables.Feeds.ddl.drop
    }
    Tables.ddl.create

    val tables = MTable.getTables.list
    assert(tables.count(_.name.name.equalsIgnoreCase("article")) == 1)
    assert(tables.count(_.name.name.equalsIgnoreCase("feed")) == 1)
  }


  test("Inserting a feed works") {
    //createSchema()
    val insertCount = insertFeed()
    assert(insertCount == 1)
  }

  test("Query feeds works") {
    //createSchema()
    //insertFeed()
    val results = feeds.list
    assert(results.size == 1)
    assert(results.head.feedurl == "url")
    assert(results.head.link == Option("link"))
  }

  test("Inserting a article works") {
    //createSchema()
    val insertCount = insertArticle()
    assert(insertCount == 1)
  }

  test("Query article works") {
    //createSchema()
    //insertArticle()
    val results = articles.list
    assert(results.size == 1)
    assert(results.head.id == Option(1))
    assert(results.head.title == Option("article title"))
  }
  test("Join works"){
    val exisitingFeed = feeds.list.head
    articles += Tables.ArticleRow(feedid = Option(exisitingFeed.id), uri = "uri2", title = Option("article 2"))
    val joinQuery: Query[(Column[String], Column[Option[String]]), (String, Option[String]), Seq] = for {
      a <- articles //if a.feedid == exisitingFeed.id
      f <- a.feed
    } yield (a.uri, f.title)
    val joinedResults = joinQuery.list.filter(_._1 == "uri2")
    assert(joinedResults.size == 1)
    assert(joinedResults.head._1 == "uri2")
    assert(joinedResults.head._2 == Option("feed title"))
  }


  after {
    session.close()
  }

}