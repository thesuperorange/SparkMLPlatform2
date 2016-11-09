package controllers

import javax.inject.Inject

import controllers.util.{Utilities, SparkConfCreator, InputForms}
import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.ml.linalg.Vector
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import views.html
import org.apache.spark.sql.functions.udf
/**
  * Created by superorange on 9/19/16.
  */
class Clustering @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def callKmeans= Action { implicit request =>
    var user = request.session.get("username").get
    InputForms.KmeansParam.bindFromRequest.fold(
      formWithErrors => {
        println("ERROR" + formWithErrors)
        BadRequest("error in callKmeans")
      }, { case (inputFilename, k) =>
        val SPARK = new SparkConfCreator(Utilities.master,this.getClass.getSimpleName)
        val SparkSession = SPARK.getSession()
        val sc = SPARK.getSC()

        var center: List[String] = List()
        var JsonStr = "{"
        try {

          val df = SparkSession.read.load(user+"/"+inputFilename)

          val kmeans = new KMeans().setK(k.toInt).setFeaturesCol("features").setPredictionCol("prediction")

          val model = kmeans.fit(df)

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

        }
        catch {
          case e: Exception => {
            println("error in meanSquaredError:" + e)
            sc.stop()
          }
        } finally {
          sc.stop()
        }

        Ok(html.mlModel.kmeans(InputForms.KmeansParam, null, center, JsonStr))
      }

    )
  }
}
