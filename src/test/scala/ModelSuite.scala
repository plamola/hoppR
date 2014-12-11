import nl.dekkr.hoppr.db.Schema
import nl.dekkr.hoppr.model.Syndication


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

  "ModelSuite" should {
    "Add a feed" in {
      val feed = Syndication.addNewFeed(testUri)
      feed.feedurl must be equalTo testUri
    }
    "Get a feed" in {
      val feed = Syndication.getFeed(testUri)
      feed.get.feedurl must be equalTo testUri
    }
    "Remove exising feed" in {
      Syndication.removeFeed(testUri) must be equalTo 1
    }
    "Remove non-existing feed" in {
      Syndication.removeFeed("http://test.non.existing") must be equalTo 0
    }
    "Get a non-existing feed" in {
      val feed = Syndication.getFeed(testUri)
      feed must be equalTo None
    }

  }

}