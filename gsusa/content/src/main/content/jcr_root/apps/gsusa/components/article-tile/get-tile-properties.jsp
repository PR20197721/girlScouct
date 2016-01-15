<%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %>
<%
	String articlePath = properties.get("article1", "");
	Node node =  resourceResolver.getResource(articlePath).adaptTo(Node.class);
	try{
		Node propNode = node.getNode("jcr:content");
        String tileTitle = propNode.getProperty("start").getString();
        
    } catch(Exception e){
        e.printStackTrace();
    }

%>

