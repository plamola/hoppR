package nl.dekkr.hoppr.db

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
  }

  def getSession = {
    Database.forURL(url = "jdbc:postgresql://localhost/feedrdev", user = "feedr", password = "narcoticflowerelecticgrey",
      driver = "org.postgresql.Driver").createSession()
  }


  }
