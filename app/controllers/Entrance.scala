package controllers

import javax.inject.Inject

import play.api.data.Form
import play.api.data.Forms._
import play.api.db.Database
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller, Session}
import views.html
import controllers.util.{DatabaseCon, InputForms, Utilities}

import scala.text


/**
  * Created by superorange on 4/1/16.
  */
class Entrance @Inject()(db: Database)(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  //--------------regression transform---------------

  def reg_trans = Action {request =>
    request.session.get("username").map { user =>
      val DB = new DatabaseCon(db)
      Ok(html.mlTrans.regression_transform(InputForms.ModelParam, DB.getPre2Info(user), DB.getModelInfo(Utilities.linearModel), null))
    }.getOrElse{
      val DB = new DatabaseCon(db)
      Ok(html.mlTrans.regression_transform(InputForms.ModelParam, DB.getPre2Info("NULL"), DB.getModelInfo(Utilities.linearModel), null))
    }
  }
  //--------------regression training---------------
  def simp_reg = Action {

    def username = System.getenv("USER")
    println("USER= " +username)
    Ok(html.lrsimple(InputForms.elasticInput, null))

  }



  //-------index
  def signin = Action {
    var str = new Session
    println(str.get("username"))
    Ok(html.signin( InputForms.SignIn))
  }
  def signup= Action {

    Ok(html.signup( InputForms.SignUp))
  }
  def logout= Action {


    Ok(html.index("outsider")).withNewSession
  }

  //-------index
  def index = Action { request=>
    request.session.get("username").map { user =>
      Ok(html.index(user.toString))
    }.getOrElse {
      Ok(html.index("outsider"))
    }
  }

  //-------------------Data Import-------------

  def dataimport_pre1 = Action {
    InputForms.InputParam.fill("sss",false)
    Ok(html.preprocess.dataimport_pre1(InputForms.InputParam,null,null,null,null))
  }

  //------------------Data Import2-------------

  def dataimport_pre2 = Action { request =>
    request.session.get("username").map {user=>
      println("dataimport_pre2" + user)
      val DB = new DatabaseCon(db)
      Ok(html.preprocess.dataimport_pre2(InputForms.csvPathIn, DB.getPre1Info(user), null))
    }.getOrElse {
      val DB = new DatabaseCon(db)
      Ok(html.preprocess.dataimport_pre2(InputForms.csvPathIn, DB.getPre1Info("NULL"), null))
    }
  }
  //------------------Data Import Direct-------------

  def dataimport_nov = Action {
    Ok(html.preprocess.dataimport_nov(InputForms.ImportDirect))
  }

  //--------------regression training---------------
  def linear_reg_param = Action {request =>
    request.session.get("username").map {user=>
      println("dataimport_pre2" + user)
      val DB = new DatabaseCon(db)
      Ok(html.mlModel.linearRegression(InputForms.elasticInput, DB.getPre2Info(user), null))
    }.getOrElse {
      val DB = new DatabaseCon(db)
      Ok(html.mlModel.linearRegression(InputForms.elasticInput, DB.getPre2Info("NULL"), null))
    }

  }
  //--------------logistic regression training---------------

  def log_reg = Action {request =>
    request.session.get("username").map {user=>
      println("dataimport_pre2" + user)
      val DB = new DatabaseCon(db)
      Ok(html.mlModel.logisticRegression(InputForms.elasticInput, DB.getPre2Info(user), null))
    }.getOrElse {
      val DB = new DatabaseCon(db)
      Ok(html.mlModel.logisticRegression(InputForms.elasticInput, DB.getPre2Info("NULL"), null))
    }

    /*val DB = new DatabaseCon(db)

    Ok(html.mlModel.logisticRegression(InputForms.elasticInput, DB.getPre2Info(true), null))*/

  }


  //----------------clustering --------------------

  def kmeans = Action {request =>
    request.session.get("username").map {user=>
      println("dataimport_pre2" + user)
      val DB = new DatabaseCon(db)
      Ok(html.mlModel.kmeans(InputForms.KmeansParam, DB.getPre2Info(user), null, null))
    }.getOrElse {
      val DB = new DatabaseCon(db)
      Ok(html.mlModel.kmeans(InputForms.KmeansParam, DB.getPre2Info("NULL"), null, null))
    }

  }
  //----------------PCA --------------------
  def pca = Action {
    request =>
      request.session.get("username").map {user=>
        val DB = new DatabaseCon(db)
        Ok(html.mlModel.pca(InputForms.KmeansParam, DB.getPre2Info(user),null))
      }.getOrElse {
        val DB = new DatabaseCon(db)
        Ok(html.mlModel.pca(InputForms.KmeansParam, DB.getPre2Info("NULL"),null))
      }

    /*val DB = new DatabaseCon(db)
  Ok(html.mlModel.pca(InputForms.KmeansParam, DB.getPre2Info(false),null))*/
  }

  /*getMysession() = {

          var user = a.get("username").toString
          println(a.get("username"))
          "welcome "+user
          }*/
}