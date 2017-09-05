package com.ddp.lrs.models

import com.ddp.lrs.logging.Logging
import com.ddp.lrs.utils.Testing

/**
  * Created by vagrant on 8/29/17.
  */
class TestAddSegment extends Testing with Logging{

  val TEST_ROAD = Road("Test", "E", null)
  val RP1 = ReferencePoint("RP1", 1.3, 2.0)
  val RP2 = ReferencePoint("RP2", 3.3, 2.5)
  val RP3 = ReferencePoint("RP3", 5.8, 0)

  val RP4 = ReferencePoint("RP4", 11.5 , 2.2)
  val RP5 = ReferencePoint("RP5", 13.7, 2.3)
  val RP6 = ReferencePoint("RP6", 16.0, 0)

  val start = SegmentPoint("start", RP1, -1.3)
  val end = SegmentPoint("end", RP3, 3.6)

  val start1 = SegmentPoint("start", RP4, -2.1)
  val end1 = SegmentPoint("end", RP6, 2.7)


  val segment_1 = Segment(start, end, length = 9.4)
  val segment_2 = Segment(start1,end1, length = 9.3)

  val direction_1 = Direction("E", null, null)
  //direction_1.addSegment( segment_1,   )
  override def beforeEach(): Unit = {
  }

  it("should add initial segment properly"){
      val direction1 = direction_1.addSegment(segment_1, List(RP1,RP2,RP3), None, false, None, false)
      val TEST_DIRECTION1 = Direction("E", List(segment_1), List(RP1,RP2,RP3))
      direction1 shouldBe TEST_DIRECTION1
  }

  it("should insert segment String properly"){
    val direction1 = direction_1.addSegmentString("1.3,RP1,2.0,RP2,2.5,RP3,3.6", None,false, None,false)
    val TEST_DIRECTION1 = Direction("E", List(segment_1), List(RP1,RP2,RP3))
      direction1 shouldEqual TEST_DIRECTION1
  }

  it("should insert segment String afterwards"){
    val direction1 = direction_1.addSegmentString("1.3,RP1,2.0,RP2,2.5,RP3,3.6", None,false, None,false)
    val direction2 = direction1.addSegmentString("2.1,RP4,2.2,RP5,2.3,RP6,2.7", Some(RP3), false, None, false)
    val TEST_DIRECTION1 = Direction("E", List(segment_1, segment_2), List(RP1,RP2,RP3,RP4,RP5,RP6))
    direction2 shouldEqual TEST_DIRECTION1
  }

}
