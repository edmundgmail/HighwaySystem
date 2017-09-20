package com.lrs.rest.models

import com.lrs.common.logging.Logging
import com.lrs.common.models.PointRecord
import com.lrs.common.utils.Testing
import spray.json.JsonParser

/**
  * Created by eguo on 9/20/17.
  */
class TestConversions extends Testing with Logging{
  it("implicit conversion of PointRecord should work") {
    import com.lrs.rest.models.marshalling.CustomMarshallers._
    val json = """{"rpName": "rp1" , "offset" : 1.2 } """
    val point = PointRecord("rp1", 1.2)
    JsonParser(json).convertTo[PointRecord] shouldBe point
  }
}
