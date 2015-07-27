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
%><%@ page import="com.day.cq.commons.Doctype" %><%
    String xs = Doctype.isXHTML(request) ? "/" : "";
    String favIcon = currentDesign.getPath() + "/favicon.ico";
    if (resourceResolver.getResource(favIcon) == null) {
        favIcon = null;
    }
%><head>
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
    <% } else { %>
    <meta name="description" content="<%= xssAPI.encodeForHTMLAttr(properties.get("jcr:description", "")) %>"<%=xs%>>
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
	(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function()
	{ (i[r].q=i[r].q||[]).push(arguments)}
	,i[r].l=1*new Date();a=s.createElement(o),
	m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
	})(window,document,'script','//www.google-analytics.com/analytics.js','ga');
	ga('create', 'UA-2646810-1', 'auto');
	ga('send', 'pageview');
	</script>
	<!-- END GA Tracking -->
</head>
