package com.lrs.common.models

import com.lrs.common.utils.AssertException

/**
  * Created by vagrant on 10/13/17.
  */
trait Line {

  val start : SegmentPoint
  val end : SegmentPoint

  private def contains(rps: List[ReferencePoint], that: SegmentPoint) : Boolean = {
    val thatRP = ReferencePoint.getByID(that.referencePoint, rps)
    val thisStart = ReferencePoint.getByID(start.referencePoint, rps)
    val thisEnd = ReferencePoint.getByID(end.referencePoint, rps)

    AssertException(thisStart.isDefined && thisEnd.isDefined && thatRP.isDefined)

    (thisStart.get.globalOffset+start.offset <= thatRP.get.globalOffset+that.offset) &&
      (thisEnd.get.globalOffset+end.offset >= thatRP.get.globalOffset+that.offset)
  }

  def overlap(rps: List[ReferencePoint], that: Line) : Boolean = {
    this.contains(rps, that.start) || this.contains(rps, that.end) || that.contains(rps, this.start) /*|| that.contains(rps, this.end)*/
  }

  @throws(classOf[Exception])
  def contains(rps: List[ReferencePoint], seg:Line) : Boolean = {
    val thisStart = ReferencePoint.getByID(start.referencePoint, rps)
    val thisEnd = ReferencePoint.getByID(end.referencePoint, rps)
    val thatStart = ReferencePoint.getByID(seg.start.referencePoint, rps)
    val thatEnd = ReferencePoint.getByID(seg.end.referencePoint, rps)

    AssertException(thisStart.isDefined && thisEnd.isDefined && thatStart.isDefined && thatEnd.isDefined)

    thisStart.get.globalOffset+start.offset <= thatStart.get.globalOffset+seg.start.offset &&
      thisEnd.get.globalOffset + end.offset >= thatEnd.get.globalOffset+seg.end.offset
  }

  def containsReferencePoint(rp:ReferencePoint, rps:List[ReferencePoint]) : Boolean = {
    val startRP = ReferencePoint.getByID(start.referencePoint, rps)
    val endRP = ReferencePoint.getByID(end.referencePoint, rps)

    if(startRP.isDefined && endRP.isDefined){
      val startOffset = startRP.get.globalOffset + start.offset
      val endOffset  = endRP.get.globalOffset+end.offset

      rp.globalOffset>= startOffset && rp.globalOffset<=endOffset
    }
    else
      false
  }

  def containedReferencePoints(rps:List[ReferencePoint]) : List[ReferencePoint] = {
    rps.filter(r=>containsReferencePoint(r, rps))
  }
}
