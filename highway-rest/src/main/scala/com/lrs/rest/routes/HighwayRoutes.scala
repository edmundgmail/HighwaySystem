package com.lrs.rest.routes

import akka.actor.ActorRef
import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.lrs.common.models.errors.HighwayStatus
import com.lrs.common.models.{AddRoadRecord, DataRecord, PointRecord}
import com.lrs.rest.actors.{RecordPersistWorker, RecordProcessWorker}
import com.lrs.rest.models.marshalling.CustomMarshallers._
import spray.json.{JsObject, JsValue}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration._

class HighwayRoutes(recordPersistWorker: ActorRef, recordProcessWorker: ActorRef)
                   (implicit ec: ExecutionContextExecutor) {

  implicit val timeout = Timeout(1000.seconds)

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
              complete(handleHighwayRecord(o))
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
      val ret = (recordProcessWorker ? record).mapTo[HighwayStatus.TypeVal]
      ret.flatMap {
        case HighwayStatus.Ok | HighwayStatus.Warning => {
          println("process ok")
          (recordPersistWorker ? RecordPersistWorker.AddHighway(record)).mapTo[HighwayStatus.TypeVal]
        }

        case _ => {
          println("process not ok")
          ret
        }
      }
  }

 }