<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%@page session="false" %>
<%
String tracking = currentSite.get("footerTracking", "");
if (!tracking.isEmpty()) {
    %><%= tracking %><%
}
%>