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
%><%@ page import="com.day.cq.commons.Doctype,
				   org.apache.sling.settings.SlingSettingsService,
				   org.apache.sling.api.SlingHttpServletRequest,
				   com.day.cq.commons.Externalizer,
				   java.util.Set"%><%
	Set<String> set = sling.getService(SlingSettingsService.class).getRunModes();
	Boolean isProd = set.contains("prod");
    String xs = Doctype.isXHTML(request) ? "/" : "";
    String favIcon = currentDesign.getPath() + "/favicon.ico";
    if (resourceResolver.getResource(favIcon) == null) {
        favIcon = null;
    }
	ValueMap siteProps = resourceResolver.resolve(currentPage.getAbsoluteParent(2).getPath() + "/jcr:content").adaptTo(ValueMap.class);
	String seoTitle = properties.get("seoTitle", "");
	String ogTitle = properties.get("ogTitle", seoTitle);
	String ogSiteName = properties.get("ogSiteName", "Girl Scouts of the USA");
	String ogUrl = properties.get("ogUrl", "");
	String ogDescription = properties.get("ogDescription", "");
	String ogImage = properties.get("ogImage", "");
	if("".equals(ogImage)){
		String pageImagePath = currentPage.getPath() + "/jcr:content/image";
	    Session session = (Session)resourceResolver.adaptTo(Session.class);
	    if (session.nodeExists(pageImagePath)) {
	    	ogImage = resourceResolver.map(currentPage.getPath() + "/jcr:content.img.png");
	    }
	} else{
		Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);
		ogImage = externalizer.absoluteLink((SlingHttpServletRequest)request, request.getScheme(), ogImage);
	}
	String canonicalUrl = properties.get("canonicalUrl", "");
	if("".equals(canonicalUrl) == false){
		// resolve only if this is relative path
		if(canonicalUrl.startsWith("/")) {
			Page canonicalUrlPage = resourceResolver.resolve(canonicalUrl).adaptTo(Page.class);
			if (canonicalUrlPage != null && !canonicalUrl.contains(".html")) {
				canonicalUrl += ".html";
			}	
			Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);
			canonicalUrl = externalizer.absoluteLink((SlingHttpServletRequest)request, request.getScheme(), canonicalUrl);
		}
	}
	
	Page parentPage = currentPage.getAbsoluteParent(2);
	String fbAppId = parentPage.getProperties().get("facebookId", "419540344831322");
	if(!"".equals(properties.get("fbAppId",""))){
		fbAppId = properties.get("fbAppId","");
	}

%><head>
	<% if (isProd) { %>
    	<script src="//assets.adobedtm.com/8fdbb9077cc907df83e5ac2a5b43422f8da0b942/satelliteLib-3d0de2c9d6782ec7986e1b3747da043a2d16bd96.js"></script>
    <% } else { %>
    	<script src="//assets.adobedtm.com/8fdbb9077cc907df83e5ac2a5b43422f8da0b942/satelliteLib-3d0de2c9d6782ec7986e1b3747da043a2d16bd96-staging.js"></script>
    <% } %>
    
