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

  Calendar Month Lens component
  ==============================================================================
  DEPRECATED since CQ 5.6.
  ==============================================================================
--%><%@ page session="false" import="java.util.Calendar,
                     com.day.cq.commons.JS,
                     com.day.cq.i18n.I18n,
                     com.day.cq.wcm.api.WCMMode,
                     com.day.cq.wcm.api.components.Toolbar" %><%
%><%@include file="/libs/foundation/global.jsp"%><%

    String name = I18n.get(slingRequest, "Month");
    String id = "cq-calendar-month-lens-" + Long.toString(Calendar.getInstance().getTimeInMillis());

    if (editContext != null && WCMMode.fromRequest(request) == WCMMode.EDIT) {
        editContext.getEditConfig().getToolbar().add(0, new Toolbar.Label(name));
        editContext.getEditConfig().getToolbar().add(1, new Toolbar.Separator());
    }

%><script type="text/javascript">
    CQ.Ext.onLoad(function() {
        var config = {
            "xtype": "monthlens",
            "id"   : "<%= id %>",
            "text" : "<%= name %>",

            // these values have to fit the css
            "headerHeight"   : <%= currentStyle.get("headerHeight", 20) %>, // height of the day names header
            "headerBorder"   : <%= currentStyle.get("headerBorder", 2) %>, // css border vertical sum for day names header
            "dayBorder"      : <%= currentStyle.get("dayBorder", 1) %>,    // css border vertical sum for day cell
            "dayHeaderHeight": <%= currentStyle.get("dayHeaderHeight", 13) %>, // height of the day header showing the day number
            "eventHeight"    : <%= currentStyle.get("eventHeight", 14) %> // height of a single event inside a day cell
        };
<%
    if ("icontext".equals(currentStyle.get("lensButtonStyle", "icon"))) {
%>        config.buttonText = "<%= name %>";<%
    }
%>        
        var buttonConfig = {
            tooltip: "<%= name %>",
            tooltipType: "title"
        };
        CQ.search.Util.addLens(CQ.Util.build(config), "month", buttonConfig);
    });
</script>
