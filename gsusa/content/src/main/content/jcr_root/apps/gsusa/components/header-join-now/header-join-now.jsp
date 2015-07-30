<%@include file="/libs/foundation/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%
	String callToActionName = properties.get("callToActionName", "Join");
	String searchBtnName = properties.get("searchBtnName", "Go");
	String title = properties.get("title", "Find Your Local Council");
	Boolean isHidden = properties.get("isJoinHidden", false);
	String source = properties.get("source", "homepage");

	if (!isHidden) {
%>
	<form class="formHeaderJoin" id="tag_header_join">
		<input type="text" name="ZipJoin" maxlength="5" placeholder="ZIP code" >
		<input type="hidden" name="source" value="<%= source %>">
		<span class="button" tabindex="25"><%= callToActionName %></span>
	</form>
<%
	} else if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
%>
	Click to edit the join component in the eyebrow
<%
	}
%>
