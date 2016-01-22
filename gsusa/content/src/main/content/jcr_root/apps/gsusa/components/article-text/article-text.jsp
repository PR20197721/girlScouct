<%--

  Article Text component.

  

--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%><%
	String pagePath = currentPage.getPath();

	String articleText = "";

	try{
        Node node =   resourceResolver.getResource(pagePath).adaptTo(Node.class);
		Node propNode = node.getNode("jcr:content");

        if(propNode.hasProperty("articleText"))
        articleText = propNode.getProperty("articleText").getString();


    } catch(Exception e){
        e.printStackTrace();
    }

%>

<div>
	<%=articleText%>
</div>