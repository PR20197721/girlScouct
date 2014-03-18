<%@include file="/libs/foundation/global.jsp" %>

<%
	String contentPath = currentPage.getAbsoluteParent(2).getContentResource().getPath();
	String parPath = contentPath + "/footer/par";
%>
<cq:include path="<%= parPath %>" resourceType="foundation/components/parsys" />