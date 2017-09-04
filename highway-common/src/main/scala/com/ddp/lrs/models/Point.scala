package com.ddp.lrs.models

/**
  * Created by eguo on 8/26/17.
  */
class Point (val name: String, val x: Double, val y:Double, val z:Double){
  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case that:Point => that.name == this.name && that.x == this.x && that.y == this.y && that.z == this.z
      case _=> false
    }
  }
}

case class SegmentPoint(override val name: String, referencePoint: ReferencePoint, offset: Double, override val x: Double = 0, override val y:Double = 0, override val z:Double = 0) extends Point(name,x,y,z)

case class ReferencePoint(override val name: String, val globalOffset: Double, val distance: Double, override val x: Double = 0, override val y:Double = 0, override val z:Double = 0) extends Point(name, x, y, z)
{
  def withIncrementOffset(offset:Double) = ReferencePoint(name, globalOffset+offset, distance, x,y,z)
  def withGlobalOffset(offset:Double) = ReferencePoint(name, offset, distance, x,y,z)
  def withDistance(d:Double) = ReferencePoint(name, globalOffset, d, x,y,z)
}
