<%@include file="/libs/foundation/global.jsp" %>
<%
    String callToActionName = properties.get("callToActionName", "Join Now");
    String searchBtnName = properties.get("searchBtnName", "Go");
    String title = properties.get("title", "Find Your Local Council");
    String mainTitle = properties.get("mainTitle", "");
    String text = properties.get("text", "");
    String source = properties.get("source", "not_set");
    int maxWidth = properties.get("maxWidth", 210);
    final boolean showVerticalRule = properties.get("showverticalrule", false);

	String bg = "";
	try {
		bg = ((ValueMap)resource.getChild("bg").adaptTo(ValueMap.class)).get("fileReference", "");
	} catch (Exception e) {}
	if (!bg.equals("")) {%>

<% if (showVerticalRule) {%>
	<div class="standalone-join join-volunteer-block bg-image" style="max-width:<%= maxWidth + "px"%>; border-left: solid 1px #e5e7e8;">
<%} else { %>
	<div class="standalone-join join-volunteer-block bg-image" style="max-width:<%= maxWidth + "px"%>;">
<%} %>		    
		<% slingRequest.setAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE, true); %>
		    <cq:include path="bg" resourceType="gsusa/components/image"/>
	    <% slingRequest.removeAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE); %>
	    <div class="wrapper">
	        <a href="#" title="Join Now" class="join button arrow"><%= callToActionName %></a>
	        <form class="formJoin hide">
	            <label><%= title %></label>
	            <input type="text" name="ZipJoin" maxlength="5" title="5 numbers zip code" pattern="[0-9]*" placeholder="Enter ZIP Code">
	            <input type="hidden" name="source" value="<%= source %>">
	        	<input class="button" class="button" type="submit" value="<%= searchBtnName %>">
	        </form>
	    </div>
	</div> <%

	} else if (!text.equals("")) { %>
		<% if (showVerticalRule) {%>
			<div class="standalone-join join-volunteer-block text-version" style="border-left: solid 1px #e5e7e8;">
		<%} else { %>
			<div class="standalone-join join-volunteer-block text-version">
		<%} %>
		
		   <h5><%= mainTitle %></h5>
		   <p><%= text %></p>
		   <div class="wrapper">
		       <a href="#" title="Volunteer" class="join button arrow"><%= callToActionName %></a>
		       <form class="formVol hide">
		           <label><%= title %></label>
		           <input type="text" name="ZipVolunteer" maxlength="5" title="5 numbers zip code" pattern="[0-9]*" placeholder="Enter ZIP Code">
		           <input type="hidden" name="source" value="<%= source %>">
		       	<input class="button" type="submit" value="<%= searchBtnName %>">
		       </form>
		   </div>
		</div>
	<% } else { %>
		<% if (showVerticalRule) {%>
			<div class="standalone-join form-no-image join-volunteer-block" style="border-left: solid 1px #e5e7e8;">
		<%} else { %>
			<div class="standalone-join form-no-image join-volunteer-block">
		<%} %>
	
	    <a href="#" title="Join Now" class="join button arrow"><%= callToActionName %></a>
	    <form class="formJoin hide">
	        <label><%= title %></label>
	        <input type="text" name="ZipJoin" maxlength="5" title="5 numbers zip code" pattern="[0-9]*" placeholder="Enter ZIP Code">
	        <input type="hidden" name="source" value="<%= source %>">
	        <input class="button" type="submit" value="<%= searchBtnName %>">
	    </form>
	</div><%
	}
	%>
