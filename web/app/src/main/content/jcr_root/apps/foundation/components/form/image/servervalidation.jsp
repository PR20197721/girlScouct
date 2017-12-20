<%@include file="/libs/foundation/global.jsp"%><%@page session="false" %><%
%><%@page import="java.util.regex.Matcher,
                java.util.regex.Pattern,java.util.Iterator,
                com.day.cq.wcm.foundation.forms.FieldDescription,
                com.day.cq.wcm.foundation.forms.FieldHelper,
                com.day.cq.wcm.foundation.forms.FormsHelper,
                com.day.cq.wcm.foundation.forms.ValidationInfo,
                org.apache.sling.api.request.RequestParameter"%><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
 %><sling:defineObjects/><% 
 final String fileUploadMaxSize = request.getParameter("file-upload-max-size");
 if(null != fileUploadMaxSize){
	 int maxSize = -1;
	 try{
		 maxSize = Integer.parseInt(fileUploadMaxSize);
	 }catch(Exception e){
		 
	 }
	 if(maxSize > -1){
		 final FieldDescription desc = FieldHelper.getConstraintFieldDescription(slingRequest);
		 final RequestParameter rp = slingRequest.getRequestParameter(desc.getName());
		 if(rp.getSize() > maxSize * 1000000){
			 desc.setConstraintMessage("File size cannot be larger than "+maxSize+" MB.");
			 ValidationInfo.addConstraintError(slingRequest, desc);
		 }
	 }
 }
%>
