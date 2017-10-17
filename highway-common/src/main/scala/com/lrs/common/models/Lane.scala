package com.lrs.common.models

import com.lrs.common.utils.{AssertException, JsonWritable}

/**
  * Created by vagrant on 10/13/17.
  */
case class Lane(val start: SegmentPoint, val end: SegmentPoint, val indexes: List[Int] = List.empty) extends Line[Lane] with JsonWritable {
  override def add(n: Int, outside: Boolean = true): Line[Lane] = {
    (this.indexes.isEmpty, outside) match {
      case (true, _) => Lane(this.start, this.end, (1 to n).toList)
      case (_, true) => Lane(this.start, this.end, this.indexes ++ (this.indexes.last + 1 to this.indexes.last+n).toList)
      case(_, false) => Lane(this.start, this.end, (this.indexes(0) - n to this.indexes(0) - 1).toList ++ this.indexes)
    }
  }

  override def clone(start: SegmentPoint, end: SegmentPoint): Lane = {
    this.copy(start = start, end = end)
  }

  override def remove(n: Int, outside: Boolean = true) : Line[Lane]  = {
    AssertException(!this.indexes.isEmpty  && this.indexes.length >= n)
    outside match {
      case true => Lane(this.start, this.end, this.indexes.slice(0, this.indexes.length - n))
      case false => Lane(this.start, this.end, this.indexes.slice(n, this.indexes.length))
    }
  }


}

case class LaneChangeRecord(override val start: SegmentPoint, override  val end: SegmentPoint, n: Int, outside : Boolean) extends Line[Lane]

object Lane {

  type LaneRecord = (SegmentPoint, SegmentPoint, String)
  /*rp1,offset1, rp2,offset2, 1, in*/
  /*rp1,offset1, rp2,offset2, 2, out*/
  private def parseLaneRecord (rps : List[ReferencePoint], inputString:String) : Option[LaneRecord] = {
    val input = inputString.split(",")
    AssertException(input.length >= 4 )
    val startRP = ReferencePoint.getByName(rps, input(0))
    val endRP = ReferencePoint.getByName(rps, input(2))
    AssertException(startRP.isDefined && endRP.isDefined)
    try {
      val startOffset = input(1).toDouble
      val endOffset = input(3).toDouble
      Some(SegmentPoint("start", startRP.get.ID, startOffset), SegmentPoint("end", endRP.get.ID, endOffset), input.drop(4).mkString(","))
    }
    catch{
      case e: Throwable => AssertException (false, e.getMessage); None
    }
  }

  @throws[Exception]
  def parseLaneChangeRecord(rps : List[ReferencePoint], inputString:String) : Option[LaneChangeRecord] = {
      val laneRecord = parseLaneRecord(rps, inputString)
      laneRecord match {
        case Some(record) => {
          val inputs = record._3.split(",")
          try{
            val n = inputs(0).toInt
            val outside = inputs.length == 1 || inputs(1)!="inside"
            Some(LaneChangeRecord(record._1, record._2,n, outside))
          }
          catch {
            case _=> None
          }
        }
        case _ => None
      }
  }

  def fromString(rps: List[ReferencePoint], inputString: String ) : Option[Lane] = {
      val laneRecord = parseLaneRecord(rps, inputString)

    laneRecord match {
        case Some(record) => {
            val indexes = "(\\d+)".r.findAllIn(record._3).toList.map(_.toInt)
            Some(Lane(record._1, record._2, indexes))
        }
        case _ => None
      }
  }

}

