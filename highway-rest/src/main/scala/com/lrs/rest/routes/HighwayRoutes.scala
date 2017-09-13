package com.lrs.rest.routes

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.lrs.rest.routes.actors.HighwayWorker

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

class HighwayRoutes(highwayWorker: ActorRef)
                   (implicit ec: ExecutionContextExecutor) {

  implicit val timeout = Timeout(10.seconds)

  def routes: Route = {
    pathPrefix("highway") {
      path("list") {
        get {
          complete{
            listHighways
          }
        }
      }
    }
  }


  private def listHighways = {
    val result = (highwayWorker ? HighwayWorker.GetHighway).map(_.toString)
    result
  }

 }