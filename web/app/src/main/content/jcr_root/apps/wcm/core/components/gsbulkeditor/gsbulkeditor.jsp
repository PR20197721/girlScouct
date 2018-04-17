<%@page session="false"%><%--
  Copyright 1997-2008 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Blueprint component

  Displays information about a site blueprint.

--%><%
%><%@include file="/libs/foundation/global.jsp" %><%
%><%@page contentType="text/html"
         pageEncoding="utf-8"
         import="java.util.Arrays,
                 java.util.HashMap,
                 java.util.HashSet,
                 java.util.Map,
                 java.util.Set,
                 org.apache.sling.commons.json.JSONException,
                 com.day.cq.commons.TidyJSONWriter" %><%
%><cq:defineObjects/><%
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN">
<html>
<head>
    <title>CQ5 BulkEditor</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <cq:includeClientLib categories="cq.wcm.edit,cq.security,cq.tagging,apps.girlscouts.authoring"/><%
        //accepted parameters: if name is null, short name is read from request.
        //search root path
        String rootPath = getString(request,"rootPath","rp");
        //search query
        String queryParams = getString(request,"queryParams","qp");
        //is content mode enabled: properties are read on jcr:content node and not on search result node
        Boolean contentMode = getBoolean(request,"contentMode","cm");
        Boolean isDeep = getBoolean(request,"isDeep","deep");
        String importType = getString(request,"importType","it");
        //searched properties (checked values from colsSelection displayed as checkboxes)
        String[] colsValue = getStringArray(request,"colsValue","cv");
        //extra searched properties (displayed in a textfield comma separated
        String[] extraCols = getStringArray(request,"extraCols","ec");
        //Girl Scouts - Sling:resourceType specified
        String[] resourceType = getStringArray(request,"resourceType","rt");
        String[] primaryType = getStringArray(request, "primaryType","pt");
        //is query performed on page load
        Boolean initialSearch = getBoolean(request,"initialSearch","is");
        //searched properties selection (displayed as checkboxes)
        String[] colsSelection = getStringArray(request,"colsSelection","cs");

        //searched properties selection and values (displayed as checkboxes)
        String[] cvs = getStringArray(request,"cvs","cvs");
        if( cvs != null && cvs.length > 0) {
            if( colsValue == null) {
                colsValue = cvs;
            } else {
                Set<String> concat = new HashSet<String>();
                concat.addAll(Arrays.asList(cvs));
                concat.addAll(Arrays.asList(colsValue));
                colsValue = concat.toArray(new String[concat.size()]);
            }
            if( colsSelection == null) {
                colsSelection = cvs;
            } else {
                Set<String> concat = new HashSet<String>();
                concat.addAll(Arrays.asList(cvs));
                concat.addAll(Arrays.asList(colsSelection));
                colsSelection = concat.toArray(new String[concat.size()]);
            }
        }

        //show only the grid and not the search panel (do not forget to set the initialSearch to true)
        Boolean showGridOnly = getBoolean(request,"showGridOnly","sgo");
        //on load, search panel is collapsed
        Boolean searchPanelCollapsed = getBoolean(request,"searchPanelCollapsed","spc");

        //hide root path field
        Boolean hideRootPath = getBoolean(request,"hideRootPath","hrp");
        //hide query field
        Boolean hideQueryParams = getBoolean(request,"hideQueryParams","hqp");
        //hide content mode field
        Boolean hideContentMode = getBoolean(request,"hideContentMode","hcm");
        Boolean hideIsDeepMode = getBoolean(request,"hideIsDeepMode","hdm");
        //hide cols selection field
        Boolean hideColsSelection =  getBoolean(request,"hideColsSelection","hcs");
        //hide extra cols field
        Boolean hideExtraCols = getBoolean(request,"hideExtraCols","hec");
        //Girl Scouts - hide resource type field
        Boolean hideResourceType = getBoolean(request,"hideResourceType","hrt");
        Boolean hidePrimaryType = getBoolean(request, "hidePrimaryType", "hpt");

        //hide search button
        Boolean hideSearchButton = getBoolean(request,"hideSearchButton","hsearchb");
        //hide save button
        Boolean hideSaveButton = getBoolean(request,"hideSaveButton","hsavep");
        //hide export button
        Boolean hideExportButton = getBoolean(request,"hideExportButton","hexpb");
        //hide import button
        Boolean hideImportButton = getBoolean(request,"hideImportButton","hib");
        //hide the import button of the grid
        Boolean hideGImportButton = getBoolean(request,"hideGImportButton","hgib");
        //hide bulkEdit button
        Boolean hideBulkEditButton = getBoolean(request,"hideBulkEditButton","hbeb");
        String bulkEditDialogType = getString(request,"bulkEditDialogType","bet");
        String bulkEditDialogPath = getString(request,"bulkEditDialogPath","bep");

        //hide grid search result number text
        Boolean hideResultNumber = getBoolean(request,"hideResultNumber","hrn");
        //hide grid insert button
        Boolean hideInsertButton = getBoolean(request,"hideInsertButton","hinsertb");
        //hide grid delete button
        Boolean hideDeleteButton = getBoolean(request,"hideDeleteButton","hdelb");

        //hide grid "path" column
        Boolean hidePathCol = getBoolean(request,"hidePathCol","hpc");
        
        //GS - for events, year column
        Boolean hideYear = getBoolean(request,"hideYear","hy");
        String year = getString(request,"year","yr");
        		
        Boolean hideResults = getBoolean(request,"hideResults","hr");
        		
        //mandatory default values
        if (colsSelection == null) {
            colsSelection = new String[]{"sling:resourceType", "jcr:title", "cq:lastModified", "subtitle", "text"};
        }

        //non mandatory default values (usability values)
        if (colsValue == null) {
            colsValue = new String[]{"sling:resourceType", "jcr:title"};
        }

        if (contentMode == null) {
            contentMode = true;
        }
        if(isDeep == null){
        	isDeep = false;
        }

        //config
        String queryURL = "/etc/importers/gsbulkeditor/query.json";
        String importURL = "/etc/importers/gsbulkeditor/import";
        String exportURL = "/etc/importers/gsbulkeditor/export.csv";
        if(importType != null && !("").equals(importType)){
        	exportURL = "/etc/importers/gsbulkeditor/" + importType + ".csv";
        }
        String renderTo = "cq-bulkeditor";

        Map<String,Object> bulkEditorConfig = new HashMap<String,Object>();
        bulkEditorConfig.put("rootPath",rootPath);
        bulkEditorConfig.put("queryParams",queryParams);
        bulkEditorConfig.put("contentMode",contentMode);
        bulkEditorConfig.put("isDeepMode",isDeep);
        bulkEditorConfig.put("colsValue",colsValue);
        bulkEditorConfig.put("extraCols",extraCols);
        bulkEditorConfig.put("resourceType",resourceType);
        bulkEditorConfig.put("primaryType",primaryType);
        bulkEditorConfig.put("initialSearch",initialSearch);
        bulkEditorConfig.put("colsSelection",colsSelection);
        bulkEditorConfig.put("queryURL",queryURL);
        bulkEditorConfig.put("importURL",importURL);
        bulkEditorConfig.put("importType",importType);
        bulkEditorConfig.put("exportURL",exportURL);
        bulkEditorConfig.put("renderTo",renderTo);
        bulkEditorConfig.put("showGridOnly",showGridOnly);
        bulkEditorConfig.put("searchPanelCollapsed",searchPanelCollapsed);
        bulkEditorConfig.put("hideRootPath",hideRootPath);
        bulkEditorConfig.put("hideQueryParams",hideQueryParams);
        bulkEditorConfig.put("hideContentMode",hideContentMode);
        bulkEditorConfig.put("hideIsDeepMode",hideIsDeepMode);
        bulkEditorConfig.put("hideColsSelection",hideColsSelection);
        bulkEditorConfig.put("hideExtraCols",hideExtraCols);
        bulkEditorConfig.put("hideResourceType",hideResourceType);
        bulkEditorConfig.put("hidePrimaryType",hidePrimaryType);
        bulkEditorConfig.put("hideSearchButton",hideSearchButton);
        bulkEditorConfig.put("hideSaveButton",hideSaveButton);
        bulkEditorConfig.put("hideExportButton",hideExportButton);
        bulkEditorConfig.put("hideImportButton",hideImportButton);
        bulkEditorConfig.put("hideGImportButton",hideGImportButton);
        bulkEditorConfig.put("hideResultNumber",hideResultNumber);
        bulkEditorConfig.put("hideInsertButton",hideInsertButton);
        bulkEditorConfig.put("hideDeleteButton",hideDeleteButton);
        bulkEditorConfig.put("hideBulkEditButton",hideBulkEditButton);
        bulkEditorConfig.put("bulkEditDialogType",bulkEditDialogType);
        bulkEditorConfig.put("bulkEditDialogPath",bulkEditDialogPath);
        bulkEditorConfig.put("hidePathCol",hidePathCol);
        bulkEditorConfig.put("hideYear",hideYear);
        bulkEditorConfig.put("year",year);
        bulkEditorConfig.put("hideResults",hideResults);
    %>
    <script src="/libs/cq/ui/resources/cq-ui.js" type="text/javascript"></script>
