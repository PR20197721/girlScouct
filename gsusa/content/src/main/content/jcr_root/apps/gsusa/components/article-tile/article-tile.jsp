<%--

  Article Tile component.

  Basic building block of the article hub components

--%><%
%><%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%
%><%@page session="false" %>
<%@page import="javax.jcr.Node"%>
<%
  String articlePath = (String)request.getAttribute("articlePath");

	String tileTitle = "";
    String tileSummary = "";
    long priority = 0;
    String type = "";
    String videoLink = "";
    String externalLink = "";
    String editedDate = "";

    boolean playOnClick = false;

	String divId = "modal";
    String modId = "modal1";
	String imageSrc = "";


	try{
        Node node =   resourceResolver.getResource(articlePath).adaptTo(Node.class);
		Node propNode = node.getNode("jcr:content");
        if(propNode.hasProperty("tileTitle"))
        tileTitle = propNode.getProperty("tileTitle").getString();

        if(propNode.hasProperty("tileSummary"))
        tileSummary = propNode.getProperty("tileSummary").getString();

        if(propNode.hasProperty("tilePriority"))
        priority = propNode.getProperty("tilePriority").getLong();

        if(propNode.hasProperty("type"))
        type = propNode.getProperty("type").getString();

        if(propNode.hasProperty("videoLink"))
        videoLink = propNode.getProperty("videoLink").getString();

        if(propNode.hasProperty("externalLink"))
        externalLink = propNode.getProperty("externalLink").getString();

        if(propNode.hasProperty("editedDate"))
        editedDate = propNode.getProperty("editedDate").getString();

        if(propNode.hasProperty("playOnClick"))
        playOnClick = propNode.getProperty("playOnClick").getBoolean();

		Node imageNode = propNode.getNode("image");
        imageSrc = imageNode.getProperty("fileReference").getString();

    } catch(Exception e){
        e.printStackTrace();
        throw e;
    }

	if(!articlePath.isEmpty())
        articlePath = articlePath + ".html";
        
%>



<section>
    <%
    if(type.equals("video") && playOnClick){
        %>
    <a href="" onlclick="populateVideoIntoModal(<%=divId%>,<%=videoLink%>)" data-reveal-id="<%=modId%>">
<% 
    } else if (type.equals("external-link")){
    %>
	<a x-cq-linkchecker="valid" href="<%=externalLink%>">
<% 
    } else{
    %>
    <a href="<%=articlePath%>">
    <%
    }
    	%>
		<img src="<%= getImageRenditionSrc(resourceResolver, imageSrc, "cq5dam.npd.tile.")%>"/>
		<div class="text-content" style="background: rgba(36, 184, 238, 0.8)">
			<h3><%=tileTitle%></h3>
			<p><%=tileSummary%></p>
		</div>
	</a>
</section>


