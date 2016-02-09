<%--

  Article Text component.



--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%><%
	String pagePath = currentPage.getPath();

	String articleText = "";
	String editedDate = "";

	try{
        Node node =   resourceResolver.getResource(pagePath).adaptTo(Node.class);
		Node propNode = node.getNode("jcr:content");

        if(propNode.hasProperty("articleText"))
        articleText = propNode.getProperty("articleText").getString();

        if(propNode.hasProperty("editedDate"))
        editedDate = propNode.getProperty("editedDate").getString();


    } catch(Exception e){
        e.printStackTrace();
    }

%>
<div class="clearfix">
    <cq:include path="social-bar" resourceType="gsusa/components/article-social-bar" />
</div>
<i>Edited: <%=editedDate%></i>
<%=articleText%>