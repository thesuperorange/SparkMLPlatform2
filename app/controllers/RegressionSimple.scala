package controllers

import javax.inject.Inject

import controllers.util.{SimpleCompute, Utilities, SparkConfCreator, InputForms}
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.sql._

import org.apache.spark.{SparkConf, SparkContext}
import org.json4s.JsonAST
import org.json4s.JsonAST.JArray
import org.json4s.jackson.JsonMethods._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import views.html
import org.json4s.JsonDSL._
/**
  * Created by superorange on 4/1/16.
  */
class RegressionSimple @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {




  def callRegression = Action { implicit request =>
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


        val training = SparkSession.read.load(inputFilename)
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



        val coef2 =  SimpleCompute.coef(coefficients)
        val residuals = trainingSummary.residuals

        val boxJson = SimpleCompute.boxplot(residuals)

       // var json=org.json4s.jackson.renderJValue("")


          var tick = residuals.rdd.map(row => row(0).asInstanceOf[Double])
        //  var mean =tick.collect()

        /*  val arrMean = new DescriptiveStatistics()
          genericArrayOps(mean).foreach(v => arrMean.addValue(v))
          val max = arrMean.getMax()
          val min = arrMean.getMin()
          var num = Math.sqrt(tick.count()).toInt + 1
          if(tick.count()>=1000){
            num = 30
          }
          var range = (max - min) / num
          var list = List[Int]()

          for (i <- 0 to num - 1) {
            var a = tick.filter(x => x >= (min + (range * i)) && x <= (min + range * (i + 1))).count.toInt
            list = list :+ a
          }
      println("max "+max)
          println("min "+min)
          println("value "+list(0))*/
         // json = json merge ("max", max) ~ ("min", min) ~ ("value", list)

        //val histJson =  pretty(json)


        SPARK.closeAll()
        var user = request.session.get("username").get
        Ok(html.lrsimple(InputForms.elasticInput,boxJson,jeffrey))


      }

    )
  }

}
