package com.lrs.common.models

/**
  * Created by vagrant on 8/18/17.
  */
class DataRecord(val action:String, val dateTime: String , val roadId: Long)


case class PointRecord(override val action: String, override  val dateTime:String, override val roadId:Long, val rpName:String, val offset:Double) extends DataRecord(action, dateTime, roadId)

case class SegmentRecord(val start: PointRecord, val end: PointRecord)

case class DirectionRecord(val dir: String, val RPs: String,val segments: Array[String])

case class AddRoadRecord(override val action: String, override val dateTime: String, override val roadId: Long,
                         val roadName:String, val mainDir: String, val jurisDictionType:String, val ownerShip:String, val prefixCode:String,
                         val routeNumber:String, val modifierCode:String, val mainlineCode:String, val routeTypeCode:String, val routeOfficialName:String,
                         val routeFullName:String, val routeAlternateName:String, val beginPlace:String, val endPlace:String,
                         val directions: Array[DirectionRecord]) extends DataRecord(action, dateTime, roadId)

case class RemoveSegmentRecord(override val action: String, override val dateTime: String, override val roadId: Long, val dir:String, val startPoint: PointRecord, val endPoint:PointRecord) extends DataRecord(action, dateTime, roadId)

case class AddSegmentRecord(override val action: String, override val dateTime: String, override val roadId: Long, val dir:String, val segment:String, val leftConnect : Boolean, afterRP: String, val rightConnect: Boolean, beforeRP:String ) extends DataRecord(action, dateTime, roadId)
