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

  Calendar component

  ==============================================================================
  DEPRECATED since CQ 5.6.  Use /libs/social/calendar/components/calendar/calendar.jsp directly.
  ==============================================================================
--%><%@ page session="false" %><%
%><%@ page import="java.util.*,
                   org.apache.jackrabbit.util.Text,
                   com.day.cq.commons.JS,
                   com.day.cq.i18n.I18n,
                   com.day.cq.wcm.api.WCMMode,
                   com.day.cq.wcm.api.components.Toolbar,
                   com.day.cq.search.Predicate,
                   com.day.cq.search.PredicateGroup,
                   com.day.cq.search.PredicateConverter,
                   com.day.cq.collab.calendar.CalendarConstants,
                   com.day.cq.collab.calendar.CalendarComponent" %><%
%><%@ include file="/libs/foundation/global.jsp" %><%

    final String DEFAULT_EVENT_FORM = "/libs/collab/calendar/content/eventform";
    final I18n i18n = new I18n(slingRequest);
    
    CalendarComponent cal = new CalendarComponent(resource);
    String defaultCalendarPath = cal.getDefaultCalendarPath();
    String[] subscriptionPaths = properties.get("subscriptions", new String[] {});

    // view / edit
    String eventForm = properties.get("eventForm", String.class);
    String eventViewForm = properties.get("eventViewForm", String.class);
    
    // - first fallback: eventviewer / eventeditor child pages
    // - second fallback: "/libs/collab/calendar/content/eventform" (deprecated since 5.4)
    if (eventForm == null || eventForm.length() == 0) {
        if (currentPage.hasChild("eventeditor")) {
            eventForm = currentPage.getPath() + "/eventeditor";
        } else {
            eventForm = DEFAULT_EVENT_FORM;
        }
    } 
    if (eventViewForm == null || eventViewForm.length() == 0) {
        if (currentPage.hasChild("eventviewer")) {
            eventViewForm = currentPage.getPath() + "/eventviewer";
        } else {
            eventViewForm = eventForm;
        }
    } 

    String eventDisplay = properties.get("eventDisplay", "formPopup");
    String allowEditing = properties.get("allowEditing", "permissionSensitive");

    String eventViewPattern = properties.get("eventViewPattern", String.class);
    String eventEditPattern = properties.get("eventEditPattern", String.class);
    
    // appearance
    int height = properties.get("height", 500);
    boolean showQuerybuilder = properties.get("showQuerybuilder", false);
    int eventDialogWidth = properties.get("eventDialogWidth", 520);
    int eventDialogHeight = properties.get("eventDialogHeight", 500);
    int eventPopupWidth = properties.get("eventPopupWidth", 450);
    int eventPopupHeight = properties.get("eventPopupHeight", 350);
    String eventDialogXType = properties.get("eventDialogXType", String.class);
    String eventPopupXType = properties.get("eventPopupXType", String.class);
    String defaultLens = properties.get("defaultLens", "month");
    
    // general
    String dateFormat = properties.get("dateFormat", "locale");
    String timeFormat = properties.get("timeFormat", "locale");
    String startOfWeek = properties.get("startOfWeek", "locale");
    String timeZone = properties.get("timeZone", pageProperties.getInherited("calendar/timeZone", TimeZone.getDefault().getID()));
    String viewModeLink = resourceResolver.map(request, currentPage.getPath() + ".html?wcmmode=disabled");
    log.debug("calendar component timezone: '" + timeZone + "'");

    if (editContext != null && WCMMode.fromRequest(request) == WCMMode.EDIT) {
        Toolbar tb = editContext.getEditConfig().getToolbar();
        tb.add(0, new Toolbar.Label("<b>" + i18n.get("Calendar") + "</b>"));
        tb.add(new Toolbar.Separator());
        if (DEFAULT_EVENT_FORM.equals(eventForm)) {
            if (eventViewForm.equals(eventForm)) {
                tb.add(new Toolbar.Label("<i>" + i18n.get("Using default event view & edit form") + "</i>"));
            } else {
                tb.add(new Toolbar.Label("<i>" + i18n.get("Using default event edit form") + "</i>"));
                tb.add(new Toolbar.Separator());
                tb.add(new Toolbar.Label("<a href=\"" + eventViewForm + ".html\">" + i18n.get("Event view form") + "</a>"));
            }
        } else if (eventViewForm.equals(eventForm)) {
            tb.add(new Toolbar.Label("<a href=\"" + eventForm + ".html\">" + i18n.get("Event view & edit form") + "</a>"));
        } else {
            tb.add(new Toolbar.Label("<a href=\"" + eventForm + ".html\">" + i18n.get("Event edit form") + "</a>"));
            tb.add(new Toolbar.Separator());
            tb.add(new Toolbar.Label("<a href=\"" + eventViewForm + ".html\">" + i18n.get("Event view form") + "</a>"));
        }
        if (!"never".equals(allowEditing)) {
            tb.add(new Toolbar.Separator());
            tb.add(new Toolbar.Label("<a href=\"" + viewModeLink + "\" target=\"blank\">" + i18n.get("View-mode preview") + "</a>"));
        }
    }

    final Long time = Calendar.getInstance().getTimeInMillis();
    final String domID = "cq-calendar-" + time;

    %><cq:includeClientLib categories="apps.cq.collab.calendar,cq.security" /><%

