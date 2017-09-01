package com.ddp.lrs.models

/**
  * Created by eguo on 8/26/17.
  */
class Point (val name: String, val x: Double, val y:Double, val z:Double){
  override def equals(obj: scala.Any): Boolean = {
    try{
      val o = obj.asInstanceOf[Point]
      this.name == o.name && this.x == o.x && this.y == o.y && this.z == o.z
    }
    catch {
      case _=> false
    }
  }
}

