package controllers.util

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * Created by superorange on 9/6/16.
  */
class SparkConfCreator(master: String, appName: String) {

    val sparkConf = new SparkConf().setAppName(appName).setMaster(master)
  val sc = new SparkContext(sparkConf)

   val session= SparkSession.builder.
      master(master)
      .appName(appName)
      .getOrCreate()

  def getSC(): SparkContext = {
    sc
  }
  def getSession() = {
    session
  }
  def closeSC() = {
    sc.stop()
  }
  def closeSession() = {
    session.stop()
  }
  def closeAll() ={
    sc.stop()
    session.stop()
  }
}

