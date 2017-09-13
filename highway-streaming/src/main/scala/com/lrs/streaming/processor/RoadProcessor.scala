package com.lrs.streaming.processor

import com.lrs.common.models._
import org.apache.spark.SparkContext

/**
  * Created by vagrant on 8/18/17.
  */
object RoadProcessor extends Processor[Road, DataRecord] {

    override def process(sc: SparkContext, road: Road, dataRecord: DataRecord): Road = {
    dataRecord match {
      case r: AddRoadRecord => {
        Road.fromJson(r)
      }

      case r: RemoveSegmentRecord => {
        road
      }
      case r: AddSegmentRecord => {
          road
      }

      case _ => throw new Exception("can't recognize the action")
    }
  }
}
