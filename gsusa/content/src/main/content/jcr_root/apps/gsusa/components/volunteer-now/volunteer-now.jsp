<%@include file="/libs/foundation/global.jsp" %>

<%
    String callToActionName = properties.get("callToActionName", "Volunteer Now");
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

	if (!bg.equals("")) { %>
		<% if (showVerticalRule) {%>
			<div class="standalone-volunteer join-volunteer-block bg-image border" style="max-width:<%= maxWidth + "px"%>">
		<% } else { %>
			<div class="standalone-volunteer join-volunteer-block bg-image" style="max-width:<%= maxWidth + "px"%>;">
		<% } %>

			<% slingRequest.setAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE, true); %>
				<cq:include path="bg" resourceType="gsusa/components/image"/>
			<% slingRequest.removeAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE); %>
		    <div class="wrapper">
		        <a href="#" title="Volunteer" class="vol button arrow"><%= callToActionName %></a>
		        <form class="formVol hide">
		            <label><%= title %></label>
		            <input type="text" name="ZipVolunteer" maxlength="5" title="5 numbers zip code" pattern="[0-9]*" placeholder="Enter ZIP Code">
		            <input type="hidden" name="source" value="<%= source %>">
		        	<input class="button" type="submit" value="<%= searchBtnName %>">
		        </form>
		    </div>
		</div> <%
	} else if (!text.equals("")) { %>
		<% if (showVerticalRule) { %>
<div class="standalone-volunteer join-volunteer-block text-version border">
		<% } else { %>
<div class="standalone-volunteer join-volunteer-block text-version">
		<% } %>
		   <h5><%= mainTitle %></h5>
		   <p><%= text %></p>
		   <div class="wrapper">
		       <a href="#" title="Volunteer" class="vol button arrow"><%= callToActionName %></a>
		       <form class="formVol hide">
		           <label><%= title %></label>
		           <input type="text" name="ZipVolunteer" title="5 numbers zip code" maxlength="5" pattern="[0-9]*" placeholder="Enter ZIP Code">
		           <input type="hidden" name="source" value="<%= source %>">
		       	<input class="button" type="submit" value="<%= searchBtnName %>">
		       </form>
		   </div>
		</div>
	<% } else { //bg is null, doing this mainly for css %>
		<% if (showVerticalRule) { %>
<div class="standalone-volunteer form-no-image  join-volunteer-block border">
		<% } else { %>
<div class="standalone-volunteer form-no-image  join-volunteer-block">
		<% } %>
		    <a href="#" title="Volunteer Now" class="vol button arrow"><%= callToActionName %></a>
		    <form class="formVol hide">
		        <label><%= title %></label>
		        <input type="text" name="ZipVolunteer" title="5 numbers zip code" maxlength="5" pattern="[0-9]*" placeholder="Enter ZIP Code">
		        <input type="hidden" name="source" value="<%= source %>">
		        <input class="button" type="submit" value="<%= searchBtnName %>">
		    </form>
		</div><%
	}

%>