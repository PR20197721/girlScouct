<%@page session="false" %><%
%><%@include file="/libs/granite/ui/global.jsp"%><%
%><%@page import="java.util.Iterator" %>
<%
final String PATH_TO_SCAFFOLDING = "/etc/scaffolding/";
try{
	Resource aemScaffolding = resourceResolver.resolve(PATH_TO_SCAFFOLDING);
	if(aemScaffolding != null && !aemScaffolding.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING) && aemScaffolding.hasChildren()){
		Iterator<Resource> scaffoldingFolders = aemScaffolding.listChildren();
		while(scaffoldingFolders.hasNext()){
			Resource councilScaffolding = scaffoldingFolders.next();
			if(councilScaffolding != null && councilScaffolding.getResourceType().equals("nt:folder") && councilScaffolding.hasChildren()){
				%>
				<div class="foundation-collection-action" data-council="<%=councilScaffolding.getName() %>" 
				data-foundation-collection-action="{&quot;activeCondition&quot;:&quot;site.scaffolding&quot;}">
					<% 
					Iterator<Resource> scaffoldingPages = councilScaffolding.listChildren();
					while(scaffoldingPages.hasNext()){
						Resource scaffoldingPage = scaffoldingPages.next();
						String scaffoldingURI = scaffoldingPage.getPath()+".html";
						String buttonName = "Create " +scaffoldingPage.getName().substring(0, 1).toUpperCase() + scaffoldingPage.getName().substring(1);
						%>
						<a is="coral-anchorlist-item" 
						class="foundation-collection-action coral-Link coral-BasicList-item coral-AnchorList-item" 
						href="<%=scaffoldingURI%>" icon="news" tabindex="0">
							<div class=" coral-BasicList-item-outerContainer" handle="outerContainer">
							  <div class=" coral-BasicList-item-contentContainer" handle="contentContainer">
							  	<coral-list-item-content class="coral-BasicList-item-content"><%=buttonName %></coral-list-item-content> 
							  </div>
							</div>
						</a>
						<%
					}
				%>
			</div>
			<%
			}
		}						
	}
}catch(Exception e){
	
}
%>
<script>
$(window).adaptTo("foundation-registry").register("foundation.collection.action.activecondition", {
	  name: "site.scaffolding",
	  handler: function(name, el, config, collection, selections) {
	    return window.location.href.indexOf("/content/"+$(el).attr("data-council")) != -1;
	  }
	});
</script>
