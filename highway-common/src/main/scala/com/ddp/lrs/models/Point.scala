package com.ddp.lrs.models
import com.ddp.lrs.utils.MyImplicits._

import scala.util.MurmurHash
import scala.util.hashing.MurmurHash3
/**
  * Created by eguo on 8/26/17.
  */
class Point (val name: String, val x: Double, val y:Double, val z:Double){
  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case that:Point => that.name == this.name && that.x =~= this.x && that.y =~= this.y && that.z =~= this.z
      case _=> false
    }
  }
}

case class SegmentPoint(override val name: String, referencePoint: Int, offset: Double, override val x: Double = 0, override val y:Double = 0, override val z:Double = 0) extends Point(name,x,y,z){
  override def toString: String = {
    s"{ SegmentPoint RP = ${referencePoint} offset=${offset}"
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case that:SegmentPoint => this.referencePoint == that.referencePoint && that.offset =~= this.offset
      case _ => false
    }
  }
}

case class ReferencePoint(override val name: String, val roadName:String, val dir:String, val globalOffset: Double, val distance: Double, override val x: Double = 0, override val y:Double = 0, override val z:Double = 0) extends Point(name, x, y, z)
{
  def withIncrementOffset(offset:Double) = ReferencePoint(name, roadName, dir, globalOffset+offset, distance, x,y,z)
  def withDistance(d:Double) = ReferencePoint(name, roadName, dir, globalOffset, d, x,y,z)
  def ID =MurmurHash3.stringHash((roadName, dir, name).toString)

  override def toString: String = {
    s"{RP name=${name} distance=${distance} globaloffset=${globalOffset}}"
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case that:ReferencePoint => super.equals(that) && that.globalOffset=~=this.globalOffset && that.distance=~=this.distance
      case  _=> false
    }
  }

  def same(that:ReferencePoint) : Boolean = this.ID == that.ID
}

object ReferencePoint{
    def findIndex(rps:List[ReferencePoint], rp:ReferencePoint) : Int= {
      val f = rps.zipWithIndex.filter(r=>rp.same(r._1))
      if(f.isEmpty) -1
      else f(0)._2
    }

   def getByID(ID:Int, rps:List[ReferencePoint]) : Option[ReferencePoint] = {
      val rpIDs = rps.filter(_.ID==ID)
      if(rpIDs.isEmpty) None
      else Some(rpIDs(0))
   }
}



