package com.lrs.common.models

import com.lrs.common.utils.{JsonReadable, JsonWritable}

/**
  * Created by eguo on 8/26/17.
  */
class Road(val name:String, val roadId: Long, val mainDir: String, val directions: List[Direction]) extends JsonWritable{
  def withUpdatedDirections(newDirections:List[Direction]) = new Road(name , roadId, mainDir, newDirections)

  override def toString: String = {
    s"{RoadName: $name mainDir:$mainDir directions: $directions"
  }

  def removeSegment(dir:String, startPoint:PointRecord, endPoint:PointRecord) = {
      val startRP = ReferencePoint(startPoint.rpName, name, dir,0,0)
      val endRP = ReferencePoint(endPoint.rpName, name, dir, 0, 0)
      val dirs = directions.filterNot(_.dir==dir) ++ directions.filter(_.dir==dir).map(d=>d.removeSegment(SegmentPoint("start", startRP.ID, startPoint.offset), SegmentPoint("end", endRP.ID, endPoint.offset)))
      Road(name, roadId, mainDir, dirs)
  }

  def addSegment(dir:String, segment:String, afterRP:String, leftConnect:Boolean, beforeRP:String, rightConnect:Boolean) = ???
}

object Road{
  def apply(name:String, roadId: Long, mainDir: String, directions: List[Direction]): Road = new Road(name, roadId, mainDir, directions)

  def fromJson(record: AddRoadRecord) : Road = {
    Road(record.roadName, record.roadId, record.mainDir, record.directions.map(d=>Direction.fromString(record.roadName, d.dir, d.segments.toList)).toList)
  }
}