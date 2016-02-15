<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page import="org.apache.sling.jcr.api.SlingRepository" %>

<%

Boolean includeCart = false;
if(homepage.getContentResource().adaptTo(Node.class).hasProperty("event-cart")){
	if(homepage.getContentResource().adaptTo(Node.class).getProperty("event-cart").getString().equals("true")){
		includeCart = true;
	}
}

if(includeCart){
%>
<cq:include path="content/event-cart" resourceType="girlscouts/components/event-cart" />
<% } %>

