
import java.util.concurrent.TimeUnit

import nl.dekkr.actors.{BootedCore, CoreActors}
import nl.dekkr.hoppr.actors.FetchSupervisor
import nl.dekkr.hoppr.actors.SyndicationActor.GetFeed
import nl.dekkr.hoppr.db.{Tables, Schema}

import scala.concurrent.duration.Duration
import scala.slick.driver.PostgresDriver.simple._
import scala.concurrent.ExecutionContext.Implicits.global


// The main application
object HelloHoppr extends App with BootedCore with CoreActors {


  val feeds = TableQuery[Tables.Feeds]
  implicit val session = Schema.getSession
  Schema.createOrUpdate(session)

  // Add some dummy feeds
  if (feeds.list.size < 1)
    feeds += Tables.FeedRow( feedurl = "http://blog.dekkr.nl/rss", link = Option("link"), title = Option("feed title"))

  if (feeds.list.size < 2)
    feeds += Tables.FeedRow( feedurl = "http://matthijsdekker.nl/rss", link = Option("link"), title = Option("Todo"))

  // List all available feeds
  println("id \tupdated \turl")
  for {c <- feeds.list} println("" + c.id + " \t" + c.updateddate + " \t" + c.feedurl)
  println("##### Done")


  system.scheduler.schedule(
    Duration.create(0, TimeUnit.MILLISECONDS),
    Duration.create(1, TimeUnit.MINUTES),
    fetchsupervisor, FetchSupervisor.Nudge)



}
