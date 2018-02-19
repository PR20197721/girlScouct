<%@page import="com.day.cq.wcm.foundation.Image,java.util.List,
java.util.Map,java.util.HashMap,java.util.Iterator,com.day.cq.wcm.api.WCMMode,
org.apache.sling.commons.json.JSONArray,org.apache.sling.commons.json.JSONException,com.day.cq.dam.api.Asset" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
Node imageNode = resource.adaptTo(Node.class);
String fileName = resource.getName();
try{
if (imageNode != null) {
	Iterator<Resource> imageSizes = resource.listChildren();
	while(imageSizes.hasNext()){
		Node imageSize = imageSizes.next().adaptTo(Node.class);
		if(imageSize.hasProperty("imagesize")){
			if(imageSize.getProperty("imagesize").getString().equalsIgnoreCase("regular")){
				fileName = imageSize.getProperty("fileReference").getString();
				if(fileName != null && fileName.trim().length()>0){
					fileName = fileName.substring(fileName.lastIndexOf("/")+1);
				}
			}
		}
	}
}
}catch(Exception e){
	
}
%>
<div data-emptytext="<%=fileName%>" class="cq-placeholder"></div>


  
