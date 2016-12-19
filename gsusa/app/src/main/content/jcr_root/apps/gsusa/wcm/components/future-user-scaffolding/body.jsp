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

  Scaffolding component

  Displays and provides editing of scaffoldings

--%><%@include file="/libs/foundation/global.jsp"%><%
%><%@ page import="com.day.cq.wcm.api.WCMMode,
	com.day.cq.commons.jcr.JcrUtil,
	javax.jcr.Session,
	java.util.Calendar" %><%
%><body>
    <script src="/libs/cq/ui/resources/cq-ui.js" type="text/javascript"></script><%
        String contentPath = properties.get("cq:targetPath", "");
	    String dlgPathProperty = properties.get("dialogPath", "");
	    String dlgPath = !dlgPathProperty.isEmpty() ? dlgPathProperty : resource.getPath() + "/dialog";
        String templatePath = properties.get("cq:targetTemplate", "");
        String scaffoldPath = resourcePage.getPath();
        String formUrl = contentPath + "/*";
    %>
    contentPath = <%= contentPath %>
    scaffoldPath = <%= scaffoldPath %>

    <h1><%= currentPage.getTitle() %></h1><%

    %><br>

<div id="CQ">
    <div id="dlg"></div>
</div>

<script type="text/javascript">

    // undo/redo is not allowed when in scaffolding mode (but undo history is active, as
    // we're recording the update)
    if (CQ.undo.UndoManager.isEnabled()) {
        CQ.undo.UndoManager.getHistory().block();
    }

    CQ.Ext.onReady(function() {
        /**
         * An array containing the xtype of widgets that need to call
         * their processRecord function even when creating a new page
         */
        var forcedFields = ["smartfile", "smartimage", "html5smartfile", "html5smartimage"];

        var myForm = new CQ.Ext.form.FormPanel({
            //standardSubmit: false,
            url: CQ.HTTP.externalize("/bin/tempUserGen.html"),
            buttonAlign: "left",
            border:false,
            processExternalDialog: function(data) {
                if (data && data.items) {
                    if (data.items instanceof Array) {
                        for (var i = 0; i < data.items.length; i++) {
                            this.processExternalItem(data.items[i]);
                        }
                    } else {
                        this.processExternalItem(data.items);
                    }
                }
            },

            processExternalItem: function(tab) {
                if (tab["xtype"] == "tabpanel") {
                    this.processExternalDialog(tab);
                } else {
                    if (tab instanceof Array) {
                        for (var i=0; i<tab.length; i++) {
                            this.processExternalItem(tab[i]);
                        }
                    } else {
                        var include = tab;
                        if (tab["xtype"] == "panel") {
                            include = CQ.Util.applyDefaults(include, {
                                layout: "form",
                                autoScroll: true,
                                border: true,
                                bodyStyle: CQ.themes.Dialog.TAB_BODY_STYLE,
                                labelWidth: CQ.themes.Dialog.LABEL_WIDTH,
                                defaultType: "textfield",
                                "stateful": false,
                                defaults: {
                                    msgTarget: CQ.themes.Dialog.MSG_TARGET,
                                    anchor: CQ.themes.Dialog.ANCHOR,
                                    "stateful": false
                                }
                            });
                        }
                        include.header = true;
                        include.border = true;
                        include.headerAsText = true;
                        if (!include.title) {
                            include.title = "untitled";
                        }
                        this.add(include);
                    }
                }
            },

            getActiveTab: function() {
                return this;
            }
        });
        myForm.addButton({
            text: CQ.I18n.getMessage("Create"),
            handler: function() {
                var frm = myForm.getForm();
                var action = new CQ.form.SlingSubmitAction(frm, {
                    success: function(frm, resp) {
                        var responseMessage = resp.result["Message"];
                        CQ.Ext.Msg.alert("Response", responseMessage);
                        frm.reset();
                        window.scrollTo(0,0);
                        frm.findField(0).focus();
                    }
                });
                frm.doAction(action);
            }
        });
        var url = CQ.HTTP.externalize("<%= dlgPath %>.infinity.json");
        var data = CQ.HTTP.eval(url);
        if (data) {
            var ct = CQ.utils.Util.formatData(data);
            myForm.processExternalDialog(ct);
        }
        myForm.render("dlg");
        // hack: register ourselves as dialog, so that the DD from the contentfinder works
        CQ.WCM.registerDialog("<%= dlgPath %>", myForm);

        myForm.fireEvent("activate", myForm);
        myForm.getForm().findField(0).focus();
        window.scrollTo(0,0);
    });
</script>
</body>