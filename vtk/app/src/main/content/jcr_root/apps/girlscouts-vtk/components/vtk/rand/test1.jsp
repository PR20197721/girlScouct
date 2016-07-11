<%

    java.sql.Connection conn =  new com.db.mysql.ConnectionHelper().getConnection();
    out.println( conn );
    
%>