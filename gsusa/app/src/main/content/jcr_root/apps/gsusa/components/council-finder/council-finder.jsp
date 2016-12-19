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
				<form class="zipSearch" name="zipSearch">
					<h6>By Zip Code</h6>
					<p>Find the Girl Scout<br/> Council Serving Your Area</p>
					<section>
						<input required type="text" pattern="[0-9]*" name="zip" placeholder="Enter ZIP Code" />
						<input type="submit" value="Go" class="button tiny" />
					</section>
				</form>
			</li>
    <script>
    	$(document).ready(function(){
    		// zip
    		zipFormSubmitted = false;
		    $('.council-finder form[name="zipSearch"]').submit(function(){
		    	if (zipFormSubmitted) {
		    		return false;
		    	}

		        var zip = $(this).find('input[name="zip"]').val();
			    var redirectUrl = '<%= resourceResolver.map(path) %>'; 
			    var currentUrl = window.location.href;
			    var isSameUrl = currentUrl.substring(0, currentUrl.indexOf('.html')) == redirectUrl.substring(0, redirectUrl.indexOf('.html'));
			    var queryPos = currentUrl.indexOf('?');
			    if (queryPos != -1) {
			    	var queryStr = currentUrl.substring(queryPos);
			    	var hashPos = queryStr.indexOf('#');
			    	if (hashPos != -1) {
			    		queryStr = queryStr.substr(0, hashPos);
			    	}
			    	redirectUrl += queryStr;
			    }
			    redirectUrl = redirectUrl + '#' + zip;
			    window.location.href = redirectUrl;
			    if (isSameUrl) {
			    	window.location.reload();
			    }
		    
		    	zipFormSubmitted = true;
		        return false;
		    });
	    });
    </script>
			
		<% } %>

			<% if(state == true) { %>
			<li>
				<form class="stateSearch" name="stateSearch">
					<h6>By State</h6>
					<p>Find a Girl Scout<br/> Council by State</p>
					<cq:include script="state-form.jsp" />
				</form>
			</li>
    <script>
    	$(document).ready(function(){
    		// state
    		stateFormSubmitted = false;
		    $('.council-finder form[name="stateSearch"]').submit(function(){
		    	if (stateFormSubmitted) {
		    		return false;
		    	}

		        var state = $(this).find('select[name="state"]').val();
			    var redirectUrl = '<%= resourceResolver.map(path) %>'; 
			    var currentUrl = window.location.href;
			    var isSameUrl = currentUrl.substring(0, currentUrl.indexOf('.html')) == redirectUrl.substring(0, redirectUrl.indexOf('.html'));
			    var queryPos = currentUrl.indexOf('?');
			    if (queryPos != -1) {
			    	var queryStr = currentUrl.substring(queryPos);
			    	var hashPos = queryStr.indexOf('#');
			    	if (hashPos != -1) {
			    		queryStr = queryStr.substr(0, hashPos);
			    	}
			    	redirectUrl += queryStr;
			    }
			    redirectUrl = redirectUrl + '#' + state;
			    window.location.href = redirectUrl;
			    if (isSameUrl) {
			    	window.location.reload();
			    }
		    
		    	stateFormSubmitted = true;
		        return false;
		    });
    	});
    </script>
			
		<% } %>

		<% if(councilName == true) { %>
			<li>
				<form class="councilCodeSearch" name="councilCodeSearch">
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

    <script>
    	$(document).ready(function(){
    		// councilCode
    		councilCodeFormSubmitted = false;
		    $('.council-finder form[name="councilCodeSearch"]').submit(function(){
		    	if (councilCodeFormSubmitted) {
		    		return false;
		    	}

		        var councilCode = $(this).find('select[name="council-code"]').val();
			    var redirectUrl = '<%= resourceResolver.map(path) %>'; 
			    var currentUrl = window.location.href;
			    var isSameUrl = currentUrl.substring(0, currentUrl.indexOf('.html')) == redirectUrl.substring(0, redirectUrl.indexOf('.html'));
			    var queryPos = currentUrl.indexOf('?');
			    if (queryPos != -1) {
			    	var queryStr = currentUrl.substring(queryPos);
			    	var hashPos = queryStr.indexOf('#');
			    	if (hashPos != -1) {
			    		queryStr = queryStr.substr(0, hashPos);
			    	}
			    	redirectUrl += queryStr;
			    }
			    redirectUrl = redirectUrl + '#' + councilCode;
			    window.location.href = redirectUrl;
			    if (isSameUrl) {
			    	window.location.reload();
			    }
		    
		    	councilCodeFormSubmitted = true;
		        return false;
		    });
    	});
    </script>
			
		<% } %>
	<% if(zip == true || state == true || councilName == true) { %>
	</ul>
	<% } %>	
<% } %>
