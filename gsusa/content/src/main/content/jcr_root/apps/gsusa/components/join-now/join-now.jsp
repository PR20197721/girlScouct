<%@include file="/libs/foundation/global.jsp" %>
<%
    String callToActionName = properties.get("callToActionName", "Join Now");
    String searchBtnName = properties.get("searchBtnName", "Go");
    String title = properties.get("title", "Find Your Local Council");
    String source = properties.get("source", "homepage");

	String bg = "";
	try {
		bg = ((ValueMap)resource.getChild("bg").adaptTo(ValueMap.class)).get("fileReference", "");
	} catch (Exception e) {}
	if (!bg.equals("")) {%>

	<div class="standalone-volunteer">
	    <img src="<%= bg %>" class="bg-image" />
	    <div class="wrapper">
	        <a href="#" title="Join Now" class="button arrow"><%= callToActionName %></a>
	        <form class="formJoin hide">
	            <label><%= title %></label>
	            <input type="text" name="ZipJoin" maxlength="5" placeholder="Enter ZIP code">
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
	        <input type="text" name="ZipJoin" maxlength="5" placeholder="Enter ZIP code">
	        <input type="hidden" name="source" value="<%= source %>">
	        <input class="button" type="submit" value="<%= searchBtnName %>">
	    </form>
	</div><%
	}
	%>
