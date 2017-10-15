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

