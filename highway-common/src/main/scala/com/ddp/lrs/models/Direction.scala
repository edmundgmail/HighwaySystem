package com.ddp.lrs.models

/**
  * Created by eguo on 8/26/17.
  */
class Direction(val dir: String, val segments: List[Segment], val rps: List[ReferencePoint]) {
  def addSegment(segment: Segment, newRPs: List[ReferencePoint], afterRP: Option[ReferencePoint], leftConnect: Boolean, rightConnect: Boolean) : Direction = {
    (leftConnect, rightConnect) match {
      case (true, true) =>
        {
          val leftConnectSegment = segments.find(_.containsRP(afterRP.get)).get
          val leftConnectSegmentIndex = segments.indexOf(leftConnectSegment)
          val (left, right) = segments.splitAt(leftConnectSegmentIndex)
          right.drop(1)
          val rightConnectSegment = right.head
          right.drop(2)
          val newSegement = Segment(leftConnectSegment.start, rightConnectSegment.end, leftConnectSegment.length+rightConnectSegment.length+segment.length)
          Direction(dir, (left :+ newSegement) ::: right, rps)
        }

    }
  }

  def removeSegment(start: SegmentPoint, end:SegmentPoint, removeRP:Boolean) : Direction = ???
}

object Direction{
  def apply(dir:String, segments:List[Segment], rps: List[ReferencePoint]) = new Direction(dir, segments, rps)
}