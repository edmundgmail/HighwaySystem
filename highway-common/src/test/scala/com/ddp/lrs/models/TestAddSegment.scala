package com.ddp.lrs.models

import com.ddp.lrs.logging.Logging
import com.ddp.lrs.utils.Testing

/**
  * Created by vagrant on 8/29/17.
  */
class TestAddSegment extends Testing with Logging{

  val TEST_ROAD = Road("Test", "E", null)

  val direction_1 = Direction("E", null, null)
  //direction_1.addSegment( segment_1,   )
  override def beforeEach(): Unit = {
  }

  it("should add initial segment properly"){
    val RP1 = ReferencePoint("RP1", TEST_ROAD.name, direction_1.dir,1.3, 2.0)
    val RP2 = ReferencePoint("RP2",  TEST_ROAD.name, direction_1.dir,3.3, 2.5)
    val RP3 = ReferencePoint("RP3", TEST_ROAD.name, direction_1.dir, 5.8, 0)
    val start = SegmentPoint("start", RP1.ID, -1.3)
    val end = SegmentPoint("end", RP3.ID, 3.6)

    val segment_1 = Segment(start, end, length = 9.4)

    val direction1 = direction_1.addSegment(segment_1, List(RP1,RP2,RP3), None, false, None, false)
      val TEST_DIRECTION1 = Direction("E", List(segment_1), List(RP1,RP2,RP3))
      direction1 shouldBe TEST_DIRECTION1
  }

  it("should insert segment String properly"){
    val RP1 = ReferencePoint("RP1",  TEST_ROAD.name, direction_1.dir,1.3, 2.0)
    val RP2 = ReferencePoint("RP2", TEST_ROAD.name, direction_1.dir, 3.3, 2.5)
    val RP3 = ReferencePoint("RP3", TEST_ROAD.name, direction_1.dir, 5.8, 0)
    val start = SegmentPoint("start", RP1.ID, -1.3)
    val end = SegmentPoint("end", RP3.ID, 3.6)

    val segment_1 = Segment(start, end, length = 9.4)
    val direction1 = direction_1.addSegmentString(TEST_ROAD.name,"1.3,RP1,2.0,RP2,2.5,RP3,3.6", None,false, None,false)
    val TEST_DIRECTION1 = Direction("E", List(segment_1), List(RP1,RP2,RP3))
      direction1 shouldEqual TEST_DIRECTION1
  }

  it("should insert segment String afterwards and not connected"){
    val RP1 = ReferencePoint("RP1", TEST_ROAD.name, direction_1.dir, 1.3, 2.0)
    val RP2 = ReferencePoint("RP2", TEST_ROAD.name, direction_1.dir, 3.3, 2.5)
    val RP3 = ReferencePoint("RP3", TEST_ROAD.name, direction_1.dir, 5.8, 0)

    val RP4 = ReferencePoint("RP4", TEST_ROAD.name, direction_1.dir, 11.5 , 2.2)
    val RP5 = ReferencePoint("RP5", TEST_ROAD.name, direction_1.dir, 13.7, 2.3)
    val RP6 = ReferencePoint("RP6",  TEST_ROAD.name, direction_1.dir,16.0, 0)

    val start = SegmentPoint("start", RP1.ID, -1.3)
    val end = SegmentPoint("end", RP3.ID, 3.6)


    val segment_1 = Segment(start, end, length = 9.4)
    val start1 = SegmentPoint("start", RP4.ID, -2.1)
    val end1 = SegmentPoint("end", RP6.ID, 2.7)
    val segment_2 = Segment(start1,end1, length = 9.3)

    val direction1 = direction_1.addSegmentString(TEST_ROAD.name,"1.3,RP1,2.0,RP2,2.5,RP3,3.6", None,false, None,false)
    val direction2 = direction1.addSegmentString(TEST_ROAD.name,"2.1,RP4,2.2,RP5,2.3,RP6,2.7", Some(RP3), false, None, false)
    val TEST_DIRECTION1 = Direction("E", List(segment_1, segment_2), List(RP1,RP2,RP3,RP4,RP5,RP6))
    direction2 shouldEqual TEST_DIRECTION1
  }

  it("should insert segment String afterwards and connected"){
    val RP1 = ReferencePoint("RP1", TEST_ROAD.name, direction_1.dir, 1.3, 2.0)
    val RP2 = ReferencePoint("RP2",  TEST_ROAD.name, direction_1.dir,3.3, 2.5)
    val RP3 = ReferencePoint("RP3", TEST_ROAD.name, direction_1.dir, 5.8, 0)
    val RP4 = ReferencePoint("RP4", TEST_ROAD.name, direction_1.dir, 11.5 , 2.2)
    val RP5 = ReferencePoint("RP5", TEST_ROAD.name, direction_1.dir, 13.7, 2.3)
    val RP6 = ReferencePoint("RP6", TEST_ROAD.name, direction_1.dir, 16.0, 0)

    val RP3C = ReferencePoint("RP3", TEST_ROAD.name, direction_1.dir, 5.8, 5.7)
    val RP6C = ReferencePoint("RP6", TEST_ROAD.name, direction_1.dir, 21.7, 0)
    val start = SegmentPoint("start", RP1.ID, -1.3)
    val end1 = SegmentPoint("end", RP6.ID, 2.7)
    val segment_1C = Segment(start,end1, length = 18.7)


    val direction1 = direction_1.addSegmentString(TEST_ROAD.name,"1.3,RP1,2.0,RP2,2.5,RP3,3.6", None,false, None,false)
    val direction2 = direction1.addSegmentString(TEST_ROAD.name,"2.1,RP4,2.2,RP5,2.3,RP6,2.7", Some(RP3), true, None, false)
    val TEST_DIRECTION1 = Direction("E", List(segment_1C), List(RP1,RP2,RP3C,RP4,RP5,RP6))
    direction2 shouldEqual TEST_DIRECTION1
  }

