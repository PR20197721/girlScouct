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

  Draws the basic input fields for an event form
  ==============================================================================
  DEPRECATED since CQ 5.6.  Use /libs/social/calendar/components/form/eventbasics/eventbasics.jsp directly.
  ==============================================================================

--%><%@include file="/libs/foundation/global.jsp"%><%
%><%@ page session="false" import="org.apache.jackrabbit.util.Text,
                   java.util.HashMap,
                   java.util.Calendar,
                   java.text.DateFormat,
                   org.apache.sling.api.resource.ValueMap,
                   org.apache.sling.api.wrappers.ValueMapDecorator,
                   com.day.cq.wcm.api.WCMMode,
                   com.day.cq.wcm.foundation.forms.FormsHelper,
                   com.day.cq.wcm.foundation.forms.LayoutHelper,
                   com.day.cq.wcm.foundation.forms.ValidationInfo,
                   com.day.cq.i18n.I18n,
                   com.day.cq.collab.calendar.CalendarUtil" %><%

    final boolean readOnly = FormsHelper.isReadOnly(slingRequest, resource);

    ValueMap values = FormsHelper.getGlobalFormValues(slingRequest);
    if (values == null) {
        values = new ValueMapDecorator(new HashMap<String, Object>());
    }
    
    final String title = values.get("jcr:title", "");
    final boolean isDate = values.get("isDate", false);
    final String isDateValue = isDate ? "true" : "false";
    final String timeZone = values.get("timeZone", String.class);
    
    final Calendar start = values.get("start", Calendar.class);
    final Calendar end =   values.get("end",   Calendar.class);
    
    // use ECMA date format for passing strings to ExtJS date fields below
    DateFormat format = CalendarUtil.getEcmaDateFormat(isDate, timeZone);

    final String startValue = start == null ? null : format.format(start.getTime());
    final String endValue   = end == null   ? null : format.format(end.getTime());
    
    final String resourceType = properties.get("resourceType", String.class);

    %><cq:includeClientLib categories="cq.collab.calendar" /><%

%>
   
    <div id="event-basics-hidden">
        <input type="hidden" name="./jcr:primaryType" value="cq:CalendarEvent" />
<%  if (resourceType != null) { %>
        <input type="hidden" name="./sling:resourceType" value="<%= resourceType %>" />
<%  } %>
    </div>
    <div class="form_row <%= readOnly ? "form-read-only" : "" %>">
        <% LayoutHelper.printTitle("./jcr:title", I18n.get(slingRequest, "Summary", "Summary/title of an event"), false, out); // "Summary/title of an event" %>
        <div class="form_rightcol">
