<%@ page import="org.apache.sling.api.resource.Resource, org.apache.sling.api.resource.ValueMap, org.girlscouts.common.osgi.component.GirlscoutsVtkConfigProvider" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<div id="right-canvas-menu">
	<ul class="side-nav" style="padding:0px; background-color:#6b6b6b;"> 
<%

	GirlscoutsVtkConfigProvider configManager = sling.getService(GirlscoutsVtkConfigProvider.class);
String headerPath = (String)request.getAttribute("headerPath");
Resource globalNav = resourceResolver.resolve(headerPath+"/global-nav");
if(globalNav != null){
	ValueMap globalNavProps = globalNav.getValueMap();
	String[] links = (String[])(request.getAttribute("globlinks"));
	request.setAttribute("globalNavigation", links);
	for (int i = 0; i < links.length; i++) {
	        String[] values = links[i].split("\\|\\|\\|");
	        String label = values[0];
	        String path = values.length >= 2 ? values[1] : "";
	        path = genLink(resourceResolver, path);
	        String clazz = values.length >= 3 ? " "+ values[2] : "";
	        String mLabel = values.length >=4 ? " "+values[3] : "";
	        String sLabel = values.length >=5 ? " "+values[4] : "";
	        if( path.toLowerCase().contains("vtk")){
	%>
			<li>
				<a class="<%= clazz %> homepage" href="<%= path %>"><%= mLabel %></a>
			</li>
			<li>
				<a class="<%= clazz %> homepage" href="<%= currentPage.getAbsoluteParent(1).getPath() + "/en.html" %>">HOME</a>
			</li>
			<li>
				<a class="<%= clazz %> homepage" href="<%= configManager.getCommunityUrl()%>">MEMBER PROFILE</a>
			</li>
	<%
		} else {
		
	%>
			<li>
				<a class="<%= clazz %> homepage" href="<%= path %>"><%= mLabel %></a>
			</li>
	<%
		 }
	}
}
%>
	</ul>
</div>

    
 
