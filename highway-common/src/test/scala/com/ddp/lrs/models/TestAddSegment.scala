package com.ddp.lrs.models

import com.ddp.lrs.logging.Logging
import com.ddp.lrs.utils.Testing

/**
  * Created by vagrant on 8/29/17.
  */
class TestAddSegment extends Testing with Logging{

  val TEST_ROAD = Road("Test", "E", null)
  val start = SegmentPoint("start1")
  val end = SegmentPoint("end1")
  val RP1 = ReferencePoint("RP1", 1.0, 2.0)
  val RP2 = ReferencePoint("RP2", 3.0, 2.5)
  val RP3 = ReferencePoint("RP3", 5.5, 0)
  val sgement_1 = Segment(start, end, length = 21)
  val direction_1 = Direction("E", null, null)
  direction_1.addSegment( segment_1,   )
  override def beforeEach(): Unit = {

  }

  it("should add initial segment properly"){

  }
}