  it("should insert segment String inbetween and connected"){
    val RP1 = ReferencePoint("RP1", TEST_ROAD.name, direction_1.dir, 1.3, 2.0)
    val RP2 = ReferencePoint("RP2", TEST_ROAD.name, direction_1.dir, 3.3, 2.5)
    val RP3 = ReferencePoint("RP3", TEST_ROAD.name, direction_1.dir, 5.8, 0)
    val RP4 = ReferencePoint("RP4", TEST_ROAD.name, direction_1.dir, 11.5 , 2.2)
    val RP5 = ReferencePoint("RP5", TEST_ROAD.name, direction_1.dir, 13.7, 2.3)
    val RP6 = ReferencePoint("RP6", TEST_ROAD.name, direction_1.dir, 16.0, 0)


    val direction1 = direction_1.addSegmentString(TEST_ROAD.name,"1.3,RP1,2.0,RP2,2.5,RP3,3.6", None,false, None,false)
    val direction2 = direction1.addSegmentString(TEST_ROAD.name,"2.1,RP4,2.2,RP5,2.3,RP6,2.7", Some(RP3), false, None, false)
    val direction3 = direction2.addSegmentString(TEST_ROAD.name,"1.2,RP7,2.1,RP8,1.5,RP9,0.9", Some(RP3), true, Some(RP4), true)

    val RP7 = ReferencePoint("RP7", TEST_ROAD.name, direction_1.dir, 10.6, 2.1)
    val RP8 = ReferencePoint("RP8", TEST_ROAD.name, direction_1.dir, 12.7, 1.5)
    val RP9 = ReferencePoint("RP9", TEST_ROAD.name, direction_1.dir, 14.2, 3.0)

    val RP4C = ReferencePoint("RP4", TEST_ROAD.name, direction_1.dir, 17.2 , 2.2)
    val RP5C = ReferencePoint("RP5", TEST_ROAD.name, direction_1.dir, 19.4, 2.3)
    val RP6C = ReferencePoint("RP6", TEST_ROAD.name, direction_1.dir, 21.7, 0)
    val start = SegmentPoint("start", RP1.ID, -1.3)
    val end = SegmentPoint("end", RP3.ID, 3.6)
    val end1C = SegmentPoint("end", RP6C.ID, 2.7)
    val segment_1C = Segment(start,end1C, length = 18.7)
    val RP3C = ReferencePoint("RP3", TEST_ROAD.name, direction_1.dir, 5.8, 4.8)

    val segment_1D = Segment(start,end1C, length = 24.4)
    val TEST_DIRECTION1 = Direction("E", List(segment_1D), List(RP1,RP2,RP3C,RP7,RP8,RP9, RP4C,RP5C,RP6C))
    direction3 shouldEqual TEST_DIRECTION1
  }


  it("should insert segment String inbetween and left connected"){
    val RP1 = ReferencePoint("RP1", TEST_ROAD.name, direction_1.dir, 1.3, 2.0)
    val RP2 = ReferencePoint("RP2", TEST_ROAD.name, direction_1.dir, 3.3, 2.5)
    val RP3 = ReferencePoint("RP3", TEST_ROAD.name, direction_1.dir, 5.8, 0)

    val RP4 = ReferencePoint("RP4", TEST_ROAD.name, direction_1.dir, 11.5 , 2.2)
    val RP5 = ReferencePoint("RP5", TEST_ROAD.name, direction_1.dir, 13.7, 2.3)
    val RP6 = ReferencePoint("RP6", TEST_ROAD.name, direction_1.dir, 16.0, 0)


    val direction1 = direction_1.addSegmentString(TEST_ROAD.name,"1.3,RP1,2.0,RP2,2.5,RP3,3.6", None,false, None,false)
    val direction2 = direction1.addSegmentString(TEST_ROAD.name,"2.1,RP4,2.2,RP5,2.3,RP6,2.7", Some(RP3), false, None, false)
    val RP3C = ReferencePoint("RP3", TEST_ROAD.name, direction_1.dir, 5.8, 4.8)

    val direction3 = direction2.addSegmentString(TEST_ROAD.name,"1.2,RP7,2.1,RP8,1.5,RP9,0.9", Some(RP3), true, Some(RP4), false)

    val RP7 = ReferencePoint("RP7", TEST_ROAD.name, direction_1.dir, 10.6, 2.1)
    val RP8 = ReferencePoint("RP8", TEST_ROAD.name, direction_1.dir, 12.7, 1.5)
    val RP9 = ReferencePoint("RP9", TEST_ROAD.name, direction_1.dir, 14.2, 0.0)

    val RP4C = ReferencePoint("RP4", TEST_ROAD.name, direction_1.dir, 17.2 , 2.2)
    val RP5C = ReferencePoint("RP5", TEST_ROAD.name, direction_1.dir, 19.4, 2.3)
    val RP6C = ReferencePoint("RP6", TEST_ROAD.name, direction_1.dir, 21.7, 0)
    val start = SegmentPoint("start", RP1.ID, -1.3)
    val end = SegmentPoint("end", RP3.ID, 3.6)

    val end1D = SegmentPoint("end", RP9.ID, 0.9)
    val segment_1D = Segment(start,end1D, length = 15.1)

    val start1 = SegmentPoint("start", RP4C.ID, -2.1)
    val end1 = SegmentPoint("end", RP6C.ID, 2.7)
    val segment_2 = Segment(start1,end1, length = 9.3)

    val TEST_DIRECTION1 = Direction("E", List(segment_1D,segment_2), List(RP1,RP2,RP3C,RP7,RP8,RP9, RP4C,RP5C,RP6C))
    direction3 shouldEqual TEST_DIRECTION1
  }

