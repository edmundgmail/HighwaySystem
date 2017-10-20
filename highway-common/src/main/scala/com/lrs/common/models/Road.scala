package com.lrs.common.models

import com.lrs.common.logging.Logging
import com.lrs.common.models.errors.ExternalResourceNotFoundException
import com.lrs.common.utils.{JsonReadable, JsonWritable}

/**
  * Created by eguo on 8/26/17.
  */
class Road(val name:String, val roadId: Long, val mainDir: String, val directions: List[Direction]) extends JsonWritable with Logging{
  def withUpdatedDirections(newDirections:List[Direction]) = new Road(name , roadId, mainDir, newDirections)

  override def toString: String = {
    s"{RoadName: $name mainDir:$mainDir directions: $directions"
  }

  def getRps(dir: String) : List[ReferencePoint] = ???

  @throws(classOf[Exception])
  def removeSegment(dir:String, startPoint:PointRecord, endPoint:PointRecord) = {
      logger.info("trying to removeSegment")
      val startRP = ReferencePoint(startPoint.rpName, name, dir,0,0)
      val endRP = ReferencePoint(endPoint.rpName, name, dir, 0, 0)
      val dirs = directions.filterNot(_.dir==dir) ++ directions.filter(_.dir==dir).map(d=>d.removeSegment(SegmentPoint("start", startRP.ID, startPoint.offset), SegmentPoint("end", endRP.ID, endPoint.offset)))
      Road(name, roadId, mainDir, dirs)
  }

  def addSegment(dir:String, segment:String, afterRPName:String, leftConnect:Boolean, beforeRPName:String, rightConnect:Boolean) = {
    val afterRP = ReferencePoint(afterRPName, name, dir,0,0)
    val beforeRP = ReferencePoint(beforeRPName, name, dir, 0, 0)
    val dirs = directions.filterNot(_.dir==dir) ++ directions.filter(_.dir==dir).map(
      d=>d.addSegmentString(name, segment, Some(afterRP), leftConnect, Some(beforeRP), rightConnect))
    Road(name, roadId, mainDir, dirs)
  }

  def getSegmentString(dir: String, start: PointRecord, end: PointRecord) : String = ???

 def updateLane(dir: String, lane: String)  = {
   val dirs = directions.filterNot(_.dir==dir) ++ directions.filter(_.dir==dir).map(
     d=>d.updateLane(lane))
   Road(name, roadId, mainDir, dirs)
 }

}

object Road{
  def apply(name:String, roadId: Long, mainDir: String, directions: List[Direction]): Road = new Road(name, roadId, mainDir, directions)

  def fromJson(record: AddRoadRecord) : Road = {
    Road(record.roadName, record.roadId, record.mainDir, record.directions.map(d=>Direction.fromString(record.roadName, d.dir, d.segments.toList)).toList)
  }
}