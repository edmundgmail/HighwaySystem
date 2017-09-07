package com.lrs.common.models

/**
  * Created by vagrant on 8/18/17.
  */
class DataRecord(val action:String, val dateTime: String , val roadId: Long)


case class PointRecord(rp:String, offset:Double)

case class SegmentRecord(start: PointRecord, end: PointRecord)

case class DirectionRecord(dir: String, RPs: String, segments: Array[SegmentRecord])

case class AddRoadRecord(override val action: String, override val dateTime: String, override val roadId: Long, val roadName:String, val mainDir: String, val directions: Array[DirectionRecord]) extends DataRecord(action, dateTime, roadId)

case class RemoveSegmentRecord(override val action: String, override val dateTime: String, override val roadId: Long, val dir:String, val segment:String ) extends DataRecord(action, dateTime, roadId)

case class AddSegmentRecord(override val action: String, override val dateTime: String, override val roadId: Long, val dir:String, val segment:String, val leftConnectPoint: PointRecord, val rightConnectPoint:PointRecord ) extends DataRecord(action, dateTime, roadId)
