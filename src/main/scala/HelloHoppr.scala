
import java.util.concurrent.TimeUnit


import nl.dekkr.hoppr.actors.{BootedCore, CoreActors}
import nl.dekkr.hoppr.actors.FetchSupervisor
import nl.dekkr.hoppr.db.{Tables, Schema}
import nl.dekkr.hoppr.model.Feed

import scala.concurrent.duration.Duration
import scala.slick.driver.PostgresDriver.simple._
import scala.concurrent.ExecutionContext.Implicits.global


// The main application
object HelloHoppr extends App with BootedCore with CoreActors {


  val feeds = TableQuery[Tables.FeedTable]
  implicit val session = Schema.getSession
  Schema.createOrUpdate(session)

  // Add some dummy feeds
  if (feeds.list.size < 1)
    feeds += Feed( feedurl = "http://blog.dekkr.nl/rss", link = Option("link"), title = Option("feed title"))

  if (feeds.list.size < 2)
    feeds += Feed( feedurl = "http://matthijsdekker.nl/rss", link = Option("link"), title = Option("Todo"))

  if (feeds.list.size < 3)
    feeds += Feed( feedurl = "http://www.nu.nl/rss", link = Option("link"), title = Option("NU"))

  // List all available feeds
  println("id \tupdated \t \t \turl")
  for {c <- feeds.list} println("" + c.id.get + " \t" + c.updateddate + " \t" + c.feedurl)
  println("##### Done")


  system.scheduler.schedule(
    Duration.create(0, TimeUnit.MILLISECONDS),
    Duration.create(1, TimeUnit.MINUTES),
    fetchsupervisor, FetchSupervisor.Nudge)

}
