<%@include file="/libs/foundation/global.jsp" %>
<%
    String callToActionName = properties.get("callToActionName", "Volunteer Now");
    String searchBtnName = properties.get("searchBtnName", "Go");
    String title = properties.get("title", "Find Your Local Council");
    String source = properties.get("source", "homepage");
	String bg = "";
	try {
		bg = ((ValueMap)resource.getChild("bg").adaptTo(ValueMap.class)).get("fileReference", "");
	} catch (Exception e) {}
%>

<div class="standalone-volunteer">
    <div class="wrapper">
        <a href="#" title="Volunteer"><%= callToActionName %>></a>
        <img src="<%= bg %>">
        <form class="formVolunteer">
            <%= title %>
            <input type="text" name="ZipVolunteer" maxlength="5">
            <input type="hidden" name="source" value="<%= source %>">
        	<input class="button" type="submit" value="<%= searchBtnName %>">
        </form>
    </div>
</div>
