package com.lrs.common.utils
import com.lrs.common.models.{AddRoadRecord, DataRecord}
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Sorts._
import spray.json._

import scala.concurrent.ExecutionContext

/**
  * Created by eguo on 9/21/17.
  */
object  MongoUtils {

  val mongoClient: MongoClient = MongoClient("mongodb://localhost")
  val database: MongoDatabase = mongoClient.getDatabase("road")
  val collectionRoadRecord: MongoCollection[Document] = database.getCollection("RoadRecordTable")

  def addHighwayRecord(record: JsObject) = {
    val doc : Document = Document.apply(record.toString)
    collectionRoadRecord.insertOne(doc).toFuture
  }


  def getHighwayRecords(roadID : Integer)(implicit ec: ExecutionContext) = {
    collectionRoadRecord.find().toFuture()
      //.sort(exists("dateTime")).sort(descending("dateTime")).toFuture

  }

  def getAllHighways = {
    collectionRoadRecord.find().projection(fields(include("roadName","roadId"), excludeId())).map(_.toJson).toFuture
  }

}
