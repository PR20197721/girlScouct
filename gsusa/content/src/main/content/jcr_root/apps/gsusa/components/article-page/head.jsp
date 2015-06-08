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
%><%@ page import="com.day.cq.commons.Doctype, com.day.cq.commons.Externalizer" %><%
    String xs = Doctype.isXHTML(request) ? "/" : "";
    String favIcon = currentDesign.getPath() + "/favicon.ico";
    if (resourceResolver.getResource(favIcon) == null) {
        favIcon = null;
    }
%><head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"<%=xs%>>
    <!--for the mobile viewport.-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="keywords" content="<%= xssAPI.encodeForHTMLAttr(WCMUtils.getKeywords(currentPage, false)) %>"<%=xs%>>
    <meta name="description" content="<%= xssAPI.encodeForHTMLAttr(properties.get("jcr:description", "")) %>"<%=xs%>>
    <%
    String shareTitle = properties.get("shareTitle", "");
    String fbMessage = properties.get("fbMessage", "");
    String twitterMessage = properties.get("twitterMessage", "");
    String filePath = properties.get("fileReference", "");

	//Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);
	//Externalizer not properly configured
	//String canonicalUrl = externalizer.publishLink(resourceResolver, "http", currentPage.getPath());
	
	String canonicalURL = currentPage.getPath().replaceFirst("content", "http://girlscouts.org");
   	
    %>
    <!-- Facebook - Open Graph Data -->
    <meta property="og:url" content="<%= canonicalUrl %>" />
    <meta property="og:type" content="website" />
    <%
    if(!shareTitle.equals("")){
    %>
    <meta property="og:title" content="<%= shareTitle %>" />
    <%
    }if(!fbMessage.equals("")){
    %>
    <meta property="og:description" content="<%= fbMessage %>" />
    <%
    }if(!filePath.equals("")){
    %>
    <meta property="og:image" content="<%= filePath %>" />
    <%
    }%>
    <!-- Twitter Card Data -->
    <meta name="twitter:card" content="summary">
    <meta name="twitter:site" content="@girlscouts" />
    <%
    if(!shareTitle.equals("")){
    %>
    <meta name="twitter:title" content="shareTitle">
    <%
    }if(!twitterMessage.equals("")){
    %>
    <meta name="twitter:description" content="<%= twitterMessage %>" >
    <%
    }if(!filePath.equals("")){
   	%>
   	<meta name="twitter:image" content="<%= filePath %>" />	
    <%} %>
    <cq:include script="headlibs.jsp"/>
    <cq:include script="/libs/wcm/core/components/init/init.jsp"/>
    <cq:include script="stats.jsp"/>
    <% if (favIcon != null) { %>
    <link rel="icon" type="image/vnd.microsoft.icon" href="<%= xssAPI.getValidHref(favIcon) %>"<%=xs%>>
    <link rel="shortcut icon" type="image/vnd.microsoft.icon" href="<%= xssAPI.getValidHref(favIcon) %>"<%=xs%>>
    <% } %>
    <title><%= currentPage.getTitle() == null ? xssAPI.encodeForHTML(currentPage.getName()) : xssAPI.encodeForHTML(currentPage.getTitle()) %></title>
</head>
