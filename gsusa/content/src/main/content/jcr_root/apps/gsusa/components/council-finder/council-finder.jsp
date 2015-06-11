<%@include file="/libs/foundation/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode" %>

<%
boolean zip = properties.get("zip",false);
boolean state = properties.get("state", false);
boolean councilCode = properties.get("council-code", false);

if(zip == false && state == false && councilCode == false && WCMMode.fromRequest(request) == WCMMode.EDIT){
%>

	<p>**Please select at least one search type</p>
	
	<% }else{ 
	long rand = System.currentTimeMillis();
	String divId = "council-finder-output" + rand;
	%>
	
	<p>Find Councils</p>
	
	<% if(zip == true){ %>
	<form class="zipSearch">
		Zip Code: <input type="text" name="zip">
		<input type="hidden" name="url" value="<%= currentNode.getPath() %>">
		<input type="hidden" name="div-id" value= "<%= divId %>">
	</form>
	<% } %>
	
	<% if(state == true){ %>
	<form class="stateSearch">
		State: <input type="text" name="state">
		<input type="hidden" name="url" value="<%= currentNode.getPath() %>">
		<input type="hidden" name="div-id" value= "<%= divId %>">
	</form>
	<% } %>
	
	<% if(councilCode == true){ %>
	<form class="councilCodeSearch">
		Council Code: <input type="text" name="council-code">
		<input type="hidden" name="url" value="<%= currentNode.getPath() %>">
		<input type="hidden" name="div-id" value= "<%= divId %>">
	</form>
	<% } %>
	
	<div id = "<%= divId %>"></div>

<% } %>