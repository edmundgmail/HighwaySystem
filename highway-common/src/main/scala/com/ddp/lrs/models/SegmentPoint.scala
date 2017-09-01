package com.ddp.lrs.models

/**
  * Created by eguo on 8/26/17.
  */
class SegmentPoint(override val name: String, val globalOffset: Double = 0, override val x: Double = 0, override val y :Double= 0, override val z :Double= 0) extends Point(name, x, y, z){
  def WithGlobalOffset(newOffset:Double) = {
    SegmentPoint.apply(name, newOffset, x,y,z)
  }
}

object SegmentPoint{
  def apply(name:String, globalOffset:Double = 0, x:Double= 0,y:Double= 0,z:Double= 0): SegmentPoint = new SegmentPoint(name,globalOffset,x,y,z)
}

case class ReferencePoint(override val name: String, override val globalOffset: Double, val distance: Double, override val x: Double = 0, override val y:Double = 0, override val z:Double = 0) extends SegmentPoint(name,globalOffset,  x, y, z)
{
  override def WithGlobalOffset(newOffset: Double) = super.WithGlobalOffset(newOffset).asInstanceOf[ReferencePoint]
}
