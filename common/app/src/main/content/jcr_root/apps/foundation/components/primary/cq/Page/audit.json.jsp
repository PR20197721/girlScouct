<%--
  ADOBE CONFIDENTIAL
  ___________________

   Copyright 2014 Adobe Systems Incorporated
   All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.

  ==============================================================================

  Export audit log entries as json

--%><%@page session="false"
            import="org.apache.sling.commons.json.io.JSONWriter,
                    com.day.cq.audit.AuditLog,
                    com.day.cq.audit.AuditLogEntry,
                    com.day.cq.i18n.I18n,
                    com.day.cq.replication.ReplicationAction,
                    com.day.cq.wcm.api.PageEvent,
                    com.day.cq.wcm.api.PageModification.ModificationType"
%><%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><sling:defineObjects /><%
    response.setContentType("application/json");
    response.setCharacterEncoding("utf-8");

    final JSONWriter writer = new JSONWriter(response.getWriter());
    final AuditLog al = sling.getService(AuditLog.class);
    AuditLogEntry[] events = null;
    final I18n i18n = new I18n(request);
    if ( al != null ) {
	//Girl Scouts - Changed 15 to 50 to increase visible audit log
        events = al.getLatestEvents(slingRequest.getResourceResolver(), new String[] {PageEvent.EVENT_TOPIC, ReplicationAction.EVENT_TOPIC}, resource.getPath(), 50);
    }
    writer.object();

    writer.key("results");
    writer.value(events == null ? 0 : events.length);

    writer.key("entries");
    writer.array();

    if ( events != null ) {
        for(int i=0; i<events.length; i++ ) {
            final String text;
            if ( events[i].getCategory().equals(ReplicationAction.EVENT_TOPIC) ) {
                final String eventType = events[i].getType();
                text = (eventType != null) ? i18n.getVar(eventType) : null;
            } else {
                final ModificationType actionType = ModificationType.fromName(events[i].getType());
                switch (actionType) {
                    case CREATED : text = i18n.get("Created");
                                   break;
                    case DELETED: text  = i18n.get("Deleted");
                                  break;
                    case MODIFIED: text  = i18n.get("Modified");
                                   break;
                    case VERSION_CREATED: text = i18n.get("Version Created");
                                   break;
                    case MOVED: text = i18n.get("Moved"); 
                                break;
                    default: text = null;
                }
            }
            if ( text != null ) {
                writer.object();
                writer.key("type");
                writer.value(text);
                writer.key("date");
                writer.value(events[i].getTime().toString());
                writer.key("user");
                writer.value(events[i].getUserId());
                writer.endObject();
            }
        }
    }
    writer.endArray();
    writer.endObject();
%>
