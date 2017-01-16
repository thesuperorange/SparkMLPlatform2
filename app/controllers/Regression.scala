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
import org.apache.spark.ml.linalg.DenseVector
import scalax.io.JavaConverters._
import scalax.file.Path
/**
  * Created by superorange on 4/1/16.
  */
class Regression @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {


  def callRegression = Action { implicit request =>
    //var user = request.session.get("username").get
    InputForms.elasticInput.bindFromRequest.fold(
      formWithErrors => {
        println("ERROR" + formWithErrors)
        BadRequest("error in callRegression")
      }, { case (inputFilename, maxIter, regParam, elaParam) =>
        var jeffrey = ""
        request.session.get("username").map { user =>
          jeffrey = user
        }.getOrElse {
          jeffrey = "NULL"
        }
        var error=false
        val SPARK = new SparkConfCreator(Utilities.master,this.getClass.getSimpleName)
        val SparkSession = SPARK.getSession()

        var x=new RegressionModel(null,0,null,null,0,0,0,null,null,null,null,null)
        try {


        val training = SparkSession.read.load(jeffrey+"/"+inputFilename)
        // val training = sqlContext.read.format("libsvm") .load(parquetName)
        //val lr = new LinearRegression().setLabelCol("label").setFeaturesCol("features")
        val lr = new LinearRegression().setMaxIter(maxIter.toInt).setRegParam(regParam.toDouble).setElasticNetParam(elaParam.toDouble)
        // Fit the model
        val lrModel = lr.fit(training)

        val coefficients: Array[Double] = lrModel.coefficients.toArray
        val intercept: Double = lrModel.intercept


        val timestamp: Long = System.currentTimeMillis
        if(jeffrey!="NULL") {
          lrModel.save(jeffrey + "/" + Utilities.linearModel + "/" + timestamp)
        }
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
        Ok(html.showtext("error in Linear Regression",jeffrey))}
        else {

          Ok(html.mlModel.linearRegression(InputForms.elasticInput, null, x,jeffrey))

        }
      }

    )
  }


  def transformRegression = Action { implicit request =>
    InputForms.ModelParam.bindFromRequest.fold(
      formWithErrors => {
        println("ERROR" + formWithErrors)
        BadRequest("error in transformRegression")
      }, {
        case (inputFilename, model) =>

          var jeffrey = ""
          request.session.get("username").map { user =>
            jeffrey = user
          }.getOrElse {
            jeffrey = "NULL"
          }
        val SPARK = new SparkConfCreator(Utilities.master,this.getClass.getSimpleName)
        val SparkSession = SPARK.getSession()

        var result = ""
        var res = Array[String]()
        try {
          println(jeffrey,inputFilename)
          val df = SparkSession.read.load(jeffrey+"/"+inputFilename)
          val lrmodel = LinearRegressionModel.load(jeffrey + "/" + Utilities.linearModel + "/" + model)
          val numModelFeatures = lrmodel.numFeatures

          val y = df.select("features").head
          val numDfFeatures = y(0).asInstanceOf[DenseVector].size

          if (numModelFeatures == numDfFeatures) {
            val prediction = lrmodel.transform(df)


            import SparkSession.implicits._
            //temp  send mean back
            prediction.show
            delete
            df.write.format("com.databricks.spark.csv").option("header",true).option("inferSchema", "true").csv(Utilities.Dpath)


            //result = prediction.select(mean($"prediction")).first.toString
            res = prediction.select("prediction").rdd.map(r=>r(0).toString).collect


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
        println(result)
        if(result=="") {
          Ok(html.mlTrans.regression_transform(InputForms.ModelParam,InputForms.download, null, null, res,null,jeffrey))
        }
          else{
          Ok(html.mlTrans.regression_transform(InputForms.ModelParam, null, null, null, null,result,jeffrey))
        }
        })

  }
  def download = Action{implicit request =>
      InputForms.download.bindFromRequest.fold(
        formWithErrors => {
          println("ERROR" + formWithErrors)
          BadRequest("error in download")
        }, {
          case (csvPath) =>
            var jeffrey = ""
            request.session.get("username").map { user =>
              jeffrey = user
            }.getOrElse {
              jeffrey = "NULL"
            }
            val SPARK = new SparkConfCreator(Utilities.master,this.getClass.getSimpleName)
            val SparkSession = SPARK.getSession()

            try {
              val prediction = SparkSession.read.csv(Utilities.Dpath)
              prediction.write.format("com.databricks.spark.csv").option("header",true).option("inferSchema", "true").csv(csvPath)


            }
            catch {
              case e: Exception => {
                println("error in meanSquaredError:" + e)
                SPARK.closeAll()
              }
            } finally {
              SPARK.closeAll()
            }
              Ok("file downloaded")

        })
  }
  def delete {
    //val path: Path = Path ("/home/pzq317/Desktop/SparkMLPlatform2/NULL")
    val path = Path.fromString(Utilities.Dpath)
    path.deleteRecursively()
    //path.deleteIfExists()
  }
}
