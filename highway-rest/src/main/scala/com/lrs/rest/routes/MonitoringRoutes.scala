package com.lrs.rest.routes
import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout

class MonitoringRoutes {

  // note how no implicits are needed here, because we aren't using futures, just returning
  // a static value

  def routes = {
    path("healthcheck") {
      get {
        complete {
          "OK"
        }
      }
    }
  }

}
