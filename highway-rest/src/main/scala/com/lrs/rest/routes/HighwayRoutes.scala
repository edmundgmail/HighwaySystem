package com.lrs.rest.routes

import akka.actor.ActorRef
import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.lrs.common.models.{AddRoadRecord, DataRecord, PointRecord}
import com.lrs.rest.actors.{RecordParseWorker, RecordPersistWorker}
import com.lrs.rest.models.marshalling.CustomMarshallers._
import spray.json.{JsObject, JsValue}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

class HighwayRoutes(recordPersistWorker: ActorRef, recordParseWorker: ActorRef)
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
          entity(as[JsObject]) {
            o => {
              complete(handleHighwayRecord(o).map(_.toString))
            }
          }
        }
    }
  }


  private def listHighways = {
    val result = (recordPersistWorker? RecordPersistWorker.GetHighway).map(_.toString)
    result
  }

  private def handleHighwayRecord(record: JsObject) = {
      val result = (recordParseWorker ? record)
      result
  }

 }