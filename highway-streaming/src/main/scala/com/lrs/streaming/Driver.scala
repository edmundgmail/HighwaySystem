package com.lrs.streaming

import java.io.File
import java.nio.file.{Files, Paths}
import java.util.concurrent.Executors

import com.google.gson.GsonBuilder
import com.lrs.common.ConfigFields
import com.lrs.common.logging.Logging
import com.lrs.common.models.{DataRecord, DataRecordDeserializer, Road}
import com.lrs.common.utils.MongoUtils
import com.lrs.streaming.processor.RoadProcessor
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.{SparkConf, SparkContext}
import org.mongodb.scala.Document
import org.mongodb.scala.bson.BsonDocument
import org.scalatest.time
import org.scalatest.time.Seconds

import scala.collection.JavaConverters._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}
import scala.util.{Failure, Success, Try}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by vagrant on 6/21/17.
  */
object Driver extends Logging{

  private def createSparkContext(sparkConfig: SparkConf): SparkContext = new SparkContext(sparkConfig)

  private def readConfiguration(args: Array[String]): Config = Try(args(0)).toOption match {
    case Some(x) => ConfigFactory.parseFile(new File(x)).resolve()
    case None => ConfigFactory.load()
  }

  private def getConfigAsMap(config: Config, root: String) = config.getConfig(root).entrySet().asScala.map(x =>
    (x.getKey, x.getValue.unwrapped().toString))


  def main(args: Array[String]) : Unit = {
    val config = readConfiguration(args)
    val appConfig = getConfigAsMap(config, ConfigFields.APP_CONFIG)

    val sparkConfig = new SparkConf().setAll(appConfig)
    val sc = createSparkContext(sparkConfig)

    // Get the application configuration
    // read JSON file data as String
    //val filePath = config.getString(ConfigFields.STAGING_DIR)
    //val fileData = new String(Files.readAllBytes(Paths.get(filePath)))

    val gsonBuilder = new GsonBuilder
    gsonBuilder.registerTypeAdapter(classOf[DataRecord], DataRecordDeserializer.getInstance)

    val gson = gsonBuilder.create()
    val records = MongoUtils.getHighwayRecords(1)

    records.onComplete({
      case Success(rs : Seq[Document]) => {
        logger.info(rs.map(_.toJson).mkString(","))
        val road = rs.toList.map(r => gson.fromJson(r.toJson, classOf[DataRecord])).foldLeft[Road](null)( (road, r)=> RoadProcessor.process(sc, road, r))
        logger.info(road.toString)
      }
      case Failure(e) => {
        println(e.getMessage)
        e.printStackTrace
      }
    })

    Await.ready(records, 60 seconds)
  }
}