<%  if (readOnly) { %>
            <div class="form-readonly"><%= title %></div>
<%  } else { %>
            <input type="text" name="./jcr:title" value="<%= Text.encodeIllegalXMLCharacters(title) %>" size="60" class="x-form-text x-form-field" />
<%  } %>
        </div>
    </div>
    <div class="form_row">
        <% LayoutHelper.printTitle("./start", I18n.get(slingRequest, "From", "Start of event"), false, out); // "Start of event" %>
        <div class="form_rightcol">
            <div class="form_rightcol_left" style="width:auto" id="event-basics-start"></div>
        
            <div class="form_rightcol_middle">&nbsp;</div>
            
            <%-- isDate checkbox, floating behind from date  --%>
            <div class="form_rightcol_right" id="event-basics-isdate">
                <input type="hidden" name="./isDate@TypeHint" value="Boolean" />
                <input type="hidden" name="./isDate@Delete" value="true" />
            </div>
        </div>
    </div>
    <div class="form_row">
        <% LayoutHelper.printTitle("./end", I18n.get(slingRequest, "To", "End of event"), false, out); // "End of event" %>
        <div class="form_rightcol" id="event-basics-end"></div>
    </div>

    <script>
        CQ.Ext.onLoad(function() {
            // date formats incl. timezone that can be handled by the
            // SlingPostServlet (even pre 5.3 versions):
            // iso8601 like with rfc822 style timezone at the end
            var DATETIME_FORMAT = "Y-m-d\\TH:i:s.000O";
            // same, but ensure time part is zero'd and use fixed UTC = 0 offset
            var DATE_ONLY_FORMAT = "Y-m-d\\T00:00:00.000+0000";

            // set up widgets

            var startField = new CQ.form.DateTime({
                "renderTo":"event-basics-start",
                "id":"cmp-event-basics-start",
                "name":"./start",
                "readOnly": <%= readOnly %>,
                "hiddenFormat": <%= isDate ? "DATE_ONLY_FORMAT" : "DATETIME_FORMAT" %>, 
                "allowBlank":false,
                "valueAsString":true,
                "dateWidth": 100,
                "timeWidth": 85
            });

            var endField = new CQ.form.DateTime({
                "renderTo":"event-basics-end",
                "id":"cmp-event-basics-end",
                "name":"./end",
                "readOnly": <%= readOnly %>,
                "hiddenFormat": <%= isDate ? "DATE_ONLY_FORMAT" : "DATETIME_FORMAT" %>,
                "allowBlank":false,
                "valueAsString":true,
                "dateWidth": 100,
                "timeWidth": 85
            });

            var isDateField = new CQ.Ext.form.Checkbox({
                "renderTo":"event-basics-isdate",
                "id":"cmp-event-basics-isdate",
                "name":"./isDate",
                "hidden": <%= readOnly && !isDate %>,
                "disabled": <%= readOnly %>,
                "width": 80, // required for IE
                "inputValue":"true", // don't send standard "on" or "off"
                "boxLabel": CQ.I18n.getMessage("All day", null, "All day event")
            });

            var timeZoneField = new CQ.Ext.form.Hidden({
                "renderTo": "event-basics-hidden",
                "id": "cmp-event-basics-timezone",
                "name": "./timeZone"
            });

            // set initial values (before change handlers below)
            
<%  if (startValue != null) { %>
            startField.setValue(new Date("<%= startValue %>"));
<%  } %>
<%  if (endValue != null) { %>
            endField.setValue(new Date("<%= endValue %>"));
<%  } %>
<%  if (isDate) { %>
            startField.tf.hide();
            endField.tf.hide();
<%  } %>
            isDateField.setValue(<%= isDateValue %>);

            // change behaviour
            
            var timezone = null;
            var noTimeSetYet = <%= isDateValue %>;
            
            function isDate() {
                return isDateField.getValue();
            }

            // returns the date (optionally from a Datetime field)
            // either in UTC timezone with no time part for date-only dates
            // or in the user timezone for date+time dates
            function getDate(date, forceIsDate) {
                if (typeof date == "undefined" || date == null) {
                    return null;
                }
                if (date.getDateValue) {
                    date = date.getDateValue();
                }
                // always return dates in the user's timezone
                if (forceIsDate || isDate()) {
                    return date.replaceTimezone(timezone).dateOnly();
                } else {
                    return date.shift(timezone);
                }
            }
            
            startField.on("change", function(sf, newStartValue, oldStartValue) {
                // mark change for iframe dialog
                window.eventbasics.dates_changed = true;
                
                // newStartValue (string) => Date => adjust timezone via getDate()
                var start = getDate(startField.valueToDate(newStartValue));
                
                // oldStartValue (string) => Date => adjust timezone via getDate()
                var oldStart = getDate(startField.valueToDate(oldStartValue));
                
                var end = getDate(endField);
                if (start && oldStart && end) {
                    // update end time when start time changes, keep same duration
                    
                    var duration = end.getTime() - oldStart.getTime();
                    // prevent negative durations
                    if (duration < 0) {
                        duration = 0;
                    }

                    // add duration to new start date for new end date
                    var adjustedEnd = new Date(start.getTime() + duration).shift(timezone);
                    if (isDate()) {
                        endField.setDate(adjustedEnd);
                    } else {
                        endField.setValue(adjustedEnd);
                    }
                }
            });

            endField.on("change", function(ef, newEndValue, oldEndValue) {
                // mark change for iframe dialog
                window.eventbasics.dates_changed = true;

                var start = getDate(startField);
                // newEndValue (string) => Date => adjust timezone via getDate()
                var end = getDate(endField.valueToDate(newEndValue));
                
                if (start && end) {
                    // prevent negative durations
                    var duration = end.getTime() - start.getTime();
                    if (duration < 0) {
                        if (isDate()) {
                            endField.setDate(start);
                        } else {
                            endField.setValue(start);
                        }
                    }
                }
            });
            
            // in this special environment, we must make sure the hidden fields are updated
            if (window.formpage_form && typeof window.formpage_form.on === "function") {
                window.formpage_form.on("beforesubmit", function() {
                    startField.checkIfChanged();
                    endField.checkIfChanged();
                });
            }

            isDateField.on("check", function(checkbox, checked) {
                // mark change for iframe dialog
                window.eventbasics.dates_changed = true;
                
                if (checked) {
                    startField.tf.hide();
                    startField.hiddenFormat = DATE_ONLY_FORMAT;
                    endField.tf.hide();
                    endField.hiddenFormat = DATE_ONLY_FORMAT;
                } else {
                    startField.tf.show();
                    startField.hiddenFormat = DATETIME_FORMAT;
                    endField.tf.show();
                    endField.hiddenFormat = DATETIME_FORMAT;
                    
                    // set time to current time if date was previously a pure-date
                    if (noTimeSetYet) {
                        noTimeSetYet = false;
                        
                        // round time to next half or full hour
                        var startTime = CQ.collab.cal.Calendar.roundedTime();

                        // start field date must still be seen as date-only
                        var start = getDate(startField, true);
                        start.setHours(startTime.hour, startTime.min);
                        startField.setValue(start);
                        
                        // set to start + 1 hour
                        var endTime = CQ.collab.cal.Calendar.roundedTime(startTime.hour + 1, startTime.min);
                        
                        // end field date must still be seen as date-only
                        var end = getDate(endField, true);
                        end.setHours(endTime.hour, endTime.min);
                        endField.setValue(end);
                    }
                }

                // update fields to switch from UTC (date-only) to
                // user timezone (date+time) or vice-versa
                startField.setValue(startField.getDateValue());
                endField.setValue(endField.getDateValue());
            });

            // global functions which can be called by the containing page
            
            window.eventbasics = {
                dates_changed: false,
                
                setEventDate: function(start, end) {
                    startField.setValue(start);
                    endField.setValue(end || start);
                },

                setIsDate: function(isDate) {
                    // further work is done in "check" event listener of the isDateField
                    isDateField.setValue(isDate);
                    noTimeSetYet = isDate;
                },

                setYearMonthDayForEventDates: function(start, end) {
                    function setYearMonthDay(date, from) {
                        date.setFullYear(from.getFullYear(), from.getMonth(), from.getDate());
                        return date;
                    }
                    
                    startField.setValue( setYearMonthDay(startField.getDateValue(), start) );
                    endField.setValue( setYearMonthDay(endField.getDateValue(), end || start) );
                },
    
                getEventStart: function() {
                    return startField.getDateValue();
                },
                
                getEventEnd: function() {
                    return endField.getDateValue();
                },
                
                setTimeZone: function(timeZoneID) {
                    timeZoneField.setValue(timeZoneID);
                    timezone = CQ.collab.cal.TimeZone.get(timeZoneID);
                    if (timezone) {
                        if (startField.getDateValue()) {
                            startField.setValue(getDate(startField));
                        }
                        if (endField.getDateValue()) {
                            endField.setValue(getDate(endField));
                        }
                    }
                },
                
                setDateFormats: function(dateFormat, timeFormat, startOfWeek) {
                    // Hack: reinitialize the format and autocompletion
                    // better solution: create fields not before a "render" method is called
                    // (can be done for ajax-based inclusions)
                    
                    startField.df.format = dateFormat;
                    endField.df.format = dateFormat;

                    startField.tf.format = timeFormat;
                    endField.tf.format = timeFormat;
                    startField.tf.store = null;
                    endField.tf.store = null;
                    startField.tf.initComponent();
                    endField.tf.initComponent();

                    CQ.Ext.DatePicker.prototype.startDay = startOfWeek;

                    // re-set values to force update of rendered components
                    startField.setValue(startField.getDateValue());
                    endField.setValue(endField.getDateValue());
                }
            };
            
        });
    </script>
    