package com.softwaremill.crawler

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import com.softwaremill.crawler.UsingAkkaTyped.Start
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class AkkaTypedCrawlerTest extends AnyFlatSpec with Matchers with BeforeAndAfterAll with CrawlerTestData {
  private val testKit = ActorTestKit()
  override def afterAll(): Unit = testKit.shutdownTestKit()

  for (testData <- testDataSets) {
    it should s"crawl a test data set ${testData.name}" in {
      import testData._

      val t = timed {
        val probe = testKit.createTestProbe[Map[String, Int]]()

        val crawler = testKit.spawn(new UsingAkkaTyped.Crawler(url => Future(http(url)), parseLinks, probe.ref).crawlerBehavior)
        crawler ! Start(startingUrl)

        probe.expectMessage(1.minute, expectedCounts)
      }
      shouldTakeMillisMin.foreach(m => t should be >= m)
      shouldTakeMillisMax.foreach(m => t should be <= m)
    }
  }
}
