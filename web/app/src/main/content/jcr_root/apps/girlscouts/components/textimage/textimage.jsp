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

  Text-Image component

  Combines the text and the image component

--%><%@ page import="com.day.cq.commons.Doctype,
    com.day.cq.wcm.api.WCMMode,
    com.day.cq.wcm.api.components.DropTarget,
    com.day.cq.wcm.foundation.Image, com.day.cq.wcm.foundation.Placeholder, com.day.cq.wcm.resource.details.AssetDetails" %><%
%><%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts.components.textimage" /><%
    boolean isAuthoringUIModeTouch = Placeholder.isAuthoringUIModeTouch(slingRequest);
	final WCMMode wcmMode = WCMMode.fromRequest(request);

	String completePath = currentNode.getPath();
	String additionalCSS = properties.get("./additionalCss", "");
	boolean nowrap = additionalCSS.contains("nowrap") ? true : false;
	boolean nopadding = additionalCSS.contains("nopadding") ? true : false;
	boolean inAccordion = completePath.contains("accordion") ? true : false;
	String imageAlignment = properties.get("./imageAlignment", "left");

	String styleImage = "";
	String styleCaption = "";
	String styleComponent = ""; 

	// padding for the component
	String pcTop = properties.get("./pctop", "0");
	String pcBottom = properties.get("./pcbottom", "0");
	String pcLeft = properties.get("./pcleft", "0");
	String pcRight = properties.get("./pcright", "0");
	
	if (!nopadding) styleComponent += "padding: " + pcTop + "px " + pcRight + "px " + pcBottom + "px " + pcLeft + "px;";	
	
%><div style="<%= styleComponent %>">
<%
	
	// padding for the image
	String piTop = properties.get("./pitop", "0");
	String piBottom = properties.get("./pibottom", "0");
	String piLeft = properties.get("./pileft", "0");
	String piRight = properties.get("./piright", "0");	
	
	String currentPath = currentPage.getPath();	
	
	// Previously, image bottom was padded with <br> for whatever reason
	// This runs once for old text image component and replaces <br> with padding that can be changed by the user
	Node node = resource.adaptTo(Node.class);
	
	if (node.hasProperty("./runOnce")) {
		//node.getProperty("runOnce").remove();
	} else {
		node.setProperty("runOnce", "corrected");
		if (node.hasNode("image")) {	// if it has image node, then it's an old component
			if (currentPath.contains("gsusa")) { //if rendered in gsusa, line height is 24
				node.setProperty("pibottom", "24");
				piBottom = "24";
			} else {
				if (nowrap) {
					node.setProperty("pibottom", "0");
					piBottom = "0";
				} else if (inAccordion) { 
					node.setProperty("pibottom", "27");
					piBottom = "27";
				} else {
					node.setProperty("pibottom", "17");
					piBottom = "17";
				}
			}
		}
	}
	try {
		if (wcmMode == WCMMode.EDIT || wcmMode == WCMMode.PREVIEW) {
			node.getSession().save();	
		}
	} catch(Exception e){
		e.printStackTrace();		
	}

	//GSWP-2212- To get original width and height
	Image image = new Image(resource, "image");
	Long originalWidth=0L;
	Long originalHeight=0L;
	try {
        Resource metaDataResource = slingRequest.getResourceResolver().getResource(image.getFileReference());
        AssetDetails assetDetails = new AssetDetails(metaDataResource);
        originalHeight = assetDetails.getHeight();
        originalWidth = assetDetails.getWidth();
    } catch(Exception e) {
        e.printStackTrace();
    }
	//GSWP-2212- End-To get original width and height

	String width = properties.get("./image/width", "0");
	String caption = properties.get("./image/jcr:description", "");
	String imgHeight = properties.get("./image/height", "0");
	String padding = piTop + piBottom + piLeft + piRight;
	String imageCaptionWidth = "";
	if (!padding.equals("0000")) {	// paddings are set, override custom style
		styleImage = "padding: " + piTop + "px " + piRight + "px " + piBottom + "px " + piLeft + "px;";
	}
	if (caption.length() > 0) {
		styleCaption = "padding: 0px 5px 0px 5px; line-height: 0.86rem"; // 5 5 1 5
        if ("0".equals(width)) {
			imageCaptionWidth += "width:" + originalWidth + "px";
        }
	}
	if (!"0".equals(width)) {
		// newWidth expands width to accomodate for paddings
		int newWidth = Integer.parseInt(width) + Integer.parseInt(piLeft) + Integer.parseInt(piRight);
        if (newWidth > originalWidth) {
			styleImage += "width:" + originalWidth + "px";
            imageCaptionWidth += "width:" + originalWidth + "px";
    	}
        else {
			styleImage += "width:" + newWidth + "px";
            imageCaptionWidth += "width:" + newWidth + "px";
        }
	}

	if (!"0".equals(imgHeight)) {
		// newImageHeight expands height to accomodate for paddings
		int newImageHeight = Integer.parseInt(imgHeight) + Integer.parseInt(piTop) + Integer.parseInt(piBottom);
		styleImage += "height:" + newImageHeight + "px; max-height:" + originalHeight + "px;";
        }


    image.setSrc(gsImagePathProvider.getImagePathByLocation(image));
    // don't draw the placeholder in case UI mode touch it will be handled afterwards
    if (isAuthoringUIModeTouch) {
        image.setNoPlaceholder(true);
    }

    //drop target css class = dd prefix + name of the drop target in the edit config
    String ddClassName = DropTarget.CSS_CLASS_PREFIX + "image";


    if (image.hasContent() || WCMMode.fromRequest(request) == WCMMode.EDIT) {
        image.loadStyleData(currentStyle);
        // add design information if not default (i.e. for reference paras)
        if (!currentDesign.equals(resourceDesign)) {
            image.setSuffix(currentDesign.getId());
        }
        image.addCssClass(ddClassName);
        //GSWP-2212,2210-When applied image styles
        image.addAttribute("style", styleImage);

        //GSWP-2212,2210-End-When applied image styles
        image.setSelector(".img");
        image.setDoctype(Doctype.fromRequest(request));

        String divId = "cq-textimage-jsp-" + resource.getPath();
        Boolean newWindow = properties.get("./newWindow", false);

        // div around image for additional formatting
        %><div class="txtimage-<%=imageAlignment%>" id="<%= divId %>" style="<%= imageCaptionWidth %>"><%
        if(!newWindow) {
           image.draw(out);
        } else { %>
           <%= image.getString().replace("<a ", "<a target=\"_blank\"") %>
           <%
        }

        if (caption.length() > 0) {
        	%><div class="textimage-caption" style="<%= styleCaption %>">
        		<cq:text property="image/jcr:description" placeholder="" tagName="small" escapeXml="true"/>
              </div> <%
        }  %>
        </div>
        <%@include file="/libs/foundation/components/image/tracking-js.jsp"%>
        <%
    }

       String placeholder = (isAuthoringUIModeTouch && !image.hasContent())
               ? Placeholder.getDefaultPlaceholder(slingRequest, component, "", ddClassName)
               : "";
		String text = properties.get("text","");
		
		if(text != null && !text.equals("")){%>
			<cq:text property="text" tagClass="text" escapeXml="true" placeholder="<%= placeholder %>"/>
			<div class="clear"></div>
		<%}else{%>
			<div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder"></div>	
		<%}
    %>

	<%-- fix CQ "new" bar misbehave --%>
	<div style="clear:both"></div>
</div>