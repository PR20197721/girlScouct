package com.db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
   
public class Alex
{
	/*
  private String url;
  private static ConnectionHelper instance;
  public ConnectionHelper()
  {
    try {
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      url = "jdbc:mysql://localhost:3306/test";
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
  */
  public Connection getConnection(){
  Connection conn = null;
  System.err.println ("test mysql conn...");

  try {
	  
	  //"autoReconnect=true&useSSL=false&" +
	  
	  //Class.forName("com.mysql.jdbc.Driver").newInstance();
	  Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
	  
      conn =DriverManager.getConnection("jdbc:mysql://localhost:3306/test?user=alex&password=alex&serverTimezone=UTC");
	  //conn =DriverManager.getConnection("jdbc:mysql://localhost/test", "alex" , "alex");

     

  } catch (SQLException ex) {
      // handle any errors
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
     // ex.printStackTrace();
  } catch (Exception ex) {
  
	  ex.printStackTrace();
}

  return conn;
}//edn 
  
  
  /*
  public static Connection getConnection__123() throws SQLException {
	  
    if (instance == null) {
      instance = new ConnectionHelper();
    }
    try {
      return DriverManager.getConnection(instance.url, "root", "root");
    }
    catch (SQLException e) {
      throw e;
    }
  }
  public static void close(Connection connection)
  {
    try {
      if (connection != null) {
      connection.close();
   }
  }
    catch (SQLException e) {
      e.printStackTrace();
    }
  }
  */
}