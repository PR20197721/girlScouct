<%@include file="/libs/foundation/global.jsp" %>
<%
    String callToActionName = properties.get("callToActionName", "Join Now");
    String searchBtnName = properties.get("searchBtnName", "Go");
    String title = properties.get("title", "Find Your Local Council");
    String source = properties.get("source", "not_set");
    int maxWidth = properties.get("maxWidth", 210);

	String bg = "";
	try {
		bg = ((ValueMap)resource.getChild("bg").adaptTo(ValueMap.class)).get("fileReference", "");
	} catch (Exception e) {}
	if (!bg.equals("")) {%>

	<div class="standalone-volunteer" style="max-width:<%= maxWidth + "px"%>;">
	    <div class="bg-image">
	    <% slingRequest.setAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE, true); %>
	    <cq:include path="bg" resourceType="gsusa/components/image"/></div>
	    <% slingRequest.removeAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE); %>
	    <div class="wrapper">
	        <a href="#" title="Join Now" class="button arrow"><%= callToActionName %></a>
	        <form class="formJoin hide">
	            <label><%= title %></label>
	            <input type="text" name="ZipJoin" maxlength="5" pattern="[0-9]*" placeholder="Enter ZIP Code">
	            <input type="hidden" name="source" value="<%= source %>">
	        	<input class="button" class="button" type="submit" value="<%= searchBtnName %>">
	        </form>
	    </div>
	</div> <%
	} else { %>
		<div class="standalone-volunteer form-no-image">
	    <a href="#" title="Join Now" class="button arrow"><%= callToActionName %></a>
	    <form class="formJoin hide">
	        <label><%= title %></label>
	        <input type="text" name="ZipJoin" maxlength="5" pattern="[0-9]*" placeholder="Enter ZIP Code">
	        <input type="hidden" name="source" value="<%= source %>">
	        <input class="button" type="submit" value="<%= searchBtnName %>">
	    </form>
	</div><%
	}
	%>
