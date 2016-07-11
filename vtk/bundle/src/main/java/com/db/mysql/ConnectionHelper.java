package com.db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
   
public class ConnectionHelper
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

  try {
	  
	  //"autoReconnect=true&useSSL=false&" +
	  
	  //Class.forName("com.mysql.jdbc.Driver").newInstance();
	  Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
	  
      //conn =DriverManager.getConnection("jdbc:mysql://localhost/test?user=alex&password=alex");
	  conn =DriverManager.getConnection("jdbc:mysql://localhost/test", "alex" , "alex");

     

  } catch (SQLException ex) {
      // handle any errors
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
  } catch (InstantiationException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
} catch (IllegalAccessException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
} catch (ClassNotFoundException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
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