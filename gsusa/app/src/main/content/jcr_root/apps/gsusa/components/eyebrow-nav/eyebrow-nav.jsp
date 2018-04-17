<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%
if(currentNode.hasNode("navs")){	
	Node links = currentNode.getNode("navs");
    NodeIterator iter = links.getNodes();
    %>
	<ul class="inline-list">
		<%
    	while(iter.hasNext()){
			Node linkNode = iter.nextNode();
			String label = linkNode.hasProperty("label") ? linkNode.getProperty("label").getString():"";
			String path = linkNode.hasProperty("path") ? linkNode.getProperty("path").getString():"";
			boolean newWindow = linkNode.hasProperty("newWindow") ? linkNode.getProperty("newWindow").getBoolean():false;
			boolean sticky = linkNode.hasProperty("sticky") ? linkNode.getProperty("sticky").getBoolean():false;
			String cssClass = linkNode.hasProperty("class") ? linkNode.getProperty("class").getString():"";
			String target = "";					
			Long eyebrowTabIndex = 10 + iter.getPosition();
			String cssId = "eyebrow";			
			String stickyClass = (!sticky) ? " class=\"hide-in-sticky-nav\"" : "";
			Page linkPage = resourceResolver.resolve(path).adaptTo(Page.class);
			if (linkPage != null && !path.contains(".html")) {
				path += ".html";
			}
			if (newWindow) {
				target = "target=\"_blank\"";
			}else{
				target = "target=\"_self\"";
			}
			%><li<%=stickyClass%>>
                <a <%= target %>id="tag_eyebrow_<%= linkifyString(label, 25)%>" class="<%= cssClass %>" href="<%= path %>" title="<%= label %>" tabindex="<%= eyebrowTabIndex++ %>"><%= label %></a>
            </li><%			
    	}
		%>
	</ul>
	<%
}else{
	if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
		%><div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder"><ul class="inline-list"><li>### No navs found ###</li></ul></div>
	<%} else {%>
		<ul class="inline-list"><li>&nbsp;</li></ul>
	<%}
}    
%>
