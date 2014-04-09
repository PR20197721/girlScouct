<%@page import="java.util.Iterator,
com.day.cq.wcm.api.WCMMode,
com.day.cq.wcm.api.PageManager,
org.apache.sling.api.resource.ValueMap" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<cq:defineObjects/>
<%
String rootPath = properties.get("path", "");
if (rootPath.isEmpty()) {
    if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
		%>Contacts List: path not configured.<%
    }
    return;
}

Page contactRoot = resourceResolver.resolve(rootPath).adaptTo(Page.class);
Iterator<Page> teamIter = contactRoot.listChildren();
while (teamIter.hasNext()) {
    Page currentTeam = teamIter.next();
    String teamName = currentTeam.getProperties().get("jcr:title", "");
    %><p><%= teamName %><br/><%
	%><table><tr><td>Name</td><td>Title</td><td>Phone</td><td>Email</td></tr><%
    Iterator<Page> contactIter = currentTeam.listChildren();
    while (contactIter.hasNext()) {
		Page currentContact = contactIter.next();
		ValueMap props = currentContact.getProperties();
		String name = props.get("jcr:title", "");
		String title = props.get("title", "");
		String phone = props.get("phone", "");
		String email = props.get("email", "");
		
		%><tr><td><%=name%></td><td><%=title%></td><td><%=phone%></td><td><%=email%></td><%
    }
    %></tr></table></p><%
}

%>