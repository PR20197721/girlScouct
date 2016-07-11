<%@include file="/libs/foundation/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode" %>

<%
boolean zip = properties.get("zip",false);
boolean state = properties.get("state", false);
boolean councilName = properties.get("council-name", false);
String path = properties.get("path","");

if(path.equals("") || (zip == false && state == false && councilName == false) && WCMMode.fromRequest(request) == WCMMode.EDIT){
%>

	<p>**Please select at least one search type</p>

	<% } else {
		path = path + ".html";
	%>
	<h3>Find Councils</h3>
	<% if(zip == true || state == true || councilName == true) { %>
	<ul class="block-grid">
	<% } %>
		<% if(zip == true) { %>
			<li>
				<form class="zipSearch" action="<%= path %>" method="get" />
					<h6>By Zip Code</h6>
					<p>Find the Girl Scout<br/> Council Serving Your Area</p>
					<section>
						<input required type="text" pattern="[0-9]*" name="zip" placeholder="Enter ZIP Code" />
						<input type="submit" value="Go" class="button tiny" />
					</section>
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

		<% if(councilName == true) { %>
			<li>
				<form class="councilCodeSearch" action="<%= path %>" method="get">
				</form>
			</li>
			<script type="text/javascript">
			$(document).ready(function() {
				$.get("/councilfinder/ajax_results.asp?short=yes", function(data){
					var request = "";
					<%
					if(slingRequest.getParameter("council-code") != null){
						%> request = <%= slingRequest.getParameter("council-code") %> <%
					}
					%>
					var json = JSON.parse(data);
					var codeSearch = $(".councilCodeSearch");
					var appendStr = "<h6>By Council Name:</h6>"+
							"<p>Find a Girl Scout<br/> Council by Council Name</p>" +
							"<section><select required name=\"council-code\"><option value=\"\">Select a Council:</option>";
					for(var i=0; i < json.councils.length; i++) {
						var option = "<option ";
						if(request == json.councils[i].councilCode){
							option = "<option selected=\"selected\" ";
						}
						appendStr = appendStr + option + "value=\"" + json.councils[i].councilCode +
								"\">" + json.councils[i].councilShortName + "</option>";
					}
					appendStr = appendStr + "</select><input type=\"submit\" value=\"Go\" class=\"button tiny\"/></section>";
					codeSearch.append(appendStr);
				}).fail(function() {
					var codeSearch = $(".councilCodeSearch");
					var appendStr = "<h6>By Council Name:</h6>"+
					"<p>Find a Girl Scout<br/> Council by Council Name</p>" +
					"<section><select required name=\"council-code\"><option value=\"\">Select a Council:</option>";
					appendStr = appendStr + "</select><input type=\"submit\" value=\"Go\" class=\"button tiny\"/></section>";
					codeSearch.append(appendStr);
				});
			});
			</script>
		<% } %>
	<% if(zip == true || state == true || councilName == true) { %>
	</ul>
	<% } %>
<% } %>
