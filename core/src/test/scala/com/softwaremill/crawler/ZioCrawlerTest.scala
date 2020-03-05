package com.softwaremill.crawler

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import zio.Runtime.{default => runtime}
import zio.Task

class ZioCrawlerTest extends AnyFlatSpec with Matchers with CrawlerTestData with ScalaFutures with IntegrationPatience {

  override implicit val patienceConfig: PatienceConfig =
    PatienceConfig(
      timeout = scaled(Span(60, Seconds)),
      interval = scaled(Span(150, Millis))
    )

  for (testData <- testDataSets) {
    it should s"crawl a test data set ${testData.name}" in {
      import testData._

      val t = timed {
        runtime.unsafeRunTask(UsingZio.crawl(
          startingUrl,
          url => Task(http(url)),
          parseLinks)/*.on(actorSystem.dispatcher)*/) should be(expectedCounts)
      }

      shouldTakeMillisMin.foreach(m => t should be >= m)
      shouldTakeMillisMax.foreach(m => t should be <= m)
    }
  }
}
