package com.lrs.common.models.errors

/**
  * Created by eguo on 10/7/17.
  */
object HighwayStatus extends Enumeration {
  val Ok = TypeVal("ok", 0)
  val Warning = TypeVal("warning", 1)
  val ErrorAddRoad = TypeVal("Error in adding road", 2)
  val ErrorRemoveRoadSegment = TypeVal("Error in removing road segment", 3)
  val ErrorAddRoadSegment = TypeVal("Error in adding road segment", 4)
  val ErrorParseRoadJson = TypeVal("Error in parsing json to Road", 5)
  val ErrorPersistRoad = TypeVal("Error in persisting road", 6)
  val ErrorGetRoad = TypeVal("Error in getting road", 7)

  def CustomError(t : TypeVal, e: Throwable) = TypeVal(t.msg, t.code, Some(e.getMessage))

  case class TypeVal(val msg: String, val code: Int, val ex: Option[String] = None) extends Val(nextId, msg)
}
