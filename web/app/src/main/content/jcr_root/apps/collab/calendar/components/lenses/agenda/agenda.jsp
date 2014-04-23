<%--
  Copyright 1997-2009 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Calendar Agenda Lens component
  ==============================================================================
  DEPRECATED since CQ 5.6.
  ==============================================================================

--%><%@ page session="false" import="java.util.Calendar,
                     com.day.cq.i18n.I18n,
                     com.day.cq.wcm.api.WCMMode,
                     com.day.cq.wcm.api.components.Toolbar" %><%
%><%@include file="/libs/foundation/global.jsp"%><%

    String name = I18n.get(slingRequest, "Agenda");
    String id = "cq-agenda-lens-" +  Long.toString(Calendar.getInstance().getTimeInMillis());

    if (editContext != null && WCMMode.fromRequest(request) == WCMMode.EDIT) {
        editContext.getEditConfig().getToolbar().add(0, new Toolbar.Label(name));
        editContext.getEditConfig().getToolbar().add(1, new Toolbar.Separator());
    }


%><script type="text/javascript">
    CQ.Ext.onLoad(function() {
        var template = new CQ.Ext.XTemplate(
            '<tpl for=".">',
                '<div class="cq-calendar-agenda-item">',
                    '<div class="cq-calendar-agenda-date">',
                    '{[this.renderEventDate(values.start)]}',
                    '</div>',
                    '<div class="cq-calendar-agenda-time">',
                    '{[this.renderEventTime(values.start, values.isDate, values.end)]}',
                    '</div>',
                    '<div class="cq-calendar-agenda-title">{[CQ.shared.XSS.getXSSValue(values.title)]}</div>',
                '</div>',
            '</tpl>'
        );
        template.renderEventDate = function(start) {
            var fm = CQ.Ext.util.Format;
            return fm.date(start, CQ.collab.cal.Calendar.getDatePattern("day"));
        };
        template.renderEventTime = function(start, isDate, end) {
            var fm = CQ.Ext.util.Format;
            var str = "";
            if (!isDate) {
                str += "  " + fm.date(start, CQ.collab.cal.Calendar.getTimePattern("normal"));
                str += " - " + fm.date(end, CQ.collab.cal.Calendar.getTimePattern("normal"));
            } 
            return str;
        };
        
        var config = {
            "xtype": "dataviewlens",
            "id": "<%= xssAPI.encodeForJSString(id) %>",
            "text": "<%= xssAPI.encodeForJSString(name) %>",
            "storeConfig": {
                "reader": new CQ.Ext.data.JsonReader({
                    "totalProperty": "results",
                    "root": "hits",
                    "fields": CQ.collab.cal.Calendar.getFields(),
                    "id": "path"
                }),
                "baseParams": {
                    "_charset_": "utf-8"
                },
                "listeners": {
                    "load": function(store, records, options) {
                        store.records = records;
                    }
                }
            },
            "items": {
                "cls": "cq-calendar-agenda-dataview",
                "tpl": template,
                "itemSelector": ".cq-calendar-agenda-item",
                "prepareData": function(data) {
                    return data;
                },
                "emptyText": CQ.I18n.getMessage("No events available"),
                "listeners": {
                    "click": function(dataview, index, domElement, e) {
                        var event = dataview.store.getAt(index);
                        CQ.collab.cal.Calendar.openEvent(event, domElement, "l-c?");
                    }
                }
            },
            "listeners": {
                "render": function() {
                    var el = this.body || this.el;
                    if(el){
                        el.unselectable();
                    }
                }
            },
            
            // CalendarLens (completely "simulated")
            loadLastData: false,
            
            setDate: function(date) {
                this.date = date; // show agenda from now on
                return true;
            },
            prev: function() {
                // Note: rendering is delayed up to renderMonth(), called upon querybuilder response
                return this.date.add(Date.MONTH, -1);
            },
            next: function() {
                // Note: rendering is delayed up to renderMonth(), called upon querybuilder response
                return this.date.add(Date.MONTH, 1);
            },
            getDateDisplayText: function() {
                return CQ.I18n.getMessage("Agenda");
            },
            
            getStartDate: function() {
                return this.date;
            },
            
            getEndDate: function() {
                return this.date.add(Date.MONTH, 1);
            }
            
        };
<%
    if ("icontext".equals(currentStyle.get("lensButtonStyle", "icon"))) {
%>        config.buttonText = "<%= xssAPI.encodeForJSString(name) %>";<%
    }
%>        
        var buttonConfig = {
            tooltip: "<%= xssAPI.encodeForJSString(name) %>",
            tooltipType: "title"
        };
        CQ.search.Util.addLens(CQ.Util.build(config), "agenda", buttonConfig);
    });
</script>

