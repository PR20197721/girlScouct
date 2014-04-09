<%@page import="java.util.Iterator,
com.day.cq.wcm.api.WCMMode,
com.day.cq.wcm.api.PageManager,
org.apache.sling.api.resource.ValueMap" %>
<%@include file="/libs/foundation/global.jsp"%><%
<%@page session="false" %><%
<cq:defineObjects/>
<%
String rootPath = properties.get("path", "");
if (path.isEmpty()) {
    if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
		%>Contacts List: path not configured.<%
    }
    return;
}

Page contactRoot = resourceResolver.resolve(rootPath).adaptTo(Page.class);
Iterator<Page> teamIter = contactRoot.listChildren();
while (teamIter.hasNext()) {
    Page currentTeam = teamIter.next();
    String teamName = currentTeam.getProperties().get("name", "");
    
    %><h2><%= teamName %></h2><p><%
    Iterator<Page> contactIter = currentTeam.listChildren();
    while (contactIter.hasNext()) {
		Page currentContact = contactIter.next();
		ValueMap props = currentContact.adaptTo(ValueMap.class);
		String name = props.get("jcr:content", "");
		String title = props.get("title", "");
		String phone = props.get("phone", "");
		String email = props.get("email", "");
		
		%><%=name%>|<%=title%>|<%=phone%>|<%=email%><br/><%
    }
    %></p><%
}

%>