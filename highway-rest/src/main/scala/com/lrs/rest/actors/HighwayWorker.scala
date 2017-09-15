package com.lrs.rest.actors

import akka.actor.{Actor, ActorLogging, Props, Stash}
import akka.pattern.pipe
import com.lrs.common.models.AddRoadRecord
import com.lrs.rest.actors.HighwayWorker.{AddHighway, GetHighway}
import org.mongodb.scala.{Document, MongoClient}
import org.mongodb.scala.model.Projections._

import scala.concurrent.ExecutionContext.Implicits.global


import org.mongodb.scala._
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Sorts._
import org.mongodb.scala.model.Updates._

import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromRegistries, fromProviders}



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

  val codecRegistry = fromRegistries(fromProviders(classOf[AddRoadRecord]), DEFAULT_CODEC_REGISTRY )


  val mongoClient = MongoClient("mongodb://localhost")
  val database = mongoClient.getDatabase("road").withCodecRegistry(codecRegistry)
  val roadTable  = database.getCollection("Road")
  val addRoadRecordTable : MongoCollection[AddRoadRecord] = database.getCollection("AddRoadRecordTable")

  def receive = {

    case GetHighway =>
      getAllHighways pipeTo sender()

    case AddHighway(r) =>
      addHighwayRecord(r) pipeTo sender()
  }

  private def addHighwayRecord(record: AddRoadRecord) = {
    addRoadRecordTable.insertOne(record).toFuture()
  }

  private def getAllHighways = {
    roadTable.find().projection(fields(include("roadName","roadId"), excludeId())).map(_.toJson).toFuture()
  }

  private def getHighwayDetails() = {

  }
}
