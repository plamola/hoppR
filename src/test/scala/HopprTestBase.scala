import akka.actor.ActorRefFactory
import nl.dekkr.hoppr.db.{Tables, Schema}
import nl.nl.dekkr.hoppr.rest.RestService
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
with Configuration { //with BeforeAndAfter {

  implicit var session: Session = _


  args(sequential = true)

  // connects the DSL to the test ActorSystem
  implicit def actorRefFactory = system

//  val spec = this

//  val customerService = new RestService {
//    override implicit def actorRefFactory: ActorRefFactory = spec.actorRefFactory
//  }.rest
//


  def cleanDB(): Unit = {
    session = Schema.getSession
    dropDatabaseTables()
    Schema.createOrUpdate(session)
  }



  private def dropDatabaseTables() : Unit = {
    val existingTables = MTable.getTables.list
    if (existingTables.exists(_.name.name.equalsIgnoreCase("fetchlog"))) {
      Tables.FetchLog.ddl.drop
    }
    if (existingTables.exists(_.name.name.equalsIgnoreCase("article"))) {
      Tables.Articles.ddl.drop
    }
    if (existingTables.exists(_.name.name.equalsIgnoreCase("feed"))) {
      Tables.Feeds.ddl.drop
    }
  }


}
