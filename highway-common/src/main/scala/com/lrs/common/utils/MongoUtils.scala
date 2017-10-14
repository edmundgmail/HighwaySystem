package com.lrs.common.utils
import com.lrs.common.models._
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}
import org.mongodb.scala.model.Aggregates._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model.Accumulators._
import spray.json._

import scala.concurrent.{Await, ExecutionContext, Future}
import Implicits._
import com.mongodb.client.model.BsonField
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.BsonField

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
    val road = collectionRoadRecordTable.insertOne(doc).toFuture
    Await.result(road, Duration.Inf)

  }


  def getHighwayRecords(roadId : Long)(implicit ec: ExecutionContext) = {
    collectionRoadRecordTable.find(equal("roadId", roadId)).toFuture()
      //.sort(exists("dateTime")).sort(descending("dateTime")).toFuture
  }

  def getAllHighways = {
    collectionRoadTable.find().projection(fields(include("roadName","roadId"), excludeId())).map(_.toJson).toFuture
  }

  def getHighwayRPs(roadId: Long, dir: String) = {
    collectionRoadTable.aggregate(
      List(`match`(equal("roadId",roadId)),
      unwind("$directions"),
      `match`(equal("directions.dir",dir)),
      group("_id", first("rps", "$directions.rps"))
      ,project(excludeId())))
      .map(_.toJson).toFuture
  }

  def getHighwaySegments(roadId: Long, dir: String) = {
    collectionRoadTable.aggregate(
      List(`match`(equal("roadId",roadId)),
        unwind("$directions"),
        `match`(equal("directions.dir",dir)),
        group("_id", first("segments", "$directions.segments"))
        ,project(excludeId())))
      .map(_.toJson).toFuture
  }

  def getRoad(roadId: Long) = {
    val road = collectionRoadTable.find().projection(excludeId()).first().toFuture
    Await.result(road, Duration.Inf).asInstanceOf[Document]
  }

  def updateRoad(road: Road) = {
    val newRoad = collectionRoadTable.findOneAndReplace(equal("roadId", road.roadId), road).toFuture
    Await.result(newRoad, Duration.Inf)
  }

  def addRoad(road: Road) = {
    val newRoad = collectionRoadTable.insertOne(road).toFuture
    Await.result(newRoad, Duration.Inf)
  }

}
