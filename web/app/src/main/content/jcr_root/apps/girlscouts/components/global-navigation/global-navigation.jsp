<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<!-- apps/girlscouts/components/global-navigation/global-navigation.jsp -->
<%
String[] links = properties.get("links", String[].class);
request.setAttribute("globalNavigation", links);

if ((links == null || links.length == 0) && WCMMode.fromRequest(request) == WCMMode.EDIT) {
	%>##### Global Navigation #####<%
} else {
%>
    <ul class="inline-list">
      <cq:include script="main.jsp"/>
    </ul>
<%
}
%>
