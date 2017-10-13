package com.lrs.rest.actors

import akka.actor.{Actor, ActorLogging, Props, Stash}
import akka.pattern.pipe
import com.lrs.common.models.errors.HighwayStatus
import com.lrs.common.models.{AddRoadRecord, DataRecord}
import com.lrs.common.utils.MongoUtils
import com.lrs.rest.actors.RecordPersistWorker.{AddHighway, GetHighway, GetHighwayRPs, GetHighwaySegments}

import scala.concurrent.ExecutionContext.Implicits.global
import spray.json._
import com.lrs.rest.models.marshalling.CustomMarshallers._

/**
  * Created by vagrant on 9/11/17.
  */

object RecordPersistWorker {

  def props(): Props = Props(new RecordPersistWorker)

  case class GetHighway(roadId:Long)
  case class GetHighwayRPs(roadId: Long, dir: String)
  case class GetHighwaySegments(roadId: Long, dir: String)
  case class AddHighway(record: JsObject)
}


class RecordPersistWorker extends Actor with ActorLogging with Stash{


  implicit val system = context.system

  def receive = {

    case GetHighwayRPs(id, dir) => {
      MongoUtils.getHighwayRPs(id, dir) pipeTo sender()
    }

    case GetHighwaySegments(id, dir) => {
      MongoUtils.getHighwaySegments(id, dir) pipeTo sender()
    }
    case GetHighway(roadId) =>
      try{
        if(roadId == 0)
          MongoUtils.getAllHighways pipeTo sender()
        else
          sender() ! MongoUtils.getRoad(roadId)
      }
      catch{
        case e: Throwable => sender() ! HighwayStatus.CustomError(HighwayStatus.ErrorGetRoad, e)
      }

    case AddHighway(r) =>
      try{
        MongoUtils.addHighwayRecord(r)
        sender() ! HighwayStatus.Ok
      }
      catch {
        case e: Throwable => sender() ! HighwayStatus.CustomError(HighwayStatus.ErrorAddRoad, e)
      }
  }


  private def getHighwayDetails() = {

  }
}
