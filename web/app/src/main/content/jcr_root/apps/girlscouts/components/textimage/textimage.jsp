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
<%@include file="/apps/girlscouts/components/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts.components.textimage" /><%
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
<cq:includeClientLib categories="apps.girlscouts.components.textimage" /><%
	
	// padding for the image
	String piTop = properties.get("./pitop", "0");
	String piBottom = properties.get("./pibottom", "0");
	String piLeft = properties.get("./pileft", "0");
	String piRight = properties.get("./piright", "0");	
	
	String currentPath = currentPage.getPath();

	// Previously, image bottom was padded with <br> for whatever reason
	// This runs once for old text image component and replaces <br> with padding that can be changed by the user
	String runOnce = "";
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
				node.setProperty("pibottom", "27"); // for councils, line height is 27
				piBottom = "27";
			}
		}
	}
	node.getSession().save();
	
	String width = properties.get("./image/width", "0");
	String caption = properties.get("./image/jcr:description", "");
	
	String padding = piTop + piBottom + piLeft + piRight;
	if (!padding.equals("0000")) {	// paddings are set, override custom style
		styleImage = "padding: " + piTop + "px " + piRight + "px " + piBottom + "px " + piLeft + "px;";
	}
	if (caption.length() > 0) {
		styleCaption = "padding: 5px 5px 0px 5px;"; // 5 5 1 5
	}
	if (!"0".equals(width)) {
		// newWidth expands width to accomodate for paddings
		int newWidth = Integer.parseInt(width) + Integer.parseInt(piLeft) + Integer.parseInt(piRight);
		styleImage += "width:" + newWidth + "px;";
	}
	
    Image image = new Image(resource, "image");
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
        image.setSelector(".img");
        image.setDoctype(Doctype.fromRequest(request));

        String divId = "cq-textimage-jsp-" + resource.getPath();
        String imageHeight = image.get(image.getItemName(Image.PN_HEIGHT));
        // div around image for additional formatting
        %><div class="image" id="<%= divId %>" style="<%= styleImage %>"><%
        %><% image.draw(out); %><%
		
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
    %><cq:text property="text" tagClass="text" escapeXml="true" placeholder="<%= placeholder %>"/><div
        class="clear"></div>

	<%-- fix CQ "new" bar misbehave --%>
	<div style="clear:both"></div>
</div>
