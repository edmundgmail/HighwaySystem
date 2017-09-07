package com.ddp.lrs.processor

import com.ddp.lrs.models._
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import scala.util.MurmurHash

/**
  * Created by vagrant on 8/18/17.
  */
object RoadProcessor extends Processor[Road, DataRecord] {

    override def process(sc: SparkContext, road: Road, dataRecord: DataRecord): Road = {
    dataRecord match {
      case r: AddRoadRecord => {
        val x = r.directions.map(
          d =>

        )

        Road(r.roadName, r.roadId, r.dateTime, r.mainDir, x)
      }

      case r: RemoveSegmentRecord => {
        Road(road.name, road.roadId, road.dateTime, road.mainDir)
      }

      case _ => throw new Exception("can't recognize the action")
    }
  }
}
