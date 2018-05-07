<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%
if(currentNode != null && currentNode.hasNode("navs")){	
	Node links = currentNode.getNode("navs");
    NodeIterator iter = links.getNodes();
    %><ul class="inline-list"><%
    	while(iter.hasNext()){ 
    		Node linkNode = iter.nextNode();
    		String label = linkNode.hasProperty("label") ? linkNode.getProperty("label").getString():"";
    		String path = linkNode.hasProperty("path") ? linkNode.getProperty("path").getString():"";
    		String cssClass = linkNode.hasProperty("class") ? linkNode.getProperty("class").getString():"";
    		long footerTabindex = 150 + iter.getPosition();
    		Page linkPage = resourceResolver.resolve(path).adaptTo(Page.class);            
            if (linkPage != null && !path.contains(".html")) {
            	path += ".html";
            }
            if (!label.isEmpty()) {%>
				<li id="tag_footer_<%= linkifyString(label, 25)%>"><a href="<%= path %>" title="<%= label %>" tabindex=<%=footerTabindex%>><%= label %></a></li>
<%			}
    	}
    %></ul><%
}
%>