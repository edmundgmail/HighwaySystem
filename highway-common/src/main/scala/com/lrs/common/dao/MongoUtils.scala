package com.lrs.common.dao

import com.lrs.common.models._
import org.mongodb.scala.model.Accumulators._
import org.mongodb.scala.model.Aggregates._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.FindOneAndReplaceOptions
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}
import spray.json._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}
import com.lrs.common.utils.Implicits._
/**
  * Created by eguo on 9/21/17.
  */
object  MongoUtils {
  val mongoClient: MongoClient = MongoClient("mongodb://localhost")
  val database: MongoDatabase = mongoClient.getDatabase("road")
  val collectionRoadRecordTable: MongoCollection[Document] = database.getCollection("RoadRecordTable")
  val collectionRoadTable: MongoCollection[Document] = database.getCollection("RoadTable")
  val collectionRoadFeaturesTable: MongoCollection[Document] = database.getCollection("RoadFeaturesTable")

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
    val road = collectionRoadTable.find(equal("roadId", roadId)).projection(excludeId()).first().toFuture
    Await.result(road, Duration.Inf).asInstanceOf[Document]
  }

  def updateRoad(road: Road) = {
    val newRoad = collectionRoadTable.findOneAndReplace(equal("roadId", road.roadId), road, new FindOneAndReplaceOptions().upsert(true)).toFuture
    Await.result(newRoad, Duration.Inf)
  }

  def addRoad(road: Road) = {
    val newRoad = collectionRoadTable.insertOne(road).toFuture
    Await.result(newRoad, Duration.Inf)
  }

  def updateRoadFeatures(roadFeatures: RoadFeatures) = {
    val newRoadFeatures = collectionRoadFeaturesTable.findOneAndReplace(and(equal("roadId", roadFeatures.roadId), equal("dir", roadFeatures.dir)), roadFeatures, new FindOneAndReplaceOptions().upsert(true)).toFuture
    Await.result(newRoadFeatures, Duration.Inf)
  }

  def getRoadFeatures(roadId: Long, dir: String) = {
    val road = collectionRoadFeaturesTable.find(and(equal("roadId",roadId), equal("dir", dir))).projection(excludeId()).first().toFuture
    Await.result(road, Duration.Inf).asInstanceOf[Document]
  }
}
