package com.lrs.common.processor

import com.lrs.common.models.{AddRoadRecord, DataRecord, RemoveSegmentRecord, Road}
import org.apache.spark.SparkContext

/**
  * Created by vagrant on 8/18/17.
  */
object RoadProcessor extends Processor[Road, DataRecord] {

    override def process(sc: SparkContext, road: Road, dataRecord: DataRecord): Road = {
    dataRecord match {
      case r: AddRoadRecord => {
        road
      }

      case r: RemoveSegmentRecord => {
        road
      }

      case _ => throw new Exception("can't recognize the action")
    }
  }
}
