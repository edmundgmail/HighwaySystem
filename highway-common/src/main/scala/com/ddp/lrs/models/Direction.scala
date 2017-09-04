package com.ddp.lrs.models

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
        val (left, right) = rps.splitAt(rps.indexOf(x) + 1)
        val newRight = right.map(_.withIncrementOffset(length))

        val newLeft = if(leftConnect) left.dropRight(1) :+ afterRP.get else left
        newLeft ++ inserted ++ newRight
      }

      case (None, Some(x)) => {
        val (left, right) = rps.splitAt(rps.indexOf(x))
        val newRight = right.map(_.withIncrementOffset(length))

        left ++ inserted ++ newRight
      }
    }
  }


  def addSegmentString(segment:String, afterRP: Option[ReferencePoint], leftConnect: Boolean, beforeRP:Option[ReferencePoint], rightConnect: Boolean): Direction ={
      val dis = segment.split((",")).zipWithIndex.filter(_._2%2 == 0)
      val distances = dis.drop(1).map(_._1.toDouble)

      val globalOffsets = dis.map(d=>dis.take(d._2/2 + 1).map(_._1.toDouble).sum).dropRight(1)
      val rps = segment.split(",").zipWithIndex.filter(_._2 % 2 == 1).map(_._1)
      val newRPs = rps  zip globalOffsets zip distances map (r=>ReferencePoint(r._1._1, r._1._2, r._2))
      val start = SegmentPoint("start", newRPs(0), 0.0 - newRPs(0).globalOffset)
      val end = SegmentPoint("end", newRPs.last, dis.last._1.toDouble)
      val segmentNew = Segment(start, end, globalOffsets.last+end.offset)

      addSegment(segmentNew, newRPs.toList, afterRP, leftConnect, beforeRP, rightConnect)
  }

  def addSegment(segment: Segment, newRPs: List[ReferencePoint], afterRP: Option[ReferencePoint], leftConnect: Boolean, beforeRP:Option[ReferencePoint], rightConnect: Boolean) : Direction = {
    val totalDistance = newRPs.last.globalOffset + segment.end.offset
    assert(segment.length == totalDistance)

    (leftConnect, rightConnect) match {
      case (true, true) =>
        {
          val leftConnectSegment = segments.find(_.containsReferencePoint(afterRP.get)).get
          val leftConnectSegmentIndex = segments.indexOf(leftConnectSegment)

          val rightConnectSegment = segments.find(_.containsReferencePoint(beforeRP.get)).get
          val rightConnectSegmentIndex = segments.indexOf(rightConnectSegment)

          assert(leftConnectSegment== rightConnectSegmentIndex - 1)
          val overalLength = leftConnectSegment.length+rightConnectSegment.length+segment.length
          val newSegment = Segment(leftConnectSegment.start, rightConnectSegment.end, overalLength )
          val (left, right) = segments.splitAt(leftConnectSegmentIndex)
          val leftRP = afterRP.get.withDistance(leftConnectSegment.end.offset - segment.start.offset)
          val lastRP = newRPs.last.withDistance(segment.end.offset - rightConnectSegment.start.offset)
          val newRPs1 = (newRPs.dropRight(1):+lastRP).map(_.withIncrementOffset(leftConnectSegment.end.referencePoint.globalOffset+leftConnectSegment.end.offset))
          val newRPList = mergedRPs(newRPs1, Some(leftRP), afterRP,  leftConnect, rightConnect, segment.length)
          Direction(dir, (left :+ newSegment) ::: right.drop(2), newRPList)
        }

      case (true, false) =>
        {
          val leftConnectSegment = segments.find(_.containsReferencePoint(afterRP.get)).get
          val leftConnectSegmentIndex = segments.indexOf(leftConnectSegment)

          val newSegement = Segment(leftConnectSegment.start, segment.end, leftConnectSegment.length+segment.length)
          val (left, right) = segments.splitAt(leftConnectSegmentIndex)
          val leftRP = afterRP.get.withDistance(leftConnectSegment.end.offset - segment.start.offset)
          val newRPs1 = newRPs.map(_.withIncrementOffset(leftConnectSegment.end.referencePoint.globalOffset+leftConnectSegment.end.offset))
          val newRPList =  mergedRPs(newRPs1, afterRP, beforeRP, leftConnect, rightConnect, segment.length)
          Direction(dir, (left :+ newSegement) ::: right.drop(1), newRPList)
        }

      case (false, true) =>
        {
          val rightConnectSegment = segments.find(_.containsReferencePoint(beforeRP.get)).get
          val rightConnectSegmentIndex = segments.indexOf(rightConnectSegment)

          val newSegement = Segment(segment.start, rightConnectSegment.end, rightConnectSegment.length+segment.length)
          val (left, right) = segments.splitAt(rightConnectSegmentIndex)
          val rightRP = newRPs.last.withDistance( segment.end.offset - rightConnectSegment.start.offset )
          val newRPs1 = (newRPs.dropRight(1):+rightRP).map(_.withIncrementOffset(rightConnectSegment.start.offset))
          val newRPList = mergedRPs(newRPs1, afterRP, beforeRP, leftConnect,rightConnect, segment.length)
          Direction(dir, (left :+ newSegement) ::: right.drop(1), newRPList)
        }
      case (false, false) => {
        (afterRP, beforeRP) match {
          case (None, None) => {
            Direction(dir, List(segment), newRPs)
          }
          case (Some(x), _) => {
            val leftConnectSegment = segments.find(_.containsReferencePoint(x)).get
            val leftConnectSegmentIndex = segments.indexOf(leftConnectSegment)
            val (left, right) = segments.splitAt(leftConnectSegmentIndex + 1)
            val newRPs1 = newRPs.map(_.withIncrementOffset(leftConnectSegment.end.offset+leftConnectSegment.end.referencePoint.globalOffset))
            val newRPList = mergedRPs(newRPs1, afterRP, beforeRP, leftConnect,rightConnect, segment.length)
            Direction(dir, (left :+ segment) ++ right, newRPList)
          }
          case(_, Some(x)) =>{
            val rightConnectSegment = segments.find(_.containsReferencePoint(x)).get
            val rightConnectSegmentIndex = segments.indexOf(rightConnectSegment)
            val (left, right) = segments.splitAt(rightConnectSegmentIndex)
            val newRPs1 = newRPs.map(_.withIncrementOffset((rightConnectSegment.start.referencePoint.globalOffset+rightConnectSegment.start.offset)))
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
}

object Direction{
  def apply(dir:String, segments:List[Segment], rps: List[ReferencePoint]) = new Direction(dir, segments, rps)
}