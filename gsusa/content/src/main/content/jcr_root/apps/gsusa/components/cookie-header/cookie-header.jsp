<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode, com.day.cq.wcm.foundation.Placeholder, java.util.Random"  %>
<%@page session="false" %>

<%
    if (true) {// contains cookie) {
%>
<div id="stay-cheader" class="show-for-small">
	<cq:include path="mobile-cookie-header" resourceType="gsusa/components/standalone-cookie-header" />
</div>
<%
}
%>
