<%@page session="false"%><%--
  Copyright 1997-2008 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Scaffolding selector script

  Finds and includes the correct scaffold for the currentPage.

--%><%@page contentType="text/html" pageEncoding="utf-8" import="
        javax.jcr.Node,
        org.apache.sling.api.resource.Resource,
        com.day.cq.wcm.api.components.IncludeOptions,
        com.day.cq.wcm.core.utils.ScaffoldingUtils" %><%
%><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
<%@include file="/libs/foundation/global.jsp"%>
<%
%><cq:defineObjects/><%

    // first check if the page has a scaffold specified
            String scaffoldPath = pageProperties.get("cq:scaffolding", "/etc/scaffolding");
	System.out.println("Scaffolding path starts out as: " + scaffoldPath);
    if (scaffoldPath.length() == 0) {
        // search all scaffolds for the correct template
        // this should be improved and respect template + best content path
        Resource scRoot = resourceResolver.getResource("/etc/scaffolding");
        Node root = scRoot == null ? null : scRoot.adaptTo(Node.class);
        if (root != null) {
            scaffoldPath = ScaffoldingUtils.findScaffoldByTemplate(root, pageProperties.get("cq:template", ""));
            System.out.println("Scaffolding path found as: " + scaffoldPath);
            %><%= scaffoldPath %><%
            if (scaffoldPath == null) {
                scaffoldPath = ScaffoldingUtils.findScaffoldByPath(root, currentPage.getPath());
                System.out.println("Scaffolding path found by path as: " + scaffoldPath);
            }
        }
    }
    if (scaffoldPath == null || scaffoldPath.length() == 0) {
        // use default
        scaffoldPath = "/etc/scaffolding";
    }
System.out.println("Scaffolding path for this page is: " + scaffoldPath);
Page root = resourceResolver.resolve(scaffoldPath).adaptTo(Page.class);
    scaffoldPath+="/jcr:content.html";
Node scaffoldingRoot = root.adaptTo(Node.class);
scaffoldingRoot = scaffoldingRoot.getNode("jcr:content");
String scaffoldResource = scaffoldingRoot.getProperty("sling:resourceType").getString();
    IncludeOptions.getOptions(request, true).forceSameContext(true);
    %><cq:include resourceType="<%= scaffoldResource %>" path="<%= scaffoldPath %>" /><%

%>
