
<%@page import="java.util.Iterator"%>
<%@ page import="org.girlscouts.vtk.models.user.*,org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>

<%
HttpSession session = request.getSession();

User user= (User) session.getValue("VTK_user");

java.util.List <org.joda.time.DateTime> sched = new java.util.ArrayList<org.joda.time.DateTime>();

if( user.getYearPlan().getSchedule()==null ) {out.println("No schedule"); return; }

String str =  user.getYearPlan().getSchedule().getDates();
java.util.StringTokenizer t= new java.util.StringTokenizer( str, ",");
while( t.hasMoreElements())
		sched.add( new org.joda.time.DateTime( Long.parseLong( t.nextToken() ) ) );


%>       
  
       <h1>Manage Calendar</h1>
       <div id="locMsg"></div>
       
       <% 
       for(int i=0;i<sched.size();i++){
			%><%@include file="include/manageCalendar.jsp" %><% 
	   }
       %>


<%!
   
org.joda.time.format.DateTimeFormatter fmt = org.joda.time.format.DateTimeFormat.forPattern("MMM d, yyyy HH:mm a");

java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy hh:mm");
java.text.SimpleDateFormat dateFormat1 = new java.text.SimpleDateFormat("MMM dd, yyyy");
java.text.SimpleDateFormat dateFormat2 = new java.text.SimpleDateFormat("hh:mm");
java.text.SimpleDateFormat dateFormat3 = new java.text.SimpleDateFormat("dd/MM/yyyy hh:mm a");
java.text.SimpleDateFormat dateFormat4 = new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm a");



org.joda.time.format.DateTimeFormatter fmtDate = org.joda.time.format.DateTimeFormat.forPattern("MM/d/yyyy");
org.joda.time.format.DateTimeFormatter fmtHr = org.joda.time.format.DateTimeFormat.forPattern("HH:mm");
org.joda.time.format.DateTimeFormatter fmtHr1 = org.joda.time.format.DateTimeFormat.forPattern("hh:mm");

%>
      
       