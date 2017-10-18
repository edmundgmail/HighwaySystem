package com.lrs.common.models

import com.lrs.common.logging.Logging
import com.lrs.common.utils.Testing

/**
  * Created by vagrant on 10/16/17.
  */
class TestAddLane extends Testing with Logging{

  val TEST_ROAD = Road("Test", 1, "E", null)

  val direction_1 = Direction("E", null, null)
  //direction_1.addSegment( segment_1,   )

  it("should add lane properly") {
    val RP1 = ReferencePoint("RP1", TEST_ROAD.name, direction_1.dir, 1.3, 2.0)
    val RP2 = ReferencePoint("RP2", TEST_ROAD.name, direction_1.dir, 3.3, 2.5)
    val RP3 = ReferencePoint("RP3", TEST_ROAD.name, direction_1.dir, 5.8, 0)

    val RP4 = ReferencePoint("RP4", TEST_ROAD.name, direction_1.dir, 11.5 , 2.2)
    val RP5 = ReferencePoint("RP5", TEST_ROAD.name, direction_1.dir, 13.7, 2.3)
    val RP6 = ReferencePoint("RP6", TEST_ROAD.name, direction_1.dir, 16.0, 0)

    val RP8 = ReferencePoint("RP8", TEST_ROAD.name, direction_1.dir, 16.0, 0)

    val direction1 = direction_1.addSegmentString(TEST_ROAD.name,"1.3,RP1,2.0,RP2,2.5,RP3,3.6", None,false, None,false)
    val direction2 = direction1.addSegmentString(TEST_ROAD.name,"2.1,RP4,2.2,RP5,2.3,RP6,2.7", Some(RP3), false, None, false)

    val start = SegmentPoint("start", RP1.ID, -0.9)
    val end = SegmentPoint("end", RP3.ID, 3.0)
    val lane = Lane.fromString(direction2.rps,  "RP1,-0.1,RP3,0.5,3").get

    val direction3 = direction2.updateLane("RP1,-0.1,RP3,0.5,3")
    direction3.lanes shouldBe List(lane)
  }

  it("should add lane properly 2") {
    val RP1 = ReferencePoint("RP1", TEST_ROAD.name, direction_1.dir, 1.3, 2.0)
    val RP2 = ReferencePoint("RP2", TEST_ROAD.name, direction_1.dir, 3.3, 2.5)
    val RP3 = ReferencePoint("RP3", TEST_ROAD.name, direction_1.dir, 5.8, 0)

    val RP4 = ReferencePoint("RP4", TEST_ROAD.name, direction_1.dir, 11.5 , 2.2)
    val RP5 = ReferencePoint("RP5", TEST_ROAD.name, direction_1.dir, 13.7, 2.3)
    val RP6 = ReferencePoint("RP6", TEST_ROAD.name, direction_1.dir, 16.0, 0)

    val RP8 = ReferencePoint("RP8", TEST_ROAD.name, direction_1.dir, 16.0, 0)

    val direction1 = direction_1.addSegmentString(TEST_ROAD.name,"1.3,RP1,2.0,RP2,2.5,RP3,3.6", None,false, None,false)
    val direction2 = direction1.addSegmentString(TEST_ROAD.name,"2.1,RP4,2.2,RP5,2.3,RP6,2.7", Some(RP3), false, None, false)
    val direction3 = direction2.updateLane("RP1,-0.1,RP3,0.5,3")
    val direction4 = direction3.updateLane("RP1,0.2,RP2,0.3,2")
    val lanes = List(
      Lane.fromString(direction4.rps, "RP1,-0.1,RP1,0.2,[1 2 3]").get,
      Lane.fromString(direction4.rps, "RP1,0.2,RP2,0.3,[1 2 3 4 5]").get,
      Lane.fromString(direction4.rps, "RP2,0.3,RP3,0.5,[1 2 3]").get
    )

    direction4.lanes should contain theSameElementsAs  lanes
  }

  it("should remove lane properly 2") {
    val RP1 = ReferencePoint("RP1", TEST_ROAD.name, direction_1.dir, 1.3, 2.0)
    val RP2 = ReferencePoint("RP2", TEST_ROAD.name, direction_1.dir, 3.3, 2.5)
    val RP3 = ReferencePoint("RP3", TEST_ROAD.name, direction_1.dir, 5.8, 0)

    val RP4 = ReferencePoint("RP4", TEST_ROAD.name, direction_1.dir, 11.5 , 2.2)
    val RP5 = ReferencePoint("RP5", TEST_ROAD.name, direction_1.dir, 13.7, 2.3)
    val RP6 = ReferencePoint("RP6", TEST_ROAD.name, direction_1.dir, 16.0, 0)

    val RP8 = ReferencePoint("RP8", TEST_ROAD.name, direction_1.dir, 16.0, 0)

    val direction1 = direction_1.addSegmentString(TEST_ROAD.name,"1.3,RP1,2.0,RP2,2.5,RP3,3.6", None,false, None,false)
    val direction2 = direction1.addSegmentString(TEST_ROAD.name,"2.1,RP4,2.2,RP5,2.3,RP6,2.7", Some(RP3), false, None, false)
    val direction3 = direction2.updateLane("RP1,-0.1,RP3,0.5,3")
    val direction4 = direction3.updateLane("RP1,0.2,RP2,0.3,2")
    val direction5 = direction4.updateLane("RP2,0.2,RP3,0.3,-1")

    val lanes = List(
      Lane.fromString(direction4.rps, "RP1,-0.1,RP1,0.2,[1 2 3]").get,
      Lane.fromString(direction4.rps, "RP1,0.2,RP2,0.2,[1 2 3 4 5]").get,
      Lane.fromString(direction4.rps, "RP2,0.2,RP2,0.3,[1 2 3 4]").get,
      Lane.fromString(direction4.rps, "RP2,0.3,RP3,0.3,[1 2]").get,
      Lane.fromString(direction4.rps, "RP3,0.3,RP3,0.5,[1 2 3]").get
    )

    direction5.lanes should contain theSameElementsAs  lanes
  }
}
