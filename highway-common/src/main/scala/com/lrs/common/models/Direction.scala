package com.lrs.common.models

import com.lrs.common.utils.{AssertException, JsonWritable}
import org.json4s.JsonWriter

/**
  * Created by eguo on 8/26/17.
  */
case class Direction(val dir: String, val segments: List[Segment] = List.empty, val rps: List[ReferencePoint] = List.empty, val lanes: List[Lane] = List.empty) extends JsonWritable{
  @throws(classOf[Exception])
  def removeSegment(start: SegmentPoint, end:SegmentPoint, removeRP:Boolean = true) : Direction = {
      val segment= Segment(start, end, 0)
      val seg = segments.filter(_.contains(rps, segment))
      if(seg.isEmpty){
          throw new Exception("Can't find the segment to be removed")
      }

      val rp1 = ReferencePoint.findIDIndex(segment.start.referencePoint,rps)
      val rp2 = ReferencePoint.findIDIndex(segment.end.referencePoint,rps)

      val length = rps.slice(rp1, rp2).map(_.distance).sum - segment.start.offset + segment.end.offset
      val segIndex = segments.indexOf(seg(0))
      val (left, right) = segments.splitAt(segIndex)

      val remainingRPs = if(!removeRP) rps else {
          val list = segment.containedReferencePoints(rps)
          if(!list.isEmpty)
           {
              val index = rps.indexOf(list(0))
              val (left, right) = rps.splitAt(index)
              val newLeft = if(!left.isEmpty) left.dropRight(1) :+ left.last.withDistance(0) else left
              val newRight = right.drop(list.size).map(_.withIncrementOffset(0 - length))
             newLeft ++ newRight
           }
          else
            rps
      }

      Direction(dir, left ++ seg(0).minus(segment, rps) ++ right.drop(1), remainingRPs, this.lanes)
  }

  private def mergedRPs(inserted: List[ReferencePoint], afterRP:Option[ReferencePoint], beforeRP:Option[ReferencePoint], leftConnect:Boolean, rightConnect:Boolean, length:Double): List[ReferencePoint] ={
    (afterRP, beforeRP) match {
      case (None, None) => {
        if(rps.isEmpty)
          inserted
        else
          throw new Exception("Before and after RP can't be both None ")
      }
      case (Some(x), _) => {
        val (left, right) = rps.splitAt(ReferencePoint.findIndex(rps, x) + 1)
        val newRight = right.map(_.withIncrementOffset(length))

        val newLeft = if(leftConnect) left.dropRight(1) :+ afterRP.get else left
        newLeft ++ inserted ++ newRight
      }

      case (None, Some(x)) => {
        val (left, right) = rps.splitAt(ReferencePoint.findIndex(rps,x))
        val newRight = right.map(_.withIncrementOffset(length))

        left ++ inserted ++ newRight
      }
    }
  }


  def addSegmentString(roadName:String, segment:String, afterRP: Option[ReferencePoint], leftConnect: Boolean, beforeRP:Option[ReferencePoint], rightConnect: Boolean): Direction ={
      val dis = segment.split((",")).zipWithIndex.filter(_._2%2 == 0).map(_._1.toDouble)
      val distances = dis.drop(1).dropRight(1):+0.0

      val globalOffsets = dis.dropRight(1).zipWithIndex.map(d=>dis.take(d._2+1).sum)
        //.take(_).map().dropRight(1)
      val _rps = segment.split(",").zipWithIndex.filter(_._2 % 2 == 1).map(_._1)
      val newRPs = _rps  zip globalOffsets zip distances map (r=>ReferencePoint(r._1._1, roadName, dir , r._1._2, r._2))
      val start = SegmentPoint("start", newRPs(0).ID, 0.0 - newRPs(0).globalOffset)
      val end = SegmentPoint("end", newRPs.last.ID, dis.last)
      val segmentNew = Segment(start, end, globalOffsets.last+end.offset)

      addSegment(segmentNew, newRPs.toList, afterRP, leftConnect, beforeRP, rightConnect)
  }

  def addSegment(segment: Segment, newRPs: List[ReferencePoint], afterRP: Option[ReferencePoint], leftConnect: Boolean, beforeRP:Option[ReferencePoint], rightConnect: Boolean) : Direction = {
    val totalDistance = newRPs.last.globalOffset + segment.end.offset
    AssertException(segment.length == totalDistance)

    (leftConnect, rightConnect) match {
      case (true, true) =>
        {
          val leftConnectSegment = segments.find(_.containsReferencePoint(afterRP.get,rps)).get
          val leftConnectSegmentIndex = segments.indexOf(leftConnectSegment)

          val rightConnectSegment = segments.find(_.containsReferencePoint(beforeRP.get, rps)).get
          val rightConnectSegmentIndex = segments.indexOf(rightConnectSegment)

          AssertException(leftConnectSegmentIndex == rightConnectSegmentIndex - 1)
          val overalLength = leftConnectSegment.length+rightConnectSegment.length+segment.length
          val newSegment = Segment(leftConnectSegment.start, rightConnectSegment.end, overalLength )
          val (left, right) = segments.splitAt(leftConnectSegmentIndex)
          val leftRP = afterRP.get.withDistance(leftConnectSegment.end.offset - segment.start.offset)
          val lastRP = newRPs.last.withDistance(segment.end.offset - rightConnectSegment.start.offset)
          val newRPs1 = (newRPs.dropRight(1):+lastRP).map(_.withIncrementOffset(leftRP.globalOffset+leftConnectSegment.end.offset))
          val newRPList = mergedRPs(newRPs1, Some(leftRP), beforeRP,  leftConnect, rightConnect, segment.length)
          Direction(dir, (left :+ newSegment) ::: right.drop(2), newRPList, this.lanes)
        }

      case (true, false) =>
        {
          val leftConnectSegment = segments.find(_.containsReferencePoint(afterRP.get, rps)).get
          val leftConnectSegmentIndex = segments.indexOf(leftConnectSegment)
          val newSegement = Segment(leftConnectSegment.start, segment.end, leftConnectSegment.length+segment.length)
          val (left, right) = segments.splitAt(leftConnectSegmentIndex)
          val leftRP = afterRP.get.withDistance(leftConnectSegment.end.offset - segment.start.offset)
          val newRPs1 = newRPs.map(_.withIncrementOffset(leftRP.globalOffset+leftConnectSegment.end.offset))
          val newRPList =  mergedRPs(newRPs1, Some(leftRP), beforeRP, leftConnect, rightConnect, segment.length)
          Direction(dir, (left :+ newSegement) ::: right.drop(1), newRPList, this.lanes)
        }

      case (false, true) =>
        {
          val rightConnectSegment = segments.find(_.containsReferencePoint(beforeRP.get, rps)).get
          val rightConnectSegmentIndex = segments.indexOf(rightConnectSegment)
          val newSegement = Segment(segment.start, rightConnectSegment.end, rightConnectSegment.length+segment.length)
          val (left, right) = segments.splitAt(rightConnectSegmentIndex)
          val rightRP = newRPs.last.withDistance( segment.end.offset - rightConnectSegment.start.offset )
          val rightConnectRP = ReferencePoint.getByID(rightConnectSegment.start.referencePoint, rps)
          val newRPs1 = (newRPs.dropRight(1):+rightRP).map(_.withIncrementOffset(rightConnectRP.get.globalOffset+rightConnectSegment.start.offset))
          val newRPList = mergedRPs(newRPs1, afterRP, beforeRP, leftConnect,rightConnect, segment.length)
          Direction(dir, (left :+ newSegement) ::: right.drop(1), newRPList, this.lanes)
        }
      case (false, false) => {
        (afterRP, beforeRP) match {
          case (None, None) => {
            Direction(dir, List(segment), newRPs, this.lanes)
          }
          case (Some(x), _) => {
            val leftConnectSegment = segments.find(_.containsReferencePoint(x, rps)).get
            val leftConnectSegmentIndex = segments.indexOf(leftConnectSegment)
            val (left, right) = segments.splitAt(leftConnectSegmentIndex + 1)
            val leftRP = ReferencePoint.getByID(leftConnectSegment.end.referencePoint, rps)
            val newRPs1 = newRPs.map(_.withIncrementOffset(leftConnectSegment.end.offset+leftRP.get.globalOffset))
            val newRPList = mergedRPs(newRPs1, afterRP, beforeRP, leftConnect,rightConnect, segment.length)
            Direction(dir, (left :+ segment) ++ right, newRPList, this.lanes)
          }
          case(_, Some(x)) =>{
            val rightConnectSegment = segments.find(_.containsReferencePoint(x, rps)).get
            val rightConnectSegmentIndex = segments.indexOf(rightConnectSegment)
            val (left, right) = segments.splitAt(rightConnectSegmentIndex)
            val rightRP = ReferencePoint.getByID(rightConnectSegment.start.referencePoint, rps)
            val newRPs1 = newRPs.map(_.withIncrementOffset((rightRP.get.globalOffset+rightConnectSegment.start.offset)))
            val newRPList = mergedRPs(newRPs1, afterRP, beforeRP, leftConnect, rightConnect, segment.length)
            Direction(dir, (left :+ segment) ++ right, newRPList, this.lanes)
          }
        }
      }
    }
  }

  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case that:Direction => {
          that.dir==this.dir &&
          that.segments==this.segments &&
          that.rps == this.rps
      }
      case _=> false
    }
  }

  override def toString: String = {
    s"{Direction dir=${dir} segments]${segments.toString} rps=${rps.toString} lanes=${lanes.toString} }"
  }

  def addLane(start: SegmentPoint, end:SegmentPoint, n:Int, outside: Boolean) : Direction = {
    val newLane = new Lane(start, end, (1 to n ).toList)
    val newLanes : List[Lane] = newLane.except(rps, this.lanes)
    val overlapLanes : List[Lane] = this.lanes.map(l=>l.getOverlap(rps, newLane)).flatten.map(f=>f.add(n, outside))
    val nonOverlaped : List[Lane] = this.lanes.filterNot(l=>l.overlap(rps, newLane))
    val overlapExcept : List[Lane] = this.lanes.map(l=>l.except(rps, newLane)).flatten

    Direction(this.dir,  this.segments, this.rps, (newLanes ++ overlapLanes ++ nonOverlaped ++ overlapExcept))
  }

  def removeLane( start: SegmentPoint, end:SegmentPoint, n:Int, outside: Boolean) : Direction = {
    val newLane = new Lane(start, end, (1 to n).toList)
    val overlapLanes1  : List[Lane] = this.lanes.map(l=>l.getOverlap(rps, newLane)).flatten
    val overlapLanes : List[Lane] =overlapLanes1.map(f=>f.remove(n, outside))
    //val nonOverlaped = this.lanes.filterNot(l=>l.overlap(rps, newLane))
    val overlapExcept = this.lanes.map(l=>l.except(rps, newLane)).flatten

    Direction(this.dir, this.segments, this.rps, (overlapLanes ++ overlapExcept))
  }

  def updateLane(inputString: String) : Direction = {
    val laneChangeRecord = Lane.parseLaneChangeRecord(rps, inputString)
    laneChangeRecord match {
      case Some(record) => {
        if(record.n < 0) removeLane(record.start, record.end, 0 - record.n, record.outside)
        else addLane(record.start, record.end, record.n, record.outside)
      }
      case None => this
    }
  }

}

object Direction{
  def fromString(roadName:String, dir:String, road: List[String]) : Direction = {
    val (_segs, _rps) = road.map(str=>Segment.fromString(roadName, dir, str)).unzip
    val segOffsets = _segs.zipWithIndex.map(s=>_segs.take(s._2).map(_.length).sum)
    val newRPs = _rps.zipWithIndex.map(l=>l._1.map(_.withIncrementOffset(segOffsets(l._2)))).flatten
    Direction(dir, _segs, newRPs, List.empty)
  }
}