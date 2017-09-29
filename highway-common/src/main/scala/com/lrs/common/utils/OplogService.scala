package com.lrs.common.utils

/**
  * Created by vagrant on 9/29/17.
  */
import akka.NotUsed
import akka.stream.scaladsl.Source
import com.mongodb.CursorType
import org.mongodb.scala.bson.{BsonDocument, BsonTimestamp}
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.{FindObservable, MongoClient}

trait OplogService {

  def source(client: MongoClient): Source[Document, NotUsed]

}

object OplogService {
  def apply() = new OplogServiceImpl

  class OplogServiceImpl extends OplogService {

    import Implicits._

    override def source(client: MongoClient): Source[Document, NotUsed] = {
      val observable = getOplogObservable(client)

      Source.fromPublisher(observable)
    }


    private def getOplogObservable(client: MongoClient): FindObservable[Document] = {
      MongoUtils.database
        .getCollection( "oplog.rs")
        .find(and(
          in("op", "i", "d", "u"),
          exists("fromMigrate", exists = false)))
      .cursorType(CursorType.TailableAwait)
        .noCursorTimeout(true)
    }
  }


}

