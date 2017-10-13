package com.lrs.common.models

import com.lrs.common.utils.AssertException

/**
  * Created by vagrant on 10/13/17.
  */
class Lane(val roadId: Long, val dir: String, val start: SegmentPoint, val end: SegmentPoint, val indexes: List[Int] = List.empty) {
  def add(n: Int, outside: Boolean = true): Lane = {
    (this.indexes, outside) match {
      case (List.empty, _) => Lane(this.roadId, this.dir, this.start, this.end, (1 to n).toList)
      case (_, true) => Lane(this.roadId, this.dir, this.start, this.end, this.indexes ++ (this.indexes.last + 1 to this.indexes.last+n).toList)
      case(_, false) => Lane(this.roadId, this.dir, this.start, this.end, (this.indexes(0) - n to this.indexes(0) - 1).toList ++ this.indexes)
    }
  }

  def remove(n: Int, outside: Boolean = true) : Lane = {
    AssertException(!this.indexes.isEmpty  && this.indexes.length >= n)
    outside match {
      case true => Lane(this.roadId, this.dir, this.start, this.end, this.indexes.slice(0, this.indexes.length - n))
      case false => Lane(this.roadId, this.dir, this.start, this.end, this.indexes.slice(n, this.indexes.length))
    }
  }
}

object Lane{
  def apply(roadId: Long, dir: String, start: SegmentPoint, end: SegmentPoint, indexes: List[Int] = List.empty) = {
      new Lane(roadId, dir, start, end, indexes)
  }

  def addLane(n: Int, outside: Boolean, )
}
