package com.lrs.common.models

/**
  * Created by eguo on 8/26/17.
  */
class Road(val name:String, val mainDir: String, directions: List[Direction]) {
  def withUpdatedDirections(newDirections:List[Direction]) = new Road(name ,mainDir, newDirections)
}

object Road{
  def apply(name:String, mainDir: String, directions: List[Direction]): Road = new Road(name, mainDir, directions)

  def fromJson(record: AddRoadRecord) : Road = ???
}