package nl.dekkr.hoppr.rest

import java.util.Date

import nl.dekkr.hoppr.model.Syndication
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

/**
 * Author: matthijs 
 * Created on: 15 Dec 2014.
 */
object AtomXml {

  val atomDateFormat: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ")

  def getAtomFeed(id: Int) = {
    val feed = Syndication.getFeedById(id).get
    val articles = Syndication.getArticles(feed.id.get, feed.lastarticlecount)

    val entriesXML =
      for(feedItem <- articles) {
        <entry>
          <title type="html">{feedItem.title}</title>
          <content type="html">{feedItem.content}</content>
          <id>{feedItem.uri}</id>
          <link href="{feedItem.url}" rel="alternate" type="text/html"/>
          <published>{atomDate(feedItem.publisheddate.get)}</published>
          <updated>{if(feedItem.updateddate != None){atomDate(feedItem.updateddate.get)}else{atomDate(feedItem.publisheddate.get)}}</updated>
          <author><name>{feedItem.author}</name></author>
        </entry>
      }

    <feed xmlns="http://www.w3.org/2005/Atom">
      <generator uri="http://hoppr.local/" version="2.0">HoppR</generator>
      <title>{feed.title.get}</title>
      <subtitle><![CDATA[{feed.description}]]></subtitle>
      <rights><![CDATA[feed.copyright]]></rights>
      <updated>{if(feed.updateddate != null){atomDate(feed.updateddate)}else{atomDate(feed.publisheddate.get)}}</updated>
      <logo>feed.logo</logo>
      <icon>feed.feed_icon</icon>
      <id>http://feedfrenzy.nl@routes.Application.atom(feed.feed_key)</id>
      <link href="feed.alternate_link" rel="alternate" type="text/html"/>
      <link href="http://hoppr.local@routes.Application.atom(feed.feed_key)" rel="self" />
      {entriesXML}
    </feed>

  }

  private def atomDate(date: DateTime) = if (date != null) date.toString(atomDateFormat)


}
