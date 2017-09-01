package com.ddp.lrs.models

/**
  * Created by eguo on 8/26/17.
  */
class Segment(val start: SegmentPoint, val end : SegmentPoint, val length:Double){
  def StartSegmentPoint = start
  def EndSegmentPoint = SegmentPoint(end.name,end.x,end.y,end.z, length)
  def containsSegmentPoint(rp:SegmentPoint) : Boolean = {
      (start.globalOffset <= rp.globalOffset && end.globalOffset>= rp.globalOffset)
  }

  def WithIncrementedEndGlobalOffset(offset: Double): Segment ={
    val newEnd = end.WithGlobalOffset(end.globalOffset+offset)
    Segment.apply(start, end, length)
  }

  override def equals(obj: scala.Any): Boolean = {
    try{
      val o = obj.asInstanceOf[Segment]
      o.start == start && o.end == end && o.length == length
    }
    catch{
      case _=>false
    }

  }

  def minus (segment:Segment) : List[Segment] = ???
}

object Segment{
  def apply(start:SegmentPoint, end:SegmentPoint, length: Double) = new Segment(start,end,length)
}