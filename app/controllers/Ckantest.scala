package controllers

import java.nio.file.{Path, Paths}
import javax.inject.Inject

import play.api.db.Database
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import views.html
import controllers.util.{DatabaseCon, InputForms, SparkConfCreator, Utilities}
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import sys.process._
import java.net.URL
import java.io.File

import models.StatSummary
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.stat.{MultivariateStatisticalSummary, Statistics}
import play.api.data.Form


/**
  * Created by superorange on 4/1/16.
  */
class Ckantest @Inject()(db: Database)(val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def md5(s: String) = {
    val m = java.security.MessageDigest.getInstance("MD5")
    val b = s.getBytes("UTF-8")
    m.update(b, 0, b.length)
    new java.math.BigInteger(1, m.digest()).toString(16)
  }
  def fileDownloader(url: String, filename: String) = {
    new URL(url) #> new File(filename) !!
  }
  def test = Action{implicit request=>
    val DB = new DatabaseCon(db)
    InputForms.ckan.bindFromRequest().fold(
      formWithErrors => {
        println("ERROR" + formWithErrors)
        BadRequest("error in download")
      },{ case(username,url,check)=>
        var err=false
        var errMessage=""
        val header="true"


          /** check md5 **/
          if (!md5(username + url).takeRight(5).equalsIgnoreCase(check)) {
            err = true
            errMessage = "Sorry! Checksum mismatch."
          }

          /** check csv  **/
          val filename: Path = Paths.get(url).getFileName
          if (!filename.toString().endsWith(".csv")) {
            err = true
            errMessage = "Sorry! Currently we only support CSV files."
          }

          /** user existed?  **/
          val directory: File = new File(Utilities.workingFolder+"/"+username);
          if (!directory.exists()) directory.mkdir()

          if (!err) {
            val path = Utilities.workingFolder+"/"+username + "/" + filename.toString
            val SPARK = new SparkConfCreator(Utilities.master, this.getClass.getSimpleName)
            val sc = SPARK.getSC()
            val sparkSession = SPARK.getSession()
            var datatype: Array[(String, String)] = Array()
            var boundForm: Form[StatSummary] = InputForms.summaryForm
            var finalString = org.json4s.jackson.renderJValue("")
            try {
              fileDownloader(url, path)

              val df = sparkSession.read
                .option("header", header) // Use first line of all files as header
                .option("inferSchema", "true") // Automatically infer data types
                .csv(path)

              datatype = df.dtypes
              val columnNames = df.columns.map(x=>x.replaceAll(" ",""))
              //df.show
              val isStringArr = new Array[(Boolean, String)](df.columns.size)

              datatype.zipWithIndex.foreach(x => {
                if (x._1._2 == "StringType") {
                  isStringArr(x._2) = (true, x._1._1)
                }
                else {
                  isStringArr(x._2) = (false, x._1._1)
                }
              }
              )

              val rawData = sc.textFile(path).filter(_.nonEmpty)

              val parseData = rawData.zipWithIndex.filter(_._2>2).map{line=>
                Vectors.dense(
                  line._1.replaceAll("\"", "").trim.split(",").zipWithIndex.filter(x=>  !isStringArr(x._2.toInt)._1  ).map(_._1.toDouble)
                )
              }



              val summaryResult: MultivariateStatisticalSummary = Statistics.colStats(parseData)

              val corMatrix = Statistics.corr(parseData)

              //heat map
              val lM = corMatrix.toArray.grouped(corMatrix.numRows).toArray
              for (i <- 0 to lM.length - 1) {
                var a = columnNames(i)
                lM(i).foreach(x => {
                  val a = List(BigDecimal(x).setScale(3, BigDecimal.RoundingMode.HALF_UP).toDouble);
                })

                var list = List[Double]()
                lM(i).foreach(
                  x => {
                    list = list :+ BigDecimal(x).setScale(3, BigDecimal.RoundingMode.HALF_UP).toDouble
                  })
                finalString = finalString merge org.json4s.jackson.renderJValue(a, list)
              }


              val x = Map(
                "max" -> summaryResult.max.toString,
                "mean" -> summaryResult.mean.toString,
                "min" -> summaryResult.min.toString,
                "variance" -> summaryResult.variance.toString,
                "numNonZero" -> summaryResult.numNonzeros.toString,
                "count" -> summaryResult.count.toString
              )
              boundForm = InputForms.summaryForm.bind(x)
            } catch {
              case e: Exception =>
                err = true
                errMessage = e.toString()

            }
            if (err) {
              SPARK.closeAll()

              Ok(html.showtext(errMessage, username, 4))
            }
            else {
              SPARK.closeAll()
              Ok(html.preprocess.dataimport_pre1(null, null, InputForms.InputParam.fill(path, false), header, datatype, boundForm, pretty(finalString), username)).withSession("username" -> username)
            }
          }

          else Ok(html.showtext(errMessage, username, 4))

      })

  }


}
