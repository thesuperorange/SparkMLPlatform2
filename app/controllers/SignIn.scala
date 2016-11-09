package controllers

import java.io.File
import javax.inject.Inject
import org.mindrot.jbcrypt.BCrypt
import controllers.util.{DatabaseCon, InputForms, Utilities}
import play.api.db.Database
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller, Session}
import views.html
/**
  * Created by pzq317 on 10/18/16.
  */

class SignIn @Inject()(db: Database)(val messagesApi: MessagesApi) extends Controller with I18nSupport{

  def signIn = Action{ implicit request =>
    val DB = new DatabaseCon(db)
    InputForms.SignIn.bindFromRequest.fold(
      formWithErrors => {
        println("ERROR" + formWithErrors)
        BadRequest("error in sign in")
      }, { case (username,password) =>
        println("xxxxx"+username)
        var test = DB.checkAccount(username,password)
        if(test){
          Redirect("/").withSession(
            "username" -> username)
        }
        else {
          Ok(html.signin(InputForms.SignIn))
        }

      }
    )
  }
  def signUp = Action{ implicit request =>
    val DB = new DatabaseCon(db)
    InputForms.SignUp.bindFromRequest.fold(
      formWithErrors => {
        println("ERROR" + formWithErrors)
        BadRequest(views.html.signup(formWithErrors))
      }, { case (username,email,password) =>
        var a = BCrypt.hashpw(password, BCrypt.gensalt(12))
        //println("hashed"+a)
        //var b = BCrypt.checkpw(password, a)
        //println(b)
        var test = DB.insertUserData(username,email,a)

        if(test){
          Ok(html.signup(InputForms.SignUp))
          //Redirect("/")
        }
        else {
          Redirect("/").withSession(
            "username" -> username)
          //show()
        }
      }
    )
  }
  def show = Action{ request=>
    request.session.get("username").map { user =>

      Ok(html.index(user.toString()))
    }.getOrElse {
      Unauthorized("Oops, you are not connected")
    }

  }

}
