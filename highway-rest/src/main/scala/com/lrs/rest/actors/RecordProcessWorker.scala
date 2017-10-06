package com.lrs.rest.actors

import akka.actor.SupervisorStrategy.{Escalate, Stop}
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props, Stash}
import com.lrs.common.models._
import com.lrs.common.utils.{JsonReadable, MongoUtils}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try}
import com.lrs.common.utils.Implicits._
import akka.pattern.pipe
import com.lrs.common.models.errors.{ExternalResourceException, ExternalResourceNotFoundException}

import scala.concurrent.Future

/**
  * Created by vagrant on 10/4/17.
  */

object RecordProcessWorker{
  def props(actor:ActorRef): Props = Props(new RecordProcessWorker(recordPersistWorker = actor))
}

class RecordProcessWorker(recordPersistWorker: ActorRef) extends Actor with ActorLogging with Stash{
  implicit val system = context.system

  override def receive = {
    case record: AddRoadRecord =>  MongoUtils.addRoad(Road.fromJson(record))  pipeTo sender()

    case record: RemoveSegmentRecord => {
      val road = MongoUtils.getRoad(record.roadId).onComplete({
        case Success(result) => {
          Try(result(0).removeSegment(record.dir, record.startPoint, record.endPoint)) match {
            case Success(road) => MongoUtils.updateRoad(road) pipeTo sender()
            case Failure(e) => Future.failed(e) pipeTo sender()
          }
        }

        case _ => {
          val exception = new ExternalResourceNotFoundException("Failed to find stock price. Stock does not exist or data is empty.")
          Future.failed(exception) pipeTo sender()
        }
      })
    }

    case record: AddSegmentRecord => {
        val road = MongoUtils.getRoad(record.roadId)
        val result = road andThen {
        case Success(rs : Seq[Road])  =>  {
        val newRoad = rs(0).addSegment(record.dir, record.segment, record.afterRP, record.leftConnect, record.beforeRP, record.rightConnect)
        MongoUtils.updateRoad(newRoad)  pipeTo sender()
      }
        case Failure(e) => throw e
      }
    }

    case _ => throw new Exception("unknown record type")
  }
}
