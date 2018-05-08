<%--
  ADOBE CONFIDENTIAL

  Copyright 2015 Adobe Systems Incorporated
  All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and may be covered by U.S. and Foreign Patents,
  patents in process, and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.
--%><%
%><%@page session="false" %><%
%><%@include file="/libs/granite/ui/global.jsp"%><%
%><%@page import="java.util.Iterator,
                  java.util.Map,
                  java.util.Map.Entry,
                  org.apache.sling.api.resource.ResourceUtil,
                  org.apache.sling.commons.json.JSONException,
                  org.apache.sling.commons.json.JSONObject,
                  org.apache.sling.commons.json.io.JSONStringer,
                  javax.jcr.Node,
                  com.day.cq.dam.commons.util.UIHelper,
                  com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.ComponentHelper.Options,
                  com.adobe.granite.ui.components.ExpressionHelper,
                  com.adobe.granite.ui.components.Tag,
                  com.adobe.granite.ui.components.ValueMapResourceWrapper" %>
<ui:includeClientLib categories="common.components.cancel-scheduled-action"/><%
Config cfg = cmp.getConfig();
final String text = cfg.get("text", String.class);
final String workflowType = cfg.get("workflowType", String.class);
String onClick = "cancel_action('"+workflowType+"')";
%>
<a is="coral-anchorlist-item" onclick="<%=onClick %>" class="cq-siteadmin-admin-actions-publish-activator foundation-collection-action coral-Link coral-BasicList-item coral-AnchorList-item" data-foundation-collection-action="{&quot;activeSelectionCount&quot;:&quot;single&quot;}" data-foundation-tracking-event="{&quot;feature&quot;:null,&quot;element&quot;:&quot;cancel scheduled activations&quot;,&quot;type&quot;:&quot;button&quot;,&quot;widget&quot;:{&quot;name&quot;:null,&quot;type&quot;:&quot;button&quot;}}" href="#" coral-list-item="" tabindex="0">
	<coral-icon class="coral-Icon coral-BasicList-item-icon coral-Icon--sizeS" icon="" size="S" handle="icon" hidden=""></coral-icon>
	<div class=" coral-BasicList-item-outerContainer" handle="outerContainer">
	  <div class=" coral-BasicList-item-contentContainer" handle="contentContainer"><coral-list-item-content class="coral-BasicList-item-content"><%=text %></coral-list-item-content></div>
	</div>
</a>