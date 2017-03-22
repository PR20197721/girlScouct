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

--%>
<%@ page session="false"%>
<%@page import="com.day.cq.wcm.api.WCMMode,
        java.util.Iterator,
        javax.jcr.query.Query,
        com.day.cq.wcm.api.PageFilter"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:includeClientLib categories="cq.foundation-main" />
<cq:includeClientLib categories="cq.shared" />
<cq:include script="/libs/cq/cloudserviceconfigs/components/servicelibs/servicelibs.jsp" />
<cq:includeClientLib css="apps.gsusa" />

<%!
    String list = "";
    public void getAllClientLibs(Node node) throws RepositoryException {

        if (node.hasProperty("test")) {
            // Include ClientLib of node
            list += "\n" + node.getDepth() + " - " + node.getName() + " - " + node.getProperty("test").getString();
        }
        Iterator<Node> children = node.getNodes();
        while (children.hasNext()) {
            Node child = children.next();        
    
            // Recurse into children
            getAllClientLibs(child);
        }

    }    
%>

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

<%--
    getAllClientLibs(currentNode);
    final String sql2 = "SELECT * FROM [cq:Component] AS c WHERE ISDESCENDANTNODE([" + currentPage.getAbsoluteParent(1).getPath() + "])";
    Iterator<Resource> resources = resourceResolver.findResources(sql2, Query.JCR_SQL2);
    while (resources.hasNext()) {
        Resource _component = resources.next();
        ValueMap _properties = _component.getValueMap();        
        //list += "\n" + _component.getName();
    }
    //list += currentPage.getProperties().keySet().toArray();
--%>
<div>
    <%=list%>
</div>