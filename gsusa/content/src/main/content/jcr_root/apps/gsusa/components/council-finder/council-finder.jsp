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
	<p>Find Councils</p>
	<% if(zip == true || state == true || councilCode == true) { %>
	<ul class="block-grid">
	<% } %>
		<% if(zip == true) { %>
			<li>
				<form class="zipSearch" action="<%= path %>" method="get" />
					<h6>By Zip Code</h6>
					<p>Find the Girl Scout<br/> Council Serving Your Area</p>
					<input type="text" name="zip" />
				</form>
			</li>
		<% } %>

			<% if(state == true) { %>
			<li>
				<form class="stateSearch" action="<%= path %>" method="get">
					<h6>By State</h6>
					<p>Find a Girl Scout<br/> Council by State</p>
					<input type="text" name="state" />
				</form>
			</li>
		<% } %>

		<% if(councilCode == true) { %>
			<li>
				<form class="councilCodeSearch" action="<%= path %>" method="get">
					<h6>By Council Code:</h6>
					<p>Find a Girl Scout<br/> Council by Council Name</p>
					<input type="text" name="council-code" />
				</form>
			</li>
		<% } %>
	<% if(zip == true || state == true || councilCode == true) { %>
	</ul>
	<% } %>
<% } %>