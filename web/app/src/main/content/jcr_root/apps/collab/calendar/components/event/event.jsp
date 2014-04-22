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

  Single event component
  
  Provides event metadata for a page. Displays nothing, only in edit mode
  the event data is shown.

  ==============================================================================
  DEPRECATED since CQ 5.6.  Use /libs/social/calendar/components/event/event.jsp directly.
  ==============================================================================
--%><%@ page session="false" %><%
%><%@ page import="java.util.*,
                   javax.jcr.Node,
                   java.text.DateFormat,
                   java.text.SimpleDateFormat,
                   org.apache.sling.api.resource.ResourceUtil,
                   com.day.cq.commons.date.DateUtil,
                   com.day.cq.tagging.Tag,
                   com.day.cq.i18n.I18n,
                   com.day.cq.collab.calendar.CalendarUtil,
                   com.day.cq.wcm.api.WCMMode" %><%
%><%@ include file="/libs/foundation/global.jsp" %><%

    // this component only displays information in wcm edit mode
    if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
        
        String title = properties.get("event/jcr:title", "-");
        String desc = properties.get("event/jcr:description", "-");
        boolean isDate = properties.get("event/isDate", false);
        
        String eventForm = properties.get("eventForm", pageProperties.getInherited("calendar/eventForm", "/libs/collab/calendar/content/eventform"));
        int eventDialogWidth = properties.get("eventDialogWidth", 520);
        int eventDialogHeight = properties.get("eventDialogHeight", 420);
        
        String timeZoneID = pageProperties.getInherited("calendar/timeZone", TimeZone.getDefault().getID());
        log.debug("event component timezone: '" + timeZoneID + "'");
        TimeZone timeZone = CalendarUtil.getTimeZone(timeZoneID);
        
    	final ResourceBundle resourceBundle = slingRequest.getResourceBundle(null);
    	I18n i18n = new I18n(resourceBundle);  
        
        DateFormat dateFormat;
        if (isDate) {
            dateFormat = DateUtil.getDateFormat(i18n.get("MM/dd/yyyy", "Java date format for a date (http://java.sun.com/j2se/1.5.0/docs/api/java/text/SimpleDateFormat.html)"), "MM/dd/yyyy", request.getLocale());
        } else {
            dateFormat = DateUtil.getDateFormat(i18n.get("MM/dd/yyyy h:mm a", "Java date format for date + time (http://java.sun.com/j2se/1.5.0/docs/api/java/text/SimpleDateFormat.html)"), "MM/dd/yyyy h:mm a", request.getLocale());
        }
        String start = "";
        String end = "";
        boolean eventExists = properties.get("event/start") != null;
        try {
            dateFormat.setTimeZone(timeZone);
            Calendar calStart = (Calendar) properties.get("event/start");
            Calendar calEnd = (Calendar) properties.get("event/end");
            if (!isDate) {
                calStart = calStart != null ? CalendarUtil.shiftTimeZone(calStart, timeZone) : calStart;
                calEnd = calEnd != null ? CalendarUtil.shiftTimeZone(calEnd, timeZone) : calEnd;
            }
            start = calStart != null ? dateFormat.format(calStart.getTime()) : "";
            end = calEnd != null ? dateFormat.format(calEnd.getTime()) : "";
        } catch(NullPointerException e) {
            log.error("TimeZone could have been null, should be specified in Olson timezone Ex Europe/Berlin");
        }   
        
        Long time = System.currentTimeMillis();
%>
<%--
    HACK: This hard-codes the dependencies for the cq.collab.calendar lib, as a workaround
          for bug #31586, and assumes that cq.wcm.edit has been loaded already.  
--%>
<script type="text/javascript" src="/libs/cq/search/widgets.js"></script>
<script type="text/javascript" src="/libs/collab/calendar/widgets.js"></script>

<style>
    .cq-event-placeholder {
        border: 2px dashed #cccccc;
    }
    .cq-event-placeholder-off {
        display:none;
    }
    .cq-event-placeholder-img {
        width: 74px;
        height: 74px;
        float: left;
        background: url( "/libs/collab/calendar/components/event/placeholder.png" ) no-repeat scroll 50% 50%
    }
    .cq-event-placeholder dl {
        margin-left: 80px;
    }
    .cq-event-placeholder dt {
        color: #ccc;
        font-weight: bold;
    }
    .cq-event-placeholder dd {
        color: #666;
    }
    .cq-eventcomponent-editbtn-wrapper {
        float: right;
        margin: 5px;
    }
</style>
<div class="cq-event-placeholder" >
    <img class="cq-event-placeholder-img" src="/etc/designs/default/0.gif" alt="Event component">
    <div id="cq-eventcomponent-editbtn-wrapper-<%= time %>" class="cq-eventcomponent-editbtn-wrapper"></div>
    <dl>
        <dt><%=i18n.get("Summary", "Summary/title of an event")%></dt>
        <dd><%= title %></dd>
        <dt><%=i18n.get("Date/time")%></dt>
        <dd><%= start %> - <%= end %></dd>
        <dt><%=i18n.get("Description")%></dt>
        <dd><%= desc %></dd>
<%  if (eventExists) { %>
        <dt><a href="<%= resource.getPath() %>.ics">Download event as ICS file</a></dt>
        <dd></dd>
<%  } %>
    </dl>
    <script type="text/javascript">
        function setFieldValue(form, field, value) {
            var f = form.elements[field];
            if (f) f.value = value;
        }
        
        var initEditButton = function() {
            var editBtn = new CQ.Ext.Button({
                "renderTo": "cq-eventcomponent-editbtn-wrapper-<%= time %>",
                "text": CQ.I18n.getMessage("Edit event"),
                "listeners": {
                    "click": function() {
                        var iframeDialog = new CQ.IframeDialog({
                            formPath: "<%= eventForm %>",
                            title: CQ.I18n.getMessage("Edit event"),
                            okText: CQ.I18n.getMessage("Save"),
                            buttons: CQ.Dialog.OKCANCEL,
                            width: <%= eventDialogWidth %>,
                            height: <%= eventDialogHeight %>,
                            success: function() {
                                // reload component
                                CQ.WCM.getEditable("<%= resource.getPath() %>").refresh()
                            },
                            listeners: {
                                "loadContent": function(dialog) {
                                    var win = dialog.getContentWin()
                                    var eventbasics = win.eventbasics;
                                    if (eventbasics) {
                                        eventbasics.setTimeZone("<%= timeZoneID %>");
                                    }
<%
    // when editing for the first time, set default values based on the current page
    if (!eventExists) {
%>
                                    if (win.formpage_form && win.formpage_form.form) {
                                        var form = win.formpage_form.form;
                                        setFieldValue(form, "./jcr:title", "<%= currentPage.getTitle() %>");
                                        setFieldValue(form, "./jcr:description", "<%= currentPage.getProperties().get("jcr:description", "") %>");
                                        setFieldValue(form, "./url", "<%= currentPage.getPath() %>");
                                    }
                                    
                                    if (eventbasics) {
                                        eventbasics.setEventDate(new Date().shift("<%= timeZoneID %>"));
                                        eventbasics.setIsDate(true);
                                    }
<%  } %>
                                }
                            }
                        });
                        iframeDialog.show();
                        iframeDialog.loadContent("<%= resource.getPath() %>/event");
                    }
                }
            });
        };
        if (CQ.Ext.isReady) {
            initEditButton();
        } else {
            CQ.Ext.onLoad(initEditButton);
        }
    </script>
</div>
<%
    }
%>
 