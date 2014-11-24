package nl.nl.dekkr.hoppr.db

import nl.dekkr.hoppr.db.{Schema, Tables}
import org.joda.time.DateTime

import scala.slick.lifted.TableQuery
import scala.slick.driver.PostgresDriver.simple._


/**
 * Created by Matthijs Dekker on 24/11/14.
 */
object Syndication {

  implicit val session = Schema.getSession

  def getFeedsForUpdate(): List[String] = {
    implicit val session = Schema.getSession
    val feeds = TableQuery[Tables.Feeds]
    // TODO get only the feeds that need updating
    //for {c <- feeds if c.updateddate.minusMinutes(c.updateInterval) < DateTime.now() } roundRobinRouter ! GetFeed(c.feedurl)
    for {c <- feeds.list } yield c.feedurl
  }


  def updateFeedLastUpdated(feedUri: String): Unit = {
    val feeds = TableQuery[Tables.Feeds]
    val q = for { c <- feeds if c.feedurl === feedUri } yield c.updateddate
    q.update(DateTime.now())
    val statement = q.updateStatement
    val invoker = q.updateInvoker
  }




}
