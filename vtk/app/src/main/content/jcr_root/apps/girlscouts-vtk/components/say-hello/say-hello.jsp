<%@page import="org.girlscouts.vtk.auth.models.User" %>
<% 
	User user = (User)session.getAttribute("org.girlscouts.vtk.auth.models.User");
	
	if (user == null) {
	    %><%@include file="./not-authenticated.jsp" %><%
    } else {
	    %><%@include file="./authenticated.jsp"%><%
    }
%>