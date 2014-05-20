<% 
	String state = (String)session.getAttribute("state");
	
	if (state == null || state.isEmpty()) {
	    %><%@include file="./not-authenticated.jsp" %><%
    } else if (state.equals("authenticated")){
	    %><%@include file="./authenticated.jsp"%><%
    }
%>