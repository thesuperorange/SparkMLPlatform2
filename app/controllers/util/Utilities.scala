package controllers.util

import com.typesafe.config.ConfigFactory


/**
  * Created by superorange on 9/6/16.
  */
object Utilities {
  //-------spark conf-------------
  val master = ConfigFactory.load().getConfig("userdefine").getString("sparkMode")

//-------machine learning algorithm
  val modelFolder = "Model"
  val linearModel = "LinearRegression"
  val logisticModel = "LogisticRegression"
  val kmeansModel = "Kmeans"
  val pcaModel = "pcaModel"
  val decisionTreeModel = "DecisionTree"

  //--------mysql table
  val modelTable = "Model"
  val pre1Table = "Pre1"
  val pre2Table = "Pre2"
  val userData = "userData"
  val path = "path"
  //---------downloadpath

  val workingFolder = ConfigFactory.load().getConfig("userdefine").getString("workingFolder")
  val Dpath = workingFolder+"/"+ConfigFactory.load().getConfig("userdefine").getString("downloadPath")
  val defaultCsv = ConfigFactory.load().getConfig("userdefine").getString("defaultCsv")
}
