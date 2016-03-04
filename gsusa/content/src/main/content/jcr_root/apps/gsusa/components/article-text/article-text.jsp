<%--

  Article Text component.



--%><%
%><%@page import="java.text.SimpleDateFormat,
				  java.util.Date,
				  java.text.DateFormat"
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%><%
	boolean isShowEditDate = "true".equals(properties.get("showEditDate", "false"));
	DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
	String pagePath = currentPage.getPath();

	String articleText = "";
	String editedDate = "";

	Date date = new Date();
	
	try{
        Node node =   resourceResolver.getResource(pagePath).adaptTo(Node.class);
		Node propNode = node.getNode("jcr:content");

        if(propNode.hasProperty("editedDate"))
        editedDate = propNode.getProperty("editedDate").getString();
        date = dateFormat2.parse(editedDate);


    } catch(Exception e){
        e.printStackTrace();
    }

%>
<div>
    <cq:include path="social-bar" resourceType="gsusa/components/article-social-bar" />
</div>
<% if (isShowEditDate) { %>
	<i>Edited: <%=dateFormat.format(date)%></i>
<% } else{ %>
	<i></i>
<% } %>