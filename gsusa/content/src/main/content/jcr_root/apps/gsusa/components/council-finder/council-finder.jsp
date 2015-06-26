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

	<% } else {
		path = path + ".html";
	%>
	<h3>Find Councils</h3>
	<% if(zip == true || state == true || councilCode == true) { %>
	<ul class="block-grid">
	<% } %>
		<% if(zip == true) { %>
			<li>
				<form class="zipSearch" action="<%= path %>" method="get" />
					<h6>By Zip Code</h6>
					<p>Find the Girl Scout<br/> Council Serving Your Area</p>
					<section><input type="text" name="zip" /></section>
				</form>
			</li>
		<% } %>

			<% if(state == true) { %>
			<li>
				<form class="stateSearch" action="<%= path %>" method="get">
					<h6>By State</h6>
					<p>Find a Girl Scout<br/> Council by State</p>
					<cq:include script="state-form.jsp" />
				</form>
			</li>
		<% } %>

		<% if(councilCode == true) { %>
			<li>
				<form class="councilCodeSearch" action="<%= path %>" method="get">
					<h6>By Council Code:</h6>
					<p>Find a Girl Scout<br/> Council by Council Name</p>
					<section><input type="text" name="council-code" /></section>
				</form>
			</li>
		<% } %>
	<% if(zip == true || state == true || councilCode == true) { %>
	</ul>
	<% } %>
<% } %>