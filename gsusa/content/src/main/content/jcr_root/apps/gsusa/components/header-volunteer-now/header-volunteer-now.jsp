<%@include file="/libs/foundation/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%
    String callToActionName = properties.get("callToActionName", "Volunteer");
    String searchBtnName = properties.get("searchBtnName", "Go");
    String title = properties.get("title", "Find Your Local Council");
	Boolean isHidden = properties.get("isVolunteerHidden", false);
    String source = properties.get("source", "homepage");

	if (!isHidden) {%>
<!--     <a href="#" title="Volunteer"><%= callToActionName %></a> -->
    <form class="formVolunteer">
	   <input type="text" name="ZipVolunteer" maxlength="5">
        <input type="hidden" name="source" value="<%= source %>">
	    <input class="button" type="submit" value="<%= callToActionName %>">
    </form>
	<%} else if (WCMMode.fromRequest(request) == WCMMode.EDIT) {%>
		Click to edit the Volunteer component in the eyebrow
	<%}
	%>