<%--

  Article Tile component.

  Basic building block of the article hub components

--%><%
%><%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%
%><%@page session="false" %>
<%@page import="javax.jcr.Node, org.apache.commons.lang.StringEscapeUtils"%>
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

	String divId = (String)request.getAttribute("tileModalDivId");

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

        if(propNode.hasProperty("playOnClick")){
        	String isOn = propNode.getProperty("playOnClick").getString();
            if(isOn.equals("on"))
            playOnClick = true;
        }

		Node imageNode = propNode.getNode("image");
        imageSrc = imageNode.getProperty("fileReference").getString();

    } catch(Exception e){
        e.printStackTrace();
    }

	if(!articlePath.isEmpty())
        articlePath = articlePath + ".html";

%>



<section>
    <%
    if(playOnClick){
        %>
    <a href="" onclick="populateVideoIntoModal('<%=divId%>','<%=StringEscapeUtils.escapeHtml(videoLink)%>')" data-reveal-id="<%=divId%>">
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


