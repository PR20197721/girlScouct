<%@include file="/libs/foundation/global.jsp" %>
<%
    String callToActionName = properties.get("callToActionName", "Join Now");
    String searchBtnName = properties.get("searchBtnName", "Go");
    String title = properties.get("title", "Find Your Local Council");
	String bg = "";
	try {
		bg = ((ValueMap)resource.getChild("bg").adaptTo(ValueMap.class)).get("fileReference", "");
	} catch (Exception e) {}
%>

<div class="standalone-volunteer">
    <div class="wrapper">
        <a href="#" title="Volunteer"><%= callToActionName %>></a>
        <img src="<%= bg %>">
        <form class="formJoin">
            <%= title %>
            <input type="text" name="ZipJoin" maxlength="5">
        	<input class="button" type="submit" value="<%= searchBtnName %>">
        </form>
    </div>
</div>
