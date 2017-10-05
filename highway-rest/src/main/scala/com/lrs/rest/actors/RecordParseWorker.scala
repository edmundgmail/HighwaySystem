package com.lrs.rest.actors

import akka.actor.{Actor, ActorLogging, Props, Stash}
import com.google.gson.GsonBuilder
import com.lrs.common.models.{DataRecord, DataRecordDeserializer}
import com.lrs.rest.AkkaHttpScalaDockerSeed.system
import spray.json.JsObject
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Created by vagrant on 10/4/17.
  */
object RecordParseWorker{
  def props(): Props = Props(new RecordParseWorker)

}

class RecordParseWorker  extends Actor with ActorLogging with Stash{
  implicit val system = context.system
  implicit val timeout = Timeout(10.seconds)

  val recordProcessWorker = system.actorOf(RecordProcessWorker.props)

  val gsonBuilder = new GsonBuilder
  gsonBuilder.registerTypeAdapter(classOf[DataRecord], DataRecordDeserializer.getInstance)

  val gson = gsonBuilder.create()

  def receive = {
    case record : JsObject => {
      try{
        val dr = gson.fromJson(record.toString, classOf[DataRecord])
        recordProcessWorker ! dr
      }
      catch {
        case _=> throw new Exception(s"can't parse the json string = ${record.toString}")
      }


    }
    case _ =>  throw new Exception("not implemented yet")
  }
}
