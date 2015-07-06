<%@include file="/libs/foundation/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode" %>

<%
    String callToActionName = properties.get("callToActionName", String.class);
    String searchBtnName = properties.get("searchBtnName", String.class);
    String title = properties.get("title", String.class);
	Boolean isHidden = properties.get("isJoinHidden", false);
	
	if (!isHidden) {%>

  <div>
    <a href="#" title="Join"><%= callToActionName %>></a>
    <form class="formJoin">
	  <%= title %>
	  <input type="text" name="ZipJoin" maxlength="5">
	  <input class="button" type="submit" value="<%= searchBtnName %>">
    </form>
  </div>

	<%} else if (WCMMode.fromRequest(request) == WCMMode.EDIT) {%>
		Click to edit the join component in the eyebrow
	<%}
	%>