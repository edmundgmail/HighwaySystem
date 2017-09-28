package com.lrs.common.utils
import com.lrs.common.models._
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Sorts._
import spray.json._

import scala.concurrent.ExecutionContext
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
/**
  * Created by eguo on 9/21/17.
  */
object  MongoUtils {
  val codecRegistry = fromRegistries(fromProviders(classOf[Road], classOf[Direction], classOf[Segment], classOf[SegmentPoint], classOf[ReferencePoint]), DEFAULT_CODEC_REGISTRY )
  val mongoClient: MongoClient = MongoClient("mongodb://localhost")
  val database: MongoDatabase = mongoClient.getDatabase("road").withCodecRegistry(codecRegistry)
  val collectionRoadRecord: MongoCollection[Document] = database.getCollection("RoadRecordTable")
  val collectionRoadTable: MongoCollection[Road] = database.getCollection("RoadTable")

  def addHighwayRecord(record: JsObject) = {
    val doc : Document = Document.apply(record.toString)
    collectionRoadRecord.insertOne(doc).toFuture
  }


  def getHighwayRecords(roadId : Integer)(implicit ec: ExecutionContext) = {
    collectionRoadRecord.find(equal("roadId", roadId)).toFuture()
      //.sort(exists("dateTime")).sort(descending("dateTime")).toFuture

  }

  def addRoad(road: Road) = {
    collectionRoadTable.insertOne(road).toFuture
  }

  def getAllHighways = {
    collectionRoadRecord.find().projection(fields(include("roadName","roadId"), excludeId())).map(_.toJson).toFuture
  }

}
