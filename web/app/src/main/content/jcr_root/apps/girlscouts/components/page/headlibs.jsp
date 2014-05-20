<%@ page session="false" %>
<%@page import="com.day.cq.wcm.api.WCMMode"%>
<%@include file="/libs/foundation/global.jsp" %>
<!-- apps/girlscouts/components/page/headlibs.jsp -->
<cq:includeClientLib categories="cq.foundation-main"/><%
%><cq:include script="/libs/cq/cloudserviceconfigs/components/servicelibs/servicelibs.jsp"/>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />

<!-- Begin: Include Girl Scout clientlibs -->
<!-- Artifact Browser -->
<!--[if lt IE 9]>
	<cq:includeClientLib categories="apps.girlscouts.ie8" />
<![endif]-->
<!-- Modern Browser -->
<!--[if gt IE 8]><!-->
	<cq:includeClientLib categories="apps.girlscouts.modern" />
<!--<![endif]-->
<% if (WCMMode.fromRequest(request) == WCMMode.EDIT) { %>
	<cq:includeClientLib categories="apps.girlscouts.authoring" />
<% } %>
<% currentDesign.writeCssIncludes(pageContext); %>
<!-- End: Include Girl Scout clientlibs -->

<!-- Begin: login logic -->
<%
	// TODO: read from OSGI
	String controllerUrl = "/content/girlscouts-vtk/controllers/auth-state.authenticate.html";
	String currentPath = currentPage.getPath() + ".html";
	String siteRoot = currentPage.getAbsoluteParent(2).getPath();
	String language = siteRoot.substring(siteRoot.lastIndexOf("/") + 1);
	if (!controllerUrl.isEmpty()) {
%>
		<script type="text/javascript">
			$(document).ready(function() {
				girlscouts.components.login.genCode('<%= controllerUrl %>', '<%= currentPath %>', '<%= language %>');
			});
		</script>
<%  } %>
<!-- End: login logic -->