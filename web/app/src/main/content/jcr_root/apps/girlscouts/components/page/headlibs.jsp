<%@ page session="false" %>
<%@page import="com.day.cq.wcm.api.WCMMode, org.girlscouts.vtk.helpers.*"%>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<!-- apps/girlscouts/components/page/headlibs.jsp -->
<cq:includeClientLib categories="cq.foundation-main"/><%
%><cq:include script="/libs/cq/cloudserviceconfigs/components/servicelibs/servicelibs.jsp"/>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
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
<% } 

Design newCurrentDesign= (Design)request.getAttribute("newCurrentDesign");
if (newCurrentDesign != null) {
  %><link rel="stylesheet" href="<%= newCurrentDesign.getPath() + ".css" %>" type="text/css"/><%
}else{
  currentDesign.writeCssIncludes(pageContext);
}
%>
<!-- End: Include Girl Scout clientlibs -->

<!-- Begin: login logic -->
<%
org.girlscouts.vtk.helpers.ConfigManager configManager = sling.getService(ConfigManager.class);

	String helloUrl = configManager.getConfig("helloUrl");
	String callbackUrl = configManager.getConfig("callbackUrl");
	String refererCouncil = null;
	try {
		String councilRoot = currentPage.getAbsoluteParent(1).getPath(); // /content/gsnetx
		refererCouncil = councilRoot.split("/")[2];  // gsnetx
	} catch (Exception e) {} // Not in a normal council
	String signInUrl = callbackUrl + "?action=signin";
	if (refererCouncil != null) {
	    signInUrl = signInUrl + "&refererCouncil=" + refererCouncil;
	}
	String signOutUrl = callbackUrl + "?action=signout";
	String siteRoot = currentPage.getAbsoluteParent(2).getPath();
	String language = siteRoot.substring(siteRoot.lastIndexOf("/") + 1);
	
	String hideSignIn = currentSite.get("hideSignIn", "");
%>
	<script type="text/javascript">
	///the variable has to be set on the page where the resizing is needed not in the header.
  //  	var fixVerticalSizing = true;
	<%
    if (!hideSignIn.equalsIgnoreCase("true")) {
	%>
		$(document).ready(function() {
			girlscouts.components.login.init('<%= language %>', '<%= signInUrl %>', '<%= signOutUrl %>');
			// TODO: Create a new servlet for redirect so this param will not show in browser.
			if (window.location.href.indexOf('isSignOutSalesForce=true') != -1) {
				$.removeCookie('girl-scout-name');
			}
			if($.cookie) {
				var name = $.cookie('girl-scout-name');
				if (name) {
					girlscouts.components.login.sayHello('signedin', name);	
				} else {
					girlscouts.components.login.genCode('<%= helloUrl %>');
				}
			}
		});
	//End: login logic
<% 	} %>
	</script>