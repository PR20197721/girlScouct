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
String footerPath = homepage.getContentResource().getPath() + "/footer";
String logoPath = homepage.getContentResource().getPath() + "/header";
%>
<!-- web/app/src/main/content/jcr_root/apps/girlscouts/components/page/footer.jsp -->
<div class="hide-for-print">
 <!--footer menu links-->
	<div id="footer" class="row">
		<cq:include path="<%= footerPath + "/nav"%>" resourceType="girlscouts/components/footer-navigation"/>
	</div>
	<!--logo for the mobile footer-->
	<div id="mobile-footer" class="row show-for-small">
		<%
			request.setAttribute("noLink", true);
		%>
		<cq:include script="/apps/girlscouts/components/logo/mobile.jsp" />
		<%-- <cq:include path="<%= logoPath + "/logo"%>" resourceType="girlscouts/components/logo" />--%>
	</div>
</div>

<cq:include script="google-analytics.jsp" />
<cq:include script="footer-tracking.jsp" />
<!-- Version Animal Cracker -->
