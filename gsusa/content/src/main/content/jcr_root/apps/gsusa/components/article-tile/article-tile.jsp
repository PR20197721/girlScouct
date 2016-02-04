<%--

  Article Tile component.

  Basic building block of the article hub components

--%><%
%><%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%
%><%@page session="false" %>
<%@page import="javax.jcr.Node, org.apache.commons.lang.StringEscapeUtils, com.day.cq.wcm.api.Page, com.day.cq.tagging.Tag"%>
<%
  	String articlePath = (String)request.getAttribute("articlePath");
	if (articlePath == null) {
		articlePath = request.getParameter("articlePath");
	}
	String linkTagAnchors = (String)request.getAttribute("linkTagAnchors");

	String tileTitle = "";
    String tileText = "";
    long priority = 0;
    String type = "";
    String videoLink = "";
    String externalLink = "";
    String editedDate = "";
	String vanityUrl = "";

    boolean playOnClick = false;

	String divId = (String)request.getAttribute("tileModalDivId");

	String imageSrc = "";
	String image2xSrc = "";

	String hexColor = "FFFFFF";

	String rgba = "rgba(166, 206, 56, 0.8)";

	Tag[] tags = null;

	try{
        Node node =   resourceResolver.resolve(articlePath).adaptTo(Node.class);
		Node propNode = node.getNode("jcr:content");

        if(propNode.hasProperty("jcr:title"))
        tileTitle = propNode.getProperty("jcr:title").getString();

        if(propNode.hasProperty("articleText"))
        tileText = propNode.getProperty("articleText").getString();

        if(propNode.hasProperty("type"))
        type = propNode.getProperty("type").getString();

        if(propNode.hasProperty("sling:vanityPath"))
        vanityUrl = propNode.getProperty("sling:vanityPath").getString() + ".html";

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

        if(propNode.hasNode("tileimage")){
			Node thumbnailNode = propNode.getNode("tileimage");
			imageSrc = thumbnailNode.getPath() + ".img.png";
			image2xSrc = thumbnailNode.getPath() + "2x.img.png";
        } else{
			Node imageNode = propNode.getNode("image");
        	imageSrc = imageNode.getProperty("fileReference").getString();
        	image2xSrc = getImageRenditionSrc(resourceResolver, imageSrc, "cq5dam.npd.tile@2x.");
            imageSrc = getImageRenditionSrc(resourceResolver, imageSrc, "cq5dam.npd.tile.");
        }

        Page tilePage = resourceResolver.getResource(articlePath).adaptTo(Page.class);
        tags = tilePage.getTags();


    } catch(Exception e){
        e.printStackTrace();
    }

	if(!articlePath.isEmpty())
        articlePath = articlePath + ".html";

	if(tags != null && tags.length > 0){
		Tag primaryTag = tags[0];
        Node primaryNode = primaryTag.adaptTo(Node.class);
        if(primaryNode.hasProperty("color")){
			hexColor = primaryNode.getProperty("color").getString();
            int rPart = Integer.parseInt(hexColor.substring(0,2), 16);
            int gPart = Integer.parseInt(hexColor.substring(2,4), 16);
            int bPart = Integer.parseInt(hexColor.substring(4,6), 16);
			rgba = "rgba("+ rPart +", "+ gPart +", "+ bPart +", 0.8)";

      	}
        if(linkTagAnchors == null){
        	String tagPath = primaryNode.getPath();
        	linkTagAnchors = "#" + tagPath.replaceAll("^/etc/tags/gsusa/content-hub/", "").replaceAll("/", "|");
        }
	}
	if(linkTagAnchors != null){
		vanityUrl = vanityUrl + linkTagAnchors;
	}
%>

<section>
    <%
    if(type.equals("video") && playOnClick){
        %>
    <a href="" onclick="populateVideoIntoModal('gsusaHiddenModal','<%=StringEscapeUtils.escapeHtml(videoLink)%>','<%=hexColor%>')" data-reveal-id="gsusaHiddenModal">
<%
    } else if(type.equals("link")){
	%>
	<a x-cq-linkchecker="valid" href="<%=externalLink%>">

    <%
	}else{
    %>
	<a href="<%=vanityUrl%>">
<%
    }
    %>
		<img src="<%=imageSrc%>" data-at2x="<%= image2xSrc %>"/>
		<div class="text-content" style="background: <%=rgba%>">
			<h3><%=tileTitle%></h3>
			<%=tileText%>
		</div>
	</a>
</section>


