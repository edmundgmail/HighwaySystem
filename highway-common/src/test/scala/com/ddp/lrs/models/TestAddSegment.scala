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
  val start = SegmentPoint("start", RP1, -1.3)
  val end = SegmentPoint("end", RP3, 3.6)

  val segment_1 = Segment(start, end, length = 9.4)
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

}
