package com.lrs.common.dao

/**
  * Created by eguo on 11/2/17.
  */

import com.lrs.common.models._

import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter, Macros, document}

object MongoDao extends App{
  // My settings (see available connection options)
  val mongoUri = "mongodb://localhost:27017/road"

  import ExecutionContext.Implicits.global // use any appropriate context

  // Connect to the database: Must be done only once per application
  val driver = MongoDriver()
  val parsedUri = MongoConnection.parseURI(mongoUri)
  val connection = parsedUri.map(driver.connection(_))

  // Database and collections: Get references
  val futureConnection = Future.fromTry(connection)
  def db1: Future[DefaultDB] = futureConnection.flatMap(_.database("road"))
  def roadCollection = db1.map(_.collection("RoadTable"))

  // Write Documents: insert or update

  implicit def roadWriter: BSONDocumentWriter[Road] = Macros.writer[Road]
  implicit def dirWriter: BSONDocumentWriter[Direction] = Macros.writer[Direction]
  implicit def segWriter: BSONDocumentWriter[Segment] = Macros.writer[Segment]
  implicit def segpWriter: BSONDocumentWriter[SegmentPoint] = Macros.writer[SegmentPoint]
  implicit def refWriter: BSONDocumentWriter[ReferencePoint] = Macros.writer[ReferencePoint]
  implicit def laneWriter: BSONDocumentWriter[Lane] = Macros.writer[Lane]

  // or provide a custom one

  def createRoad(road: Road): Future[Unit] =
    roadCollection.flatMap(_.insert(road).map(_ => {})) // use personWriter

  def updateRoad(road:Road): Future[Int] = {
    val selector = document(
      "name" -> road.name
    )
    // Update the matching person
    roadCollection.flatMap(_.update(selector, road).map(_.n))
  }


}