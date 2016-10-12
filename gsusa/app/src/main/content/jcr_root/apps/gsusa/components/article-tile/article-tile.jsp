<%--

  Article Tile component.

  Basic building block of the article hub components

--%><%
%><%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%
%><%@page session="false" %>
<%@page import="javax.jcr.Node, org.apache.commons.lang.StringEscapeUtils, javax.jcr.Value, com.day.cq.tagging.TagManager, com.day.cq.wcm.api.Page, com.day.cq.tagging.Tag"%>
<%
  	String articlePath = (String)request.getAttribute("articlePath");
	if (articlePath == null) {
		articlePath = request.getParameter("articlePath");
		if (articlePath.endsWith(".html")) {
			articlePath = articlePath.substring(0, articlePath.length() - 5);
		}
		if (!articlePath.startsWith("/content/gsusa")) {
			articlePath = "/content/gsusa" + articlePath;
		}
	}
	String linkTagAnchors = (String)request.getAttribute("linkTagAnchors");

	String tileTitle = "";
    String tileText = "";
    long priority = 0;
    String type = "";
    String videoLink = "";
    String externalLink = "";
    String editedDate = "";

    boolean playOnClick = false;
	boolean openInNewWindow = false;

	String divId = (String)request.getAttribute("tileModalDivId");

	String imageSrc = "";
	String image2xSrc = "";

	String hexColor = "FFFFFF";

	String rgba = "rgba(166, 206, 56, 0.8)";

	Value[] tags = null;

	String linkToArticle = "";

	try{
        Node node =   resourceResolver.resolve(articlePath).adaptTo(Node.class);
		Node propNode = node.getNode("jcr:content");
		linkToArticle = node.getPath() + ".html";

        if(propNode.hasProperty("jcr:title"))
        tileTitle = propNode.getProperty("jcr:title").getString();

        if(propNode.hasProperty("shortTitle"))
        tileTitle = propNode.getProperty("shortTitle").getString();

        if(propNode.hasProperty("jcr:description"))
        tileText = propNode.getProperty("jcr:description").getString();

        if(propNode.hasProperty("type"))
        type = propNode.getProperty("type").getString();

        if(propNode.hasProperty("videoLink"))
        videoLink = propNode.getProperty("videoLink").getString();

        if(propNode.hasProperty("externalLink"))
        externalLink = propNode.getProperty("externalLink").getString();

        if(propNode.hasProperty("editedDate"))
        editedDate = propNode.getProperty("editedDate").getString();

        if(propNode.hasProperty("cq:tags"))
        tags = propNode.getProperty("cq:tags").getValues();

        if(propNode.hasProperty("playOnClick")){
        	String isOn = propNode.getProperty("playOnClick").getString();
            if(isOn.equals("on"))
            playOnClick = true;
        }

        if(propNode.hasProperty("openInNewWindow")){
        	String openIn = propNode.getProperty("openInNewWindow").getString();
            if(openIn.equals("on"))
            openInNewWindow = true;
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



    } catch(Exception e){
        e.printStackTrace();
    }

	if(!articlePath.isEmpty())
        articlePath = articlePath + ".html";

	if(tags != null && tags.length > 0){
		String primaryTagId = tags[0].getString();
		try{
	       	TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
	        Tag primaryTag = tagManager.resolve(primaryTagId);
	
	        Node primaryNode = primaryTag.adaptTo(Node.class);
	        if(primaryNode.hasProperty("color")){
				hexColor = primaryNode.getProperty("color").getString();
	            int rPart = Integer.parseInt(hexColor.substring(0,2), 16);
	            int gPart = Integer.parseInt(hexColor.substring(2,4), 16);
	            int bPart = Integer.parseInt(hexColor.substring(4,6), 16);
				rgba = "rgba("+ rPart +", "+ gPart +", "+ bPart +", 0.8)";
	
	      	}
		}catch(Exception e){
			System.err.println("Tag: " + primaryTagId + " is not found for article: " + articlePath);
		}
	}
	if(linkTagAnchors != null){
		linkToArticle += linkTagAnchors;
	}
%>

<section>
    <%
    if(type.equals("video")){
		if(playOnClick){
        	%><a class="video" href="" onclick="populateVideoIntoModal('gsusaHiddenModal','<%=StringEscapeUtils.escapeHtml(videoLink)%>','#FFFFFF')" data-reveal-id="gsusaHiddenModal"><%
    	} else {
			%><a class="video non-click" href="<%=linkToArticle%>"><%
    	}
	} else if(type.equals("link")){
        if(openInNewWindow){

		%>

			<a x-cq-linkchecker="valid" href="<%=genLink(resourceResolver, externalLink)%>" target="_blank">
    	<%
        } else {
		%>
			<a x-cq-linkchecker="valid" href="<%=genLink(resourceResolver, externalLink)%>">
    	<%
        }
	} else {
    	%> <a class="photo" href="<%=linkToArticle%>"> <%
    }
    %>
		<img src="<%=imageSrc%>" data-at2x="<%= image2xSrc %>"/>
		<div class="text-content" style="background: <%=rgba%>">
			<h3><%=tileTitle%></h3>
            <p><%=tileText%></p>
		</div>
	</a>
</section>


