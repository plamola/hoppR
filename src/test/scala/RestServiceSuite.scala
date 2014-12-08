/**
 * Test the REST services
 */


import nl.dekkr.hoppr.db.{Tables, Schema}
import nl.dekkr.hoppr.model.FetchLog
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.meta._
import spray.http.StatusCodes._

import scala.slick.lifted.TableQuery
import nl.dekkr.hoppr.rest.MyJsonProtocol._
import spray.httpx.SprayJsonSupport._


class RestServiceSuite extends HopprTestBase {

  def before() = {
    session = Schema.getSession
    cleanDB()
    createTestData()
  }

  lazy val logEntry = FetchLog(uri = "log-uri", result = Option("result"))

  def createTestData(): Unit = {
    val fetchLogTable = TableQuery[Tables.FetchLogTable]
    fetchLogTable += logEntry
  }


  def after() = ???

  "Rest service" should {
    "show log entries" in {
      Get("/api/log") ~> restService ~> check {
        response.status should be equalTo OK
        response.entity should not be equalTo(None)
        responseAs[List[FetchLog]].size should be equalTo 1
        responseAs[List[FetchLog]].head.uri must be equalTo logEntry.uri
      }
    }

  }


}
