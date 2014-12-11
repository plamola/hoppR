import nl.dekkr.hoppr.db.Schema
import nl.dekkr.hoppr.model.Syndication

import scala.slick.jdbc.meta.MTable

/**
 * Author: matthijs 
 * Created on: 11 Dec 2014.
 */
class ModelSuite extends HopprTestBase {

  def before() = {
    session = Schema.getSession
    cleanDB()
  }

  "MdoelSuite" should {
    "Add a feed" in {
      val testUri = "http://test.test.uri"
      val feed = Syndication.addNewFeed(testUri)
      //feed.id.get should be equalTo 1
      feed.feedurl must be equalTo testUri
    }
  }

}