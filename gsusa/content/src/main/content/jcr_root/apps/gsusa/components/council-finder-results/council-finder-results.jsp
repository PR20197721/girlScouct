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
				for(var i=0; i < json.councils.length; i++){	
					res.append("<ul class=\"council\">");
					res.append("<li><p><strong>" + json.councils[i].councilFullName + "</strong></p></li>");
					res.append("<p>" + json.councils[i].city + ", " + json.councils[i].state + " " + json.councils[i].zipcode + "</p>");
					res.append("<p>" + json.councils[i].phone + "</p>");
					if(json.councils[i].tollFreePhone != undefined && json.councils[i].tollFreePhone != ""){
						res.append("<p>" + json.councils[i].tollFreePhone + " - Local Toll Free Phone</p>");
					}
					res.append("<p>Website: <a href=\"" + json.councils[i].website + "\">" + json.councils[i].website + "</a></p>");
					res.append("<p>Facebook: <a href=\"" + json.councils[i].facebook + "\">" + json.councils[i].facebook + "</a></p>");
					res.append("<p>Twitter: <a href=\"" + json.councils[i].twitter + "\">" + json.councils[i].twitter + "</a></p>");
					res.append("<a href=\"" + json.councils[i].joinUrl + "\">Join</a>");
					res.append("<a href=\"" + json.councils[i].volunteerUrl + "\">Volunteer</a>");
					res.append("<a href=\"" + json.councils[i].onlineRegistrationUrl + "\">Online Registration</a>");
					res.append("</ul>");
				}
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