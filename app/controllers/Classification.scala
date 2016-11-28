package controllers

import javax.inject.Inject

import controllers.util._
import models.logRegModel
import org.apache.spark.ml.classification.{LogisticRegression, BinaryLogisticRegressionSummary}
import org.apache.spark.sql.functions._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import views.html

/**
  * Created by superorange on 9/19/16.
  */
class Classification  @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport{
  def callLogistic = Action { implicit request =>

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
        val SPARK = new SparkConfCreator(Utilities.master,this.getClass.getSimpleName)
        val SparkSession = SPARK.getSession()
        val sc = SPARK.getSC()
        var x = logRegModel(null, 0, 0, 0, 0, 0, 0, "", "", "", "", "", "")

        try {

          val df = SparkSession.read.load(jeffrey+"/"+inputFilename)
          val lr = new LogisticRegression().setMaxIter(maxIter.toInt).setRegParam(regParam.toDouble).setElasticNetParam(elaParam.toDouble).setLabelCol("label").setFeaturesCol("features")
          val lrModel = lr.fit(df)


          val sqlContext = new org.apache.spark.sql.SQLContext(sc)
          import sqlContext.implicits._

          val coefficients: Array[Double] = lrModel.coefficients.toArray
          val intercept: Double = lrModel.intercept

          val trainingSummary = lrModel.summary


          val binarySummary = trainingSummary.asInstanceOf[BinaryLogisticRegressionSummary]

          //receiver-operating characteristic
          val roc = binarySummary.roc // FPR  TPR
          val areaUnderROC = binarySummary.areaUnderROC //double
          binarySummary.predictions
          //precision & recall
          binarySummary.pr
          binarySummary.precisionByThreshold //precision   threshold
          binarySummary.recallByThreshold //recall threshold
          binarySummary.fMeasureByThreshold //fmeasure  threshold

          val fMeasure = binarySummary.fMeasureByThreshold

          val maxFMeasure = fMeasure.select(max("F-Measure")).head().getDouble(0)


          val bestThreshold = fMeasure.where($"F-Measure" === maxFMeasure).select("threshold").head().getDouble(0)

          lrModel.setThreshold(bestThreshold)

          val precisionTH = binarySummary.precisionByThreshold
          val precision = precisionTH.where($"threshold" === bestThreshold).head().getDouble(0)

          val recallTH = binarySummary.recallByThreshold
          val recall = recallTH.where($"threshold" === bestThreshold).head().getDouble(0)

          val TPR = roc.select("TPR").rdd.map(x => x(0)).collect.mkString(",")
          val FPR = roc.select("FPR").rdd.map(x => x(0)).collect.mkString(",")


          val thresholdJson = precisionTH.select("threshold").rdd.map(x => x(0)).collect.mkString(",")
          val precisionJson = precisionTH.select("precision").rdd.map(x => x(0)).collect.mkString(",")
          val recallJson = recallTH.select("recall").rdd.map(x => x(0)).collect.mkString(",")
          val fmeasureJson = fMeasure.select("F-Measure").rdd.map(x => x(0)).collect.mkString(",")
          val coefJson = SimpleCompute.coef(coefficients)
          x = logRegModel(coefJson, intercept, areaUnderROC, maxFMeasure, bestThreshold, precision, recall
            , TPR, FPR, precisionJson, recallJson, fmeasureJson, thresholdJson)

        }
        catch {
          case e: Exception => {
            println("error in meanSquaredError:" + e)
            sc.stop()
          }
        } finally {
          sc.stop()
        }


        Ok(html.mlModel.logisticRegression(InputForms.elasticInput, null, x))
      }

    )
  }

}