%>

<style>
@import url("<%= request.getContextPath() %>/etc/designs/calendar/ui.widgets.css");
@import url("<%= request.getContextPath() %>/etc/designs/calendar/static.css");
</style>

<div id="<%= domID %>" class="cq-calendar">

    <div class="sidebox">
<%    if (showQuerybuilder) { %>
        <cq:include path="querybuilder" resourceType="cq/search/components/querybuilder" />
<%    } else { %>
        <form id="querybuilder-<%= time %>-form">
            <script type="text/javascript">
            
                CQ.Ext.onLoad(function() {
                    var form = new CQ.Ext.form.BasicForm("querybuilder-<%= time %>-form", {
                        "method": "GET",
                        "url": "/bin/querybuilder.json"
                    });
                    var qb = new CQ.search.QueryBuilder({
                        "form": form,
                        "renderFieldsTo": "querybuilder-<%= time %>-form"
                    });
            
                    CQ.search.Util.setQueryBuilder(qb);
                });
            
            </script>
        </form>
<%    } %>
    </div>
    
    <div class="content">
        <table class="navbar">
            <tr>
                <td class="left"></td>
                <td class="center"></td>
                <td class="right">
                    <div id="cq-calendar-lensdeck-buttons"></div>
                </td>
            </tr>
        </table>
        
        <%-- make sure this onLoad handler is registered before any lenses register such a handler --%>
        <script type="text/javascript">
            CQ.Ext.onLoad(function() {
                CQ.search.Util.setLensContainer(CQ.Util.build({
                    "xtype": "lensdeck",
                    "renderTo": "cq-calendar-lensdeck-lenses",
                    "renderButtonsTo": "cq-calendar-lensdeck-buttons",
                    "activateFirstLens": false
                }));

                var leftCt = CQ.Ext.DomQuery.selectNode("#<%= domID %> .navbar .left");
                    
                // add fulltext search field (outside the form, thus
                // setting the fulltext field in the ENTER key handler)
                var searchField = new CQ.Ext.form.TwinTriggerField({
                    renderTo: leftCt,
                    name: "fulltext",

                    trigger1Class: "x-form-clear-trigger",
                    trigger2Class: "x-form-search-trigger",
                    hideTrigger1: true,

                    hasSearch: false,
                    
                    onTrigger1Click: function() {
                        if (this.hasSearch) {
                            this.setValue("");
                            this.hasSearch = false;
                            this.search();
                            this.getTrigger(0).hide();
                            this.focus();
                        }
                    },
                    onTrigger2Click: function() {
                        this.search();
                        if (this.getValue()) {
                            this.hasSearch = true;
                            this.getTrigger(0).show();
                            this.focus();
                        } else {
                            this.onTrigger1Click();
                        }
                    },
                    search: function() {
                        CQ.collab.cal.Calendar.search(this.getValue());
                    },
                    listeners: {
                        "specialkey": function(field, e) {
                            if (e.getKey() == CQ.Ext.EventObject.ENTER) {
                                field.onTrigger2Click();
                            }
                        },
                        "render": function(field) {
                            field.wrap.addClass("search");
                        }
                    }
                });

                var dateDisplayWidget = new CQ.Util.build({
                    xtype: "box",
                    renderTo: leftCt,
                    autoEl: { tag: "span", cls: "date", html: "" }
                });
                
                var pathQueryGroup = {};
<%
    Map<String, String> map = PredicateConverter.createMap(cal.getCalendarPathQuery());
    for (Map.Entry<String, String> entry : map.entrySet()) {
%>                pathQueryGroup["<%= entry.getKey() %>"] = "<%= entry.getValue() %>";
<%
    }
%>
                // init Calendar
                CQ.collab.cal.Calendar.init({
                    element:             CQ.Ext.get("<%= domID %>"),
                    queryBuilder:        CQ.search.Util.getQueryBuilder(),
                    defaultCalendarPath: "<%= defaultCalendarPath %>",
                    subscriptions:       <%= JS.array(cal.getSubscriptions()) %>,
                    pathQueryGroup:      pathQueryGroup,
                    eventForm:           <%= JS.str(eventForm) %>,
                    eventViewForm:       <%= JS.str(eventViewForm) %>,
                    eventDisplay:        <%= JS.str(eventDisplay) %>,
                    allowEditing:        <%= JS.str(allowEditing) %>,
                    eventEditPattern:    <%= JS.str(eventEditPattern) %>,
                    eventViewPattern:    <%= JS.str(eventViewPattern) %>,
                    height:              <%= height %>,
                    eventDialogWidth:    <%= eventDialogWidth %>,
                    eventDialogHeight:   <%= eventDialogHeight %>,
                    eventPopupWidth:     <%= eventPopupWidth %>,
                    eventPopupHeight:    <%= eventPopupHeight %>,
                    eventDialogXType:    <%= JS.str(eventDialogXType) %>,
                    eventPopupXType:     <%= JS.str(eventPopupXType) %>,
                    timeZone:            "<%= timeZone %>",
                    dateFormat:          "<%= dateFormat %>",
                    timeFormat:          "<%= timeFormat %>",
                    startOfWeek:         "<%= startOfWeek %>",
                    colors:              <%= JS.array(currentStyle.get("colors", new String[] {"40864B"})) %>,
                    setDateDisplay: function(html) {
                        dateDisplayWidget.getEl().update(html);
                    }
                });

                // navigation button links
                var navCt = CQ.Ext.DomQuery.selectNode("#<%= domID %> .navbar .center");
                
                var prevLink = CQ.Util.build({
                    xtype: 'box',
                    renderTo: navCt,
                    tooltip: CQ.I18n.getMessage("Go backwards"),
                    autoEl: {tag: 'a', href: '#', html: '&laquo;', cls: 'previous'}
                });
                prevLink.getEl().on("click", function() {
                    CQ.collab.cal.Calendar.prev();
                });
                
                var todayLink = CQ.Util.build({
                    xtype: 'box',
                    renderTo: navCt,
                    autoEl: {tag: 'a', href: '#', html: CQ.I18n.getMessage("Today"), cls: 'today'}
                });
                todayLink.getEl().on("click", function() {
                    CQ.collab.cal.Calendar.today();
                });
                
                var nextLink = CQ.Util.build({
                    xtype: 'box',
                    renderTo: navCt,
                    tooltip: CQ.I18n.getMessage("Go forward"),
                    autoEl: {tag: 'a', href: '#', html: '&raquo;', cls: 'next'}
                });
                nextLink.getEl().on("click", function() {
                    CQ.collab.cal.Calendar.next();
                });
                
            });
        </script>
        
        <div id="cq-calendar-lensdeck-lenses" style="height: <%= height %>px;">
            <%-- lenses will be rendered into this div --%>
        </div>
        
        <div id="cq-calendar-lensdeck-parsys">
            <%-- "dummy" div for parsys in edit mode, where lenses can be dragged into and where they will place their init script --%>
            <cq:include path="lenses" resourceType="collab/calendar/components/lenses/parsys" />
        </div>

    </div>

    <script type="text/javascript">
        CQ.Ext.onLoad(function() {
            CQ.search.Util.getLensContainer().setActiveLens("<%= defaultLens %>");
        });
    </script>
</div>