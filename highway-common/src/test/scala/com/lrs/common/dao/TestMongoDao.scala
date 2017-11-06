package com.lrs.common.dao

import com.lrs.common.logging.Logging
import com.lrs.common.models.Road
import com.lrs.common.utils.Testing

import scala.concurrent.Await
import scala.concurrent.duration.Duration
/**
  * Created by eguo on 11/3/17.
  */
class TestMongoDao extends Testing with Logging{

  it("should add record properly") {
      val TEST_ROAD = Road("Test", 11, "E")
      //MongoDao.createRoad(TEST_ROAD)
    //  val road = MongoDao.findRoadById(11)
    val roadFuture = MongoDao.findPersonByAge(11)

      val road = Await.result(roadFuture, Duration.Inf)
      road shouldEqual List(TEST_ROAD)
  }
}
