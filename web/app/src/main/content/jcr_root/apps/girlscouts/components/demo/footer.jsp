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
String footerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/footer";
String logoPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header";
%>
<!-- web/app/src/main/content/jcr_root/apps/girlscouts/components/page/footer.jsp -->
<div class="hide-for-print">
 <!--footer menu links-->
	<div id="footer" class="row">
		<!-- <cq:include path="<%= footerPath + "/nav"%>" resourceType="girlscouts/components/footer-navigation"/> -->
		<div class="vtk-demo-info-footer small-push-1 small-23  medium-push-1 medium-18 columns">
			<p>&copy; 2016 Girl Scouts of the United States of America A501(c)(3) Organization. All Rights Reserved</p>
		</div>
		<div class="vtk-demo-reference-footer small-push-1 small-23  medium-push-1 medium-4 end columns">
			<a href="#">Demo details</a> <!-- Link -->
		</div>
	</div>
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
