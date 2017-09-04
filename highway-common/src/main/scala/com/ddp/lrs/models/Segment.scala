package com.ddp.lrs.models

/**
  * Created by eguo on 8/26/17.
  */
class Segment(val start: SegmentPoint, val end : SegmentPoint, val length:Double){
  def containsReferencePoint(rp:ReferencePoint) : Boolean = {
    val startOffset = start.referencePoint.globalOffset + start.offset
    val endOffset  = end.referencePoint.globalOffset+end.offset

    return rp.globalOffset>= startOffset && rp.globalOffset<=endOffset
  }

  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case that:Segment => that.start == start && that.end == end && that.length == length
      case _=>false
    }
  }

  def minus (segment:Segment) : List[Segment] = ???
}

object Segment{
  def apply(start:SegmentPoint, end:SegmentPoint, length: Double) = new Segment(start,end,length)
}