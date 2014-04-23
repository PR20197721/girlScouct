<%--
  Copyright 1997-2008 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Form 'element' component

  Allows to push an event subscription to another calendar
  ==============================================================================
  DEPRECATED since CQ 5.6.  Use /libs/social/calendar/components/form/pushworkflow/pushworkflow.jsp directly.
  ==============================================================================
--%><%@include file="/libs/foundation/global.jsp"%><%
%><%@ page session="false" import="java.util.List,
        com.day.cq.wcm.foundation.forms.FormsHelper,
        com.day.cq.wcm.foundation.forms.LayoutHelper,
        com.day.cq.wcm.foundation.forms.ValidationInfo" %><%

    final String workflow = properties.get("workflow", String.class);
    final String[] targets = properties.get("targets", new String[0]);
    
    String[] targetLabels = new String[targets.length];
    for (int i=0; i < targets.length; i++) {
        // get page labels
        Page targetPage = pageManager.getContainingPage(targets[i]);
        if (page != null) {
            targetLabels[i] = targetPage.getTitle() + " (" + targets[i] + ")";
        } else {
            targetLabels[i] = targets[i];
        }
    }
    
    final String buttonLabel = properties.get("buttonLabel", "Start workflow");
    
    List<Resource> editResources = FormsHelper.getFormEditResources(slingRequest);
    String eventResourcePath = null;
    
    boolean eventExists = false;
    if (editResources != null && editResources.size() > 0) {
        Resource editRes = editResources.get(0);
        eventExists = editRes.adaptTo(Node.class) != null;
        eventResourcePath = editRes.getPath();
    }
%>
    <div class="form_row">
<%
        final String title = FormsHelper.getTitle(resource, "Promote event to");
        LayoutHelper.printTitle("", title, false, out);
%>
        <div class="form_rightcol">
            <div id="event-pushworkflow" style="float:left"></div>
            <div id="event-pushworkflow-button" style="float:left; margin-left: 10px;"></div>
            <div style="clear: both"></div>
        </div>
    </div>
    
    <script>
        CQ.Ext.onLoad(function() {
            var targetCombo = new CQ.Ext.form.ComboBox({
                "renderTo":"event-pushworkflow",
                "name":":ignore",
                "editable": false,
                "triggerAction": "all",
                "store": [
<% for (int i=0; targets != null && i < targets.length; i++) { %>
                    ["<%= targets[i] %>", "<%= targetLabels[i] %>"]<% if (i < targets.length-1) { %>,<% } %> 
<% } %>
                ]
            });
            var targetSelection = new CQ.Ext.Button({
                "renderTo":"event-pushworkflow-button",
                "text":"<%= buttonLabel %>",
                "disabled": <%=  (workflow != null && eventExists) ? "false" : "true" %>,
                "listeners": {
                    "click": function(button, event) {
                        var source = "<%= eventResourcePath %>";
                        var target = targetCombo.getValue();
                        var store = targetCombo.getStore();
                        var idx = store.find(targetCombo.valueField, target);
                        var targetLabel = store.getAt(idx).data[targetCombo.displayField];
                        if (target === "") {
                            CQ.Ext.Msg.alert("Cannot start workflow", "Please select a target calendar first.");
                            return;
                        }
                        CQ.HTTP.post(
                            "/etc/workflow/instances?model=<%= workflow %>&payload=" + source + "&payloadType=JCR_PATH&target=" + target + "&startComment=" + targetLabel,
                            function(o, success, response) {
                                if (success) {
                                    CQ.Ext.Msg.alert("Started", "Workflow was successfully started.");
                                } else {
                                    CQ.Ext.Msg.alert("Error", "Could not start workflow. " + response.headers[CQ.HTTP.HEADER_MESSAGE]);
                                }                                    
                            }
                        );
                    }
                }
            });
        });
    </script>