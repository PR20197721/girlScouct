<%@include file="/libs/foundation/global.jsp" %>
<%
    String callToActionName = properties.get("callToActionName", "Volunteer Now");
    String searchBtnName = properties.get("searchBtnName", "Go");
    String title = properties.get("title", "Find Your Local Council");
    String source = properties.get("source", "not_set");

    String bg = "";
	try {
		bg = ((ValueMap)resource.getChild("bg").adaptTo(ValueMap.class)).get("fileReference", "");
	} catch (Exception e) {}

	if (!bg.equals("")) {%>
		<div class="standalone-volunteer">
			<div class="bg-image">
			<% slingRequest.setAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE, true); %>
			<cq:include path="bg" resourceType="gsusa/components/image"/></div>
			<% slingRequest.removeAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE); %>
		    <div class="wrapper">
		        <a href="#" title="Volunteer" class="button arrow"><%= callToActionName %></a>
		        <form class="formVol hide">
		            <label><%= title %></label>
		            <input type="text" name="ZipVolunteer" maxlength="5" pattern="[0-9]*" placeholder="Enter ZIP Code">
		            <input type="hidden" name="source" value="<%= source %>">
		        	<input class="button" type="submit" value="<%= searchBtnName %>">
		        </form>
		    </div>
		</div> <%
	} else { //bg is null, doing this mainly for css %>
		<div class="standalone-volunteer form-no-image">
		    <a href="#" title="Volunteer Now" class="button arrow"><%= callToActionName %></a>
		    <form class="formVol hide">
		        <label><%= title %></label>
		        <input type="text" name="ZipVolunteer" maxlength="5" pattern="[0-9]*" placeholder="Enter ZIP Code">
		        <input type="hidden" name="source" value="<%= source %>">
		        <input class="button" type="submit" value="<%= searchBtnName %>">
		    </form>
		</div><%
	}

%>