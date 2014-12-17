package nl.dekkr.hoppr.model

import nl.dekkr.hoppr.db.{Schema, Tables}
import nl.dekkr.hoppr.db.Tables.FetchLogTable
import org.joda.time.DateTime
import scala.language.{postfixOps, implicitConversions}
import scala.slick.driver.PostgresDriver.simple._

import scala.slick.lifted.TableQuery

case class FetchLog(id: Option[Int] = None, uri: String, result: Option[String] = None,  logdate: DateTime = DateTime.now(), level : LogLevel = Info)

/**
 * Log results of fetch actions
 */
object FetchLogger {


  implicit val session = Schema.getSession

  implicit def toFetchRowToFetchRowLog(rows : List[FetchLogTable#TableElementType]) : List[FetchLog] = {
    for (row <- rows) yield
    new FetchLog(uri = row.uri, result = row.result,logdate = row.logdate,level = row.level)

  }

  def LogDebug(feedUri: String, fetchResult: String): Unit = writeToFetchLog(feedUri, fetchResult, Debug)

  private def writeToFetchLog(feedUri: String, fetchResult: String, level: LogLevel): Int = {
    TableQuery[Tables.FetchLogTable] += FetchLog(uri = feedUri, result = Option(fetchResult), level = level)
  }

  def LogInfo(feedUri: String, fetchResult: String): Int = writeToFetchLog(feedUri, fetchResult, Info)

  def LogError(feedUri: String, fetchResult: String): Int = writeToFetchLog(feedUri, fetchResult, Error)

  def getLast100: List[FetchLog] = TableQuery[Tables.FetchLogTable].sortBy(_.logdate desc).take(100).list

  // TODO filter on level (Critical, Error)
  def getLast100Errors: List[FetchLog] = TableQuery[Tables.FetchLogTable].sortBy(_.logdate desc).take(100).list



}
