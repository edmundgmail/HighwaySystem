package com.ddp.lrs.models

import com.ddp.lrs.utils.MyImplicits._
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
      case that:Segment => that.start == start && that.end == end && that.length =~= length
      case _=>false
    }
  }

  def minus (segment:Segment) : List[Segment] = ???

  def withIncrementOffset(offset:Double) : Segment = {
      val newStart = SegmentPoint(this.start.name, this.start.referencePoint.withIncrementOffset(offset), this.start.offset)
      val newEnd = SegmentPoint(this.end.name, this.end.referencePoint.withIncrementOffset(offset), this.end.offset)
      Segment(newStart,newEnd, this.length)
  }

  override def toString: String = {
    s"{Segment start=${start} end=${end} length=${length}"
  }
}

object Segment{
  def apply(start:SegmentPoint, end:SegmentPoint, length: Double) = new Segment(start,end,length)

  def fromString(segment:String) : (Segment, List[ReferencePoint]) = {
    val dis = segment.split(",").zipWithIndex.filter(_._2%2 == 0).map(_._1.toDouble)
    val rpnames = segment.split(",").zipWithIndex.filter(_._2%2 == 1).map(_._1)
    val globalOffsets = dis.dropRight(1).zipWithIndex.map(d=>dis.take(d._2+1).sum)

    val rps = rpnames zip globalOffsets zip dis.drop(1).dropRight(1) :+ 0.0 map(r=>ReferencePoint(r._1._1, r._1._2, r._2))
    val seg = Segment(SegmentPoint("start", rps(0), 0 - dis(0)), SegmentPoint("end", rps.last, dis.last), dis.sum)
    (seg, rps.toList)
  }
}