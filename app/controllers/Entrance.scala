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
import java.net.URL
import scalax.io.JavaConverters._
import scalax.file.Path

/**
  * Created by superorange on 4/1/16.
  */
class Entrance @Inject()(db: Database)(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  //--------------regression transform---------------

  def reg_trans = Action {request =>
    request.session.get("username").map { user =>
      val DB = new DatabaseCon(db)
      Ok(html.mlTrans.regression_transform(InputForms.ModelParam,null, DB.getPre2Info(user), DB.getModelInfo(user,Utilities.linearModel), null, null,user))
    }.getOrElse{
      val DB = new DatabaseCon(db)
      Ok(html.mlTrans.regression_transform(InputForms.ModelParam,null, DB.getPre2Info("NULL"), DB.getModelInfo("NULL",Utilities.linearModel), null, null,"NULL"))
    }
  }
  //--------------regression training---------------
  def simp_reg = Action {

    def username = System.getenv("USER")
    println("USER= " +username)
    Ok(html.lrsimple(InputForms.elasticInput, null,username))

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
  def index = Action { request =>
    request.session.get("username").map { user =>

      Ok(html.index(user.toString))
    }.getOrElse {
      Ok(html.index("outsider"))
    }

    //delete("/home/pzq317/Desktop/SparkMLPlatform2/NULL/pre2")
  }
    /*def delete{
      //val path: Path = Path ("/home/pzq317/Desktop/SparkMLPlatform2/NULL")
      val path = Path.fromString("/home/pzq317/Desktop/SparkMLPlatform2/NULL")
      path.deleteRecursively()
      //path.deleteIfExists()
  }*/


  //-------------------Data Import-------------

  def dataimport_pre1 = Action {request =>
    InputForms.InputParam.fill("sss",false)
    request.session.get("username").map {user=>
      Ok(html.preprocess.dataimport_pre1(InputForms.InputParam,null,null,null,null,user))
    }.getOrElse {
      Ok(html.preprocess.dataimport_pre1(InputForms.InputParam,null,null,null,null,"NULL"))
    }
  }

  //------------------Data Import2-------------

  def dataimport_pre2 = Action { request =>
    request.session.get("username").map {user=>
      println("dataimport_pre2" + user)
      val DB = new DatabaseCon(db)
      Ok(html.preprocess.dataimport_pre2(InputForms.csvPathIn, DB.getPre1Info(user), null,user))
    }.getOrElse {
      val DB = new DatabaseCon(db)
      Ok(html.preprocess.dataimport_pre2(InputForms.csvPathIn, DB.getPre1Info("NULL"), null,"NULL"))
    }
  }
  //------------------Data Import Direct-------------

  def dataimport_nov = Action {
    request =>
      request.session.get("username").map {user=>
    Ok(html.preprocess.dataimport_nov(InputForms.ImportDirect,user))
      }.getOrElse {
        Ok(html.preprocess.dataimport_nov(InputForms.ImportDirect,"NULL"))
      }
  }

  //--------------regression training---------------
  def linear_reg_param = Action {request =>
    request.session.get("username").map {user=>
      println("dataimport_pre2" + user)
      val DB = new DatabaseCon(db)
      Ok(html.mlModel.linearRegression(InputForms.elasticInput, DB.getPre2Info(user), null,user))
    }.getOrElse {
      //println(DB.getPre2Info("NULL"))
      val DB = new DatabaseCon(db)
      println(DB.getPre2Info("NULL"))
      Ok(html.mlModel.linearRegression(InputForms.elasticInput, DB.getPre2Info("NULL"), null,"NULL"))
    }

  }
  //--------------logistic regression training---------------

  def log_reg = Action { request =>
    request.session.get("username").map { user =>
      println("dataimport_pre2" + user)
      val DB = new DatabaseCon(db)
      Ok(html.mlModel.logisticRegression(InputForms.elasticInput, DB.getPre2Info(user), null,user))
    }.getOrElse {
      val DB = new DatabaseCon(db)
      Ok(html.mlModel.logisticRegression(InputForms.elasticInput, DB.getPre2Info("NULL"), null,"NULL"))
    }
  }
  //---------------logistic regression transform--------------
  def log_trans = Action {request =>
    request.session.get("username").map { user =>
      val DB = new DatabaseCon(db)
      Ok(html.mlTrans.log_transform(InputForms.ModelParam,null, DB.getPre2Info(user), DB.getModelInfo(user,Utilities.logisticModel), null, null,user))
    }.getOrElse{
      val DB = new DatabaseCon(db)
      Ok(html.mlTrans.log_transform(InputForms.ModelParam,null, DB.getPre2Info("NULL"), DB.getModelInfo("NULL",Utilities.logisticModel), null, null,"NULL"))
    }
  }
    /*val DB = new DatabaseCon(db)

    Ok(html.mlModel.logisticRegression(InputForms.elasticInput, DB.getPre2Info(true), null))*/




  //----------------clustering --------------------

  def kmeans = Action {request =>
    request.session.get("username").map {user=>
      println("dataimport_pre2" + user)
      val DB = new DatabaseCon(db)
      Ok(html.mlModel.kmeans(InputForms.KmeansParam, DB.getPre2Info(user), null, null,user))
    }.getOrElse {
      val DB = new DatabaseCon(db)
      Ok(html.mlModel.kmeans(InputForms.KmeansParam, DB.getPre2Info("NULL"), null, null,"NULL"))
    }

  }

  def kmean_trans = Action {request =>
    request.session.get("username").map { user =>
      val DB = new DatabaseCon(db)
      Ok(html.mlTrans.kmeans(InputForms.ModelParam,null, DB.getPre2Info(user), DB.getModelInfo(user,Utilities.kmeansModel), null, null,user))
    }.getOrElse{
      val DB = new DatabaseCon(db)
      Ok(html.mlTrans.kmeans(InputForms.ModelParam,null, DB.getPre2Info("NULL"), DB.getModelInfo("NULL",Utilities.kmeansModel), null, null,"NULL"))
    }
  }
  //----------------PCA --------------------
  def pca = Action {
    request =>
      request.session.get("username").map { user =>
        val DB = new DatabaseCon(db)
        Ok(html.mlModel.pca(InputForms.KmeansParam, DB.getPre2Info(user), null,user))
      }.getOrElse {
        val DB = new DatabaseCon(db)
        Ok(html.mlModel.pca(InputForms.KmeansParam, DB.getPre2Info("NULL"), null,"NULL"))
      }
  }
      def pca_trans = Action {request =>
        request.session.get("username").map { user =>
          val DB = new DatabaseCon(db)
          Ok(html.mlTrans.pca(InputForms.ModelParam,null, DB.getPre2Info(user), DB.getModelInfo(user,Utilities.pcaModel), null, null, user))
        }.getOrElse{
          val DB = new DatabaseCon(db)
          Ok(html.mlTrans.pca(InputForms.ModelParam,null, DB.getPre2Info("NULL"), DB.getModelInfo("NULL",Utilities.pcaModel), null, null,"NULL"))
        }
      }
    /*val DB = new DatabaseCon(db)
  Ok(html.mlModel.pca(InputForms.KmeansParam, DB.getPre2Info(false),null))*/


  /*getMysession() = {

          var user = a.get("username").toString
          println(a.get("username"))
          "welcome "+user
          }*/
}