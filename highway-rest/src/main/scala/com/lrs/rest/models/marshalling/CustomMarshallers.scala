package com.lrs.rest.models.marshalling

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.lrs.rest.models.HighwayModel
import spray.json.DefaultJsonProtocol


/***
  * Akka HTTP default Json Support is done using spray-json.
  *
  * http://doc.akka.io/docs/akka/current/scala/http/common/json-support.html
  * https://github.com/spray/spray-json
  */
object CustomMarshallers extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val highwayModelFormats  = jsonFormat2(HighwayModel)

}
