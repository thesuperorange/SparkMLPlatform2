name := """SparkMLPlatform"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= {
  val sparkV = "2.0.0"
  Seq(
    jdbc,
    cache,
    ws,
    "org.mindrot" % "jbcrypt" % "0.3m",
    "mysql" % "mysql-connector-java" % "5.1.34",
    "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
    "org.apache.spark" %% "spark-core" % sparkV,
    "org.apache.spark" %% "spark-mllib" % sparkV,
    "org.apache.spark" %% "spark-sql" % sparkV,
    "org.apache.spark" %% "spark-graphx" % sparkV

  )
}

dependencyOverrides ++= Set(
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.0"
)
