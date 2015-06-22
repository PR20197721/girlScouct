<%@include file="/libs/foundation/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode" %>

<%
boolean zip = properties.get("zip",false);
boolean state = properties.get("state", false);
boolean councilCode = properties.get("council-code", false);
String path = properties.get("path","");

if(path.equals("") || (zip == false && state == false && councilCode == false) && WCMMode.fromRequest(request) == WCMMode.EDIT){
%>

	<p>**Please select at least one search type</p>
	
	<% }else{ 
	%>
	
	<p>Find Councils</p>
	
	<% if(zip == true){ %>
	<form class="zipSearch" action="<%= path %>" method="get">
		Zip Code: <input type="text" name="zip">
	</form>
	<% } %>
	
	<% if(state == true){ %>
	<form class="stateSearch" action="<%= path %>" method="get">
		State (Abbreviated e.g. 'NY'): <input type="text" name="state">
	</form>
	<% } %>
	
	<% if(councilCode == true){ %>
	<form class="councilCodeSearch" action="<%= path %>" method="get">
		Council Code: <input type="text" name="council-code">
	</form>
	<% } %>
	
	<div id = "<%= divId %>"></div>

<% } %>