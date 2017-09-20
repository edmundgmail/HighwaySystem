package com.lrs.rest.routes

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.lrs.common.models.{AddRoadRecord, PointRecord}
import com.lrs.rest.actors.HighwayWorker
import com.lrs.rest.models.marshalling.CustomMarshallers._
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._


class HighwayRoutes(highwayWorker: ActorRef)
                   (implicit ec: ExecutionContextExecutor) {

  implicit val timeout = Timeout(10.seconds)

  def routes: Route = {
    path("highway") {
        get {
          complete{
            listHighways
          }
        } ~
        post{
          entity(as[AddRoadRecord]) {
            o => {
              complete(addHighway(o))
            }
          }
        }
    }
  }


  private def listHighways = {
    val result = (highwayWorker ? HighwayWorker.GetHighway).map(_.toString)
    result
  }

  private def addHighway(record: AddRoadRecord) = {
    val result = (highwayWorker ? HighwayWorker.AddHighway(record)).map(_.toString)
    result
  }

 }