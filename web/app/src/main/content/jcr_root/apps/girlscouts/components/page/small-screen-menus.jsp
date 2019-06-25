<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
  String headerPath = (String)request.getAttribute("headerPath");
  int depth = currentPage.getDepth();
%>
<aside class="left-off-canvas-menu closed">
	<cq:include script="/apps/girlscouts/components/global-navigation/off-canvas-nav.jsp"/>
</aside>
<a class="exit-off-canvas-el"></a>
