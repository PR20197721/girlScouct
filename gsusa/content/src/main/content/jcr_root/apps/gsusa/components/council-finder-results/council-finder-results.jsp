<%@include file="/libs/foundation/global.jsp" %>

<%
if(slingRequest.getParameter("zip") == null && slingRequest.getParameter("state") == null && slingRequest.getParameter("council-code") == null){
	%> Sorry, no results found <%
}
else{
	%>
	<div id="results-area"></div>
	<script>
		var res = $("#results-area");
		var display = function(data){
			var json = JSON.parse(data);
			if (json.councils == undefined){
				res.html("<p>It looks like something went wrong with your search. Please try again</p>");
			}
			else if(json.councils.length == 0){
				res.html("<p>No results found</p>");
			}
			else{
				var result = "<ul class=\"councils\">";
				for(var i=0; i < json.councils.length; i++) {
					result += "<li><h5>" + json.councils[i].councilFullName + "</h5>";
					result += "<section><p>" + json.councils[i].city + ", " + json.councils[i].state + " " + json.councils[i].zipcode + "</p>";
					result += "<p>" + json.councils[i].phone + "</p>";
					if(json.councils[i].tollFreePhone != undefined && json.councils[i].tollFreePhone != "") {
						result += "<p>" + json.councils[i].tollFreePhone + " - Local Toll Free Phone</p>";
					}
					result += "<p>Website: <a href=\"" + json.councils[i].website + "\">" + json.councils[i].website + "</a></p>";
					result += "<p>Facebook: <a href=\"" + json.councils[i].facebook + "\">" + json.councils[i].facebook + "</a></p>";
					result += "<p>Twitter: <a href=\"" + json.councils[i].twitter + "\">" + json.councils[i].twitter + "</a></p></section>";
					result += "<a class=\"button small radius\" href=\"" + json.councils[i].joinUrl + "\">Join</a>";
					result += "<a class=\"button small radius\" href=\"" + json.councils[i].volunteerUrl + "\">Volunteer</a>";
					result += "<a class=\"button small radius\" href=\"" + json.councils[i].onlineRegistrationUrl + "\">Online Registration</a></li>";
				}
				result += "</ul>";
				res.html(result);
			}
		};
	</script>
	<%
	String zip = slingRequest.getParameter("zip");
	String state = slingRequest.getParameter("state");
	String code = slingRequest.getParameter("council-code");
	if(zip != null){
		String url = "/councilfinder/ajax_results.asp?zip=" + zip;
		%>
		<script>
		$.get("<%= url %>",display);
		</script>
		<%
	}
	if(state != null){
		String url = "/councilfinder/ajax_results.asp?state=" + state.toUpperCase();
		%>
		<script>
		$.get("<%= url %>",display);
		</script>
		<%
	}
	if(code != null){
		String url = "/councilfinder/ajax_results.asp?code=" + code;
		%>
		<script>
		$.get("<%= url %>",display);
		</script>
		<%
	}
}
%>