package com.lrs.common.models

/**
  * Created by vagrant on 8/18/17.
  */
class DataRecord(action:String, dateTime: String , roadId: Long)


case class PointRecord(val rpName:String, val offset:Double)

case class SegmentRecord(val start: PointRecord, val end: PointRecord)

case class DirectionRecord(val dir: String,val segments: Array[String])

case class AddRoadRecord(val action: String, val dateTime: String, val roadId: Long,
                         val roadName:String, val mainDir: String, val jurisDictionType:String, val ownerShip:String, val prefixCode:String,
                         val routeNumber:String, val modifierCode:String, val mainlineCode:String, val routeTypeCode:String, val routeOfficialName:String,
                         val routeFullName:String, val routeAlternateName:String, val beginPlace:String, val endPlace:String,
                         val directions: Array[DirectionRecord]) extends DataRecord(action, dateTime, roadId)

case class RemoveSegmentRecord(val action: String, val dateTime: String, val roadId: Long, val dir:String, val startPoint: PointRecord, val endPoint:PointRecord)
  extends DataRecord(action, dateTime, roadId)

case class AddSegmentRecord(val action: String, val dateTime: String, val roadId: Long, val dir:String, val segment:String, val leftConnect : Boolean,
                            val afterRP: String, val rightConnect: Boolean, val beforeRP:String ) extends DataRecord(action, dateTime, roadId)

/*rp1,offset1, rp2,offset2, 1, in*/
/*rp1,offset1, rp2,offset2, 2, out*/
case class UpdateLaneRecord(val action:String, val dateTime: String, val roadId: Long, val dir:String, val lane: String) extends DataRecord(action, dateTime, roadId)

case class TransferSegmentRecord(val action: String, val dateTime: String, val fromRoadId: Long, val fromDir: String, val startPoint: PointRecord,
                                 val endPoint: PointRecord, val toRoadId: Long, val toDir: String, val afterRP: String, val beforeRP: String,
                                 leftConnect: Boolean, rightConnect: Boolean) extends DataRecord(action, dateTime, fromRoadId)

case class AddRoadFeaturesRecord(val action: String, val dateTime: String, val roadId: Long, val dir: String,
                                val segments: List[SegmentRecord], val roadFeature: RoadFeature) extends DataRecord(action, dateTime, roadId )

case class RemoveRoadFeaturesRecord(val action: String, val dateTime: String, val roadId: Long, val dir: String,
                                 val segment: SegmentRecord) extends DataRecord(action, dateTime, roadId )


