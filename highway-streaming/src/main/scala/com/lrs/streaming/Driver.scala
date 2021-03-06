package com.lrs.streaming

import java.io.File

import com.google.gson.GsonBuilder
import com.lrs.common.ConfigFields
import com.lrs.common.dao.MongoUtils
import com.lrs.common.logging.Logging
import com.lrs.common.models.{DataRecord, DataRecordDeserializer, Road}
import com.lrs.streaming.processor.RoadProcessor
import com.lrs.streaming.utils.MongoMonitor
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.{SparkConf, SparkContext}
import org.mongodb.scala.Document

import scala.collection.JavaConverters._
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}
import scala.concurrent.{Await, Future}
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

    val result = records andThen {
      case Success(rs : Seq[Document]) => {
        val rss = rs.toList.map(r => gson.fromJson(r.toJson, classOf[DataRecord]))
        rss.foreach(r=>logger.info(gson.toJson(r)))
        val road= rss.foldLeft[Road](null)( (road, r)=> RoadProcessor.process(road, r))
        MongoUtils.addRoad(road)
      }
      case Failure(e) => {
        println(e.getMessage)
        e.printStackTrace
      }
    }

    Await.ready(result, Duration.Inf)

      val monitor = new MongoMonitor()
      monitor.run





    /*
    implicit val system = ActorSystem("main-actor-system")
    implicit val materializer = ActorMaterializer()

    val oplogService = OplogService()
    oplogService.source(MongoUtils.mongoClient).map(OplogModel.documentToOplogEntry).runForeach(println)
  */

  }
}
