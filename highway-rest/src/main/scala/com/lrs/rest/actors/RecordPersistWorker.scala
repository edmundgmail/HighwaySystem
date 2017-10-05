package com.lrs.rest.actors

import akka.actor.{Actor, ActorLogging, Props, Stash}
import akka.pattern.pipe
import com.lrs.common.models.{AddRoadRecord, DataRecord}
import com.lrs.common.utils.MongoUtils
import com.lrs.rest.actors.RecordPersistWorker.{AddHighway, GetHighway}

import scala.concurrent.ExecutionContext.Implicits.global
import spray.json._
import com.lrs.rest.models.marshalling.CustomMarshallers._

/**
  * Created by vagrant on 9/11/17.
  */

object RecordPersistWorker {

  def props(): Props = Props(new RecordPersistWorker)

  case class GetHighway()
  case class GetHighwayDetails(name: String, id: String)
  case class AddHighway(record: JsObject)
}


class RecordPersistWorker extends Actor with ActorLogging with Stash{


  implicit val system = context.system

  def receive = {

    case GetHighway =>
      MongoUtils.getAllHighways pipeTo sender()

    case AddHighway(r) =>
      MongoUtils.addHighwayRecord(r) pipeTo sender()
  }


  private def getHighwayDetails() = {

  }
}