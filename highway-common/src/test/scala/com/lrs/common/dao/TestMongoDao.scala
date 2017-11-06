package com.lrs.common.dao

import com.lrs.common.logging.Logging
import com.lrs.common.models.Road
import com.lrs.common.utils.Testing
/**
  * Created by eguo on 11/3/17.
  */
class TestMongoDao extends Testing with Logging{

  it("should add record properly") {
      val TEST_ROAD = Road("Test", 11, "E")
      MongoDao.createRoad(TEST_ROAD)
      val road = MongoDao.findRoadById(1)

      road.onSuccess(
        ret => ret shouldEqual TEST_ROAD
      )
  }
}
