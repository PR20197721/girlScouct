<%--
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

--%>

<%@ page session="false" %>
<%@ page import="com.day.cq.commons.Doctype,com.day.cq.wcm.api.components.DropTarget,com.day.cq.wcm.foundation.Image, com.day.cq.wcm.foundation.Placeholder" %>
<%@ include file="/libs/foundation/global.jsp" %>
<%@ include file="/apps/gsusa/components/global.jsp"%>
<%@ taglib prefix="gsusa" uri="https://girlscouts.org/gsusa/taglib" %>
<%@ taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>

<%
	String styleImage = "";

	String additionalCssStyle = properties.get("./additionalCssStyle", "");	
	if(additionalCssStyle.length() > 0) {
		styleImage = additionalCssStyle + ";"; //style=\""+additionalCssStyle+"\"";
	}
	
	String styleClass = DropTarget.CSS_CLASS_PREFIX + "image " + properties.get("./additionalCss", "");
	
    // add design information if not default (i.e. for reference paras)
    String suffix = null;
    if (!currentDesign.equals(resourceDesign)) {
    		suffix = currentDesign.getId();
    }
    
	String styleCaption = "";
	
	String pTop = properties.get("./ptop", "0");
	String pBottom = properties.get("./pbottom", "0");
	String pLeft = properties.get("./pleft", "0");
	String pRight = properties.get("./pright", "0");
	String imageWidth = properties.get("./width", "0");
	String caption = properties.get("./jcr:description", "");
		
	String padding = pTop + pBottom + pLeft + pRight;
	
	if (!padding.equals("0000")) {	// paddings are set, override custom style
		styleImage += "padding: " + pTop + "px " + pRight + "px " + pBottom + "px " + pLeft + "px;";
		styleImage += "margin: 0px !important;"; 
	}
	if (caption.length() > 0) { // if there's caption, apply padding to the caption
		styleCaption = "padding: 0px 5px;"; 
	}
	
	if (!"0".equals(imageWidth)) {
		// imageWidth + padding
		int newWidth = Integer.parseInt(imageWidth) + Integer.parseInt(pLeft) + Integer.parseInt(pRight);
		styleImage += "width:" + newWidth + "px; max-width:100% !important;";
	}
%>

<div id="<%= "cq-image-jsp-" + resource.getPath() %>" style="<%= styleImage %>" >
<% 
		Image image = new Image(resource);
	    image.setSrc(gsImagePathProvider.getImagePathByLocation(image));
	  	try{
		    image.setIsInUITouchMode(Placeholder.isAuthoringUIModeTouch(slingRequest));
		
		    //drop target css class = dd prefix + name of the drop target in the edit config
		    image.addCssClass(DropTarget.CSS_CLASS_PREFIX + "image");
		    image.addCssClass(styleClass);
		    image.setSuffix(suffix);
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
			<cq:text property="jcr:description" placeholder="" tagName="small" escapeXml="true"/>
		</div>
</div>
