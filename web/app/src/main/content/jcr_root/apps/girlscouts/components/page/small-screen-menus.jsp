<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
  String headerPath = (String)request.getAttribute("headerPath");
  int depth = currentPage.getDepth();
%>
<aside class="right-off-canvas-menu">
	<%if(depth==3){ %>
		<cq:include script="/apps/girlscouts/components/global-navigation/smaller-view.jsp"/>
	<%}else{ %>
		<cq:include script="/apps/girlscouts/components/global-navigation/small-screen-menus.jsp"/>
	<%} %>
	<cq:include script="/apps/girlscouts/components/eyebrow-navigation/smaller-view.jsp"/>
</aside>
<a class="exit-off-canvas"></a>
