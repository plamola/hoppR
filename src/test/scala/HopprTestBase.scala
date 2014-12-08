import akka.actor.{ActorSystem, ActorRefFactory}
import nl.dekkr.hoppr.db.{Tables, Schema}
import nl.dekkr.hoppr.rest.RestService
import scala.slick.driver.PostgresDriver.simple._
import org.scalatest.prop.Configuration
import org.specs2.mutable.Specification
import spray.routing.HttpService
import spray.testkit.Specs2RouteTest

import scala.slick.jdbc.meta.MTable

/**
 * Standard Test Base.
 */
trait HopprTestBase extends Specification
with Specs2RouteTest with HttpService
with Configuration {

  implicit var session: Session = _


  args(sequential = true)

  // connects the DSL to the test ActorSystem
  implicit def actorRefFactory: ActorSystem = system

  val spec = this

  val restService = new RestService {
    override implicit def actorRefFactory: ActorRefFactory = spec.actorRefFactory
  }.myRoute



  def cleanDB(): Unit = {
    session = Schema.getSession
    dropDatabaseTables()
    Schema.createOrUpdate(session)
  }



  private def dropDatabaseTables() : Unit = {
    val existingTables = MTable.getTables.list
    if (existingTables.exists(_.name.name.equalsIgnoreCase("fetchlog"))) {
      Tables.fetchLogTable.ddl.drop
    }
    if (existingTables.exists(_.name.name.equalsIgnoreCase("article"))) {
      Tables.articleTable.ddl.drop
    }
    if (existingTables.exists(_.name.name.equalsIgnoreCase("feed"))) {
      Tables.feedTable.ddl.drop
    }
  }


}
