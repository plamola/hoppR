package nl.dekkr.hoppr.db

import nl.nl.dekkr.hoppr.model.{Info, Error, Warning, Critical, Debug, LogLevel}

/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = scala.slick.driver.PostgresDriver
} with Tables



/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: scala.slick.driver.JdbcProfile

  import org.joda.time.DateTime

  import scala.slick.driver.PostgresDriver.simple._
  import scala.slick.lifted.{ProvenShape, ForeignKeyQuery}
  import com.github.tototoshi.slick.PostgresJodaSupport._

  //lazy val ddl = Suppliers.ddl ++ Coffees.ddl ++ Articles.ddl ++ Feeds.ddl
  lazy val ddl = FetchLog.ddl ++ Articles.ddl ++ Feeds.ddl

  implicit val loglevelColumnType = MappedColumnType.base[LogLevel, Int](
  {  b =>
    if (b == Critical) 1 else
    if (b == Error) 2 else
    if (b == Warning) 3 else
    if (b == Info) 4 else
    if (b == Debug) 5 else
      0
  }, // Map LogLevel to Int
  { i =>
    if (i == 1)  Critical else
    if (i == 2)  Error else
    if (i == 3)  Warning else
    if (i == 4)  Info else
    Debug
  } // map Int to LogLevel
  )



  case class FetchLogRow(id: Option[Int] = None, uri: String, result: Option[String] = None,  logdate: DateTime = DateTime.now(), level : LogLevel = Info)

  class FetchLog(tag: Tag) extends Table[FetchLogRow](tag, "fetchlog") {
    val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    val uri: Column[String] = column[String]("uri", O.Length(1024, varying = true))
    val result : Column[Option[String]] = column[Option[String]]("result", O.Length(1024, varying = true), O.Default(None))
    val logdate: Column[DateTime] = column[DateTime]("logdate")
    val level : Column[LogLevel] = column[LogLevel]("loglevel")

    def * : ProvenShape[FetchLogRow] =
      (id.?, uri, result, logdate, level) <>(FetchLogRow.tupled, FetchLogRow.unapply)
  }
  lazy val FetchLog = new TableQuery(tag => new FetchLog(tag))


  case class ArticleRow(id: Option[Int] = None, feedid: Option[Int] = None, uri: String, link: Option[String] = None, title: Option[String] = None, content: Option[String] = None, author: Option[String] = None, publisheddate: Option[DateTime] = None, updateddate: Option[DateTime] = None, lastsynceddate: Option[DateTime] = None)

  class Articles(tag: Tag) extends Table[ArticleRow](tag, "article") {
    val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    val feedid: Column[Int] = column[Int]("feedid")
    val uri: Column[String] = column[String]("uri", O.Length(1024, varying = true))
    val link: Column[Option[String]] = column[Option[String]]("link", O.Length(1024, varying = true), O.Default(None))
    val title: Column[Option[String]] = column[Option[String]]("title", O.Length(255, varying = true), O.Default(None))
    val content: Column[Option[String]] = column[Option[String]]("content", O.Length(10485760, varying = true), O.Default(None))
    val author: Column[Option[String]] = column[Option[String]]("author", O.Length(255, varying = true), O.Default(None))
    val publisheddate: Column[Option[DateTime]] = column[Option[DateTime]]("publisheddate", O.Default(None))
    val updateddate: Column[Option[DateTime]] = column[Option[DateTime]]("updateddate", O.Default(None))
    val lastsynceddate: Column[Option[DateTime]] = column[Option[DateTime]]("lastsynceddate", O.Default(None))

    /** Uniqueness Index over (feedid, uri)  **/
    def idx = index("unique_feedid_uri", (feedid, uri), unique = true)

    def * : ProvenShape[ArticleRow] =
      (id.?, feedid.?, uri, link, title, content, author, publisheddate, updateddate, lastsynceddate) <>(ArticleRow.tupled, ArticleRow.unapply)

    def feed: ForeignKeyQuery[Feeds, FeedRow] =
      foreignKey("feed_fk", feedid, TableQuery[Feeds])(_.id)
  }
  lazy val Articles = new TableQuery(tag => new Articles(tag))



  case class FeedRow(id: Option[Int] = None, feedurl: String, link: Option[String] = None, title: Option[String] = None, description: Option[String] = None, image: Option[String] = None, publisheddate: Option[DateTime] = None, updateddate: DateTime = DateTime.now(), updateInterval: Int = 60, nextupdate : Long = DateTime.now().getMillis, lastarticlecount: Int = 0, faviconfk: Int = 0)

  class Feeds(tag: Tag) extends Table[FeedRow](tag, "feed") {
    val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    val feedurl: Column[String] = column[String]("feedurl", O.Length(1024, varying = true))
    val link: Column[Option[String]] = column[Option[String]]("link", O.Length(1024, varying = true), O.Default(None))
    val title: Column[Option[String]] = column[Option[String]]("title", O.Length(255, varying = true), O.Default(None))
    val description: Column[Option[String]] = column[Option[String]]("description", O.Length(1024, varying = true), O.Default(None))
    val image: Column[Option[String]] = column[Option[String]]("image", O.Length(255, varying = true), O.Default(None))
    val publisheddate: Column[Option[DateTime]] = column[Option[DateTime]]("publisheddate", O.Default(None))
    val updateddate: Column[DateTime] = column[DateTime]("updateddate", O.Default(DateTime.now()))
    val updateInterval: Column[Int] = column[Int]("update_interval", O.Default(60))
    val nextupdate: Column[Long] = column[Long]("nextupdate", O.Default(DateTime.now().getMillis))
    val lastarticlecount: Column[Int] = column[Int]("lastarticlecount", O.Default(0))
    val faviconfk: Column[Int] = column[Int]("faviconfk", O.Default(0))

    /** Uniqueness Index over (feedurl) (database name feed_feedurl_key) */
    val index1 = index("feed_feedurl_key", feedurl, unique = true)

    def * : ProvenShape[FeedRow] =
      (id.?, feedurl, link, title, description, image, publisheddate, updateddate, updateInterval, nextupdate, lastarticlecount, faviconfk) <>(FeedRow.tupled, FeedRow.unapply)
  }
  lazy val Feeds = new TableQuery(tag => new Feeds(tag))








  // A Suppliers table with 6 columns: id, name, street, city, state, zip
  class Suppliers(tag: Tag)
    extends Table[(Int, String, String, String, String, String)](tag, "suppliers") {

    // This is the primary key column:
    def id: Column[Int] = column[Int]("sup_id", O.PrimaryKey)
    def name: Column[String] = column[String]("sup_name")
    def street: Column[String] = column[String]("street")
    def city: Column[String] = column[String]("city")
    def state: Column[String] = column[String]("state")
    def zip: Column[String] = column[String]("zip")

    // Every table needs a * projection with the same type as the table's type parameter
    def * : ProvenShape[(Int, String, String, String, String, String)] =
      (id, name, street, city, state, zip)
  }
  lazy val Suppliers = new TableQuery(tag => new Suppliers(tag))



  // A Coffees table with 5 columns: name, supplier id, price, sales, total
  class Coffees(tag: Tag)
    extends Table[(String, Int, Double, Int, Int)](tag, "coffees") {

    def name: Column[String] = column[String]("cof_name", O.PrimaryKey)
    def supID: Column[Int] = column[Int]("sup_id")
    def price: Column[Double] = column[Double]("price")
    def sales: Column[Int] = column[Int]("sales")
    def total: Column[Int] = column[Int]("total")

    def * : ProvenShape[(String, Int, Double, Int, Int)] =
      (name, supID, price, sales, total)

    // A reified foreign key relation that can be navigated to create a join
    def supplier: ForeignKeyQuery[Suppliers, (Int, String, String, String, String, String)] =
      foreignKey("sup_fk", supID, TableQuery[Suppliers])(_.id)
  }
  lazy val Coffees = new TableQuery(tag => new Coffees(tag))


}