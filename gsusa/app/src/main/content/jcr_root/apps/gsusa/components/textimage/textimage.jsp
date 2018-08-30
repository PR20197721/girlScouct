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
    com.day.cq.wcm.foundation.Image, com.day.cq.wcm.foundation.Placeholder" %><%
%><%@include file="/libs/foundation/global.jsp"%>
<%@ include file="/apps/gsusa/components/global.jsp"%>
<cq:includeClientLib categories="apps.gsusa.components.textimage" /><%
    boolean isAuthoringUIModeTouch = Placeholder.isAuthoringUIModeTouch(slingRequest);

	boolean isAuthoringUIModeTouch = Placeholder.isAuthoringUIModeTouch(slingRequest);
	
	String styleImage = "";
	String styleCaption = "";
	String styleComponent = ""; 

	// padding for the component
	String pcTop = properties.get("./pctop", "0");
	String pcBottom = properties.get("./pcbottom", "0");
	String pcLeft = properties.get("./pcleft", "0");
	String pcRight = properties.get("./pcright", "0");
	
	styleComponent += "padding: " + pcTop + "px " + pcRight + "px " + pcBottom + "px " + pcLeft + "px;";
	
%><div style="<%= styleComponent %>">
<cq:includeClientLib categories="apps.gsusa.components.textimage"/><%
	
	// padding for the image
	String piTop = properties.get("./pitop", "0");
	String piBottom = properties.get("./pibottom", "0");
	String piLeft = properties.get("./pileft", "0");
	String piRight = properties.get("./piright", "0");

	// Previously, image bottom was padded with <br> for whatever reason
	// This runs once for old text image component and replaces <br> with padding that can be changed by the user
	String runOnce = properties.get("./runOnce", "old"); 
	
	if (runOnce.equals("old")) {
		Node node = resource.adaptTo(Node.class);
		node.setProperty("runOnce", "corrected");
		node.setProperty("pibottom", "24");
		node.setProperty("pitop", "8");
		node.setProperty("piright", "8");
		piRight = "8";
		piTop = "8";
		piBottom = "24";
		node.getSession().save();
		//node.getProperty("runOnce").remove();
	}

	String width = properties.get("./image/width", "0");
	String caption = properties.get("./image/jcr:description", "");	
	
	String padding = piTop + piBottom + piLeft + piRight;
	if (!padding.equals("0000")) {	// paddings are set, override custom style
		styleImage = "padding: " + piTop + "px " + piRight + "px " + piBottom + "px " + piLeft + "px;";
		styleImage += "margin: 0px !important;";
	}
	if (caption.length() > 0) {
		styleCaption = "padding: 0px 5px 1px 5px;";
	} else  {
		styleCaption = "";
	}
	if (!"0".equals(width)) {
		styleCaption += "width:" + width + "px;";
	}
	
    Image image = new Image(resource, "image");
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
        image.setSelector(".img");
        image.setDoctype(Doctype.fromRequest(request));
        if (!"0".equals(width)) {
        	image.addAttribute("width", width + "px");
    	}
        if (!"0".equals(height)) {
        	image.addAttribute("height", height + "px");
    	}

        String divId = "cq-textimage-jsp-" + resource.getPath();
        String imageHeight = image.get(image.getItemName(Image.PN_HEIGHT));
        // div around image for additional formatting
        %><div class="image" id="<%= divId %>" style="<%= styleImage %>"><%
        %><% image.draw(out); %><%
    	
        if (caption.length() > 0) {
        	%><div class="textimage-caption" style="<%= styleCaption %>">
        		<cq:text property="image/jcr:description" placeholder="" tagName="small" escapeXml="true"/><%
        	%></div><% 
        } %>
        </div>
        <%@include file="/libs/foundation/components/image/tracking-js.jsp"%>
        <%
    }

       String placeholder = (isAuthoringUIModeTouch && !image.hasContent())
               ? Placeholder.getDefaultPlaceholder(slingRequest, component, "", ddClassName)
               : "";
    %><cq:text property="text" tagClass="text" escapeXml="true" placeholder="<%= placeholder %>"/><div
        class="clear"></div>

	<%-- fix CQ "new" bar misbehave --%>
	<div style="clear:both"></div>
</div>
