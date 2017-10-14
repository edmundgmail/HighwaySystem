package com.lrs.common.models

import com.lrs.common.utils.AssertException

/**
  * Created by vagrant on 10/13/17.
  */
trait Line{

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

  def overlap(rps: List[ReferencePoint], that:Line) : Boolean = {
    this.contains(rps, that.start) || this.contains(rps, that.end) || that.contains(rps, this.start) /*|| that.contains(rps, this.end)*/
  }

  private def isBefore(rps: List[ReferencePoint], a: SegmentPoint,b: SegmentPoint): Int = {
      val aRP = ReferencePoint.getByID(a.referencePoint, rps)
      val bRP = ReferencePoint.getByID(b.referencePoint, rps)
      AssertException(aRP.isDefined && bRP.isDefined)
      val aGlobalOffset = aRP.get.globalOffset + a.offset
      val bGlobalOffset = bRP.get.globalOffset + b.offset
      if(aGlobalOffset < bGlobalOffset) 1
      else if(aGlobalOffset > bGlobalOffset) -1
      else  0
  }


  def getOverlap(rps: List[ReferencePoint], that: Line) : Option[Line] = {
    if(this.overlap(rps, that)) {
        if(this.contains(rps, that)) Some(that)
        else if(that.contains(rps,this)) Some(this)
        else {
          isBefore(rps, this.start, that.start) match {
            case 1 => Some(Line(that.start, this.end))
            case -1 => Some(Line(this.start,that.end))
          }
        }
    }
    else
      None
  }

  def except (rps:List[ReferencePoint], that: Line ) : List[Line ] = {
    if(this.overlap(rps, that)) {
      (isBefore(rps, this.start, that.start), isBefore(rps, this.end, that.end)) match {
        case (1, -1) => List(Line(this.start, that.start), Line(that.end,this.end))
        case (1, _)   => List(Line(this.start, that.start))
        case (_, -1) => List(Line(that.end, this.end))
        case (_, _) => List.empty
      }
    }
    else
      List(this)
  }

  def getOverlap(rps: List[ReferencePoint], lines : List[Line ]) : List[Line ] = {
    lines.map(line=>this.getOverlap(rps, line)).filter(_.isDefined).map(_.get)
  }

  def except(rps: List[ReferencePoint], those: List[ Line ]) : List[Line ] = {
      those.foldRight[List[Line]](List(this)){
        (line : Line , lines : List[Line ]) => {
          lines.map(result=> result.except(rps, line)).flatten.toList
        }
      }
  }

  @throws(classOf[Exception])
  def contains(rps: List[ReferencePoint], seg: Line ) : Boolean = {
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

object Line{
  def apply(start : SegmentPoint, end: SegmentPoint): Line = new Line{
      val start : SegmentPoint = start
      val end  : SegmentPoint = end
    }
}