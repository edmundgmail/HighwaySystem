import sbt.Keys._
import NativePackagerHelper._
import com.typesafe.sbt.packager.MappingsHelper.{contentOf => _}
import sbtassembly.AssemblyPlugin.autoImport._
import scala.collection.JavaConverters._

import scala.util.Try

val meta = """META.INF(.)*""".r


/**
  * Dependencies
  */
val hadoopCommon          = "org.apache.hadoop"               %     "hadoop-common"                 %  "2.7.1"  // % "provided"
val hadoopCommonNotProvided  = "org.apache.hadoop"            %     "hadoop-common"                 %  "2.7.1"
val hadoopCommonTest      = "org.apache.hadoop"               %     "hadoop-common"                 %  "2.7.1"   % "test" classifier "tests"
val hdfs                  = "org.apache.hadoop"               %     "hadoop-hdfs"                   %  "2.7.1"  // % "provided"
val hdfsNotProvided       = "org.apache.hadoop"               %     "hadoop-hdfs"                   %  "2.7.1"
val hdfsTest              = "org.apache.hadoop"               %     "hadoop-hdfs"                   %  "2.7.1"   % "test" classifier "tests"
val hadoopMiniCluster     = "org.apache.hadoop"               %     "hadoop-minicluster"            %  "2.7.1"   % "test"
val hbaseClient           = "org.apache.hbase"                %     "hbase-client"                  %  "1.1.2"
val hbaseCommon           = "org.apache.hbase"                %     "hbase-common"                  %  "1.1.2"
val hbaseCommonTest       = "org.apache.hbase"                %     "hbase-common"                  %  "1.1.2"   % "test" classifier "tests"
val hbaseServer           = "org.apache.hbase"                %     "hbase-server"                  %  "1.1.2"   % "test"
val hbaseServerTest       = "org.apache.hbase"                %     "hbase-server"                  %  "1.1.2"   % "test" classifier "tests"
val hbaseHdpCompat        = "org.apache.hbase"                %     "hbase-hadoop-compat"           %  "1.1.2"   % "test"
val hbaseHdpCompatTest    = "org.apache.hbase"                %     "hbase-hadoop-compat"           %  "1.1.2"   % "test" classifier "tests"
val hbaseHdp2Compat       = "org.apache.hbase"                %     "hbase-hadoop2-compat"          %  "1.1.2"   % "test"
val hbaseHdp2CompatTest   = "org.apache.hbase"                %     "hbase-hadoop2-compat"          %  "1.1.2"   % "test" classifier "tests"
val jacksonCore           = "com.fasterxml.jackson.core"      %     "jackson-core"                  %  "2.7.5"
val jacksonDatabind       = "com.fasterxml.jackson.core"      %     "jackson-databind"              %  "2.7.5"
val jacksonScala          = "com.fasterxml.jackson.module"    %%    "jackson-module-scala"          %  "2.7.5"
val jodaTime              = "com.github.nscala-time"          %%    "nscala-time"                   %  "2.12.0"
val log4j                 = "log4j"                           %     "log4j"                         %  "1.2.17"
val mockito               = "org.mockito"                     %     "mockito-all"                   %  "1.10.19" % "test"
val nifiSparkReceiver     = "org.apache.nifi"                 %     "nifi-spark-receiver"           %  "0.7.0"
val scalaTest             = "org.scalatest"                   %%    "scalatest"                     %  "2.2.6"   % "test"
val scalaMock             = "org.scalamock"                   %%    "scalamock-scalatest-support"   %  "3.2.2"   % "test"
val sparkCore             = "org.apache.spark"                %%    "spark-core"                    %  "2.0.2"   //% "provided"
val sparkStreaming        = "org.apache.spark"                %%    "spark-streaming"               %  "2.0.2"   //% "provided"
val typesafeConfig        = "com.typesafe"                    %     "config"                        %  "1.3.0"
val gson                  = "com.google.code.gson"            %     "gson"                          % "2.8.1"


/**
  * Shared settings for all the projects
  */
lazy val commonSettings = Seq(
  version := s"0.0.1-SNAPSHOT${Try("_" + sys.env("BUILD_NUMBER")).getOrElse("")}",
  organization := "com.ddp",
  scalaVersion := "2.11.6",
  coverageMinimum := 80,
  coverageFailOnMinimum := false,
  parallelExecution in Test := false,
  fork in Test := true,
  baseDirectory in Test := file("."),
  javaOptions in Test += "-Xms1g",
  javaOptions in Test += "-Xmx2g",
  javaOptions in Test += "-XX:MaxMetaspaceSize=1g"
)

/**
  * This only holds Universal package
  */
lazy val HighwaySystem = (project in file(".")).aggregate(common, streaming).
  enablePlugins(UniversalPlugin).
  settings(commonSettings: _*).
  settings(
    mappings in Universal ++= contentOf("highway-configs/"),
    mappings in Universal ++= contentOf("highway-scripts/"),
    mappings in Universal ++= contentOf("highway-streaming/target/scala-2.11/"),
    mappings in Universal := (mappings in Universal).value filter {
      case (file, name) => !name.contains("classes")
    }
  )

/**
  * Common project which contains shared code among the other projects
  */
lazy val common = (project in file("highway-common")).
  settings(commonSettings: _*).
  settings(
    unmanagedBase := baseDirectory.value / "lib",
    libraryDependencies ++= Seq(jacksonCore, jacksonDatabind, jacksonScala, jodaTime, typesafeConfig, log4j, sparkCore,
      sparkStreaming, scalaTest, scalaMock, mockito, nifiSparkReceiver, hbaseClient, hbaseCommon, hbaseServer,
      hbaseServerTest, hbaseCommonTest, hbaseHdpCompat, hbaseHdpCompatTest, hbaseHdp2Compat, hbaseHdp2CompatTest,
      hadoopMiniCluster, hdfs, hdfsTest, hadoopCommon, hadoopCommonTest)
  )

/**
  * Streaming project which is in charge of manageing the spark streaming
  */
lazy val streaming = (project in file("highway-streaming")).
  settings(commonSettings: _*).
  settings(

    libraryDependencies ++= Seq(sparkCore, sparkStreaming, gson, scalaTest, scalaMock),
    mainClass in Compile := Some("com.ddp.highway.Driver"),
    mainClass in run := Some("com.ddp.highway.Driver"),
    assemblyMergeStrategy in assembly := {
      case meta(_) => MergeStrategy.discard
      case x => MergeStrategy.first
    }
  ).dependsOn(common % "compile->compile;test->test")

        
