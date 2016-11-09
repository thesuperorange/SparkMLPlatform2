package models

/**
  * Created by superorange on 4/29/16.
  */
case class RegressionModel(
                              coefficients: String,
                              intercept: Double,
                              pValues: Seq[Double],
                              coefficientStandardErrors: Seq[Double],
                              meanSquaredError: Double,
                              totalIter: Int,
                              timestamp:Long,
                              residualJson:String,
                              predictionJson:String,
                              labelJson:String,
                              resBoxJson:String,
                              resHistJson:String

                            )

