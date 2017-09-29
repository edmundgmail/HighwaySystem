package com.lrs.common.utils

import com.lrs.common.utils.OplogModel.OplogOperation.OplogOperation
import org.mongodb.scala.bson.{BsonDocument, BsonTimestamp}
import org.mongodb.scala.bson.collection.immutable.Document

import scala.collection.script.Update

/**
  * Created by vagrant on 9/29/17.
  */
object OplogModel {
  object OplogOperation extends Enumeration {
    type OplogOperation = Value

    val Insert, Update, Delete = Value
  }

  case class OplogEntry(ns: String, oRo2: BsonDocument, ts: BsonTimestamp, op: OplogOperation)

  def documentToOplogEntry(doc: Document): OplogEntry = {
    val entry = doc.toBsonDocument
    val ns    = entry.getString("ns").getValue
    val ts    = entry.getTimestamp("ts")
    val oRo2  = if (doc.get("o").isDefined) entry.getDocument("o")
    else entry.getDocument("o2")
    val op    = entry.getString("op").getValue match {
      case "i" => OplogOperation.Insert
      case "u" => OplogOperation.Update
      case "d" => OplogOperation.Delete
    }

    OplogEntry(ns, oRo2, ts, op)
  }

}
