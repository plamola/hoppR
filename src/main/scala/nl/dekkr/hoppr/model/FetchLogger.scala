package nl.dekkr.hoppr.model

import nl.dekkr.hoppr.db.{Schema, Tables}
import nl.dekkr.hoppr.db.Tables.FetchLog
import nl.dekkr.hoppr.db.Tables.FetchLogRow
import nl.nl.dekkr.hoppr.model.{Info, Debug, Error, LogLevel}
import scala.slick.driver.PostgresDriver.simple._

import scala.slick.lifted.TableQuery


/**
 * Created by Matthijs Dekker on 25/11/14.
 */
object FetchLogger {

  implicit def toFetchRowToFetchRowLog(rows : List[FetchLog#TableElementType]) : List[FetchLogRow] = {
    for (row <- rows) yield
    new FetchLogRow(uri = row.uri, result = row.result,logdate = row.logdate,level = row.level)

  }

  implicit val session = Schema.getSession

  def LogDebug(feedUri: String, fetchResult: String): Unit = writeToFetchLog(feedUri, fetchResult, Debug)
  def LogInfo(feedUri: String, fetchResult: String): Int = writeToFetchLog(feedUri, fetchResult, Info)
  def LogError(feedUri: String, fetchResult: String): Int = writeToFetchLog(feedUri, fetchResult, Error)

  private def writeToFetchLog(feedUri: String, fetchResult: String, level: LogLevel): Int = {
    //log.debug(s"[$feedUri] $fetchResult")
    TableQuery[Tables.FetchLog] += Tables.FetchLogRow(uri = feedUri, result = Option(fetchResult), level = level)
  }

  def getLast100 : List[FetchLogRow] = TableQuery[Tables.FetchLog].take(100).sortBy(_.logdate desc).list

}
