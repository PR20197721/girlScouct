<%@include file="/libs/foundation/global.jsp" %>
<%
    String callToActionName = properties.get("callToActionName", String.class);
    String searchBtnName = properties.get("searchBtnName", String.class);
    String title = properties.get("title", String.class);
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
        	<input class="button" type="submit" value="<%= searchBtnName %>">
        </form>
    </div>
</div>
