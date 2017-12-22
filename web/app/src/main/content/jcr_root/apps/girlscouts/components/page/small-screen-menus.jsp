<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
  String headerPath = (String)request.getAttribute("headerPath");
  int depth = currentPage.getDepth();
%>
<aside class="right-off-canvas-menu">
<%if(depth==3){ %>
	<cq:include path="<%= headerPath + "/global-nav" %>" resourceType="girlscouts/components/global-navigation" replaceSelectors="smaller-view"/>
<%}else{ %>
	<cq:include path="<%= headerPath + "/global-nav" %>" resourceType="girlscouts/components/global-navigation" replaceSelectors="small-screen-menus"/>
<%} %>
<!-- TEST small screen eyebrow-nav start -->
	<cq:include path="<%= headerPath + "/eyebrow-nav" %>" resourceType="girlscouts/components/eyebrow-navigation" replaceSelectors="smaller-view"/>
<!-- TEST small screen eyebrow-nav end -->
</aside>
<a class="exit-off-canvas"></a>
