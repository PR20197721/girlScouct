<%

    java.sql.Connection conn =  com.db.mysql.ConnectionHelper.getConnection();
    out.println( conn );
    
%>