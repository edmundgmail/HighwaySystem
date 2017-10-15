package com.lrs.common.models

import com.lrs.common.utils.{AssertException, JsonWritable}
import com.lrs.common.utils.MyImplicits._
/**
  * Created by eguo on 8/26/17.
  */

case class Segment(val start: SegmentPoint, val end : SegmentPoint, val length:Double) extends Line[Segment] with JsonWritable{


  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case that:Segment => that.start == start && that.end == end && that.length =~= length
      case _=>false
    }
  }

  override def clone(start: SegmentPoint, end: SegmentPoint): Segment = {
    this.copy(start = start, end  = end)
  }

  def minus (segment:Segment, rps:List[ReferencePoint]) : List[Segment] = {
      if(segment.start==this.start && segment.end==this.end) {
        List.empty
      }
      else if(segment.start==this.start){
        List(Segment(segment.end.useNext(rps), this.end, this.length-segment.length))
      }
      else if(segment.end == this.end){
        List(Segment(this.start, segment.start, this.length-segment.length))
      }
      else {
        val thisStart = ReferencePoint.getByID(start.referencePoint, rps)
        val thisEnd = ReferencePoint.getByID(end.referencePoint, rps)
        val thatStart = ReferencePoint.getByID(segment.start.referencePoint, rps)
        val thatEnd = ReferencePoint.getByID(segment.end.referencePoint, rps)

        val leftLength = thatStart.get.globalOffset+segment.start.offset - (thisStart.get.globalOffset+start.offset)
        val rightLength = thisEnd.get.globalOffset + end.offset - (thatEnd.get.globalOffset+segment.end.offset)
        List(Segment(this.start, segment.start, leftLength),Segment(segment.end.useNext(rps), this.end, rightLength))
      }
  }

  override def toString: String = {
    s"{Segment start=${start} end=${end} length=${length}}"
  }
}

object Segment{
  def fromString(roadName:String, dir:String, segment:String) : (Segment, List[ReferencePoint]) = {
    val dis = segment.split(",").zipWithIndex.filter(_._2%2 == 0).map(_._1.toDouble)
    val rpnames = segment.split(",").zipWithIndex.filter(_._2%2 == 1).map(_._1)
    val globalOffsets = dis.dropRight(1).zipWithIndex.map(d=>dis.take(d._2+1).sum)

    val rps = rpnames zip globalOffsets zip dis.drop(1).dropRight(1) :+ 0.0 map(r=>ReferencePoint(r._1._1, roadName, dir, r._1._2, r._2))
    val seg = Segment(SegmentPoint("start", rps(0).ID, 0 - dis(0)), SegmentPoint("end", rps.last.ID, dis.last), dis.sum)
    (seg, rps.toList)
  }
}