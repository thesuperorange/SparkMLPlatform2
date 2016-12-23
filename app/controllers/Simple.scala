package controllers

import java.io.File
import javax.inject.Inject

import controllers.util.{InputForms, DatabaseCon, Utilities}
import play.api.db.Database
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Controller, Action}
import views.html

/**
  * Created by superorange on 9/14/16.
  */
class Simple @Inject()(db: Database)(val messagesApi: MessagesApi) extends Controller with I18nSupport {
  //---------------------save model-------------------

  def saveModel = Action { implicit request =>

    val modelFolder = Utilities.modelFolder
    InputForms.modelform.bindFromRequest.fold(
      formWithErrors => BadRequest("error"), {
        case (timestamp, modelType,outputFolder) =>
          var jeffrey = ""
          request.session.get("username").map { user =>
            jeffrey = user
          }.getOrElse {
            jeffrey = "NULL"
          }

          try {
            val DB = new DatabaseCon(db)

            val dir = new File(jeffrey+"/"+modelType+"/"+timestamp);
            val newName = new File(jeffrey+"/"+modelType+"/"+outputFolder);
            println("rename from:" +jeffrey+"/"+modelType+"/"+timestamp+"  to: "+jeffrey+"/"+modelType+"/"+outputFolder)

            if ( dir.isDirectory() ) {
              dir.renameTo(newName);
            } else {
              dir.mkdir();
              dir.renameTo(newName);
            }
            DB.insertModel(outputFolder,modelType,jeffrey)
          }
          catch {
            case e: Exception => {
              println("error in save model:" + e)

            }
          }
          Ok(html.showtext("save to "+outputFolder+" successfully",jeffrey)) }
    )

  }
}
