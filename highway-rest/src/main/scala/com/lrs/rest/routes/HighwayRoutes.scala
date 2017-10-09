package com.lrs.rest.routes

import akka.actor.ActorRef
import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.lrs.common.models.{AddRoadRecord, DataRecord, PointRecord}
import com.lrs.rest.actors.{RecordParseWorker, RecordPersistWorker, RecordProcessWorker}
import com.lrs.rest.models.QueueMessage
import com.lrs.rest.models.marshalling.CustomMarshallers._
import spray.json.{JsObject, JsValue}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration._

class HighwayRoutes(recordPersistWorker: ActorRef, recordParseWorker: ActorRef, recordProcessWorker: ActorRef)
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
              handleHighwayRecord(o)
              complete("ok")
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
      val result = (recordProcessWorker ? record).onComplete{
        case util.Success(result) =>
          println(s"success: $result")

        case util.Failure(ex) =>
          println(s"FAIL: ${ex.getMessage}")
          ex.printStackTrace()
      }
  }

 }