package com.ddp.lrs.models
import com.ddp.lrs.utils.MyImplicits._
/**
  * Created by eguo on 8/26/17.
  */
class Direction(val dir: String, val segments: List[Segment], val rps: List[ReferencePoint]) {
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
    assert(segment.length == totalDistance)

    (leftConnect, rightConnect) match {
      case (true, true) =>
        {
          val leftConnectSegment = segments.find(_.containsReferencePoint(afterRP.get,rps)).get
          val leftConnectSegmentIndex = segments.indexOf(leftConnectSegment)

          val rightConnectSegment = segments.find(_.containsReferencePoint(beforeRP.get, rps)).get
          val rightConnectSegmentIndex = segments.indexOf(rightConnectSegment)

          assert(leftConnectSegmentIndex == rightConnectSegmentIndex - 1)
          val overalLength = leftConnectSegment.length+rightConnectSegment.length+segment.length
          val newSegment = Segment(leftConnectSegment.start, rightConnectSegment.end, overalLength )
          val (left, right) = segments.splitAt(leftConnectSegmentIndex)
          val leftRP = afterRP.get.withDistance(leftConnectSegment.end.offset - segment.start.offset)
          val lastRP = newRPs.last.withDistance(segment.end.offset - rightConnectSegment.start.offset)
          val newRPs1 = (newRPs.dropRight(1):+lastRP).map(_.withIncrementOffset(leftRP.globalOffset+leftConnectSegment.end.offset))
          val newRPList = mergedRPs(newRPs1, Some(leftRP), beforeRP,  leftConnect, rightConnect, segment.length)
          Direction(dir, (left :+ newSegment) ::: right.drop(2), newRPList)
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
          Direction(dir, (left :+ newSegement) ::: right.drop(1), newRPList)
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
          Direction(dir, (left :+ newSegement) ::: right.drop(1), newRPList)
        }
      case (false, false) => {
        (afterRP, beforeRP) match {
          case (None, None) => {
            Direction(dir, List(segment), newRPs)
          }
          case (Some(x), _) => {
            val leftConnectSegment = segments.find(_.containsReferencePoint(x, rps)).get
            val leftConnectSegmentIndex = segments.indexOf(leftConnectSegment)
            val (left, right) = segments.splitAt(leftConnectSegmentIndex + 1)
            val leftRP = ReferencePoint.getByID(leftConnectSegment.end.referencePoint, rps)
            val newRPs1 = newRPs.map(_.withIncrementOffset(leftConnectSegment.end.offset+leftRP.get.globalOffset))
            val newRPList = mergedRPs(newRPs1, afterRP, beforeRP, leftConnect,rightConnect, segment.length)
            Direction(dir, (left :+ segment) ++ right, newRPList)
          }
          case(_, Some(x)) =>{
            val rightConnectSegment = segments.find(_.containsReferencePoint(x, rps)).get
            val rightConnectSegmentIndex = segments.indexOf(rightConnectSegment)
            val (left, right) = segments.splitAt(rightConnectSegmentIndex)
            val rightRP = ReferencePoint.getByID(rightConnectSegment.start.referencePoint, rps)
            val newRPs1 = newRPs.map(_.withIncrementOffset((rightRP.get.globalOffset+rightConnectSegment.start.offset)))
            val newRPList = mergedRPs(newRPs1, afterRP, beforeRP, leftConnect, rightConnect, segment.length)
            Direction(dir, (left :+ segment) ++ right, newRPList)
          }
        }
      }
    }
  }

  def removeSegment(segment:Segment, removeRP:Boolean) : Direction = {
    null
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
    s"{Direction dir=${dir} segments]s${segments.toString} rps=s${rps.toString} }"
  }
}

object Direction{
  def apply(dir:String, segments:List[Segment], rps: List[ReferencePoint]) = new Direction(dir, segments, rps)

  def fromString(roadName:String, dir:String, road: List[String]) : Direction = {
    val (_segs, _rps) = road.map(str=>Segment.fromString(roadName, dir, str)).unzip
    val segOffsets = _segs.zipWithIndex.map(s=>_segs.take(s._2).map(_.length).sum)
    val newRPs = _rps.zipWithIndex.map(l=>l._1.map(_.withIncrementOffset(segOffsets(l._2)))).flatten
    Direction(dir, _segs, newRPs)
  }
}