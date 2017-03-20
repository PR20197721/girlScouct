<%--
  Copyright 1997-2010 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Includes the scripts and css to be included in the head tag

  ==============================================================================

--%><%@ page session="false" %><%
%><%@page import="com.day.cq.wcm.api.WCMMode" %><%
%><%@include file="/libs/foundation/global.jsp" %><%
%><cq:includeClientLib categories="cq.foundation-main"/><%
%><cq:includeClientLib categories="cq.shared"/><%
%><cq:include script="/libs/cq/cloudserviceconfigs/components/servicelibs/servicelibs.jsp"/>
<cq:includeClientLib css="apps.gsusa" />

<!-- Begin: Include Girl Scout clientlibs -->
<!-- Artifact Browser -->
<!--[if lt IE 10]>
	<link rel="stylesheet" type="text/css" href="/etc/designs/gsusa/clientlibs/css/app0.css">
	<link rel="stylesheet" type="text/css" href="/etc/designs/gsusa/clientlibs/css/app1.css">
	<link rel="stylesheet" type="text/css" href="/etc/designs/gsusa/clientlibs/css/app2.css">
	<link rel="stylesheet" type="text/css" href="/etc/designs/gsusa/clientlibs/css/app3.css">
	<link rel="stylesheet" type="text/css" href="/etc/designs/gsusa/clientlibs/css/app4.css">
<![endif]-->
<!-- Modern Browser -->
<!--[if gt IE 9]><!-->
	<link rel="stylesheet" type="text/css" href="/etc/designs/gsusa/clientlibs/css/app.css">
<!--<![endif]-->
	<link media="print" rel="stylesheet" type="text/css" href="/etc/designs/gsusa/clientlibs/css/app_print.css">
<script src="/etc/designs/gsusa/clientlibs/js/modernizr.js" type="text/javascript"></script>
<%
	ValueMap siteProps = resourceResolver.resolve(currentPage.getAbsoluteParent(2).getPath() + "/jcr:content").adaptTo(ValueMap.class);
	String addThisId = siteProps.get("addThisId", "");
	if (!addThisId.isEmpty()) {
%>
		<script type="text/javascript" src="//s7.addthis.com/js/300/addthis_widget.js#pubid=<%= addThisId %>"></script>
<%
	}
%>

<% if (WCMMode.fromRequest(request) == WCMMode.EDIT) { %>
	<cq:includeClientLib categories="apps.girlscouts.authoring" />
	<cq:includeClientLib categories="apps.gsusa.authoring" />
<% } %>

    
<%@ page import="java.util.Iterator,
        com.day.cq.wcm.api.PageFilter"%><%
    /*
    String listroot = properties.get("listroot", currentPage.getPath());
    Page rootPage = pageManager.getPage(listroot);
    if (rootPage != null) {
        Iterator<Page> children = rootPage.listChildren(new PageFilter(request));
        while (children.hasNext()) {
            Page child = children.next();
            String title = child.getTitle() == null ? child.getName() : child.getTitle();
            String date = child.getProperties().get("date","");
            String desc = child.getProperties().get("jcr:description","");
            %>
            <script>
                console.log("<%=title%> - <%=date%> - <%=desc%>");
            </script>
            <%
        }
    }
    */        
        // http://stackoverflow.com/questions/16383541/adding-to-head-from-a-cq-component
        Iterator<Node> children = currentNode.getNode("content").getNodes();
        while (children.hasNext()) {
            Node child = children.next();
            String title = child.getName();
            //String desc = child.getProperty("jcr:title").getValues()[0].getString();
            %>
            <script>
                console.log("<%=title%>");
            </script>
            <%
        }

%>