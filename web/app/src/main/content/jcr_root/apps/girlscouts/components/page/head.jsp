<%@page session="false"%><%--
  Copyright 1997-2010 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Default head script.

  Draws the HTML head with some default content:
  - includes the WCML init script
  - includes the head libs script
  - includes the favicons
  - sets the HTML title
  - sets some meta data

  ==============================================================================

--%><%@include file="/libs/foundation/global.jsp" %><%
%><%@include file="/apps/girlscouts/components/global.jsp"%>
<%@ page import="com.day.cq.commons.Doctype,
					org.apache.sling.settings.SlingSettingsService,
					com.day.cq.commons.Externalizer,
					org.apache.sling.api.SlingHttpServletRequest,
					java.util.Set" %><%
    String xs = Doctype.isXHTML(request) ? "/" : "";
    String favIcon = currentDesign.getPath() + "/favicon.ico";
    String reqProtocol = request.getHeader("X-Forwarded-Proto");
	if(reqProtocol == null) reqProtocol = "http";
    if (resourceResolver.getResource(favIcon) == null) {
        favIcon = null;
    }else{
    	if(favIcon.startsWith("/")) {
            Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);
			favIcon = externalizer.absoluteLink((SlingHttpServletRequest)request, reqProtocol, favIcon);
			favIcon = favIcon.replace(":80/","/");
        }
    }
%><head>

<% 
Set<String> set = sling.getService(SlingSettingsService.class).getRunModes();
Boolean isProd = set.contains("prod");
String eventToSalesforce = isProd ? "https://gsmembers.force.com/members/Event_join?EventId=" : "https://gsuat-gsmembers.cs17.force.com/members/Event_join?EventId=";
%>

<script>
eventToSalesforce = "<%= eventToSalesforce %>";
</script>

<%
	String pageCategory = "DEFAULT";
	Object pageCategoryObject = request.getAttribute("PAGE_CATEGORY");
	if (pageCategoryObject != null) {
		pageCategory = (String) pageCategoryObject;
	}
%>
	<!-- page category = <%= pageCategory%> -->
	<meta http-equiv="content-type" content="text/html; charset=UTF-8"<%=xs%>>
	<meta name="keywords" content="<%= xssAPI.encodeForHTMLAttr(WCMUtils.getKeywords(currentPage, false)) %>"<%=xs%>>
	<meta name="description" content="<%= xssAPI.encodeForHTMLAttr(properties.get("jcr:description", "")) %>"<%=xs%>>
	<cq:include script="headlibs.jsp"/>
	<cq:include script="/libs/wcm/core/components/init/init.jsp"/>
	<cq:include script="stats.jsp"/>
<% 
	if (favIcon != null) {
%>
	<link rel="icon" type="image/vnd.microsoft.icon" href="<%= favIcon %>"<%=xs%>>
	<link rel="shortcut icon" type="image/vnd.microsoft.icon" href="<%= favIcon %>"<%=xs%>>
<%
	}
	String title = "";
	try {
        	title = currentPage.getContentResource().adaptTo(ValueMap.class).get("seoTitle", "");
    		if (title.isEmpty()) {
    			title = currentPage.getTitle() == null ? currentPage.getName() : currentPage.getTitle();
		}
        } catch (Exception e) {}
    	title = xssAPI.encodeForHTML(title);

	if ("VTK".equals(pageCategory)) {
%>
	<link rel="stylesheet" href="/etc/designs/girlscouts-vtk/clientlibs.css" type="text/css"/>
	<script type="text/javascript"src="/etc/designs/girlscouts-vtk/clientlibs.js"></script>
<%
	}
%>
<!--//loading a font file for all the small icons on the site-->
  <link rel="stylesheet" href="/etc/designs/girlscouts/fonts/style.css" type="text/css"/>
  <title><%= title %></title>
<%
	Boolean googleOptimize = currentSite.get("./googleOptimize", Boolean.FALSE);
	String googleOptimizeId = currentSite.get("googleOptimizeId", "");
	if(googleOptimize) {
%>  
	<style>.async-hide { opacity: 0 !important} </style>
	<script>(function(a,s,y,n,c,h,i,d,e){s.className+=' '+y;h.start=1*new Date;
	h.end=i=function(){s.className=s.className.replace(RegExp(' ?'+y),'')};
	(a[n]=a[n]||[]).hide=h;setTimeout(function(){i();h.end=null},c);h.timeout=c;
	})(window,document.documentElement,'async-hide','dataLayer',4000,
	{'<%=googleOptimizeId%>':true});</script>		
<%
	} 
%>
</head>
