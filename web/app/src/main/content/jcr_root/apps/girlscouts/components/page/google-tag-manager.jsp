<%@include file="/libs/foundation/global.jsp"%>
<%
Page newCurrentPage = (Page)request.getAttribute("newCurrentPage");
Design newCurrentDesign= (Design)request.getAttribute("newCurrentDesign");
if (newCurrentPage != null) {
    currentPage = newCurrentPage;
}
%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page session="false" %>
<%
String id = currentSite.get("gtmId", "");
if (!id.isEmpty()) {
%>

<!-- Google Tag Manager -->
<noscript><iframe src="//www.googletagmanager.com/ns.html?id=<%= id %>"
height="0" width="0" style="display:none;visibility:hidden"></iframe></noscript>
<!-- End Google Tag Manager -->
<% } %>