package com.lrs.rest.actors

import akka.actor.{Actor, ActorLogging, Props, Stash}
import com.lrs.common.models._
import com.lrs.common.utils.{JsonReadable, MongoUtils}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import com.lrs.common.utils.Implicits._

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
          val oldRoad : Road = rs(0)
            val newRoad = oldRoad.removeSegment(record.dir, record.startPoint, record.endPoint)
            MongoUtils.updateRoad(newRoad)
        }
        case Failure(e) => throw e
      }

      result onFailure {
        case e :Throwable => e.printStackTrace
      }
    }
    case record: AddSegmentRecord => {
        val road = MongoUtils.getRoad(record.roadId)
        val result = road andThen {
        case Success(rs : Seq[Road])  =>  {
        val newRoad = rs(0).addSegment(record.dir, record.segment, record.afterRP, record.leftConnect, record.beforeRP, record.rightConnect)
        MongoUtils.updateRoad(newRoad)
      }
        case Failure(e) => throw e
      }
    }

    case _ => throw new Exception("unknown record type")
  }
}
