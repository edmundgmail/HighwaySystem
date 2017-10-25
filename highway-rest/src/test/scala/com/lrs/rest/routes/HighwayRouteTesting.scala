package com.lrs.rest.routes

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.lrs.rest.AkkaHttpScalaDockerSeed.system
import com.lrs.rest.actors.{RecordPersistWorker, RecordProcessWorker}
import com.typesafe.config.ConfigFactory
import org.scalatest.{Matchers, WordSpec}

class HighwayRouteTesting extends WordSpec with Matchers with ScalatestRouteTest {

  implicit val ec = system.dispatcher

  val conf = ConfigFactory.load()

  val recordPersistWorker = system.actorOf(RecordPersistWorker.props, "recordPersistWorker-actor")
  val recordProcessWorker = system.actorOf(RecordProcessWorker.props(recordPersistWorker), "recordProcessWorker-actor")

  val highwayRoutes = new HighwayRoutes(recordPersistWorker, recordProcessWorker)

  // merge all routes here
  def allRoutes = {
      highwayRoutes.routes
  }

  "The service" should {
    "respond to highway" in {
      Get("/highway") ~> allRoutes ~> check {
        responseAs[String] should  have length 40
      }
    }

    "respond to transferHighway" in {
      Post("/highway") ~> allRoutes ~> check {

      }
    }
  }



}
