<%--

  Article Hero component.

  

--%><%
%><%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%@page session="false" %><%
%><%
	String pagePath = currentPage.getPath();

	String type = "";
	String videoLink = "";
	String imageSrc = "";

	try{
        Node node =   resourceResolver.getResource(pagePath).adaptTo(Node.class);
		Node propNode = node.getNode("jcr:content");


		if(propNode.hasProperty("type"))
        type = propNode.getProperty("type").getString();


        if(propNode.hasProperty("videoLink"))
        videoLink = propNode.getProperty("videoLink").getString();

		Node imageNode = propNode.getNode("image");
        imageSrc = imageNode.getProperty("fileReference").getString();


    } catch(Exception e){
        e.printStackTrace();
    }

	if(type.equals("video")){%>
    <div><%=videoLink%></div>
    <%} else{%>
	<div><img src="<%= getImageRenditionSrc(resourceResolver, imageSrc, "cq5dam.npd.hubHero.")%>"/></div>
    <%}%>
