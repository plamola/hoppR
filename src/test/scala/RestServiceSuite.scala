/**
 * Test the REST services
 */

import nl.dekkr.hoppr.db.Tables.FetchLog
import nl.dekkr.hoppr.db.{Tables, Schema}
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.meta._
import spray.http.StatusCodes._

import scala.slick.lifted.TableQuery


class RestServiceSuite extends HopprTestBase {

  def before() = {
    session = Schema.getSession
    cleanDB()
  }
  def after() = ???

  "Rest service" should {
    "show log entries" in {
//      val fetchlog = TableQuery[Tables.FetchLog]
//      fetchlog += Tables.FetchLogRow(uri = "log-uri", result = Option("result"))
      Get("/api/log") ~> restService ~> check {
        response.status should be equalTo OK
        response.entity should not be equalTo(None)
        //responseAs[FetchLog].copy(id = Some(customersIds(1))) must be equalTo customers(1)
      }
    }

  }


}
