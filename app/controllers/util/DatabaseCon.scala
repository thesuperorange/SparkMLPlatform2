package controllers.util

import java.lang.ProcessBuilder.Redirect
import play.api.db.Database
import org.mindrot.jbcrypt.BCrypt
/**
  * Created by superorange on 9/12/16.
  */
class DatabaseCon(db: Database) {
  //-------------------DB-------------
  def insertUserData(user:String, email: String,password:String):Boolean={
    //------sql connect------
    val conn = db.getConnection()
    val tablename = Utilities.userData
    //println(user)
    try {
      val stmt = conn.createStatement
      //println(email,password,user)
      //val rs = stmt.executeQuery("SELECT * from " + tablename )
      //println("SELECT * from " + tablename +" where id = "+"'"+user+"'")
      val rs = stmt.executeQuery("SELECT * from " + tablename +" where id = "+"'"+user+"'" )
      if (rs.next()){
        println("user exist try another one")
         true
      }
      else{
        //println("success")
        stmt.executeUpdate("INSERT INTO " + tablename + " VALUES ('" + user + "','" + email + "','"+ password + "')")
         false
      }
      //stmt.executeUpdate("INSERT INTO " + tablename + " VALUES ('" + user + "','" + email + "','"+ password + "')");

    } finally {
      conn.close()
    }
  }
  def checkAccount(user:String, password: String):Boolean={
    //------sql connect------
    val conn = db.getConnection()
    val tablename = Utilities.userData

    //println(user)
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT * from " + tablename +" where id = "+"'"+user+"'" )
      if(rs.next()){
        var secret = rs.getString("password")
        var b = BCrypt.checkpw(password, secret)
        if(b){
          println("Welcome")
          return true
        }
        else{
          println("password invalid")
          return false
        }
      }else{
        println("no account here")
        return false
      }

    } finally {
      conn.close()
    }
  }
  def removeNULL()={
    //------sql connect------
    val conn = db.getConnection()
    val tablename1 = Utilities.pre1Table
    val tablename2 = Utilities.pre2Table
    val tablename3 = Utilities.modelTable

    try {
      val stmt = conn.createStatement

      stmt.executeUpdate("delete from " + tablename1 + " where users = 'NULL'" )
      stmt.executeUpdate("delete from " + tablename2 + " where users = 'NULL'" )
      stmt.executeUpdate("delete from " + tablename3 + " where users = 'NULL'" )

    } finally {
      conn.close()
    }
  }
  def insertPre1(outputPathName:String, fromPath: String,user:String)={
    //------sql connect------
    val conn = db.getConnection()
    val tablename = Utilities.pre1Table
    //println(user)
    try {
      val stmt = conn.createStatement
      //println(outputPathName,fromPath,user.toString);
      stmt.executeUpdate("INSERT INTO " + tablename + " VALUES ('" + outputPathName + "','" + fromPath + "','"+ user + "')");

    } finally {
      conn.close()
    }
  }


  def insertPre2(outputPathName: String, fromPath: String, withLabel: Boolean,user:String) = {


    //------sql connect------
    val conn = db.getConnection()
    val tablename = Utilities.pre2Table

    try {
      val stmt = conn.createStatement
      stmt.executeUpdate("INSERT INTO " + tablename + " VALUES ('" + outputPathName + "','" + fromPath + "'," + withLabel +",'"+ user + "')");

    } finally {
      conn.close()
    }

  }
  def getModelInfo(user:String,modelType: String): List[String] = {
    val tablename = Utilities.modelTable
    val conn = db.getConnection()
    var dbname = List[String]()
    try {
      val stmt = conn.createStatement
      //println("SELECT * from " + tablename + " where type='" + modelType+"'"+"AND where users = '" +user+"'")
      val rs = stmt.executeQuery("SELECT * from " + tablename + " where type='" + modelType+"'"+"AND users = '" +user+"'")
      while (rs.next()) {
        dbname = rs.getString("name") :: dbname
      }
    } finally {
      conn.close()
    }
     dbname
  }


  def getPre2Info(withLabel: Boolean): List[String] = {
    val tablename = Utilities.pre2Table
    val conn = db.getConnection()
    var dbname = List[String]()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT * from " + tablename + " where label=" + withLabel)
      while (rs.next()) {
        dbname = rs.getString("name") :: dbname
      }
    } finally {
      conn.close()
    }
     dbname
  }

  def getPre2Info(users:String,withLabel: Boolean): List[String] = {
    val tablename = Utilities.pre2Table
    val conn = db.getConnection()
    var dbname = List[String]()
    try {
      val stmt = conn.createStatement
      //println(users)
      if(users =="NULL") {
        //println("SELECT * from " + tablename + " where users='" + users +"'"+" AND label = "+ withLabel )
        val rs = stmt.executeQuery("SELECT * from " + tablename + " where users='" + users +"'" +" AND label =" + withLabel )
        while (rs.next()) {
          dbname = rs.getString("name") :: dbname
        }
      }
      else{
        //val rs = stmt.executeQuery("SELECT * from " + tablename + " where users='" + users +"AND label = '" + withLabel + "'")
        val rs = stmt.executeQuery("SELECT * from " + tablename + " where users='" + users +"'" +" AND label =" + withLabel )
        while (rs.next()) {
          dbname = rs.getString("name") :: dbname
        }
      }


    } finally {
      conn.close()
    }
     dbname
  }
  def getPre2Info(users:String): List[String] = {
    val tablename = Utilities.pre2Table
    val conn = db.getConnection()
    var dbname = List[String]()
    try {
      val stmt = conn.createStatement
      //println(users)
      if(users =="NULL") {
        //println("SELECT * from " + tablename + " where users='" + users +"'"+" AND label = "+ withLabel )
        val rs = stmt.executeQuery("SELECT * from " + tablename + " where users='" + users +"'" )
        while (rs.next()) {
          dbname = rs.getString("name") :: dbname
        }
      }
      else{
        //val rs = stmt.executeQuery("SELECT * from " + tablename + " where users='" + users +"AND label = '" + withLabel + "'")
        val rs = stmt.executeQuery("SELECT * from " + tablename + " where users='" + users +"'"  )
        while (rs.next()) {
          dbname = rs.getString("name") :: dbname
        }
      }


    } finally {
      conn.close()
    }
     dbname
  }



  def getPre1Info(user:String): List[String] = {
    val tablename = Utilities.pre1Table
    val conn = db.getConnection()
    var dbname = List[String]()
    try {
      val stmt = conn.createStatement
      //println("SELECT * from " + tablename +" where users = "+user)
      val rs = stmt.executeQuery("SELECT * from " + tablename +" where users = "+"'"+user+"'" )
      while (rs.next()) {
        dbname = rs.getString("name") :: dbname
      }
    } finally {
      conn.close()
    }
     dbname
  }
  def getPathInfo(user:String): List[String] = {
    val tablename = Utilities.path
    val conn = db.getConnection()
    var dbname = List[String]()
    try {
      val stmt = conn.createStatement
      //println("SELECT * from " + tablename +" where users = "+user)
      val rs = stmt.executeQuery("SELECT * from " + tablename +" where users = "+"'"+user+"'" )
      while (rs.next()) {
        dbname = rs.getString("name") :: dbname
      }
    } finally {
      conn.close()
    }
     dbname
  }

  def getPath(name:String): String = {
    val tablename = Utilities.path
    val conn = db.getConnection()
    var pa = ""
    try {
      val stmt = conn.createStatement
      //println("SELECT * from " + tablename +" where name = "+"'"+name+"'")
      val rs = stmt.executeQuery("SELECT * from " + tablename +" where name = "+"'"+name+"'" )

        rs.next()
        pa = rs.getString("path")

    } finally {
      conn.close()
    }
     pa
  }


  def insertModel(outputPathName:String, modelType: String, user:String)={
    //------sql connect------
    val conn = db.getConnection()
    val tablename = "Model"
    //println(outputPathName,modelType)
    try {
      val stmt = conn.createStatement
      stmt.executeUpdate("INSERT INTO " + tablename + " VALUES ('" + outputPathName + "','" + modelType +"','"+ user +"')");

    } finally {
      conn.close()
    }
  }

}
