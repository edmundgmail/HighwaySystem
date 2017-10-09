package com.lrs.rest.actors

import akka.actor.{Actor, Status}

/**
  * Created by eguo on 10/8/17.
  */
trait FailurePropatingActor extends Actor{
  override def preRestart(reason:Throwable, message:Option[Any]){
    super.preRestart(reason, message)
    sender() ! Status.Failure(reason)
  }
}