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
	Boolean zip = (properties.get("zip", "Yes")).equals("Yes");
	String href = properties.get("href", "");
	if(!href.startsWith("http://") && !href.startsWith("https://")){
		href = "http://" + href;
 	}

	if(!zip && href.isEmpty() && WCMMode.fromRequest(request) == WCMMode.EDIT){
		%>****** If you do not use the zip code option, you must enter a URL *******<%
	}else if(zip || !href.isEmpty()){

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
	    	<% if(zip){ %>
			<a class="button form"><%= title %></a>
				<form class="formDonate clearfix hide" onsubmit="completeAndRedirectDonate(); return false;" method="POST">
		            <!-- <label for="zipcode">Enter ZIP Code: </label> -->
		            <input type="text" name="zipcode" maxlength="5" pattern="[0-9]*" placeholder="Enter ZIP Code">
					<button type="submit" class="button">GO</button>
				</form>
				<% }else{ %>
				<a class="button" href="<%= href %>" target="_blank"><%= title %></a>
				<% } %>
		</div>
	</div> <%
	} else { %>
		<div class="standalone-donate form-no-image donate-block">
		<% if(zip){ %>
	    <a class="button form"><%= title %></a>
			<form class="formDonate clearfix hide" onsubmit="completeAndRedirectDonate(); return false;" method="POST">
	            <!-- <label for="zipcode">Enter ZIP Code: </label> -->
	            <input type="text" name="zipcode" maxlength="5" pattern="[0-9]*" placeholder="Enter ZIP Code">
				<button type="submit" class="button">GO</button>
			</form>
			<% }else{ %>
				<a class="button" href="<%= href %>" target="_blank"><%= title %></a>
			<% } %>
		</div>
	<%}
	}%>
