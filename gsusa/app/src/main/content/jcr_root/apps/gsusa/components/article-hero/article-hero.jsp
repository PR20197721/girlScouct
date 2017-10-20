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
	String imgAlt = "";

	try {
        Node node =   resourceResolver.getResource(pagePath).adaptTo(Node.class);
		Node propNode = node.getNode("jcr:content");

		if(propNode.hasProperty("type")) {
        	type = propNode.getProperty("type").getString();
		}
        if(propNode.hasProperty("videoLink")) {
        	videoLink = propNode.getProperty("videoLink").getString();
        }
        if (propNode.hasProperty("imgAlt")) {
        	imgAlt = propNode.getProperty("imgAlt").getString();
        }
        
		Node imageNode = propNode.getNode("image");
        imageSrc = imageNode.getProperty("fileReference").getString();
    } catch(Exception e){
        e.printStackTrace();
    }

	if(type.equals("video")){%>
        <%=videoLink%>
    <%} else{%>
	   <img src="<%= getImageRenditionSrc(resourceResolver, imageSrc, "cq5dam.npd.hubHero.")%>" data-at2x="<%= getImageRenditionSrc(resourceResolver, imageSrc, "cq5dam.npd.hubHero@2x.") %>" alt="<%=imgAlt%>" title="<%=imgAlt%>" />
    <%}%>
