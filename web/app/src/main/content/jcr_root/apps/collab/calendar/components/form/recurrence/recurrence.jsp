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

  Form 'element' component

  Draws a recurrence rule an event form
  ==============================================================================
  DEPRECATED since CQ 5.6.  Use /libs/social/calendar/components/form/recurrence/recurrence.jsp directly.
  ==============================================================================
--%><%@include file="/libs/foundation/global.jsp"%><%
%><%@ page session="false" import="java.util.HashMap,
                   org.apache.sling.api.resource.ValueMap,
                   org.apache.sling.api.wrappers.ValueMapDecorator,
                   com.day.cq.collab.calendar.CalendarConstants,
                   com.day.cq.collab.calendar.Event,
                   com.day.cq.wcm.foundation.forms.FormsHelper,
                   com.day.cq.wcm.foundation.forms.LayoutHelper,
                   com.day.cq.wcm.foundation.forms.ValidationInfo,
                   com.day.cq.i18n.I18n, com.day.cq.wcm.api.WCMMode" %><%

    final boolean readOnly = FormsHelper.isReadOnly(slingRequest, resource);

    ValueMap values = FormsHelper.getGlobalFormValues(slingRequest);
    if (values == null) {
        values = new ValueMapDecorator(new HashMap<String, Object>());
    }
    
    Resource loadResource = FormsHelper.getFormLoadResource(slingRequest);
    Event event = loadResource == null ? null : FormsHelper.getFormLoadResource(slingRequest).adaptTo(Event.class);
    String rulePath = "recurrence/rule";
    if (event != null && event.isRecurrence()) {
        rulePath = event.getOriginal().getPath() + "/recurrence/rule";
    }
    
    // do not show anything in read-only case if there is no recurrence
    if (!readOnly || event.isRecurringSeries()) {

    %><cq:includeClientLib categories="cq.collab.calendar" /><%

%>
    <div class="form_row">
        <% LayoutHelper.printTitle("", I18n.get(slingRequest, "Repeats", "Calendar event recurrence"), false, out); %>
        <div class="form_rightcol" id="event-recurrence-rule"></div>
    </div>
    <script>
        CQ.Ext.onLoad(function() {
            var recurrenceField = new CQ.collab.cal.RecurrenceRuleField({
                "renderTo":"event-recurrence-rule",
                "id":"cmp-event-recurrence-rule",
                "readOnly": <%= readOnly %>,
                "inDateFormat": null, // use javascript Date constructor that parses ecma date format used in json below
                "outDateFormat": "Y-m-d\\TH:i:s.000O", // format for sling post servlet
                "rule": <% FormsHelper.inlineValuesAsJson(slingRequest, out, rulePath); %>
            });
            
            // in this special environment, we must make sure the hidden fields are updated
            if (window.formpage_form && typeof window.formpage_form.on === "function") {
                window.formpage_form.on("beforesubmit", function() {
                    recurrenceField.untilField.checkIfChanged();
                    recurrenceField.frequencyField.focus();
                    recurrenceField.intervalField.focus();
                });
            }
            
            recurrenceField.on("change", function() {
                // required for iframe dialog to detect changes for recurrence save
                window.recurrence.rule_changed = true;
            });
            
            window.recurrence = {
                rule_changed: false,

                setRecurrenceDisabled: function(disable) {
                    recurrenceField.setDisabled(disable);
                },
                
                setDateFormat: function(dateFormat) {
                    recurrenceField.setDisplayDateFormat(dateFormat);
                }
            };
        });
    </script>
<%
    }
%>
    