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
	<div id="footer" class="hide-for-small row">
		<% setCssClasses("large-24 medium-24 small-24 columns", request); %>
<%
	request.setAttribute("centerLinks", false);
%>
	<cq:include path="<%= footerPath + "/nav"%>" resourceType="girlscouts/components/footer-navigation"/>
	</div>
	<div id="mobile-nav-footer" class="show-for-small collapse">
<%
	request.setAttribute("centerLinks", true);
%>
		<cq:include path="<%= footerPath + "/nav"%>" resourceType="girlscouts/components/footer-navigation"/>
	</div> 
	<div id="mobile-footer" class="row show-for-small">
<%
	request.setAttribute("noLink", true);
%>
		<cq:include path="<%= logoPath + "/logo"%>" resourceType="girlscouts/components/logo" />
	</div>
</div>

<script type="text/javascript">
  //seems to be only used in VTK sections
	var resizeWindow = function(){
		if(fixVerticalSizing) {
			//get height of the actual page
			var currentMainHeight = $('#main').height();
			//get the height of the window
			targetMainHeight = $(this).height();
		//	var targetMainHeight = $(this).height() - $("#header").height() - $("#headerBar").height() - $("#footer").height() - 15;
			//if the content of the page is not to the bottom of the window add this padding, note the row that is the wrapper
			//must have class content
			if (targetMainHeight > currentMainHeight) {
				$('#main .row.content').css('padding-bottom',targetMainHeight-contentHeight);
			}
		}
	};
	window.onload = resizeWindow;
	$(window).resize(resizeWindow);
</script>
