import nl.dekkr.hoppr.db.Schema
import nl.dekkr.hoppr.model.Syndication

import scala.slick.jdbc.meta.MTable

/**
 * Author: matthijs 
 * Created on: 11 Dec 2014.
 */
class ModelSuite extends HopprTestBase {

  val testUri = "http://test.test.uri"

  def before() = {
    session = Schema.getSession
    cleanDB()
  }

  "MdoelSuite" should {
    "Add a feed" in {
      val feed = Syndication.addNewFeed(testUri)
      feed.feedurl must be equalTo testUri
    }
    "Remove exising feed" in {
      Syndication.removeFeed(testUri) must be equalTo 1
    }
    "Remove non-exising feed" in {
      Syndication.removeFeed("http://test.non.existing") must be equalTo 0
    }
  }

}