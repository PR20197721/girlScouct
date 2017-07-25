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

  Form 'action' component

  Called after the form start to add action-specific fields

--%><%@include file="/libs/foundation/global.jsp"
%><%@ page session="false" %><%
%><%@ page import="java.util.List,
                org.apache.sling.api.resource.ResourceUtil,
                   org.apache.commons.lang3.StringEscapeUtils,
                   org.apache.sling.api.resource.Resource,
                   org.apache.sling.api.resource.ValueMap,
                   java.util.HashMap,
                   com.day.cq.wcm.foundation.forms.FormsConstants,
                   com.day.cq.wcm.foundation.forms.FormResourceEdit,
                   org.girlscouts.vtk.helpers.ConfigManager"%><%
%><%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><sling:defineObjects/><%
    //get councilmapping in vtk system
		String councilCode = "";
		ConfigManager configs=sling.getService(ConfigManager.class);
		String[] mappings = configs.getCouncilMapping();
		HashMap<String,String> councilMap = new HashMap<String, String>();
		 if (mappings != null) {
		  for (int i = 0; i < mappings.length; i++) {
		    String[] configRecord = mappings[i].split("::");
		    if (configRecord.length >= 2) {
		      councilMap.put(configRecord[1], configRecord[0]);
		    } else {
		      log.error("Malformatted council mapping record: "
		          + mappings[i]);
		    }
		  }
		} 
		//get branch name
		String branch = "";
		final String pagePath = currentPage.getPath();
		// /content/gsctx/en/formpage -> gsctx
		int pos = pagePath.indexOf('/', 1);
    branch = pagePath.substring(pos + 1);
    pos = branch.indexOf('/');
    branch = branch.substring(0, pos);
		//get council code        
		if(councilMap.get(branch)!=null){
			  councilCode = councilMap.get(branch);
		}  
		//get cw or rw
    final ValueMap props = ResourceUtil.getValueMap(resource);
	String campaignID = props.get("campaignID", "");
	String organizationID = props.get("organizationID", "");
	String campaignIDFieldName1 = props.get("campaignIDFieldName1", "");
	String campaignIDFieldName2 = props.get("campaignIDFieldName2", "");
    if(!campaignID.isEmpty() && !organizationID.isEmpty() && !campaignIDFieldName1.isEmpty() && !campaignIDFieldName1.isEmpty()){
%>
<!-- ORGANIZATION ID -->
<input type=hidden name="oid" value="<%=organizationID%>"> 
<!-- CAMPAIGN ID -->
<input type=hidden name="<%=campaignIDFieldName1%>" value="<%=campaignID%>" />
<input type=hidden name="<%=campaignIDFieldName2%>" value="<%=campaignID%>" />

<%  } 
    String debugEmail = props.get("debug", "");
    if(!debugEmail.isEmpty()){
%>
<input type="hidden" name="debug" value=1>
<input type="hidden" name="debugEmail" value="<%=debugEmail%>">
<%  } %>
