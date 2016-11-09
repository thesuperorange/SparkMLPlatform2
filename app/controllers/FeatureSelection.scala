package controllers

import javax.inject.Inject

import controllers.util.{Utilities, SparkConfCreator, InputForms}
import org.apache.spark.ml.feature.PCA
import org.apache.spark.ml.linalg.Vector
import org.json4s.jackson.JsonMethods._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import views.html

import org.json4s.JsonDSL._
/**
  * Created by superorange on 9/20/16.
  */
class FeatureSelection @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def callPCA = Action { implicit request =>
    var user = request.session.get("username").get
    InputForms.KmeansParam.bindFromRequest.fold(
      formWithErrors => {
        println("ERROR" + formWithErrors)
        BadRequest("error in callPCA")
      }, { case (inputFilename, k) =>

        val SPARK = new SparkConfCreator(Utilities.master,this.getClass.getSimpleName)
        val SparkSession = SPARK.getSession()
        //val sc = SPARK.getSC()

        var json = org.json4s.jackson.renderJValue("")

        try {
          val df = SparkSession.read.load(user+"/"+inputFilename)

          val pca = new PCA().setInputCol("features").setOutputCol("pcaFeatures").setK(k.toInt).fit(df)
          val pcaDF = pca.transform(df)

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

        }
        catch {
          case e: Exception => {
            println("error in meanSquaredError:" + e)
            SPARK.closeAll()
          }
        } finally {
          SPARK.closeAll()
        }


        Ok(html.mlModel.pca(InputForms.KmeansParam, null, pretty(json)))
      }

    )
  }
}
