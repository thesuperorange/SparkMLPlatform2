package controllers.util


/**
  * Created by superorange on 9/6/16.
  */
object Utilities {
  //-------spark conf-------------

  val master = "local"//spark://ubuntu:7077"
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

  val Dpath = "/home/pzq317/Desktop/test"

}
