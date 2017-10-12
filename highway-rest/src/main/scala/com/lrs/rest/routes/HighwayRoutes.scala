package com.lrs.rest.routes

import akka.actor.{ActorRef, ActorSystem}
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
                   (implicit system: ActorSystem, ec: ExecutionContextExecutor) {

  implicit val timeout = Timeout(10.seconds)
  implicit val log = system.log

  def routes: Route = {

    pathPrefix("highway") {
      (path("rps") & get){
        parameters('roadId.as[Long], 'dir.as[String]){
          (roadId, dir) => {
            complete(getHighwayRPs(roadId, dir))
          }
        }
      } ~
      get {
          parameters('roadId? 0){
            (roadId) => complete(listHighways(roadId))
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


  private def listHighways(roadId: Long) = {
    val result = (recordPersistWorker? RecordPersistWorker.GetHighway(roadId)).map(_.toString)
    result
  }

  private def getHighwayRPs(roadId: Long, dir: String) = {
    val result = (recordPersistWorker? RecordPersistWorker.GetHighwayRPs(roadId, dir)).map(_.toString)
    result
  }

  private def handleHighwayRecord(record: JsObject) = {
      val ret = (recordProcessWorker ? record).mapTo[HighwayStatus.TypeVal]
      ret.flatMap {
        case HighwayStatus.Ok | HighwayStatus.Warning => {
          log.info("process ok")
          (recordPersistWorker ? RecordPersistWorker.AddHighway(record)).mapTo[HighwayStatus.TypeVal]
        }

        case _ => {
          log.info("process not ok")
          ret
        }
      }
  }

 }