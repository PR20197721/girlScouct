<%--
	Volunteer Toolkit component.
--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%><%
	// TODO add you code here
%>

<!-- TODO -->
<!-- <cq:includeClientLib categories="apps.girlscouts-vtk" /> -->
<link rel="stylesheet" href="/etc/designs/girlscouts-vtk/clientlibs.css" type="text/css" media="screen"/>
<script type="text/javascript"src="/etc/designs/girlscouts-vtk/clientlibs.js"></script>

<script>
	fixVerticalSizing = true;
</script>
<!--PAGE STRUCTURE: MAIN-->
<div id="main">
	<cq:include path="vtk" resourceType="girlscouts-vtk/components/vtk" />
</div>

