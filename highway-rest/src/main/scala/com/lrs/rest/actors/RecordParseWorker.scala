package com.lrs.rest.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Stash}
import com.google.gson.GsonBuilder
import com.lrs.common.models.{DataRecord, DataRecordDeserializer, Road}
import com.lrs.rest.AkkaHttpScalaDockerSeed.system
import spray.json.JsObject
import akka.pattern.pipe
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Created by vagrant on 10/4/17.
  */
object RecordParseWorker{
  def props(actor: ActorRef): Props = Props(new RecordParseWorker(actor))

}

class RecordParseWorker(recordProcessWorker: ActorRef)  extends Actor with ActorLogging with Stash{
  implicit val system = context.system
  implicit val timeout = Timeout(10.seconds)

  val gsonBuilder = new GsonBuilder
  gsonBuilder.registerTypeAdapter(classOf[DataRecord], DataRecordDeserializer.getInstance)

  val gson = gsonBuilder.create()

  def receive = {
    case record : JsObject => {
        val dr = gson.fromJson(record.toString, classOf[DataRecord])
        val result = (recordProcessWorker ? dr)
        sender() ! result
    }
    case _ =>  throw new Exception("not implemented yet")
  }
}
