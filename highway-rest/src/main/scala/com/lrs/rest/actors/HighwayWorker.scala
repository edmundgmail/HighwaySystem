package com.lrs.rest.actors

import akka.actor.{Actor, ActorLogging, Props, Stash}
import akka.pattern.pipe
import com.lrs.common.models.AddRoadRecord
import com.lrs.rest.actors.HighwayWorker.{AddHighway, GetHighway}
import org.mongodb.scala.{Document, MongoClient}
import org.mongodb.scala.model.Projections._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future



/**
  * Created by vagrant on 9/11/17.
  */

object HighwayWorker {

  def props(): Props = Props(new HighwayWorker)

  case class GetHighway()
  case class GetHighwayDetails(name: String, id: String)
  case class AddHighway(record: AddRoadRecord)

  // To directly connect to the default server localhost on port 27017

  // Use a Connection String

}

class HighwayWorker extends Actor with ActorLogging with Stash{
  implicit val system = context.system

  //val codecRegistry = fromRegistries(fromProviders(classOf[AddRoadRecord], classOf[DirectionRecord]), DEFAULT_CODEC_REGISTRY )

  def receive = {

    case GetHighway =>
      getAllHighways pipeTo sender()

    case AddHighway(r) =>
      addHighwayRecord(r) pipeTo sender()
  }

  private def addHighwayRecord(record: AddRoadRecord) = {
    Future{

    }
  }

  private def getAllHighways = {
    //roadTable.find().projection(fields(include("roadName","roadId"), excludeId())).map(_.toJson).toFuture()
    Future{

    }
  }

  private def getHighwayDetails() = {

  }
}
