package com.lrs.rest.actors

import akka.actor.{Actor, ActorLogging, Props, Stash}
import com.lrs.common.models.{AddRoadRecord, AddSegmentRecord, RemoveSegmentRecord, Road}
import com.lrs.common.utils.MongoUtils
import scala.concurrent.ExecutionContext.Implicits.global


import scala.util.{Failure, Success}

/**
  * Created by vagrant on 10/4/17.
  */

object RecordProcessWorker{
  def props(): Props = Props(new RecordProcessWorker)

}

class RecordProcessWorker extends Actor with ActorLogging with Stash{
  implicit val system = context.system

  override def receive = {
    case record: AddRoadRecord => MongoUtils.collectionRoadTable.insertOne(Road.fromJson(record)).toFuture

    case record: RemoveSegmentRecord => {
      val road = MongoUtils.getRoad(record.roadId)
      val result = road andThen {
        case Success(rs : Seq[Road])  =>  {
          val newRoad = rs(0).removeSegment(record.dir, record.startPoint, record.endPoint)
          MongoUtils.updateRoad(newRoad)
        }
        case Failure(e) => {
          println(e.getMessage)
          e.printStackTrace
        }
      }
    }
    case record: AddSegmentRecord => {
        val road = MongoUtils.getRoad(record.roadId)
        val result = road andThen {
        case Success(rs : Seq[Road])  =>  {
        val newRoad = rs(0).addSegment(record.dir, record.segment, record.afterRP, record.leftConnect, record.beforeRP, record.rightConnect)
        MongoUtils.updateRoad(newRoad)
      }
        case Failure(e) => {
        println(e.getMessage)
        e.printStackTrace
      }
      }
    }

    case _ =>
  }
}
