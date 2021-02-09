<%@page session="false"%><%--
  Copyright 1997-2008 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Image component

  Draws an image. 

--%><%@ page import="com.day.cq.commons.Doctype,
    com.day.cq.wcm.api.components.DropTarget, org.slf4j.Logger, org.slf4j.LoggerFactory,
    com.day.cq.wcm.foundation.Image, javax.jcr.NodeIterator,javax.jcr.Node,com.day.cq.wcm.foundation.Placeholder, com.day.cq.wcm.resource.details.AssetDetails" %><%
%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<cq:includeClientLib categories="common.components.image" />
<cq:includeClientLib categories="apps.girlscouts.components.image" /><%
	String divId = "cq-image-jsp-" + resource.getPath();
	//GSWP-2140-case1-For new image component dragged and dropped, Left dropdown is selected by default
	String imageAlignment = properties.get("./imageAlignment", "left");
	String additionalCss = properties.get("./additionalCss", "");
	//GSWP-2140-case3-For old/existing image component, where Additional CSS has left value, you should keep that, and leave Image Alignment dropdown empty
  	if ((!additionalCss.isEmpty()) && (additionalCss.indexOf("left") >=0 || additionalCss.indexOf("center") >=0 || additionalCss.indexOf("right") >=0)) {
      //if additional css has multiple values seperated by space
      String[] additionalCssValues = additionalCss.split("\\s+");
      imageAlignment = Arrays.asList(additionalCssValues).contains("left")? "left" : Arrays.asList(additionalCssValues).contains("center")? "center" : "right";
  	}
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    Node pageNode = currentPage.getContentResource().adaptTo(Node.class);
    boolean showButton = false;
     try{
        if(pageNode.hasProperty("cssPrint"))
            showButton = pageNode.getProperty("cssPrint").getBoolean();
     }catch(Exception e){
        logger.error("Error finding cssPrint property: ",e);
     }

	//To get original width and height of the image
	Image image = new Image(resource);
	Long originalWidth=0L;
	Long originalHeight=0L;
	try {
        Resource imageResource = slingRequest.getResourceResolver().getResource(image.getFileReference());
        AssetDetails assetDetails = new AssetDetails(imageResource);
        originalHeight = assetDetails.getHeight();
        originalWidth = assetDetails.getWidth();
    } catch(Exception e) {
        e.printStackTrace();
    }

	String styleImage = "";
	String styleCaption = "";
	
	String pTop = properties.get("./ptop", "0");
	String pBottom = properties.get("./pbottom", "0");
	String pLeft = properties.get("./pleft", "0");
	String pRight = properties.get("./pright", "0");
	String imageWidth = properties.get("./width", "0");
	String imageHeight = properties.get("./height", "0");
	String caption = properties.get("./jcr:description", "");
	String imageCaptionWidth = "";
    String buttonPath = currentPage.getPath() + "/print-css";
	String padding = pTop + pBottom + pLeft + pRight;
	String currentPath = currentPage.getPath();
	
	
	if (caption.length() > 0) { // if there's caption, apply padding to the caption
			styleCaption = "padding: 0px 5px;";
        	if ("0".equals(imageWidth)) {
				imageCaptionWidth += "width:" + originalWidth + "px";
        }
	}
	
	if (!padding.equals("0000")) {	// paddings are set, override custom style
		styleImage = "padding: " + pTop + "px " + pRight + "px " + pBottom + "px " + pLeft + "px;";
	}
	
	if (!"0".equals(imageWidth)) {
		// imageWidth + padding
		int newWidth = Integer.parseInt(imageWidth) + Integer.parseInt(pLeft) + Integer.parseInt(pRight);
		if (newWidth > originalWidth) {
			styleImage += "width:" + originalWidth + "px";
            imageCaptionWidth += "width:" + originalWidth + "px";
    	}
        else {
			styleImage += "width:" + newWidth + "px";
            imageCaptionWidth += "width:" + newWidth + "px";
        }
	}

	if (!"0".equals(imageHeight)) {
		// newImageHeight expands height to accomodate for paddings
		int newImageHeight = Integer.parseInt(imageHeight) + Integer.parseInt(pTop) + Integer.parseInt(pBottom);
		styleImage += "height:" + newImageHeight + "px; max-height:" + originalHeight + "px;";
    }

	//GSAWDO-120-Image and Text&Image component needs to expand to full width of the container without any gaps
        String imageClass=imageAlignment;

	%><div class="img-wrapper img-print <%= "img-cmp image-" + imageClass %>" id="<%= divId %>" style="<%= imageCaptionWidth %>"><%
	    image.setSrc(gsImagePathProvider.getImagePathByLocation(image));

	  	try{
		    image.setIsInUITouchMode(Placeholder.isAuthoringUIModeTouch(slingRequest));

		    //drop target css class = dd prefix + name of the drop target in the edit config
		    image.addCssClass(DropTarget.CSS_CLASS_PREFIX + "image");
		  	//GSWP-2140-When applied both styleImage (width) and image dropdown
		  	image.addAttribute("style", styleImage);
		  	//GSWP-2140-End-When applied both styleImage (width) and image dropdown
		    image.loadStyleData(currentStyle);
		    image.setSelector(".img"); // use image script
		    image.setDoctype(Doctype.fromRequest(request));

			Boolean newWindow = properties.get("./newWindow", false);

		    // add design information if not default (i.e. for reference paras)
		    if (!currentDesign.equals(resourceDesign)) {
		        image.setSuffix(currentDesign.getId());
		    }

		    if(!newWindow) {
		       image.draw(out);
		   	} else { %>
				<%= image.getString().replace("<a ", "<a target=\"_blank\"") %>
				<%
			}
	  	}catch (Exception e){

	  	}
		%>
		<div class="image-caption" style="<%= styleCaption %>">
		<%
			if(caption != null && !caption.equals("")){%>
				<cq:text property="jcr:description" placeholder="" tagName="small" escapeXml="true"/>
			<%}
		%>

		</div>
	</div>
    <%@include file="/libs/foundation/components/image/tracking-js.jsp"%>
    <%
    NodeIterator nodeItr;
    Node currNode = currentPage.adaptTo(Node.class);
    try{
        Node parNode = currentPage.adaptTo(Node.class).getNode("jcr:content/content/middle/par");
        if(parNode != null){
            nodeItr= parNode.getNodes();
            currNode = nodeItr.nextNode();
        }
	if(showButton){
        if(currNode.hasProperty("sling:resourceType") && "girlscouts/components/image".equals(currNode.getProperty("sling:resourceType").getString()) && currentNode.getPath().equals(currNode.getPath())){
        %>
            <cq:include path="<%= buttonPath %>" resourceType="girlscouts/components/print-css" />
       <% }
    }
    }catch(Exception e){
        //logger.error("Error getting first page component: ",e);
    }
    %>

<style>
.image-no-margin{
    margin:0;
}

</style>

          <script>
$(window).on('load', function () {
  const getTextContainer = $(".img-cmp").parents();  for(let i = 0; i < getTextContainer.length; i++){
		 const getContainerWidth = $(getTextContainer[i]).width() -15;
    	const getimageWidth = $($(getTextContainer[i]).find('img')).width();	
      getimageWidth >= getContainerWidth ? $(getTextContainer[i]).children().addClass('image-no-margin') : $(getTextContainer[i]).children().removeClass('image-no-margin');
	}


});
</script>