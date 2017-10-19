package com.lrs.rest.actors

import akka.actor.SupervisorStrategy.{Escalate, Stop}
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props, Stash, Status}
import com.lrs.common.models._
import com.lrs.common.utils.{JsonReadable, MongoUtils}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try}
import com.lrs.common.utils.Implicits._
import akka.pattern.pipe
import com.google.gson.GsonBuilder
import com.lrs.common.models.errors.{ExternalResourceException, ExternalResourceNotFoundException, HighwayStatus}
import org.joda.time
import org.joda.time.Seconds
import spray.json.JsObject

import scala.concurrent.{Await, Future}

/**
  * Created by vagrant on 10/4/17.
  */

object RecordProcessWorker{
  def props(actor:ActorRef): Props = Props(new RecordProcessWorker(recordPersistWorker = actor))

}

class RecordProcessWorker(recordPersistWorker: ActorRef) extends Actor with ActorLogging with Stash{
  implicit val system = context.system
  val gsonBuilder = new GsonBuilder
  gsonBuilder.registerTypeAdapter(classOf[DataRecord], DataRecordDeserializer.getInstance)

  val gson = gsonBuilder.create()

  def handleTransferSegment(record: TransferSegmentRecord) : HighwayStatus.TypeVal = {
    val fromRoad = MongoUtils.getRoad(record.fromRoadId)
    val toRoad = MongoUtils.getRoad(record.toRoadId)
    val segment = fromRoad.getSegmentString(record.fromDir, record.startPoint, record.endPoint)
    val newfromRoad =fromRoad.removeSegment(record.fromDir, record.startPoint, record.endPoint)
    val newToRoad = toRoad.addSegment(record.toDir, segment, record.afterRP, record.leftConnect, record.beforeRP, record.rightConnect)
    val roadFeatures =
  }


  override def receive = {

    case record : JsObject => {
      val dr = gson.fromJson(record.toString, classOf[DataRecord])

      dr match {

        case record: AddRoadRecord =>  {
          try {
            val road = Road.fromJson(record)
            MongoUtils.addRoad(road)
            sender() ! HighwayStatus.Ok
          }
          catch {
            case e:Throwable => sender() ! HighwayStatus.CustomError(HighwayStatus.ErrorAddRoad, e)
          }
        }

        case record: RemoveSegmentRecord => {
          try{
            val road = MongoUtils.getRoad(record.roadId)
            val newRoad = road.removeSegment(record.dir, record.startPoint, record.endPoint)
            MongoUtils.updateRoad(newRoad)
            sender() ! HighwayStatus.Ok
          }
          catch {
            case e:Throwable => sender() ! HighwayStatus.CustomError(HighwayStatus.ErrorRemoveRoadSegment, e)
          }

        }

        case record: AddSegmentRecord => {
          try{
            val road = MongoUtils.getRoad(record.roadId)
            val newRoad = road.addSegment(record.dir, record.segment, record.afterRP, record.leftConnect, record.beforeRP, record.rightConnect)
            MongoUtils.updateRoad(newRoad)
            sender() ! HighwayStatus.Ok
          }
          catch {
            case e:Throwable => sender() ! HighwayStatus.CustomError(HighwayStatus.ErrorAddRoadSegment, e)
          }
        }

        case record: UpdateLaneRecord => {
          try{
            val road = MongoUtils.getRoad(record.roadId)
            val newRoad = road.updateLane(record.dir, record.lane)
            MongoUtils.updateRoad(newRoad)
            sender() ! HighwayStatus.Ok
          }
          catch{
            case e:Throwable => sender() ! HighwayStatus.CustomError(HighwayStatus.ErrorUpdateLane, e)
          }
        }

        case record: TransferSegmentRecord => {
          sender() ! handleTransferSegment(record)
        }

        case e : Throwable => sender() ! HighwayStatus.CustomError(HighwayStatus.ErrorParseRoadJson, e)
      }

    }
  }
}