<meta name="twitter:card" content="summary_large_image">
<meta name="twitter:site" content="@girlscouts" />
    
    <% if (ogTitle.length() > 0) {%>
    	<meta property="og:title" content="<%=ogTitle %>"/>
		<meta name="twitter:title" content="<%=ogTitle %>" />
    <%} %>
    <% if (ogSiteName.length() > 0) {%>
		<meta property="og:site_name" content="<%=ogSiteName %>"/>
	<%} %>
	<% if (ogUrl.length() > 0) {%>
		<meta property="og:url" content="<%=ogUrl%>"/>
	<%} %>
	<% if (ogDescription.length() > 0) {%>
		<meta property="og:description" content="<%=ogDescription %>"/>
		<meta name="twitter:description" content="<%=ogDescription %>" />
	<%} %>
	<%
	if (ogImage.length() > 0) {
		if (ogImage.indexOf("http:") != -1) { %>
			<meta property="og:image" content="<%=ogImage %>"/>
			<meta name="twitter:image" content="<%=ogImage %>" />
	<%	} else { 
			Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);
	%>
			<meta property="og:image" content="<%=externalizer.absoluteLink((SlingHttpServletRequest)request, "http", ogImage) %>"/>
			<meta name="twitter:image" content="<%=externalizer.absoluteLink((SlingHttpServletRequest)request, "http", ogImage) %>" />
	<%
		}
	} %>
	<% if (fbAppId.length() > 0) {%>
		<meta property="fb:app_id" content="<%=fbAppId %>"/>
	<%} %>
    
    
    <meta http-equiv="X-UA-Compatible" content="IE=9">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"<%=xs%>>
    <!--for the mobile viewport.-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <% if(!properties.get("keywords","").equals("")){ %>
    <meta name="keywords" content="<%= xssAPI.encodeForHTMLAttr(properties.get("keywords", "")) %>" <%=xs%>>
    <% }else{ %>
    <meta name="keywords" content="<%= xssAPI.encodeForHTMLAttr(WCMUtils.getKeywords(currentPage, false)) %>"<%=xs%>>
    <% } %>
    <% if (!properties.get("description", "").equals("")) { %>
    	<meta name="description" content="<%= xssAPI.encodeForHTMLAttr(properties.get("description", "")) %>"<%=xs%>>
    	<% if (ogDescription.length() == 0) { %>
			<meta property="og:description" content="<%= xssAPI.encodeForHTMLAttr(properties.get("description", "")) %>"/>
			<meta name="twitter:description" content="<%= xssAPI.encodeForHTMLAttr(properties.get("description", "")) %>" />
    	<% } %>
    <% } else { %>
	    <meta name="description" content="<%= xssAPI.encodeForHTMLAttr(properties.get("jcr:description", "")) %>"<%=xs%>>
    	<% if (ogDescription.length() == 0) { %>
			<meta property="og:description" content="<%= xssAPI.encodeForHTMLAttr(properties.get("jcr:description", "")) %>"/>
			<meta name="twitter:description" content="<%= xssAPI.encodeForHTMLAttr(properties.get("jcr:description", "")) %>" />
    	<% } %>
    <% } %>
    
    <% if (canonicalUrl.length() > 0) {%>
    	<link rel="canonical" href="<%=canonicalUrl%>" />
    <% } %>
    
    <cq:include script="headlibs.jsp"/>
    <cq:include script="/libs/wcm/core/components/init/init.jsp"/>
    <cq:include script="stats.jsp"/>
    <% if (favIcon != null) { %>
    <link rel="icon" type="image/vnd.microsoft.icon" href="<%= xssAPI.getValidHref(favIcon) %>"<%=xs%>>
    <link rel="shortcut icon" type="image/vnd.microsoft.icon" href="<%= xssAPI.getValidHref(favIcon) %>"<%=xs%>>
    <% }

	String title = "";
	try {
        	title = currentPage.getContentResource().adaptTo(ValueMap.class).get("seoTitle", "");
    		if (title.isEmpty()) {
    			title = currentPage.getTitle() == null ? currentPage.getName() : currentPage.getTitle();
		}
        } catch (Exception e) {}
    	title = xssAPI.encodeForHTML(title);
    %>
    <title><%= title %></title>

    <!-- Google Analytics Tracking -->
    <script type="text/javascript">
	$(document).ready(function() {
		(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function()
		{ (i[r].q=i[r].q||[]).push(arguments)}
		,i[r].l=1*new Date();a=s.createElement(o),
		m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
		})(window,document,'script','//www.google-analytics.com/analytics.js','ga');
		ga('create', 'UA-2646810-1', 'auto');
		ga('send', 'pageview');
	});
	</script>
	<!-- END GA Tracking -->

</head>
