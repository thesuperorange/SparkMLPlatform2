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
    println(user)
    try {
      val stmt = conn.createStatement
      println(email,password,user)
      //val rs = stmt.executeQuery("SELECT * from " + tablename )
      println("SELECT * from " + tablename +" where id = "+"'"+user+"'")
      val rs = stmt.executeQuery("SELECT * from " + tablename +" where id = "+"'"+user+"'" )
      if (rs.next()){
        println("user exist try another one")
        return true
      }
      else{
        println("success")
        stmt.executeUpdate("INSERT INTO " + tablename + " VALUES ('" + user + "','" + email + "','"+ password + "')")
        return false
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

    println(user)
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

  def insertPre1(outputPathName:String, fromPath: String,user:String)={
    //------sql connect------
    val conn = db.getConnection()
    val tablename = Utilities.pre1Table
    println(user)
    try {
      val stmt = conn.createStatement
      println(outputPathName,fromPath,user.toString);
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
  def getModelInfo(modelType: String): List[String] = {
    val tablename = Utilities.modelTable
    val conn = db.getConnection()
    var dbname = List[String]()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT * from " + tablename + " where type='" + modelType+"'")
      while (rs.next()) {
        dbname = rs.getString("name") :: dbname
      }
    } finally {
      conn.close()
    }
    return dbname
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
    return dbname
  }

  def getPre2Info(users:String): List[String] = {
    val tablename = Utilities.pre2Table
    val conn = db.getConnection()
    var dbname = List[String]()
    try {
      val stmt = conn.createStatement
      println(users)
      if(users =="NULL") {
        val rs = stmt.executeQuery("SELECT * from " + tablename + " where users IS NULL" )
        while (rs.next()) {
          dbname = rs.getString("name") :: dbname
        }
      }
      else{
        val rs = stmt.executeQuery("SELECT * from " + tablename + " where users='" + users + "'")
        while (rs.next()) {
          dbname = rs.getString("name") :: dbname
        }
      }


    } finally {
      conn.close()
    }
    return dbname
  }



  def getPre1Info(user:String): List[String] = {
    val tablename = Utilities.pre1Table
    val conn = db.getConnection()
    var dbname = List[String]()
    try {
      val stmt = conn.createStatement
      println("SELECT * from " + tablename +"where users = "+user)
      val rs = stmt.executeQuery("SELECT * from " + tablename +" where users = "+"'"+user+"'" )
      while (rs.next()) {
        dbname = rs.getString("name") :: dbname
      }
    } finally {
      conn.close()
    }
    return dbname
  }
  def insertModel(outputPathName:String, modelType: String)={
    //------sql connect------
    val conn = db.getConnection()
    val tablename = "Model"

    try {
      val stmt = conn.createStatement
      stmt.executeUpdate("INSERT INTO " + tablename + " VALUES ('" + outputPathName + "','" + modelType + "')");

    } finally {
      conn.close()
    }
  }

}
