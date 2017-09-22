package com.lrs.common.utils
import com.lrs.common.models.{AddRoadRecord, DataRecord}
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}
import org.mongodb.scala.model.Projections._
import spray.json._

/**
  * Created by eguo on 9/21/17.
  */
object  MongoUtils {

  val mongoClient: MongoClient = MongoClient("mongodb://localhost")
  val database: MongoDatabase = mongoClient.getDatabase("road")
  val collectionAddRoadRecord: MongoCollection[Document] = database.getCollection("AddRoadRecord")

  def addHighwayRecord(record: JsObject) = {
    val doc : Document = Document.apply(record.toString)
    collectionAddRoadRecord.insertOne(doc).toFuture
  }

  def getAllHighways = {
    collectionAddRoadRecord.find().projection(fields(include("roadName","roadId"), excludeId())).map(_.toJson).toFuture()
  }

}
