<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%><%
%><%@include file="/libs/foundation/global.jsp"%>


<%
String[] links = properties.get("links", String[].class);
String path = "";
if ((links == null || links.length == 0) && WCMMode.fromRequest(request) == WCMMode.EDIT) {
	%>##### Frequently Access Forms #####<%
}else {
	    for (int i = 0; i < links.length; i++) {
	    	
	    	
	    	
	        String[] values = links[i].split("\\|\\|\\|");
	        System.out.println("What is the length " +values.length);
	    	System.out.println("What is the value in this" +values[0] + "Second Value" +values[1] +"----Third Value");
	    	
	    	
	    	
	        String label = values[0];
	        String externalLink = values.length>=2? values[1] : "" ;
	        String internalLink = values.length>=3 ? values[2] : "";
	        if(!externalLink.isEmpty()){
	        	path = externalLink;
	        	
	        }
	        if(!internalLink.isEmpty()){
	        	path = genLink(resourceResolver, path);
	        	
	        }
	      %><a href="<%= path %>"><%= label %></a><%
	    }
	}

%>
	
