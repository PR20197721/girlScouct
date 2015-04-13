
<%@page import="
                javax.jcr.Session,
				java.io.*,
                org.apache.sling.api.resource.ResourceResolver,
                org.apache.sling.api.resource.Resource,
				org.apache.sling.api.adapter.Adaptable,
                com.day.cq.wcm.api.PageManager,
                com.day.cq.wcm.api.Page,
                com.day.cq.dam.api.Asset,
                com.day.cq.search.PredicateGroup,
                com.day.cq.search.Query,
                com.day.cq.search.QueryBuilder,
                org.girlscouts.vtk.ejb.YearPlanUtil,
                com.day.cq.search.result.SearchResult,
                org.girlscouts.vtk.helpers.CouncilMapper"%>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp"%>

<%@include file="../session.jsp"%>

<% 
String resourceName = "";
if (request.getParameter("resource") != null) {
	resourceName = request.getParameter("resource");
}

Resource resourceContent;
Node resourceNode;
%>

<% if(!resourceName.equals("")) { 
	try {
		String councilId = null;
		if (apiConfig != null) {
		    if (apiConfig.getTroops().size() > 0) {
		        councilId = Integer.toString(apiConfig.getTroops().get(0).getCouncilCode());
		    }
		}
		CouncilMapper mapper = sling.getService(CouncilMapper.class);
		String branch = mapper.getCouncilBranch(councilId);

		// TODO: language? 
        resourceContent = resourceResolver.getResource(resourceName);
        resourceNode = resourceContent.adaptTo(Node.class);
%>
<div class="modal_volunteer">
	<div class="header clearfix">
		<h3 class="columns large-22">
			<%
            if(resourceNode.hasProperty("name")){
            	out.println(xssAPI.encodeForHTML(resourceNode.getProperty("name").getString()));
        	}
        	else{
				out.println(xssAPI.encodeForHTML(resourceNode.getName()));
            }
			%>
		</h3>
		<a class="close-reveal-modal columns large-2" href="#"><i
			class="icon-button-circle-cross"></i></a>
	</div>
	<div class="scroll content">
		<section class="content">
		<%
            String fullPath = resourceContent.getPath() + "/meetingInfo/overview";
        	Resource textResource = resourceResolver.getResource(fullPath);
            Node textNode = textResource.adaptTo(Node.class);
            if(textNode.hasProperty("str")){
                out.println(textNode.getProperty("str").getString());
            }
		%>
		</section>
	</div>
</div>
<% }catch(Exception e){
    StringWriter sw = new StringWriter();
  	e.printStackTrace(new PrintWriter(sw));
  	String stackTrace = sw.toString();
        out.println(stackTrace);
    }
}
%>