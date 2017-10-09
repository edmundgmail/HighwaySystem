package com.lrs.rest.models

import com.lrs.common.models.Road

/**
  * Created by eguo on 10/7/17.
  */
object HighwayStatus extends Enumeration {
  val Ok = Value("ok", 0)
  val Warning = Value("warning", 1)
  val ErrorAddRoad = Value("Error in adding road", 2)
  val ErrorRemoveRoadSegment = Value("Error in removing road segment", 3)
  val ErrorAddRoadSegment = Value("Error in adding road segment", 4)
  val ErrorParseRoadJson = Value("Error in parsing json to Road", 5)

  case class TypeVal(val name: String, val code: Int) extends Val(nextId, name)
  protected final def Value(name: String, code: Int) = new TypeVal(name, code)
}
