package controllers.util

import models. StatSummary
import play.api.data.Form
import play.api.data.Forms._


/**
  * Created by superorange on 9/6/16.
  */
object InputForms {
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
