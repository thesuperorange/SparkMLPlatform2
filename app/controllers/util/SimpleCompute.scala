package controllers.util

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.apache.spark.sql.DataFrame
import org.json4s.JsonAST
import org.json4s.JsonAST.JArray

/**
  * Created by superorange on 8/30/16.
  */

import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
object SimpleCompute {
  import org.apache.commons.math3.stat.descriptive._

  def coef(coefficient:Array[Double]):String ={

    var myList = List[JsonAST.JValue]()
    for (i <-  0 to coefficient.size-1) {
      val element= org.json4s.jackson.renderJValue("") merge ("coef", "C"+i) ~ ("count", coefficient(i))
      myList = element::myList
    }

    pretty(JArray(myList))


  }
  def hist(convertDF:DataFrame):String ={
    var json=org.json4s.jackson.renderJValue("")
    try{

      var tick = convertDF.rdd.map(row => row(0).asInstanceOf[Double])
      var mean =tick.collect()
      val arrMean = new DescriptiveStatistics()
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

      json = json merge ("max", max) ~ ("min", min) ~ ("value", list)
    }catch{
      case e:Exception => println(e,"error")
    }
    return pretty(json)
  }

  def boxplot(convertDF:DataFrame):String={
    var json=org.json4s.jackson.renderJValue("")


    try{
      val mean =convertDF.rdd.map(row => row(0).asInstanceOf[Double]).collect()

      val arrMean = new DescriptiveStatistics()
      genericArrayOps(mean).foreach(v => arrMean.addValue(v))
      val max = arrMean.getMax()
      val min = arrMean.getMin()
      val meanQ1 = arrMean.getPercentile(25)
      val meanQ2 = arrMean.getPercentile(50)
      val meanQ3 = arrMean.getPercentile(75)
      json=json merge org.json4s.jackson.renderJValue(convertDF.dtypes(0)._1 , List(min, meanQ1, meanQ2, meanQ3, max))
      // print(pretty(json))

    }catch{
      case e:Exception => println(e,"error")
    }
    return pretty(json)
  }
}
