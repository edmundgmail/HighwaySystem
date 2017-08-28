package algorithms

import com.ddp.lrs.models.{ReferencePoint, Segment, SegmentPoint}

/**
  * Created by eguo on 8/26/17.
  */
trait SegmentOperations {
    def removeSegment(cuttee:Segment, cutter:Segment) : List[Segment]
    def overlap(a:Segment, b: Segment): Boolean
    def include(cuttee:Segment, cutter:Segment) : Boolean
    def addSegment(segments: List[Segment], segment:Segment, afterRP: ReferencePoint) : List[Segment]
    def cutSegment(segment:Segment, point:SegmentPoint):List[Segment]
    def connected(a: Segment, b:Segment) : Boolean
    def mergeConnectedSegments(segments:List[Segment]):List[Segment]
}

