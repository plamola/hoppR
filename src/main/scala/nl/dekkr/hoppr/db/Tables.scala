package nl.dekkr.hoppr.db

import nl.dekkr.hoppr.model._


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


  class FetchLogTable(tag: Tag) extends Table[FetchLog](tag, "fetchlog") {
    val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    val uri: Column[String] = column[String]("uri", O.Length(1024, varying = true))
    val result : Column[Option[String]] = column[Option[String]]("result", O.Length(1024, varying = true), O.Default(None))
    val logdate: Column[DateTime] = column[DateTime]("logdate")
    val level : Column[LogLevel] = column[LogLevel]("loglevel")

    def * : ProvenShape[FetchLog] =
      (id.?, uri, result, logdate, level) <>(FetchLog.tupled, FetchLog.unapply)
  }
  lazy val fetchLogTable = new TableQuery(tag => new FetchLogTable(tag))


  class ArticleTable(tag: Tag) extends Table[Article](tag, "article") {
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

    def * : ProvenShape[Article] =
      (id.?, feedid.?, uri, link, title, content, author, publisheddate, updateddate, lastsynceddate) <>(Article.tupled, Article.unapply)

    def feed: ForeignKeyQuery[FeedTable, Feed] =
      foreignKey("feed_fk", feedid, TableQuery[FeedTable])(_.id)
  }
  lazy val articleTable = new TableQuery(tag => new ArticleTable(tag))


  class FeedTable(tag: Tag) extends Table[Feed](tag, "feed") {
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

    def * : ProvenShape[Feed] =
      (id.?, feedurl, link, title, description, image, publisheddate, updateddate, updateInterval, nextupdate, lastarticlecount, faviconfk) <>(Feed.tupled, Feed.unapply)
  }
  lazy val feedTable = new TableQuery(tag => new FeedTable(tag))


}