</head>
<body>
<h1>BulkEditor</h1>

<div id="CQ">
    <div id="cq-bulkeditor">
    </div>
</div>
<script type="text/javascript">
    CQ.Ext.onReady(function() {
        var blkeditor = new CQ.wcm.GSBulkEditorForm(<%writeConfig(out,bulkEditorConfig);%>);
    });
</script>
</body>
</html>
<%!
    public void writeConfig(JspWriter out, Map<String,Object> config) throws JSONException {
        TidyJSONWriter w = new TidyJSONWriter(out);
        w.object();
        for(String key: config.keySet()) {
            Object o = config.get(key);
            if( o != null) {
                if( o instanceof String[]) {
                    String[] array = (String[]) o;
                    w.key(key);
                    w.array();
                    for(String v: array) {
                        w.value(v);
                    }
                    w.endArray();
                } else {
                    w.key(key).value(o);
                }

            }
        }
        w.endObject();
    }

    public String getString(HttpServletRequest request, String name, String shortName) {
        String v = request.getParameter(name);
        if ( v == null) return request.getParameter(shortName);
        return v;
    }

    public String[] getStringArray(HttpServletRequest request, String name, String shortName) {
        String[] v = request.getParameterValues(name);
        if ( v == null) return request.getParameterValues(shortName);
        return v;
    }

    public Boolean getBoolean(HttpServletRequest request, String name, String shortName) {
        String v = request.getParameter(name);
        if ( v == null) v = request.getParameter(shortName);
        if( v == null) return null;
        return Boolean.parseBoolean(v);
    }
%>
