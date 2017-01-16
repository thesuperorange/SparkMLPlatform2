package controllers

import javax.inject.Inject

import controllers.util.{InputForms, SparkConfCreator, Utilities}
import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.ml.clustering.KMeansModel
import org.apache.spark.ml.linalg.{DenseVector, Vector}
import org.apache.spark.ml.regression.LinearRegressionModel
import org.apache.spark.sql._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import views.html
import org.apache.spark.sql.functions.udf
import org.json4s._
import org.json4s.JsonDSL._
import net.liftweb.json._

//import org.json4s.jackson.JsonMethods._

import scalax.io.JavaConverters._
import scalax.file.Path
/**
  * Created by superorange on 9/19/16.
  */
//case class iris(prediction: String, f0: String, f1: String,f2: String,f3: String,f4: String)
class Clustering @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def callKmeans= Action { implicit request =>
    
    InputForms.KmeansParam.bindFromRequest.fold(
      formWithErrors => {
        println("ERROR" + formWithErrors)
        BadRequest("error in callKmeans")
      }, { case (inputFilename, k) =>
	
      var jeffrey = ""
      request.session.get("username").map { user =>
        jeffrey = user
      }.getOrElse {
        jeffrey = "NULL"
      }
        val SPARK = new SparkConfCreator(Utilities.master,this.getClass.getSimpleName)
        val SparkSession = SPARK.getSession()
        val sc = SPARK.getSC()
	
        val df = SparkSession.read.load(jeffrey+"/"+inputFilename)
        var center: List[String] = List()
        var JsonStr = "{"
        var x = ""
        try {

         

          val kmeans = new KMeans().setK(k.toInt).setFeaturesCol("features").setPredictionCol("prediction")

          val model = kmeans.fit(df)

          val timestamp: Long = System.currentTimeMillis
          if(jeffrey!="NULL") {
            model.save(jeffrey + "/" + Utilities.kmeansModel + "/" + timestamp)
          }
            // Shows the result
          println("Final Centers: ")
          model.clusterCenters.foreach(x => center = x.toString :: center)

          val result = model.transform(df)
          // result.write.format("com.databricks.spark.csv").save("kkmeansoutput.csv")
          import SparkSession.implicits._




          // Simple helper to convert vector to array<double>


          // Prepare a list of columns to create




          val predStr = "[" + result.select("prediction").map(x => x(0).toString).collect.mkString(",") + "]"
          JsonStr += "prediction:" + predStr + ","

          //no efficiency
          val headerSize = result.select("features").map(x => x.getAs[Vector](0).toDense.size).first
          val vecToSeq = udf((v: Vector) => v.toArray)
          val exprs = (0 until headerSize).map(i => $"_tmp".getItem(i).alias(s"f$i"))
          val seqDF = df.select(vecToSeq($"features").alias("_tmp")).select(exprs:_*)

          var str = ""
          for (i <- 0 to headerSize - 1) {
            str += "f" + i + ":[" + seqDF.select("f"+i).rdd.map(x=>x(0)).collect.mkString(",") + "],"
          }
          JsonStr += str.substring(0, str.length - 1) + "}"
          x = timestamp.toString
        }
        catch {
          case e: Exception => {
            println("error in meanSquaredError:" + e)
            sc.stop()
          }
        } finally {
          sc.stop()
        }
        //count(JsonStr)
        if(JsonStr.size<20000) {
          Ok(html.mlModel.kmeans(InputForms.KmeansParam, null, center, JsonStr, jeffrey, x))
        }else{
          Ok(html.mlModel.kmeans(InputForms.KmeansParam, null, center, count(JsonStr), jeffrey, x))
        }
      }

    )
  }
  def  kmean_trans = Action { implicit request =>
    InputForms.ModelParam.bindFromRequest.fold(
      formWithErrors => {
        println("ERROR" + formWithErrors)
        BadRequest("error in kmeans")
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
            println(jeffrey,inputFilename, model)
            val df = SparkSession.read.load(jeffrey+"/"+inputFilename)

            val kmeans = KMeansModel.load(jeffrey + "/" + Utilities.kmeansModel + "/" + model)
            //val ans = kmeans.fit(df)

            println(kmeans.getFeaturesCol)
            val prediction = kmeans.transform(df)
            prediction.show
            res = prediction.select("prediction").rdd.map(r=>r(0).toString).collect

            delete
            prediction.write.format("com.databricks.spark.csv").option("header",true).option("inferSchema", "true").csv(Utilities.Dpath)

          }
          catch {
            case e: Exception => {
              println("error in meanSquaredError:" + e)
              SPARK.closeAll()
            }
          } finally {
            SPARK.closeAll()
          }

          if(result=="") {
            Ok(html.mlTrans.kmeans(InputForms.ModelParam,InputForms.download, null, null, res,null,jeffrey))
          }
          else{
            Ok(html.mlTrans.kmeans(InputForms.ModelParam, null, null, null, null,result,jeffrey))
          }
      })
  }


  def count(JsonString:String): String={
    //implicit val formats = DefaultFormats
    case class iris(prediction: String, f0: String, f1: String,f2: String,f3: String,f4: String)
    val SPARK = new SparkConfCreator(Utilities.master,this.getClass.getSimpleName)
    val SparkSession = SPARK.getSession()
    val sc = SPARK.getSC()
    var jS = org.json4s.jackson.renderJValue("")
    try {
      /*val rdd = sc.parallelize(Seq(JsonString))
      val df = SparkSession.read.json(rdd)
      df.show()*/
      jS = jS merge org.json4s.jackson.renderJValue(JsonString)
      println(jS)
      //var js =JsonParser.parse(JsonString)
      //val m = js.extract[iris]
      //println(m.prediction)

      //df.select("prediction").show()

    }catch {
      case e: Exception => {
        //println("error in meanSquaredError:" + e)
        SPARK.closeAll()
      }
    } finally {
      SPARK.closeAll()
    }
    return "sss"

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
