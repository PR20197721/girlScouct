<%@page import="org.girlscouts.vtk.auth.models.User" %>
<% 
	User user = (User)session.getAttribute(User.class.getName());
	// TODO
	String logoutUrl = "/content/girlscouts-vtk/controllers/auth.html";
	String language = "en";
	
	if (user == null) {
	    %>girlscouts.components.login.sayHello('loggedout');<%
    } else {
	    String name = user.getName();
	    %>$.cookie('girl-scout-name', '<%= name %>');<%
	    %>girlscouts.components.login.sayHello('loggedin', '<%= name %>'); <%
    }
%>