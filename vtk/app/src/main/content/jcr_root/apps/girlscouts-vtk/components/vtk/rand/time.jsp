
<%@ page import="java.util.*, java.text.*" %>
<%
/*
java.util.Calendar cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"));
out.println( cal.getTime() );
out.println("<hr/>");
java.util.Calendar cal1 = null; //java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("ETC"));
cal1.setTime(cal.getTime() );
cal1.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
out.println( cal1.getTime() );
out.println("<hr/>");
java.util.Calendar cal2 = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("CTC"));
out.println( cal1.getTime() );
*/


java.util.Date d= new java.util.Date();
d.setTime(Long.parseLong("1411400916620"));
out.println(d +"<hr/>");

try{
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy hh:mm:ss Z");
    Date datetime = new Date();

    out.println("<br/>date "+sdf.format(datetime));

    sdf.setTimeZone(TimeZone.getTimeZone("EST"));
    out.println("<br/>EST "+ sdf.format(datetime));

    
    
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

    out.println("<br/>GMT "+ sdf.format(datetime));

    sdf.setTimeZone(TimeZone.getTimeZone("GMT+13"));

   	out.println("<br/>GMT+13 "+ sdf.format(datetime));

    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

    out.println("<br/>utc "+sdf.format(datetime));

    Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));

    DateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");    
    formatter.setTimeZone(TimeZone.getTimeZone("GMT+13"));  

    String newZealandTime = formatter.format(calendar.getTime());

   	out.println("<br/>using calendar "+newZealandTime);

}catch (Exception e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
}

%>