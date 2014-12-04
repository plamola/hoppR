package nl.dekkr.hoppr.db

import com.typesafe.config.ConfigFactory

import scala.slick.jdbc.meta.MTable
import scala.slick.driver.PostgresDriver.simple._

/**
 * Author: matthijs 
 * Created on: 31 Aug 2014.
 */
object Schema {


  def createOrUpdate(implicit session: Session) {
    val existingTables = MTable.getTables.list
    if (!existingTables.exists(_.name.name.equalsIgnoreCase("feed"))) {
      Tables.Feeds.ddl.create
    } else {
      // Update existing table structure
    }
    if (!existingTables.exists(_.name.name.equalsIgnoreCase("article"))) {
      Tables.Articles.ddl.create
    } else {
      // Update existing table structure
    }
    if (!existingTables.exists(_.name.name.equalsIgnoreCase("fetchlog"))) {
      Tables.FetchLog.ddl.create
    } else {
      // Update existing table structure
    }
  }

  def getSession = {
    var conf = ConfigFactory.load
    Database.forURL(url = conf.getString("hoppr.database.url"), user = conf.getString("hoppr.database.user"), password = conf.getString("hoppr.database.password"),
      driver = "org.postgresql.Driver").createSession()
  }

}
