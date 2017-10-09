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
import com.lrs.common.models.errors.{ExternalResourceException, ExternalResourceNotFoundException}
import com.lrs.rest.models.QueueMessage
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

class RecordProcessWorker(recordPersistWorker: ActorRef) extends FailurePropatingActor with ActorLogging with Stash{
  implicit val system = context.system
  val gsonBuilder = new GsonBuilder
  gsonBuilder.registerTypeAdapter(classOf[DataRecord], DataRecordDeserializer.getInstance)

  val gson = gsonBuilder.create()

  override def receive = {

    case record : JsObject => {
      val dr = gson.fromJson(record.toString, classOf[DataRecord])

      dr match {

        case record: AddRoadRecord =>  {
          val road = MongoUtils.addRoad(Road.fromJson(record))
          road.map(r=>QueueMessage("addRoad", Some(r.toString))) pipeTo sender()
        }

        case record: RemoveSegmentRecord => {
          val road = MongoUtils.getRoad(record.roadId)
          try{
            sender() ! road(0).removeSegment(record.dir, record.startPoint, record.endPoint)
          }
          catch {
            case _ => sender() ! "error"
          }
        }

        case record: AddSegmentRecord => {
          val road = MongoUtils.getRoad(record.roadId)
          /*
          val result = road andThen {
            case Success(rs : Seq[Road])  =>  {
              val newRoad = rs(0).addSegment(record.dir, record.segment, record.afterRP, record.leftConnect, record.beforeRP, record.rightConnect)
              MongoUtils.updateRoad(newRoad).map(r=>QueueMessage("add", Some(r.toString)))  pipeTo sender()
            }
            case Failure(e) => throw e
          }*/
        }

        case e : Throwable => sender() ! e

      }

    }
    case Status.Failure(ex) => {
      ex.printStackTrace()
      throw ex
    }


  }
}
