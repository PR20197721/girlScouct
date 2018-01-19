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

--%><%@include file="/libs/foundation/global.jsp"%>


<%
%><%@ page
	import="com.day.cq.wcm.api.WCMMode,
	com.day.cq.commons.jcr.JcrUtil,
	javax.jcr.Session,
	java.util.Calendar,
	org.girlscouts.web.events.search.GSDateTime,
	java.util.regex.Pattern,
	java.util.regex.Matcher"%>
<%
%><body>
	<script src="/libs/cq/ui/resources/cq-ui.js" type="text/javascript"></script>
	<%
        try{
		String contentPath = properties.get("cq:targetPath", "");
		String dlgPathProperty = properties.get("dialogPath", "");
		String dlgPath = !dlgPathProperty.isEmpty() ? dlgPathProperty : resource.getPath() + "/dialog";
		String templatePath = properties.get("cq:targetTemplate", "");
		String scaffoldPath = resourcePage.getPath();
		String formUrl = contentPath + "/*";
		boolean isUpdate = false;
		if (!resourcePage.getPath().equals(currentPage.getPath())) {
			contentPath = currentPage.getPath();
			formUrl = currentPage.getPath();
			isUpdate = true;
		}
	%>
	contentPath =
	<%=contentPath%>
	scaffoldPath =
	<%=scaffoldPath%>

	<h1><%=currentPage.getTitle()%></h1>
	<%
		if (!isUpdate) {
			if (WCMMode.fromRequest(request) == WCMMode.DESIGN) {
	%>You can edit this form using the
	<a target="_new" href="<%=dlgPath%>.html">dialog editor</a>
	<br>
</body>
<%
	return;
		}
		String descr = properties.get("jcr:description", "");
		if (descr.length() > 0) {
%><em><%=descr%></em>
<br>
<br>
<%
	}
		if (scaffoldPath.equals("/etc/scaffolding")) {
%></body>
<%
	return;
		}
		if (contentPath.length() == 0 || templatePath.length() == 0) {
%>Please define the target path and a template in the page
properties of this scaffolding.
<br>
</body>
<%
	return;
		} else {
	        try{
		        Pattern p = Pattern.compile(".*/etc/scaffolding/([^/]*)/.*");
		        Matcher m = p.matcher(currentPage.getPath());
		        m.matches();
		        
		        String bulkPage = "/content/" + m.group(1) + "/en/events-repository";
		        
		        String yr = "";
		        try{
		        	GSDateTime dateTime = new GSDateTime();
		        	yr = "" + dateTime.getYear();
		        }catch(Exception e){
		        	e.printStackTrace();
		        }
		        //TODO Separate start and end into dates and times. Separate tags into grade level and categories
		        %><div>For the bulk editor, please <a href="<%= "/etc/importers/gsbulkeditor.html?rp=" + bulkPage + "&cm=true&deep=false&cv=jcr:title&ec=data%2Fstart%2Cdata%2Fend%2Cdata%2Fregion%2Cdata%2FlocationLabel%2Cdata%2Faddress%2Cdata%2Fdetails%2Cdata%2Fsrchdisp%2Cdata%2Fcolor%2Cdata%2Fregister%2Ccq%3Atags%2Cdata%2Fimage%2FfileReference%2Cdata%2FregOpen%2Cdata%2FregClose%2Cdata%2Ftimezone%2Cdata%2FprogType%2Cdata%2Fgrades%2Cdata%2FgirlFee%2Cdata%2FadultFee%2Cdata%2FminAttend%2Cdata%2FmaxAttend%2Cdata%2FprogramCode&hib=false&is=true&rt=girlscouts/components/event-page&it=events&hpc=false&hy=false&hr=true&yr=" + yr %>">Click Here</a></div><%
	        }catch(Exception e){
	        	System.err.println("Event bulkeditor - could not determine path");
	        }
%>Create pages below
<a href="/editor.html<%=contentPath%>.html"><%=contentPath%></a>
<ul id="linklist"></ul>
<%
	}
	}
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

        var isUpdate = <%=isUpdate%>;
        var myForm = new CQ.Ext.form.FormPanel({
            //standardSubmit: false,
            url: CQ.HTTP.externalize("<%=formUrl%>"),
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

            /**
             * Loads the content from the given data store or path.
             * @param {Store/String} content The data store or path
             */
            loadContent: function(content) {
                var store;
                if (typeof(content) == "string") {
                    this.path = content;
                    // hack to fetch only jcr:content
                    var url = CQ.HTTP.externalize(this.path + "/jcr:content");
                    store = new CQ.data.SlingStore({"url": url + CQ.Sling.SELECTOR_INFINITY + CQ.HTTP.EXTENSION_JSON});
                } else if (content instanceof CQ.Ext.data.Store) {
                    store = content;
                }
                if (store) {
                    store.load({
                        callback: this.processRecords,
                        scope: this
                    });
                }
            },

            /**
             * Processes the given records. This method should only be used as
             * a callback by the component's store when loading content.
             * @param {CQ.Ext.data.Record[]} recs The records
             * @param {Object} opts The options such as the scope (optional)
             * @param {Boolean} success <code>true</code> if retrieval of records was
             *        successful, <code>false</code> otherwise
             * @private
             */
            processRecords: function(recs, opts, success) {
                var rec;
                if (success) {
                    rec = recs[0];
                    // hack to move data below jcr:content
                    rec.data = { "jcr:content" : rec.data };
                } else {
                    CQ.Log.warn("scaffolding processRecords: retrieval of records unsuccessful");
                    rec = new CQ.data.SlingRecord();
                    rec.data = {};
                }
                rec.data.allowUpload = isUpdate;
                CQ.Log.debug("scaffolding processRecords: processing records for fields");
                var scope = opts.scope ? opts.scope : this;
                var fields = CQ.Util.findFormFields(this);
                for (var name in fields) {
                    for (var i = 0; i < fields[name].length; i++) {
                        try {
                            if (fields[name][i].processPath) {
                                CQ.Log.debug("scaffolding processRecords: calling processPath of field '{0}'", [name]);
                                fields[name][i].processPath(this.path);
                            }
                            if (isUpdate || ($CQ && $CQ.inArray(fields[name][i].xtype, forcedFields) !== -1)) {
                                if (!fields[name][i].initialConfig.ignoreData) {
                                    CQ.Log.debug("scaffolding processRecords: calling processRecord of field '{0}'", [name]);
                                    fields[name][i].processRecord(rec, this.path);
                                }
                            }
                        }
                        catch (e) {
                            CQ.Log.debug("scaffolding processRecords: {0}", e.message);
                        }
                    }
                }
                //this.fireEvent("loadContent", this);

                // prepare creating an undo step from the update operation
                if (CQ.undo.UndoManager.isEnabled()) {
                    CQ.undo.util.UndoUtils.addUndoMarker(this);
                    CQ.undo.UndoManager.getHistory().prepareUndo(
                            new CQ.undo.util.OriginalData(this.path, rec, this, true));
                }
            },


            /**
             * Processes the given records. This method should only be used as
             * a callback by the component's store when loading content.
             */
            processPath: function(path) {
                var fields = CQ.Util.findFormFields(this);
                for (var name in fields) {
                    for (var i = 0; i < fields[name].length; i++) {
                        try {
                            if (fields[name][i].processPath) {
                                fields[name][i].processPath(path);
                            }
                        }
                        catch (e) {
                            CQ.Log.debug("scaffolding processPath: {0}", e.message);
                        }
                    }
                }
            },

            getActiveTab: function() {
                return this;
            }
        });
        myForm.addButton({
            text: isUpdate ? CQ.I18n.getMessage("Update") : CQ.I18n.getMessage("Create"),
            handler: function() {
                var frm = myForm.getForm();
                var params;
                if (isUpdate) {
                    params = {
                        "_charset_": "utf-8",
                        "./jcr:content/cq:scaffolding": "<%=scaffoldPath%>"
                    };
                } else {
                    params = {
                        "_charset_": "utf-8",
                        "./jcr:primaryType": "cq:Page",
                        "./jcr:content@CopyFrom": "<%=templatePath%>/jcr:content",
                        "./jcr:content/jcr:primaryType": "cq:PageContent",
                        "./jcr:content/cq:scaffolding": "<%=scaffoldPath%>"
                    };
                }
                var title = frm.findField("./jcr:content/jcr:title");
                if (title) {
                    var hint = title.getValue();
                    if (hint) {
                        params[":nameHint"] = hint;
                    }
                }
                
                /****************************************
                // Customize code to add year
                ****************************************/
				var dateField = frm.findField("./jcr:content/data/start");
                var month = dateField.getValue().getFullYear();
                var startDate = dateField.getValue();
                var endDateField = frm.findField("./jcr:content/data/end");
                var endDate = endDateField.getValue();

                var year;
                if (dateField.getValue()) {
                	year = dateField.getValue().getFullYear();	
                } else {
                	year = new Date().getFullYear();
                }
                if(!isUpdate){
                var destDir = '<%=contentPath%>/' + year;
                girlscouts.functions.createPath(destDir, 'cq:Page');
                frm.url = destDir + '/*';
                }
                var action = new CQ.form.SlingSubmitAction(frm, {
                    params: params,
                    success: function(frm, resp) {
                        var contentPath = resp.result["Path"];
                        if (isUpdate) {
                            //CQ.Ext.Msg.alert("Success", "Updated " + contentPath);
                            CQ.Util.reload(CQ.WCM.getContentWindow(), CQ.HTTP.externalize(contentPath + ".html"));
                        } else if((startDate < endDate) | endDate == ""){
                            //CQ.Ext.Msg.alert("Success", "Created page " + contentPath);
                            var title = contentPath;
                            var html = "<li><a href='/editor.html"+ CQ.HTTP.externalize(contentPath + ".html")+"'>"+title+"</a></li>";
                            CQ.Ext.DomHelper.append("linklist", html);
                            frm.reset();
                            window.scrollTo(0,0);
                            frm.findField(0).focus();
                        }

            }
                });
                if(typeof endDate === 'object' && startDate < endDate){
                    
                    var startDateValue = frm.findField("./jcr:content/data/start").el.dom.value;

                    var periodVariance = (endDate.getTime() - startDate.getTime())/900000;
                    var stringPeriod = "";
                    if(periodVariance < 10){
						stringPeriod = "00" + (periodVariance | 0);
                    } else if(periodVariance < 100){
						stringPeriod = "0" + (periodVariance | 0);
                    } else if(periodVariance < 1000){
						stringPeriod = "" + (periodVariance | 0);
                    } else{
						stringPeriod = "";
                    }


					 if(0 != stringPeriod.length){
						var firstPart = startDateValue.substring(0, startDateValue.length - 9);
						var lastPart = startDateValue.substring(startDateValue.length - 6, startDateValue.length);
                        var newStartDate = firstPart + stringPeriod + lastPart;
						frm.findField("./jcr:content/data/start").el.dom.value = newStartDate;

                    }
                	frm.doAction(action);
                } else if (endDate.length == 0){
					frm.doAction(action);
                } else {
                    CQ.Ext.Msg.alert("Error", "The Event End Date cannot be before or at the same time as Event Start Date");
                   frm.reset();
                   window.scrollTo(0,0);
                   frm.findField(0).focus();
                }
            }
        });
        var url = CQ.HTTP.externalize("<%=dlgPath%>.infinity.json");
        var data = CQ.HTTP.eval(url);
        if (data) {
            var ct = CQ.utils.Util.formatData(data);
            myForm.processExternalDialog(ct);
        }
        myForm.render("dlg");
        myForm.loadContent("<%=contentPath%>");
        // hack: register ourselves as dialog, so that the DD from the contentfinder works
        CQ.WCM.registerDialog("<%=dlgPath%>", myForm);

				myForm.fireEvent("activate", myForm);
				myForm.getForm().findField(0).focus();
				window.scrollTo(0, 0);
			});
</script><% } catch(Exception e) { e.printStackTrace();} %>
</body>
