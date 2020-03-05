package com.softwaremill.crawler

import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AkkaCrawlerTest
    extends TestKit(ActorSystem("crawler-test"))
    with AnyFlatSpecLike
    with Matchers
    with BeforeAndAfterAll
    with CrawlerTestData
    with ScalaFutures
    with IntegrationPatience {

  override def afterAll: Unit = TestKit.shutdownActorSystem(system)

  for (testData <- testDataSets) {
    it should s"crawl a test data set ${testData.name}" in {
      import testData._
      val t = timed {
        UsingAkka.crawl(startingUrl, url => Future(http(url)), parseLinks).futureValue should be(expectedCounts)
      }
      shouldTakeMillisMin.foreach(m => t should be >= m)
      shouldTakeMillisMax.foreach(m => t should be <= m)
    }
  }
}
