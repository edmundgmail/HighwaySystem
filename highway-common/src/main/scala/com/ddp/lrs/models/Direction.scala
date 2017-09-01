package com.ddp.lrs.models

/**
  * Created by eguo on 8/26/17.
  */
class Direction(val dir: String, val segments: List[Segment], val rps: List[ReferencePoint]) {

  private def mergedRPs(rps:List[ReferencePoint], inserted: List[ReferencePoint], afterRP:Option[ReferencePoint], beforeRP:Option[ReferencePoint], globalOffset:Double, length:Double): List[ReferencePoint] ={

    val newRps = inserted.map(r=>r.WithGlobalOffset(r.globalOffset+globalOffset))

    (afterRP, beforeRP) match {
      case (None, None) => {
        if(rps.isEmpty)
          newRps
        else
          throw new Exception("Before and after RP can't be both None ")
      }
      case (Some(x), _) => {
        val (left, right) = rps.splitAt(rps.indexOf(x) + 1)
        left ++ newRps ++ right.map(r=>r.WithGlobalOffset(r.globalOffset+length))
      }
      case (_, Some(x)) => {
        val (left, right) = rps.splitAt(rps.indexOf(x))
        left ++ newRps ++ right.map(r=>r.WithGlobalOffset(r.globalOffset+length))
      }
    }
  }

  def addSegment(segment: Segment, newRPs: List[ReferencePoint], afterRP: Option[ReferencePoint], leftConnect: Boolean, beforeRP:Option[ReferencePoint], rightConnect: Boolean) : Direction = {
    (leftConnect, rightConnect) match {
      case (true, true) =>
        {
          val leftConnectSegment = segments.find(_.containsSegmentPoint(afterRP.get)).get
          val leftConnectSegmentIndex = segments.indexOf(leftConnectSegment)

          val rightConnectSegment = segments.find(_.containsSegmentPoint(beforeRP.get)).get
          val rightConnectSegmentIndex = segments.indexOf(rightConnectSegment)

          assert(leftConnectSegment== rightConnectSegmentIndex - 1)
          val overalLength = leftConnectSegment.length+rightConnectSegment.length+segment.length
          val newSegment = Segment(leftConnectSegment.start, rightConnectSegment.end.WithGlobalOffset(rightConnectSegment.end.globalOffset + segment.length), overalLength )
          val (left, right) = segments.splitAt(leftConnectSegmentIndex)
          val newRight = right.drop(2).map(_.WithIncrementedEndGlobalOffset(segment.length))
          val newRPList = mergedRPs(rps, newRPs, afterRP, beforeRP, leftConnectSegment.end.globalOffset, segment.length)
          Direction(dir, (left :+ newSegment) ::: newRight, newRPList)
        }

      case (true, false) =>
        {
          val leftConnectSegment = segments.find(_.containsSegmentPoint(afterRP.get)).get
          val leftConnectSegmentIndex = segments.indexOf(leftConnectSegment)

          val newSegement = Segment(leftConnectSegment.start, segment.end.WithGlobalOffset(segment.length+leftConnectSegment.end.globalOffset), leftConnectSegment.length+segment.length)
          val (left, right) = segments.splitAt(leftConnectSegmentIndex)
          val newRight = right.drop(1).map(_.WithIncrementedEndGlobalOffset(segment.length))
          val newRPList = mergedRPs(rps, newRPs, afterRP, beforeRP, leftConnectSegment.end.globalOffset,segment.length)
          Direction(dir, (left :+ newSegement) ::: newRight, newRPList)
        }

      case (false, true) =>
        {
          val rightConnectSegment = segments.find(_.containsSegmentPoint(beforeRP.get)).get
          val rightConnectSegmentIndex = segments.indexOf(rightConnectSegment)

          val newSegement = Segment(segment.start.WithGlobalOffset(rightConnectSegment.start.globalOffset), rightConnectSegment.end, rightConnectSegment.length+segment.length).WithIncrementedEndGlobalOffset(segment.length)
          val (left, right) = segments.splitAt(rightConnectSegmentIndex)
          val newRPList = mergedRPs(rps, newRPs, afterRP, beforeRP, rightConnectSegment.end.globalOffset, segment.length)
          val newRight = right.drop(1).map(_.WithIncrementedEndGlobalOffset(segment.length))
          Direction(dir, (left :+ newSegement) ::: newRight, newRPList)
        }
      case (false, false) => {
        (afterRP, beforeRP) match {
          case (None, None) => {
            Direction(dir, List(segment), newRPs)
          }
          case (Some(x), _) => {
            val leftConnectSegment = segments.find(_.containsSegmentPoint(x)).get
            val leftConnectSegmentIndex = segments.indexOf(leftConnectSegment)
            val (left, right) = segments.splitAt(leftConnectSegmentIndex + 1)
            val newRPList = mergedRPs(rps, newRPs, afterRP, beforeRP, leftConnectSegment.end.globalOffset,segment.length)
            val newSegment = Segment(segment.start.WithGlobalOffset(leftConnectSegment.end.globalOffset), segment.end.WithGlobalOffset(leftConnectSegment.end.globalOffset+segment.length), segment.length)
            val newRight =right.map(r=>r.WithIncrementedEndGlobalOffset(segment.length))
            Direction(dir, (left :+ newSegment) ++ newRight, newRPList)
          }
          case(_, Some(x)) =>{
            val rightConnectSegment = segments.find(_.containsSegmentPoint(x)).get
            val rightConnectSegmentIndex = segments.indexOf(rightConnectSegment)
            val (left, right) = segments.splitAt(rightConnectSegmentIndex)
            val newRPList = mergedRPs(rps, newRPs, afterRP, beforeRP, rightConnectSegment.start.globalOffset,segment.length)
            val newSegment = Segment(segment.start.WithGlobalOffset(rightConnectSegment.start.globalOffset), segment.end.WithGlobalOffset(rightConnectSegment.start.globalOffset + segment.length), segment.length)
            val newRight = right.map(r=>r.WithIncrementedEndGlobalOffset(segment.length))
            Direction(dir, (left :+ newSegment) ++ newRight, newRPList)
          }
        }
      }
    }
  }

  def removeSegment(segment:Segment, removeRP:Boolean) : Direction = {
    val startSeg = segments.find(_.containsSegmentPoint(segment.start))
    val endSeg = segments.find(_.containsSegmentPoint(segment.end))

    if(startSeg.isEmpty || endSeg.isEmpty || startSeg != endSeg){
      throw new Exception("Start and End must reside on the same segment")
    }

    val newRPs = if (!removeRP) rps else {
        rps.filterNot(segment.containsSegmentPoint(_))
    }


    val startSegIndex= segments.indexOf(startSeg.get)
    val (left, right) = segments.splitAt(startSegIndex)

    Direction(dir, left ++ startSeg.get.minus(segment) ++ right.drop(1), newRPs)

  }
}

object Direction{
  def apply(dir:String, segments:List[Segment], rps: List[ReferencePoint]) = new Direction(dir, segments, rps)
}