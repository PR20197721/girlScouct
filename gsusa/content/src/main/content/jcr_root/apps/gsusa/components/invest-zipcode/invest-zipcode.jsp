<%@include file="/libs/foundation/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode" %>

<%
boolean local = properties.get("local",false);
boolean national = properties.get("national",false);
String link = properties.get("link","");
if(!local && !national && WCMMode.fromRequest(request) == WCMMode.EDIT){
	%> Please select an invest button to display <%
}
else{
	if(local){
%>
	<script type="text/javascript">
		var completeAndRedirectInvest = function(data){
			<% if(WCMMode.fromRequest(request) != WCMMode.EDIT){ %>
			var toPost = $('.invest-zip').serialize();
			$(document).ready(function() {
				$.ajax({
					method: "POST",
					url: '/invest/ajax_CouncilFinder.asp',
					data: toPost,
					async: false,
					success: function(resp){
						if(resp == null || resp == ""){
							alert("The council you have searched for does not exist");
						}
						else{
							//console.log(resp);
							var url = resp.split(',',3);
							//console.log(url[2]);
							window.open(url[2],'_blank');
						}
					}
				});
			});
			<% }else{ %>
			alert("This tool can only be used on a live page");
			<% } %>
		}
	</script>

	<div class="invest-button">
		<p class="invest-text">Support Your Local Girl Scout Council</p>
		<form class="invest-zip" onsubmit="completeAndRedirectInvest(); return false;" method="POST">
			<p class="invest-text">Enter ZIP Code: </p>
			<section class="clearfix">
				<input type="text" name="zipcode" required />
				<button type="submit" class="fa fa-play-circle"></button>
			</section>
		</form>
	</div>

	<% } 
	if(national){ %>
	
		<div class="invest-button national">
			<div class="invest-zip">
				<section class="clearfix">
					<div class="invest-text">Support Girl Scouts of the USA</div>
					<a href="<%= link %>"><button class="fa fa-play-circle"></button></a>
				</section>	
			</div>
		</div>
		
	<% } 
}%>
