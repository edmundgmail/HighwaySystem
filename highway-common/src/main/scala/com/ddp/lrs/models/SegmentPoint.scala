package com.ddp.lrs.models

/**
  * Created by eguo on 8/26/17.
  */
class SegmentPoint(override val name: String, override val x: Double, override val y:Double, override val z:Double, var offset: Double = 0, var globalOffset: Double = 0) extends Point(name,  x, y, z)
object SegmentPoint{
  def apply(name:String, x:Double,y:Double,z:Double,offset:Double = 0, globalOffset:Double = 0): SegmentPoint = new SegmentPoint(name,x,y,z,offset)
}
case class ReferencePoint(override val name: String, override val x: Double, override val y:Double, override val z:Double, override var offset: Double, override var globalOffset: Double) extends SegmentPoint(name,  x, y, z, offset)
