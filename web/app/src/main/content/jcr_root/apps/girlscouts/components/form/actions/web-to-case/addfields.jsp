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
                   org.girlscouts.web.osgi.component.*"%><%
%><%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><sling:defineObjects/><%
    //get councilmapping in vtk system
		String councilCode = "";
        final GirlscoutsVtkConfigProvider configManager = sling.getService(GirlscoutsVtkConfigProvider.class);
		String[] mappings = configManager.getCouncilMapping();
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
    String cwrw = props.get("cwrw", "");
    if(!councilCode.isEmpty() && !cwrw.isEmpty()){
%>

<!-- EMAIL SERVICE -->
<input type=hidden name="00NG000000DdXSU" id="00NG000000DdXSU" value="Web">
<!-- CASE ESCALATION -->
<input type="hidden" name="00NG000000DdU4U" id="00NG000000DdU4U" value="Tier 1">
<!-- <input type=hidden name="retURL" value="/">-->
<input type="hidden" name="origin" value="<%= councilCode+cwrw %>">
<%  } 
    String debugEmail = props.get("debug", "");
    if(!debugEmail.isEmpty()){
%>
<input type="hidden" name="debug" value=1>
<input type="hidden" name="debugEmail" value="<%=debugEmail%>">
<%  } %>
