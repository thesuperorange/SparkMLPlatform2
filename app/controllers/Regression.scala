package controllers

import javax.inject.Inject

import controllers.util.{SparkConfCreator, InputForms, SimpleCompute, Utilities}
import models.RegressionModel
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.sql.functions._
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.ml.regression.{LinearRegressionModel, LinearRegression}
import org.apache.spark.sql._
import org.json4s.JsonAST
import org.json4s.JsonAST.JArray
import org.json4s.jackson.JsonMethods._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import views.html

/**
  * Created by superorange on 4/1/16.
  */
class Regression @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {


  def callRegression = Action { implicit request =>
    var user = request.session.get("username").get
    InputForms.elasticInput.bindFromRequest.fold(
      formWithErrors => {
        println("ERROR" + formWithErrors)
        BadRequest("error in callRegression")
      }, { case (inputFilename, maxIter, regParam, elaParam) =>

        var error=false
        val SPARK = new SparkConfCreator(Utilities.master,this.getClass.getSimpleName)
        val SparkSession = SPARK.getSession()

        var x=new RegressionModel(null,0,null,null,0,0,0,null,null,null,null,null)
        try {


        val training = SparkSession.read.load(user+"/"+inputFilename)
        // val training = sqlContext.read.format("libsvm") .load(parquetName)
        //val lr = new LinearRegression().setLabelCol("label").setFeaturesCol("features")
        val lr = new LinearRegression().setMaxIter(maxIter.toInt).setRegParam(regParam.toDouble).setElasticNetParam(elaParam.toDouble)
        // Fit the model
        val lrModel = lr.fit(training)

        val coefficients: Array[Double] = lrModel.coefficients.toArray
        val intercept: Double = lrModel.intercept


        val timestamp: Long = System.currentTimeMillis
        lrModel.save(Utilities.modelFolder + "/" + Utilities.linearModel + "/" + timestamp)

        val trainingSummary = lrModel.summary

        val pValues: Seq[Double] = try {
          trainingSummary.pValues
        } catch {
          case e: Exception => println("error in pvalue:" + e)
            null
        }
        val coefficientStandardErrors: Seq[Double] = try {
          trainingSummary.coefficientStandardErrors
        } catch {
          case e: Exception => println("error in coefficientStandardErrors:" + e)
            null
        }
        val meanSquaredError: Double = try {
          trainingSummary.meanSquaredError
        } catch {
          case e: Exception => println("error in meanSquaredError:" + e)
            -1
        }
        val totalIter: Int = try {
          trainingSummary.totalIterations
        } catch {
          case e: Exception => println("error in meanSquaredError:" + e)
            -1
        }


        val residuals = trainingSummary.residuals
        val histJson = SimpleCompute.hist(residuals)
        val boxJson = SimpleCompute.boxplot(residuals)

        val residualArr = residuals.rdd.map(r => Math.round(r(0).toString.toDouble * 1000.0) / 1000.0).collect
        val resStr = "[" + residualArr.mkString(",") + "]"


        val predictArr = trainingSummary.predictions.select("prediction").rdd.map(r => Math.round(r(0).toString.toDouble * 1000.0) / 1000.0).collect
        val predStr = "[" + predictArr.mkString(",") + "]"


        val labelArr = trainingSummary.predictions.select("label").rdd.map(r => r(0)).collect()
        var labelStr = "[" + labelArr.mkString(",") + "]"



         x = RegressionModel(SimpleCompute.coef(coefficients), intercept, pValues, coefficientStandardErrors, meanSquaredError, totalIter, timestamp, resStr, predStr, labelStr,boxJson,histJson)
        }
        catch {
          case e: Exception => {
            SPARK.closeAll()
            error=true
            println("error in Linear Regression"+e)

          }
        } finally {
          SPARK.closeAll()
        }

        if(error){
        Ok(html.showtext("error in Linear Regression"))}
        else {

          Ok(html.mlModel.linearRegression(InputForms.elasticInput, null, x))

        }
      }

    )
  }


  def transformRegression = Action { implicit request =>
    InputForms.ModelParam.bindFromRequest.fold(
      formWithErrors => {
        println("ERROR" + formWithErrors)
        BadRequest("error in transformRegression")
      }, { case (inputFilename, model) =>
        val SPARK = new SparkConfCreator(Utilities.master,this.getClass.getSimpleName)
        val SparkSession = SPARK.getSession()

        var result = ""
        try {
          val df = SparkSession.read.load(inputFilename)
          val lrmodel = LinearRegressionModel.load(Utilities.modelFolder + "/" + Utilities.linearModel + "/" + model)
          val numModelFeatures = lrmodel.numFeatures
          val numDfFeatures = df.select("features").first.getAs[Vector](0).size
          if (numModelFeatures == numDfFeatures) {
            val prediction = lrmodel.transform(df)


            import SparkSession.implicits._
            //temp  send mean back
            result = prediction.select(mean($"prediction")).first.toString
          }
          else {
            result = "[Error] feature number is not consistent"
          }


        }
        catch {
          case e: Exception => {
            println("error in meanSquaredError:" + e)
           SPARK.closeAll()
          }
        } finally {
          SPARK.closeAll()
        }

        Ok(html.mlTrans.regression_transform(InputForms.ModelParam, null, null, result))
      })

  }

}
