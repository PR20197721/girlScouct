<%@page import="com.day.cq.wcm.api.components.IncludeOptions"%>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/include-options.jsp"%>

<%
    String footerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/footer";
%>
<!--PAGE STRUCTURE: FOOTER-->
<div id="footer" class="row">
	<% setCssClasses("large-24 medium-24 small-24 column", request); %>
    <cq:include path="<%= footerPath + "/nav"%>" resourceType="girlscouts/components/navigation-bar"/>
</div>