package controllers

import javax.inject.Inject

import controllers.util.{InputForms, SparkConfCreator, Utilities}
import org.apache.spark.ml.feature.PCA
import org.apache.spark.ml.feature.PCAModel
import org.apache.spark.ml.linalg.{DenseVector, Vector}
import org.json4s.jackson.JsonMethods._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import views.html
import org.json4s.JsonDSL._
import scalax.io.JavaConverters._
import scalax.file.Path
/**
  * Created by superorange on 9/20/16.
  */
class FeatureSelection @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def callPCA = Action { implicit request =>
	var jeffrey = ""
        request.session.get("username").map { user =>
          jeffrey = user
        }.getOrElse {
          jeffrey = "NULL"
        }
    //var user = request.session.get("username").get
    InputForms.KmeansParam.bindFromRequest.fold(
      formWithErrors => {
        println("ERROR" + formWithErrors)
        BadRequest("error in callPCA")
      }, { case (inputFilename, k) =>

        val SPARK = new SparkConfCreator(Utilities.master,this.getClass.getSimpleName)
        val SparkSession = SPARK.getSession()
        //val sc = SPARK.getSC()
        var x = ""
        var json = org.json4s.jackson.renderJValue("")

        try {
          val df = SparkSession.read.load(jeffrey+"/"+inputFilename)

          val pca = new PCA().setInputCol("features").setOutputCol("pcaFeatures").setK(k.toInt).fit(df)
          val pcaDF = pca.transform(df)

          val timestamp: Long = System.currentTimeMillis

          if(jeffrey!="NULL") {
            pca.save(jeffrey + "/" + Utilities.pcaModel + "/" + timestamp)
          }
            //------

          val b = pcaDF.select("pcaFeatures").rdd.map { x => x.getAs[Vector](0) }

          def transpose(m: Array[Array[Double]]): Array[Array[Double]] = {
            (for {
              c <- m(0).indices
            } yield m.map(_ (c))).toArray
          }
          var c = b.map(_.toArray).collect
          var a = transpose(c)

          var list = List[Int]()
          for (i <- 0 to a.length - 1) {
            //print(a(i))
            val count = a(i).length
            val mean = a(i).sum / count
            val devs = a(i).map(x => (x - mean) * (x - mean))
            val stddev = Math.sqrt(devs.sum / count)
            val stdround = Math.round(stddev * 1000) / 1000.0
            val name = "Comp" + i.toString
            json = json merge org.json4s.jackson.renderJValue(name, stdround)

          }


          //println(pretty(json))
          //result = pcaDF.select("pcaFeatures").map(_.toString).collect
         x = timestamp.toString
        }
        catch {
          case e: Exception => {
            println("error in meanSquaredError:" + e)
            SPARK.closeAll()
          }
        } finally {

          SPARK.closeAll()
        }


        Ok(html.mlModel.pca(InputForms.KmeansParam, null, pretty(json),jeffrey,x))
      }

    )
  }
  def pca_trans = Action{
    implicit request =>
      InputForms.ModelParam.bindFromRequest.fold(
        formWithErrors => {
          println("ERROR" + formWithErrors)
          BadRequest("error in pcatransformation")
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
              val pca = PCAModel.load(jeffrey + "/" + Utilities.pcaModel + "/" + model)
              val numFeatures= pca.pc.numRows
              val y = df.select("features").head
              val numDfFeatures = y(0).asInstanceOf[DenseVector].size
              println(numFeatures)

              if (numFeatures == numDfFeatures) {
                val prediction = pca.transform(df)
                prediction.show
                res = prediction.select("pcaFeatures").rdd.map(r => r(0).toString).collect
                println(res)
                delete
                prediction.write.format("com.databricks.spark.csv").option("header", true).option("inferSchema", "true").csv(Utilities.Dpath)
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
              Ok(html.mlTrans.pca(InputForms.ModelParam,InputForms.download, null, null, res,null,jeffrey))
            }
            else{
              Ok(html.mlTrans.pca(InputForms.ModelParam, null, null, null, null,result,jeffrey))
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
