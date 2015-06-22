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
		var display = function(json){
			if (json.councils == undefined){
				res.html("<p>It looks like something went wrong with your search. Please try again</p>");
			}
			else if(json.councils.length == 0){
				res.html("<p>No results found</p>");
			}
			else{
				for(council in json.councils){
					res.append("<div class=\"council\"");
					res.append("<p><strong>" + council.councilFullName + "</strong></p>");
					res.append("<p>" + council.city + ", " + council.state + " " + council.zipcode + "</p>");
					res.append("<p>" + council.phone + "</p>");
					if(council.tollFreePhone != undefined && council.tollFreePhone != ""){
						res.append("<p>" + council.tollFreePhone + " - Local Toll Free Phone</p>");
					}
					res.append("<p>Website: " + council.website + "</p>");
					res.append("<p>Facebook: " + council.facebook + "</p>");
					res.append("<p>Twitter: " + council.twitter + "</p>");
					res.append("<a href=\"" + council.joinUrl + "\">Join</a>");
					res.append("<a href=\"" + council.volunteerUrl + "\">Volunteer</a>");
					res.append("<a href=\"" + council.onlineRegistrationUrl + "\">Online Registration</a>");
					res.append("</div>");
				}
			}
		};
	</script>
	<%
	String zip = slingRequest.getParameter("zip");
	String state = slingRequest.getParameter("state");
	String code = slingRequest.getParameter("council-code");
	if(zip != null){
		String ajax = "/councilfinder/ajax_results.asp?zip=" + zip;
		%>
		<script>
		$.ajax(
				{url: "<%= ajax %>",
					success: function(response){
						display(response);
						}
					});
		</script>
		<%
	}
	if(state != null){
		String ajax = "/councilfinder/ajax_results.asp?state=" + state.toUpperCase();
		%>
		<script>
		$.ajax(
				{url: "<%= ajax %>",
					success: display(response)
					});
		</script>
		<%
	}
	if(code != null){
		String ajax = "/councilfinder/ajax_results.asp?code=" + code;
		%>
		<script>
		$.ajax(
				{url: "<%= ajax %>",
					success: display(response)
					});
		</script>
		<%
	}
}
%>