  it("should insert segment String inbetween and right connected"){
    val RP1 = ReferencePoint("RP1", TEST_ROAD.name, direction_1.dir, 1.3, 2.0)
    val RP2 = ReferencePoint("RP2", TEST_ROAD.name, direction_1.dir, 3.3, 2.5)
    val RP3 = ReferencePoint("RP3", TEST_ROAD.name, direction_1.dir, 5.8, 0)

    val RP4 = ReferencePoint("RP4", TEST_ROAD.name, direction_1.dir, 11.5 , 2.2)
    val RP5 = ReferencePoint("RP5", TEST_ROAD.name, direction_1.dir, 13.7, 2.3)
    val RP6 = ReferencePoint("RP6", TEST_ROAD.name, direction_1.dir, 16.0, 0)


    val direction1 = direction_1.addSegmentString(TEST_ROAD.name,"1.3,RP1,2.0,RP2,2.5,RP3,3.6", None,false, None,false)
    val direction2 = direction1.addSegmentString(TEST_ROAD.name,"2.1,RP4,2.2,RP5,2.3,RP6,2.7", Some(RP3), false, None, false)
    val direction3 = direction2.addSegmentString(TEST_ROAD.name,"1.2,RP7,2.1,RP8,1.5,RP9,0.9", Some(RP3), false, Some(RP4), true)

    val TEST_DIRECTION1 = Direction.fromString(TEST_ROAD.name, direction_1.dir, List("1.3,RP1,2.0,RP2,2.5,RP3,3.6",
        "1.2,RP7,2.1,RP8,1.5,RP9, 3.0,RP4,2.2,RP5,2.3,RP6,2.7"))
    direction3 shouldEqual TEST_DIRECTION1
  }

  it("should remove segment"){
    val RP1 = ReferencePoint("RP1", TEST_ROAD.name, direction_1.dir, 1.3, 2.0)
    val RP2 = ReferencePoint("RP2", TEST_ROAD.name, direction_1.dir, 3.3, 2.5)
    val RP3 = ReferencePoint("RP3", TEST_ROAD.name, direction_1.dir, 5.8, 0)

    val RP4 = ReferencePoint("RP4", TEST_ROAD.name, direction_1.dir, 11.5 , 2.2)
    val RP5 = ReferencePoint("RP5", TEST_ROAD.name, direction_1.dir, 13.7, 2.3)
    val RP6 = ReferencePoint("RP6", TEST_ROAD.name, direction_1.dir, 16.0, 0)

    val RP8 = ReferencePoint("RP8", TEST_ROAD.name, direction_1.dir, 16.0, 0)

    val direction1 = direction_1.addSegmentString(TEST_ROAD.name,"1.3,RP1,2.0,RP2,2.5,RP3,3.6", None,false, None,false)
    val direction2 = direction1.addSegmentString(TEST_ROAD.name,"2.1,RP4,2.2,RP5,2.3,RP6,2.7", Some(RP3), false, None, false)
    val direction3 = direction2.addSegmentString(TEST_ROAD.name,"1.2,RP7,2.1,RP8,1.5,RP9,0.9", Some(RP3), false, Some(RP4), true)

    val start = SegmentPoint("start", RP8.ID, 1.0)
    val end = SegmentPoint("end", RP5.ID, 1.0)
    val direction4 = direction3.removeSegment(Segment(start, end, 6.7))

    val TEST_DIRECTION1 = Direction.fromString(TEST_ROAD.name, direction_1.dir, List("1.3,RP1,2.0,RP2,2.5,RP3,3.6",
      "1.2,RP7,2.1,RP8,1.0", "1.3,RP6,2.7"))
    direction4 shouldEqual TEST_DIRECTION1
  }

}
