<%@include file="/libs/foundation/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode" %>

<%
    String callToActionName = properties.get("callToActionName", "Join");
    String searchBtnName = properties.get("searchBtnName", "Go");
    String title = properties.get("title", "Find Your Local Council");
	Boolean isHidden = properties.get("isJoinHidden", false);
    String source = properties.get("source", "homepage");


	if (!isHidden) {%>
    <form class="formJoin">
	   <input type="text" name="ZipJoin" maxlength="5">
	   <input type="hidden" name="source" value="<%= source %>">
<!-- 	  <input class="button" type="submit" value="<%= searchBtnName %>"> -->
        <span class="button"><%= callToActionName %></span>
    </form>
<!--     <a href="#" title="Join"><%= callToActionName %>></a> -->


	<%} else if (WCMMode.fromRequest(request) == WCMMode.EDIT) {%>
		Click to edit the join component in the eyebrow
	<%}
	%>