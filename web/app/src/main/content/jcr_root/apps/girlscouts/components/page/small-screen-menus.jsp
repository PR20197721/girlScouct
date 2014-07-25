<%@include file="/libs/foundation/global.jsp" %>
<%
  String headerPath = (String)request.getAttribute("headerPath");
  int depth = currentPage.getDepth();
%>
<aside class="right-off-canvas-menu">
<%if(depth==3){ %>
	<sling:include path="<%= headerPath + "/global-nav" %>" resourceType="girlscouts/components/global-navigation" addSelectors="smaller-view"/>
<%}else{ %>
	<sling:include path="<%= headerPath + "/global-nav" %>" resourceType="girlscouts/components/global-navigation" replaceSelectors="small-screen-menus"/>
<%} %>
	<sling:include path="<%= headerPath + "/eyebrow-nav" %>" resourceType="girlscouts/components/eyebrow-navigation" addSelectors="smaller-view"/>
</aside>
<a class="exit-off-canvas"></a>
