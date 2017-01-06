package controllers.util

import models. StatSummary
import play.api.data.Form
import play.api.data.Forms._


/**
  * Created by superorange on 9/6/16.
  */
object InputForms {
  val download= Form(

      "csvPath" -> nonEmptyText

  )
  val SignIn= Form(
    tuple(
      "username" -> text,
      "password" -> text

    )
  )
  val SignUp= Form(
    tuple(
      "username" -> nonEmptyText,
      "email"->email,
      "password" -> nonEmptyText

    )
  )
  val ImportDirect= Form(
    tuple(
      "inputpath" -> text,
      "header"->boolean,
      "outputfolder" -> text

    )
  )

  val elasticInput = Form(
    tuple(
      "inputpath" -> text,
      "maxIter" -> text,
      "regParam" -> text,
      "elaParam" -> text
      /*"maxIter" -> default(text, "10"),
      "regParam" -> default(text,"0.3"),
      "elaParam" -> default(text, "0.8")*/

    )
  )
  val InputParam = Form(
    tuple(
      "path" -> default(text,"novalue~novalue"),
      "header"->boolean
    )
  )


  val summaryForm: Form[StatSummary] = Form(
    mapping(
      "max" -> text,
      "min" -> text,
      "mean" -> text,
      "variance" -> text,
      "numNonZero" -> text,
      "count" -> text
    )(StatSummary.apply)(StatSummary.unapply)
  )


  val csvPathIn = Form(
    "path" -> nonEmptyText
  )
  val guestSelect = Form(
    tuple(
    "file"->nonEmptyText,
    "header"->boolean
    )
  )

//save model
  val modelform = Form(
    tuple(
      "timestamp" ->nonEmptyText,
      "type"->nonEmptyText,
      "outputFolder" -> nonEmptyText
    )
  )

  val KmeansParam = Form(
    tuple(
      "path" -> text,
      "k" -> text
    )
  )

  val ModelParam = Form(
    tuple(
      "inputpath" -> text,
      "model"->text
    )
  )

}
