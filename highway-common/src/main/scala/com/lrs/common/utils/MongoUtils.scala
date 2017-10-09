package com.lrs.common.utils
import com.lrs.common.models._
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model.Filters._
import spray.json._

import scala.concurrent.{Await, ExecutionContext, Future}
import Implicits._

import scala.concurrent.duration.Duration

/**
  * Created by eguo on 9/21/17.
  */
object  MongoUtils {
  val mongoClient: MongoClient = MongoClient("mongodb://localhost")
  val database: MongoDatabase = mongoClient.getDatabase("road")
  val collectionRoadRecordTable: MongoCollection[Document] = database.getCollection("RoadRecordTable")
  val collectionRoadTable: MongoCollection[Document] = database.getCollection("RoadTable")

  def addHighwayRecord(record: JsObject) = {
    val doc : Document = Document.apply(record.toString)
    collectionRoadRecordTable.insertOne(doc).toFuture
  }


  def getHighwayRecords(roadId : Long)(implicit ec: ExecutionContext) = {
    collectionRoadRecordTable.find(equal("roadId", roadId)).toFuture()
      //.sort(exists("dateTime")).sort(descending("dateTime")).toFuture
  }

  def addRoad(road: Road) = {
    collectionRoadTable.insertOne(road).toFuture
  }

  def getRoad(roadId: Long) = {
    val road = collectionRoadTable.find(equal("roadId", roadId)).first().map(docToRoad).toFuture
    Await.result(road, Duration.Inf).asInstanceOf[List[Document]]
  }

  def updateRoad(road: Road) = {
    collectionRoadTable.findOneAndReplace(equal("roadId", road.roadId), road).toFuture
  }

  def getAllHighways = {
    collectionRoadRecordTable.find().projection(fields(include("roadName","roadId"), excludeId())).map(_.toJson).toFuture
  }

}
