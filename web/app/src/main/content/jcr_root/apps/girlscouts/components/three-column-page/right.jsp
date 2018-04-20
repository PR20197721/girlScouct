<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page import="org.apache.sling.jcr.api.SlingRepository" %>
<!-- apps/girlscouts/components/three-column-page/right.jsp -->

<%
if(currentPage.getPath().contains("event-list") || currentPage.getPath().contains("events-list") || currentPage.getPath().contains("activity-list") || currentPage.getPath().contains("activities-list")){
	Boolean includeCart = false;
	if(homepage.getContentResource().adaptTo(Node.class).hasProperty("event-cart")){
		if(homepage.getContentResource().adaptTo(Node.class).getProperty("event-cart").getString().equals("true")){
			includeCart = true;
		}
	}
	
	if(includeCart){
%>
<cq:include path="content/right/event-cart" resourceType="girlscouts/components/event-cart" />
<% } 
}%>

<% 

SlingRepository repository = (SlingRepository)sling.getService(SlingRepository.class);
Session session = repository.loginAdministrative(null);
if (resourceResolver.resolve(currentDesign.getPath() + "/jcr:content/three-column-page/event-cart-par").getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
	Node threecol = session.getNode(currentDesign.getPath() + "/jcr:content/three-column-page");
	Node par = threecol.addNode("event-cart-par", "nt:unstructured");
	par.setProperty("sling:resourceType", "girlscouts-common/components/styled-parsys");
	par.setProperty("components", "/apps/girlscouts/components/event-cart");
}

try{
	session.save();
	session.logout();
}catch(Exception e){
	e.printStackTrace();
}
	
%>

<cq:include path="content/right/advertisement" resourceType="girlscouts/components/advertisement" />
