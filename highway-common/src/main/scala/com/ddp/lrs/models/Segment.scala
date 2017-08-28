package com.ddp.lrs.models

import algorithms.SegmentOperations

/**
  * Created by eguo on 8/26/17.
  */
class Segment(val start: SegmentPoint, val end : SegmentPoint, val length:Double){
  def StartSegmentPoint = start
  def EndSegmentPoint = SegmentPoint(end.name,end.x,end.y,end.z, length)
  def containsRP(rp:ReferencePoint) : Boolean = ???
}

object Segment extends SegmentOperations{

  def apply(start:SegmentPoint, end:SegmentPoint, length: Double) = new Segment(start,end,length)

  def removeSegment(cuttee:Segment, cutter:Segment) : List[Segment] = ???
  def overlap(a:Segment, b: Segment): Boolean = ???
  def include(a:Segment, b:Segment) : Boolean = ???
  def addSegment(segments: List[Segment], segment:Segment, afterRP: ReferencePoint) : List[Segment] = ???
  def cutSegment(segment:Segment, point:SegmentPoint):List[Segment] = ???
  def connected(a: Segment, b:Segment) : Boolean = ???
  def mergeConnectedSegments(segments:List[Segment]):List[Segment] = ???
}