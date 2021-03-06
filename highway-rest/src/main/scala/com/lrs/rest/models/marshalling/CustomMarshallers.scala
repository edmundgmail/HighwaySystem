package com.lrs.rest.models.marshalling

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.lrs.common.models.errors.HighwayStatus
import com.lrs.common.models.{AddRoadRecord, DirectionRecord, PointRecord, SegmentRecord}
import spray.json.DefaultJsonProtocol


/***
  * Akka HTTP default Json Support is done using spray-json.
  *
  * http://doc.akka.io/docs/akka/current/scala/http/common/json-support.html
  * https://github.com/spray/spray-json
  */

object CustomMarshallers extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val pointRecordFormats = jsonFormat2(PointRecord)
  implicit val segmentRecordFormats  = jsonFormat2(SegmentRecord)
  implicit val directionRecordFormats = jsonFormat2(DirectionRecord)
  implicit val addRoadRecordFormats =  jsonFormat18(AddRoadRecord)
  implicit val statusCodeFormats = jsonFormat3(HighwayStatus.TypeVal)

}
