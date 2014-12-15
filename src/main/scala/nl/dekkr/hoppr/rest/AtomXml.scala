package nl.dekkr.hoppr.rest

import java.util.Date

import nl.dekkr.hoppr.model.Syndication
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

/**
 * Author: matthijs 
 * Created on: 15 Dec 2014.
 */
class AtomXml {

  val atomDateFormat: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ")

  def getAtomFeed(id: Int) = {
    val feed = Syndication.getFeedById(id).get
    val articles = Syndication.getArticles(feed.id.get, feed.lastarticlecount)
    // TODO implement this
    val xml = ???
    /*
    <feed xmlns="http://www.w3.org/2005/Atom">
      <generator uri="http://hoppr.local/" version="2.0">HoppR</generator>
      <title>feed.name</title>
      <subtitle><![CDATA[feed.feed_subtitle]]></subtitle>
      <rights><![CDATA[feed.copyright]]></rights>
      <updated>if(feed.last_update != null){atomDate(feed.last_update)}else{atomDate(feed.created_at)}</updated>
      <logo>feed.logo</logo>
      <icon>feed.feed_icon</icon>
      <id>http://feedfrenzy.nl@routes.Application.atom(feed.feed_key)</id>
      <link href="feed.alternate_link" rel="alternate" type="text/html"/>
      <link href="http://feedfrenzy.nl@routes.Application.atom(feed.feed_key)" rel="self" />
      for(feedItem <- articles) {
        <entry>
          <title type="html">feedItem.title</title>
          <content type="html">feedItem.content</content>
          <id>feedItem.uid</id>
          <link href="feedItem.url" rel="alternate" type="text/html"/>
          <published>@atomDate(feedItem.published)</published>
          <updated>if(feedItem.updated != null){atomDate(feedItem.updated)}else{atomDate(feedItem.published)}</updated>
          <author><name>feedItem.author</name></author>
      </entry>
    }
    </feed>
    */
  }

  private def atomDate(date: DateTime) = if (date != null) date.toString(atomDateFormat)


}
