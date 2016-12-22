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
%><%@page session="false" contentType="text/html; charset=utf-8"%><%
%><%@page import="com.adobe.granite.ui.components.Config,
                  com.day.cq.i18n.I18n" %><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0"%><%
%><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %><%
%><%@taglib prefix="ui" uri="http://www.adobe.com/taglibs/granite/ui/1.0"%><%
%><cq:defineObjects /><%

    I18n i18n = new I18n(slingRequest);
    Config cfg = new Config(resource);

%>
<ui:includeClientLib categories="cq.tagging.touch.publishtag" />
<div id="<%= cfg.get("id") %>" class="coral-Modal notice <%= cfg.get("rel") %>">
    <div class="coral-Modal-header">
        <i class="coral-Modal-typeIcon coral-Icon coral-Icon--sizeS"></i>
        <h2 class="coral-Modal-title coral-Heading coral-Heading--2"><%= i18n.get("Publish Tag") %></h2>
        <button type="button" class="coral-MinimalButton coral-Modal-closeButton" title="Close" data-dismiss="modal">
            <i class="coral-Icon coral-Icon--sizeXS coral-Icon--close coral-MinimalButton-icon "></i></button>
    </div>
    <div class="coral-Modal-body">
        <p><%= i18n.get("Are you sure to publish the selected tag(s)?") %></p>
    </div>
    <div class="coral-Modal-footer">
        <button type="button" class="coral-Button" data-dismiss="modal"><%= i18n.get("Cancel") %></button>
        <button type="button" class="coral-Button coral-Button--warning coral-Button--primary" data-dismiss="modal"><%= i18n.get("Publish") %></button>
    </div>
</div>


<%-- error dialog --%>
<div class="coral-Modal error tags-publish-error">
    <div class="coral-Modal-header">
        <i class="coral-Modal-typeIcon coral-Icon coral-Icon--sizeS coral-Icon--alert"></i>
        <h2 class="coral-Modal-title coral-Heading coral-Heading--2"><%= i18n.get("Error") %></h2>
        <button type="button" class="coral-MinimalButton coral-Modal-closeButton" title="Close" data-dismiss="modal">
            <i class="coral-Icon coral-Icon--sizeXS coral-Icon--close coral-MinimalButton-icon "></i></button>
    </div>
    <div class="coral-Modal-body">
        <p><%= i18n.get("Failed to publish tag(s).") %></p>
    </div>
    <div class="coral-Modal-footer">
        <button type="button" class="coral-Button" data-dismiss="modal"><%= i18n.get("Close") %></button>
    </div>
</div>
