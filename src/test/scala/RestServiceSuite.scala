/**
 * Test the REST services
 */


import nl.dekkr.hoppr.db.{Tables, Schema}
import nl.dekkr.hoppr.model.{Feed, FetchLog}
import nl.dekkr.hoppr.rest.Url
import org.json4s.native.Serialization
import spray.http.{MediaTypes, HttpRequest, HttpEntity}
import scala.slick.driver.PostgresDriver.simple._
import spray.http.StatusCodes._
import scala.slick.lifted.TableQuery
import spray.http.HttpMethods._
import nl.dekkr.hoppr.rest.MyJsonProtocol._
import spray.httpx.SprayJsonSupport._

import org.json4s._
import org.json4s.native.Serialization.{read, write}


class RestServiceSuite extends HopprTestBase {

  lazy val logEntry = FetchLog(uri = "log-uri", result = Option("result"))
  implicit val formats = Serialization.formats(NoTypeHints)
  val url: Url = new Url(uri = "http://test.test.url/")

  def before() = {
    session = Schema.getSession
    cleanDB()
    createTestData()
  }

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
    "add a feed" in {
      HttpRequest(POST, "http://localhost:9090/api/feed",
        entity = HttpEntity(MediaTypes.`application/json`, Serialization.write(url))
      ) ~> restService ~> check {
        response.status should be equalTo Created
        response.entity should not be equalTo(None)
        val feed = responseAs[Feed]
        feed.id.get must be greaterThan 0
        feed.feedurl must be equalTo url.uri
      }
    }
    "remove a feed" in {
      HttpRequest(DELETE, "http://localhost:9090/api/feed",
        entity = HttpEntity(MediaTypes.`application/json`, Serialization.write(url))
      ) ~> restService ~> check {
        response.status should be equalTo OK
      }
    }
    "remove a non-existing feed" in {
      HttpRequest(DELETE, "http://localhost:9090/api/feed",
        entity = HttpEntity(MediaTypes.`application/json`, Serialization.write(url))
      ) ~> restService ~> check {
        response.status should be equalTo NotFound
      }
    }


  }


}
