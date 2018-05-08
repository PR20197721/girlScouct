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


    The script to render a single version event.

--%><%@page session="false" import="javax.jcr.Node,
                                    javax.jcr.PathNotFoundException,
                                    javax.jcr.RepositoryException,
                                    javax.jcr.Session,
                                    javax.jcr.security.AccessControlManager,
                                    javax.jcr.security.Privilege,
                                    javax.jcr.version.Version,
                                    javax.jcr.version.VersionHistory,
                                    javax.jcr.version.VersionManager,
                                    java.text.SimpleDateFormat,
                                    java.util.Locale,
                                    java.util.ResourceBundle,
                                    com.adobe.granite.timeline.TimelineEvent,
                                    com.day.cq.commons.date.RelativeTimeFormat,
                                    com.day.cq.commons.jcr.JcrConstants"%><%
%><%@include file="/libs/granite/ui/global.jsp" %><%

    TimelineEvent event = (TimelineEvent) request.getAttribute("cq.gui.common.admin.timeline.event");

    AccessControlManager acm = null;
    Session session = resourceResolver.adaptTo(Session.class);
    try {
        acm = session.getAccessControlManager();
    } catch (RepositoryException e) {
        log.error("Unable to get access manager", e);
    }

    String path = slingRequest.getRequestParameter("item").getString("UTF-8");
    if (path == null) {
        log.warn("Unable to display event {}, missing content path", event);
    }
    Node node = session.getNode(path);
    String versionablePath = path; // the versionable path

    // determine if node itself (e.g. asset) or jcr:content (e.g. page) is versionable
    if (!node.isNodeType(JcrConstants.MIX_VERSIONABLE)) {
        try {
            if (node.getNode(JcrConstants.JCR_CONTENT).isNodeType(JcrConstants.MIX_VERSIONABLE)) {
                versionablePath += "/" + JcrConstants.JCR_CONTENT;
            } else {
                // resource not versionable
                return;
            }
        } catch (PathNotFoundException e) {
            return;
        }
    }

    VersionManager versionManager = session.getWorkspace().getVersionManager();
    VersionHistory versionHistory = versionManager.getVersionHistory(versionablePath);
    Version version = versionHistory.getVersion(event.getDescription());

    Locale locale = slingRequest.getLocale();

    ResourceBundle resourceBundle = slingRequest.getResourceBundle(locale);
    String dateText = null;
    try {
        RelativeTimeFormat tf = new RelativeTimeFormat("r", resourceBundle);
        dateText = tf.format(event.getTime(), true);
    } catch (IllegalArgumentException e) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateText = sdf.format(event.getTime());
    }

    String label;
    String label2 = i18n.get("New Version {0}", "example: New Version 1.1", event.getDescription());
    String[] labels = versionHistory.getVersionLabels(version);
    if (labels.length > 0) {
        label = labels[0];
    } else {
        label = label2;
        label2 = null;
    }

    String comment = null;
    Node nd = version.getNode("jcr:frozenNode");
    if (nd.hasProperty("cq:versionComment"))  {
        comment =nd.getProperty("cq:versionComment").getString();
    }


%>
<section class="cq-common-admin-timeline-event cq-common-admin-timeline-event--version">
    <div class="cq-common-admin-timeline-event-icon">
        <coral-icon icon="layers" size="S"></coral-icon>
    </div>
    <div class="cq-common-admin-timeline-event-text">
        <div class="cq-common-admin-timeline-event-title"><%= xssAPI.encodeForHTML(label) %></div>
        <% if (label2 != null) {%>
        <div class="cq-common-admin-timeline-event-subtitle"><%= xssAPI.encodeForHTML(label2) %></div>
        <%}%>
        <div class="cq-common-admin-timeline-event-subtitle"><%= xssAPI.encodeForHTML(dateText) %></div>
        <% if (comment != null) {%>
        <div class="cq-common-admin-timeline-event-balloon"><%= xssAPI.encodeForHTML(comment) %></div>
        <%}%>
    </div>
    <div class="cq-common-admin-timeline-event-expanded">
        <%
        Node contentNode = node.hasNode(JcrConstants.JCR_CONTENT) ? node.getNode(JcrConstants.JCR_CONTENT) : node;
        if (!contentNode.isLocked() || hasPermission(acm, contentNode.getPath(), Privilege.JCR_LOCK_MANAGEMENT)) {
    %><form action="/bin/wcmcommand" onsubmit="return false"
            data-disable=".cq-common-admin-timeline-event--version .cq-common-admin-timeline-event-action-ok"
            data-successmessage="<%= xssAPI.encodeForHTMLAttr(i18n.get("Version reverted")) %>" data-errormessage="<%= xssAPI.encodeForHTMLAttr(i18n.get("Failed to revert version")) %>">
        <input type="hidden" name="cmd" value="restoreVersion">
        <input type="hidden" name="_charset_" value="utf-8">
        <input type="hidden" name="id" value="<%= xssAPI.encodeForHTMLAttr(version.getIdentifier()) %>">
        <input type="hidden" name="path" value="<%= xssAPI.getValidHref(path) %>">
        <button is="coral-button" class="cq-common-admin-timeline-event-button cq-common-admin-timeline-event-action-ok foundation-collection-action">
            <%= xssAPI.encodeForHTML(i18n.get("Revert to this Version")) %>
        </button>
    </form><%
        }
    %></div>
</section><%!

    private boolean hasPermission(AccessControlManager acm, String path, String privilege) {
        try {
            if (acm != null) {
                Privilege p = acm.privilegeFromName(privilege);
                return acm.hasPrivileges(path, new Privilege[]{p});
            }
        } catch (RepositoryException e) {
            // ignore
        }
        return false;
    }
%>
