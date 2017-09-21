package com.lrs.rest.actors

import akka.actor.{Actor, ActorLogging, Props, Stash}
import akka.pattern.pipe
import com.lrs.common.models.AddRoadRecord
import com.lrs.rest.actors.HighwayWorker.{AddHighway, GetHighway}
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}
import scala.concurrent.ExecutionContext.Implicits.global
import org.mongodb.scala.model.Projections._
import spray.json._
import com.lrs.rest.models.marshalling.CustomMarshallers._
/**
  * Created by vagrant on 9/11/17.
  */

object HighwayWorker {

  def props(): Props = Props(new HighwayWorker)

  case class GetHighway()
  case class GetHighwayDetails(name: String, id: String)
  case class AddHighway(record: AddRoadRecord)

  val mongoClient: MongoClient = MongoClient("mongodb://localhost")
  val database: MongoDatabase = mongoClient.getDatabase("road")
  val collectionAddRoadRecord: MongoCollection[Document] = database.getCollection("AddRoadRecord");
}


class HighwayWorker extends Actor with ActorLogging with Stash{
  implicit val system = context.system

  def receive = {

    case GetHighway =>
      getAllHighways pipeTo sender()

    case AddHighway(r) =>
      addHighwayRecord(r) pipeTo sender()
  }

  private def addHighwayRecord(record: AddRoadRecord) = {
     val json = record.toJson.toString()
     val doc : Document = Document.apply(json)
     HighwayWorker.collectionAddRoadRecord.insertOne(doc).toFuture
  }

  private def getAllHighways = {
    HighwayWorker.collectionAddRoadRecord.find().projection(fields(include("roadName","roadId"), excludeId())).map(_.toJson).toFuture()
  }

  private def getHighwayDetails() = {

  }
}
