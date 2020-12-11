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

--%>

<%@ page session="false" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts.components.form.actions.gsmail" />
<div id="validation-errors" class="form_error"></div>
<%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><%
int fileUploadMaxSize = -1;
String secret = currentSite.get("recaptcha_secret", "");
try{
    String fileUploadSizeLimit =  (String) properties.get("fileUploadSizeLimit", "");
	if(null != fileUploadSizeLimit && fileUploadSizeLimit.trim().length() > 0){
		fileUploadMaxSize = Integer.parseInt(fileUploadSizeLimit);
	}
}catch(Exception e){
	
}
%>
<input type="hidden" name="secret" value="<%=secret%>">
<input type="hidden" name="file-upload-max-size" value="<%=fileUploadMaxSize%>">