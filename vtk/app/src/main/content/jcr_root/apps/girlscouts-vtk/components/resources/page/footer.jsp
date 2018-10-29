<%@page import="com.day.cq.wcm.api.components.IncludeOptions"%>
<%@include file="/libs/foundation/global.jsp" %>
<!-- apps/girlscouts/components/page/footer.jsp -->
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
// Force currentPage and currentDesign from request
Page newCurrentPage = (Page)request.getAttribute("newCurrentPage");
Design newCurrentDesign= (Design)request.getAttribute("newCurrentDesign");
if (newCurrentPage != null) {
    currentPage = newCurrentPage;
}
if (newCurrentDesign != null) {
    currentDesign = newCurrentDesign;
}

String councilPath = currentPage.getAbsoluteParent(3).getContentResource().getPath().replaceAll("^\\/content\\/[^/]+", "/content");
String footerPath = councilPath + "/footer";
String logoPath = councilPath + "/header";
%>
<!-- web/app/src/main/content/jcr_root/apps/girlscouts/components/page/footer.jsp -->
<div class="hide-for-print">

	<!--logo for the mobile footer-->
	<div id="mobile-footer" class="row show-for-small">
		<%
			request.setAttribute("noLink", true);
		%>
		<cq:include path="<%= logoPath + "/logo"%>" resourceType="girlscouts/components/logo" />
	</div>
</div>

<cq:include script="google-analytics.jsp" />
<cq:include script="footer-tracking.jsp" />
<!-- Version Animal Cracker -->
