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
//get the counicl code
String councilCode = "111";
ConfigManager configs=sling.getService(ConfigManager.class);
String[] mappings = configs.getCouncilMapping();
System.out.print(mappings);
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
final String pagePath = currentPage.getPath();
// /content/gsctx/en/formpage -> gsctx
int pos = pagePath.indexOf('/', 1);
        String branch = pagePath.substring(pos + 1);
        pos = branch.indexOf('/');
        branch = branch.substring(0, pos);
if(councilMap.get(branch)!=null){
councilCode = councilMap.get(branch);
}  



//get cw or rw
    final ValueMap props = ResourceUtil.getValueMap(resource);
    String cwrw = props.get("cwrw", "cw");
%>
<input type="hidden" name="origin" value="<%= councilCode+cwrw %>">
