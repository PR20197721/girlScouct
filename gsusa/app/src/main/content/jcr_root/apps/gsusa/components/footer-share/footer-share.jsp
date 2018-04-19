<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<% 
if(currentNode.hasNode("socialIcons")){	
	Node links = currentNode.getNode("socialIcons");
    NodeIterator iter = links.getNodes();
    %>
	<ul class="inline-list">
		<%
    	while(iter.hasNext()){
    		Node linkNode = iter.nextNode();
			String name = linkNode.hasProperty("name") ? linkNode.getProperty("name").getString():"";
			String url = linkNode.hasProperty("url") ? linkNode.getProperty("url").getString():"";
			String iconPath = linkNode.hasProperty("icon-path") ? linkNode.getProperty("icon-path").getString():"";
			if (iconPath.isEmpty()) {
				%><li> Please input a valid name/url/icon path. </li> <%
				continue;
		     }		      
			 if (name.isEmpty()) {
				%><li> Name is empty. </li> <%
				continue;
			 }
		     if (url.isEmpty()) {
				%><li> URL is empty. </li> <%
				continue;
			 }
		     %>
		     <li><a id="tag_social-icon_<%=name.toLowerCase()%>" target="_blank" href="<%= url %>"><img alt="<%=name%>" title="<%=name%>" src="<%= iconPath %>"/></a></li>
		     <%
    	}
		%>
	</ul>
	<%
}else{
	if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
		%><li>Please click to edit the footer share component</li><% 
	}
}
%>