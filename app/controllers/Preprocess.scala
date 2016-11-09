package controllers

import javax.inject.Inject

import models.StatSummary
import org.apache.spark.ml.feature.{VectorAssembler, StringIndexer}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.stat.{Statistics, MultivariateStatisticalSummary}
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{DoubleType, StringType}
import org.apache.spark.{SparkContext, SparkConf}
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._
import play.api.data.Form
import play.api.db.Database
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import views.html
import controllers.util._

/**
  * Created by superorange on 9/6/16.
  */
class Preprocess @Inject()(db: Database)(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  //-----------preprocess1
  def preprocess1_summary = Action { implicit request =>
    InputForms.InputParam.bindFromRequest.fold(
      formWithErrors => BadRequest("error"), { case (path,header) =>


        val SPARK = new SparkConfCreator(Utilities.master,this.getClass.getSimpleName)

        val sc = SPARK.getSC()
        val sparkSession = SPARK.getSession()


        var datatype: Array[(String, String)] =Array()
        var boundForm: Form[StatSummary] = InputForms.summaryForm
        var finalString=org.json4s.jackson.renderJValue("")

        try {


          val df = sparkSession.read
            .option("header", header.toString) // Use first line of all files as header
            .option("inferSchema", "true") // Automatically infer data types
            .csv(path)


          datatype= df.dtypes
          val columnNames =df.columns

          val isStringArr = new Array[(Boolean,String)](df.columns.size)

          datatype.zipWithIndex.foreach(x=> {
            if ((x._1._2) == "StringType"){ isStringArr(x._2) = (true,x._1._1)}
            else{isStringArr(x._2) = (false,x._1._1)}
          }
          )

          val rawData = sc.textFile(path)

          val parseData = rawData.zipWithIndex.filter(_._2>2).map{line=>
            Vectors.dense(
              line._1.split(",").zipWithIndex.filter(x=> ( !isStringArr(x._2.toInt)._1  )).map(_._1.toDouble)
            )
          }

          val summaryResult: MultivariateStatisticalSummary = Statistics.colStats(parseData)

          val corMatrix =Statistics.corr(parseData)

          //corMatrix.numCols

          //val corResult = corMatrix.toArray.map(x=>Math.round(x*1000.0)/1000.0)
          //println(corMatrix)
          //heat map
          val lM = corMatrix.toArray.grouped(corMatrix.numRows).toArray
          for(i<-0 to lM.length-1){
            var a=columnNames(i)

            lM(i).foreach(x=>{val a = List(BigDecimal(x).setScale(3, BigDecimal.RoundingMode.HALF_UP).toDouble); print(a)})
            println()
            var list = List[Double]()
            lM(i).foreach(
              x=> {
                list = list :+ BigDecimal(x).setScale(3, BigDecimal.RoundingMode.HALF_UP).toDouble
              }
            )
            finalString = finalString merge org.json4s.jackson.renderJValue(a, list)
            println(finalString)
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
        }
        catch {
          case e: Exception => {
            // Ok(html.error(e.toString))   //useless
            println("error in preprocess:" + e)

            SPARK.closeAll()
          }
        } finally {
          SPARK.closeAll()

        }
        Ok(html.preprocess.dataimport_pre1(InputForms.InputParam.fill(path,false),header.toString,datatype, boundForm, pretty(finalString)))
      }

    )

  }


  //-----------preprocess1
  def preprocess1_rmcol = Action {
    implicit request =>

      val SPARK = new SparkConfCreator(Utilities.master,this.getClass.getSimpleName)

      //val sc =SPARK.getSC()
      var outputFolder = ""

      val DB = new DatabaseCon(db)

      try {
        val session =SPARK.getSession()

        //parse url
        var inputFilename = ""

        var convStringCol: List[String] = List()
        var categoryCol: List[String] = List()
        var dropCol: List[String] = List()
        var header = "false"
        val user = request.session.get("username").get

        request.body.asFormUrlEncoded.get.foreach {
          //request.queryString.foreach {
          case (key, value) =>
            if (key == "inputFile") inputFilename = value(0)
            else if (key == "outputFolder") outputFolder = value(0)
            else if (key == "header") header = value(0)
            // else if(value(0)=="numeric")numericCol+key
            else if (value(0) == "category") categoryCol = key :: categoryCol
            else if (value(0) == "removal") dropCol = key :: dropCol
            println(key + " " + value(0))
        }



        var tempdf = session.read
          .option("header", header) // Use first line of all files as header
          .option("inferSchema", "true") // Automatically infer data types
          .csv(inputFilename)

        val dftypes: Array[(String, String)] = tempdf.dtypes

        categoryCol.foreach { x =>
          if (dftypes.exists(_ ==(x, "IntegerType")) || dftypes.exists(_ ==(x, "DoubleType")))
            convStringCol = x :: convStringCol
        }


        if (dropCol != null) {
          for (dropme <- dropCol) {
            // println(dropme)
            tempdf = tempdf.drop(dropme)
          }
        }
        if (convStringCol != null) {
          for (castme <- convStringCol) {
            // println(castme)
            tempdf = tempdf.withColumn(castme, tempdf(castme).cast(StringType))
          }
        }
        tempdf.write.parquet(user+"/"+outputFolder)

        DB.insertPre1(outputFolder,inputFilename,user)
      }
      catch {
        case e: Exception => {
          println("error in selectFeatureResult:" + e)
          SPARK.closeAll()


        }
      } finally {
        SPARK.closeAll()
      }
      Ok(html.showtext(outputFolder))

  }

  //--------------preprocess2

  def preprocess2 = Action { implicit request =>
    var user = request.session.get("username").get;
    InputForms.csvPathIn.bindFromRequest.fold(
      formWithErrors => BadRequest("error"), { case (path) =>


        val SPARK = new SparkConfCreator(Utilities.master,this.getClass.getSimpleName)
        val SparkSession = SPARK.getSession()
        var jsonString =""
        try{
          println("path::"+user)
          val df = SparkSession.read.load(user+"/"+path)
          jsonString = createJsonArray(df)
        } catch {
          case e: Exception => {
            println("error in selectFeatureResult:" + e)
            SPARK.closeAll()


          }
        } finally {
          SPARK.closeAll()
        }


        Ok(html.preprocess.dataimport_pre2(InputForms.csvPathIn.fill(user+"/"+path), null,jsonString))
      }
    )
  }
  import org.apache.spark.ml.feature.VectorAssembler
  //csv to vector   without visualization
  def preprocess_direct = Action { implicit request =>
    InputForms.ImportDirect.bindFromRequest.fold(
      formWithErrors => BadRequest("error"), { case (inputFilename,header,outputFolder) =>


        val SPARK = new SparkConfCreator(Utilities.master,this.getClass.getSimpleName)
        val SparkSession = SPARK.getSession()
        val DB = new DatabaseCon(db)
        val user = request.session.get("username").get

        try{
          val df = SparkSession.read
            .option("header", header) // Use first line of all files as header
            .option("inferSchema", "true") // Automatically infer data types
            .csv(inputFilename)

          val colNames = df.columns
          val assembler = new VectorAssembler().setInputCols(colNames).setOutputCol("features")
          val df2 = assembler.transform(df).select("features")
          df2.write.parquet(outputFolder)

          DB.insertPre2(outputFolder, inputFilename, true,user)


        } catch {
          case e: Exception => {
            println("error in selectFeatureResult:" + e)
            SPARK.closeAll()


          }
        } finally {
          SPARK.closeAll()
        }


        Ok(html.showtext(outputFolder))
      }
    )
  }

  def createJsonArray(df: DataFrame): String = {
    var finalString = "["
    df.dtypes.foreach(x => {
      var jsonString = "{\"columnName\":\"" + x._1 + "\",\"type\":\"" + x._2 + "\","
      if (x._2 == "StringType") {
        jsonString += "\"category\":["
        df.groupBy(x._1).count.toJSON.collect.foreach(jsonString += _ + ",")
        jsonString = jsonString.substring(0, jsonString.length() - 1) + "]"

      } else {

        val toDouble = udf[Double, Integer]( _.toDouble)
        var convertDF =df

        val myType = x._2
        val header = x._1
        if(myType=="IntegerType"){
          convertDF = df.withColumn(header,toDouble(df(header)))

        }

        jsonString += //"\"numeric\":100"
          "\"boxplot\":"+SimpleCompute.boxplot(convertDF.select(header))+","+
            "\"histogram\":"+SimpleCompute.hist(convertDF.select(header))
      }
      finalString += jsonString + "},"
    })
    finalString = finalString.substring(0, finalString.length() - 2) + "}]"

    println(finalString)

    return finalString
  }
  //-------------pre2   convert vector

  def convertVector = Action {
    implicit request =>
      val SPARK = new SparkConfCreator(Utilities.master,this.getClass.getSimpleName)
      val SparkSession = SPARK.getSession()

      val DB = new DatabaseCon(db)

      //parse url
      var inputFilename = ""
      var outputFolder = ""
      var labelColumn = ""
      var labelTag = true;
      val user = request.session.get("username").get
      //var convStringCol:List[String]=List()
      //var categoryCol:List[String]=List()
      //var dropCol:List[String]=List()
      request.body.asFormUrlEncoded.get.foreach {
        case (key, value) =>
          if (key == "inputFile") inputFilename = value(0)
          else if (key == "outputFolder") outputFolder = value(0)
          // else if(value(0)=="numeric")numericCol+key
          else if (value(0) == "label") labelColumn = key
          //else if(value(0)=="removal")dropCol=key::dropCol
          println(key + " " + value(0))
      }


      var DF = SparkSession.read.load(inputFilename)


      // val dftypes :Array[(String,String)]= tempdf.dtypes

      DF.dtypes.foreach(x => {
        if (x._2 == "StringType") {
          if (x._1 == labelColumn) labelColumn += "Index"
          val indexer1 = new StringIndexer().setInputCol(x._1).setOutputCol(x._1 + "Index")
          DF = indexer1.fit(DF).transform(DF).drop(x._1)
        }
      })

      val header = DF.columns.filter(line => line != (labelColumn))
      val assembler = new VectorAssembler().setInputCols(header).setOutputCol("features")
      var transformed = assembler.transform(DF)


      if (labelColumn == "") {
        transformed = transformed.select("features")
        labelTag = false
      }
      else transformed = transformed.withColumn("label", transformed(labelColumn).cast(DoubleType)).select("features", "label")

      transformed.write.parquet(user+"/"+outputFolder)

      //insert into sql
      DB.insertPre2(outputFolder, inputFilename, labelTag,user)

      SPARK.closeAll()

      Ok(html.showtext(outputFolder))

  }
}






