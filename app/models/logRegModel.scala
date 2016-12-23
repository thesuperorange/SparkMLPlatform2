package models

/**
  * Created by superorange on 7/12/16.
  */
case class logRegModel(

                        coefficients: String,
                        intercept: Double,
                        areaUnderROC: Double,
                        fmeasure: Double,
                        bestTH: Double,
                        precision: Double,
                        recall: Double,
                        TPR:String,
                        FPR:String,
                        precisionJson:String,
                        recallJson:String,
                        fmeasureJson:String,
                        threshold:String,
                        timestamp:Long
                      )

