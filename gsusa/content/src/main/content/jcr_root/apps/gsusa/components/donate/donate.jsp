<%@page import="com.day.cq.wcm.api.WCMMode"%>
<%@include file="/libs/foundation/global.jsp" %>

<script type="text/javascript">
		var completeAndRedirectDonate = function(data){
			<% if(WCMMode.fromRequest(request) != WCMMode.EDIT){ %>
			var toPost = $('.formDonate').serialize();
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

<%  String title = properties.get("title", "Donate");
	int maxWidth = properties.get("maxWidth", 210);

	String bg = "";
	try {
		bg = ((ValueMap)resource.getChild("bg").adaptTo(ValueMap.class)).get("fileReference", "");
	} catch (Exception e) {}
	if (!bg.equals("")) {%>

	<div class="standalone-donate donate-block" style="max-width:<%= maxWidth + "px"%>;">
	    <div class="bg-image">

	    <% slingRequest.setAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE, true); %>
	    <cq:include path="bg" resourceType="gsusa/components/image"/></div>
	    <% slingRequest.removeAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE); %>
	    <div class="button-wrap">
			<a class="button"><%= title %></a>
				<form class="formDonate clearfix hide" onsubmit="completeAndRedirectDonate(); return false;" method="POST">
		            <!-- <label for="zipcode">Enter Zip Code: </label> -->
		            <input type="text" name="zipcode" maxlength="5" pattern="[0-9]*" placeholder="Enter ZIP Code">
					<input type="submit" class="button">GO</input>
				</form>
		</div>
	</div> <%
	} else { %>
		<div class="standalone-donate form-no-image donate-block">
	    <a class="button"><%= title %></a>
			<form class="formDonate clearfix hide" onsubmit="completeAndRedirectDonate(); return false;" method="POST">
	            <!-- <label for="zipcode">Enter Zip Code: </label> -->
	            <input type="text" name="zipcode" maxlength="5" pattern="[0-9]*" placeholder="Enter ZIP Code">
				<button type="submit" class="button">GO</button>
			</form>
		</div>
	<%}%>