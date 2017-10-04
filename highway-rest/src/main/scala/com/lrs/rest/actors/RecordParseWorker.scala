package com.lrs.rest.actors

import akka.actor.{Actor, ActorLogging, Props, Stash}
import spray.json.JsObject

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by vagrant on 10/4/17.
  */
object RecordParseWorker{
  def props(): Props = Props(new RecordProcessWorker)

}

class RecordParseWorker  extends Actor with ActorLogging with Stash{
  implicit val system = context.system


  def receive = {
    case record : JsObject => {

    }
    case _ =>  throw new Exception("not implemented yet")
  }
}
