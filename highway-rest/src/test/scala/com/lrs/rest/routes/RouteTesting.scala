package com.lrs.rest.routes

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.config.ConfigFactory
import org.scalatest.{Matchers, WordSpec}

class RouteTesting extends WordSpec with Matchers with ScalatestRouteTest {

  implicit val ec = system.dispatcher

  val conf = ConfigFactory.load()
  val monitoringRoutes = new MonitoringRoutes()

  // merge all routes here
  def allRoutes = {
      monitoringRoutes.routes
  }

  "The service" should {
    "respond to healthcheck" in {
      Get("/healthcheck") ~> allRoutes ~> check {
        responseAs[String] shouldEqual "OK"
      }
    }
  }



}
