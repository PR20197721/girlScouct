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
    com.day.cq.wcm.api.components.DropTarget,
    com.day.cq.wcm.foundation.Image, com.day.cq.wcm.foundation.Placeholder" %><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@include file="/apps/girlscouts/components/global.jsp"%><%
    /***********************************************************/
    // Girl Scouts customize code to generate renditions
    // TODO:
	request.setAttribute(IMG_RENDITION_ATTR, "100x100");	

	String rendition = (String)request.getAttribute(IMG_RENDITION_ATTR);	
	if (rendition != null) {
	    try {
	    	String[] values = rendition.split("x");
	    	if (values.length != 2) {
	    	    throw new IllegalArgumentException();
	    	}
	    	String widthStr = values[0];
	    	String heightStr = values[1];
	    	// Just to make sure that the integers are in correct format
    		int width = Integer.parseInt(widthStr);
    		int height = Integer.parseInt(heightStr);
    		
    		Node imageNode = resource.adaptTo(Node.class);
    		if (width != 0) imageNode.setProperty("width", widthStr);
    		if (height != 0) imageNode.setProperty("height", heightStr);
    		resourceResolver.adaptTo(Session.class).save();
	    } catch (IllegalArgumentException ie) {
	        log.error("Error parsing image rendition:" + rendition 
	                + " resource: " + resource.getPath());
	    } catch (RepositoryException re) {
	        log.error("Repository Exception while generatint rendition: " + rendition
	                + " resource: " + resource.getPath());
	    }
	}
    /***********************************************************/

    Image image = new Image(resource);
    image.setIsInUITouchMode(Placeholder.isAuthoringUIModeTouch(slingRequest));

    //drop target css class = dd prefix + name of the drop target in the edit config
    image.addCssClass(DropTarget.CSS_CLASS_PREFIX + "image");
    image.loadStyleData(currentStyle);
    image.setSelector(".img"); // use image script
    image.setDoctype(Doctype.fromRequest(request));
    // add design information if not default (i.e. for reference paras)
    if (!currentDesign.equals(resourceDesign)) {
        image.setSuffix(currentDesign.getId());
    }
    String divId = "cq-image-jsp-" + resource.getPath();


    %><div id="<%= divId %>"><% image.draw(out); %></div><%
    %><cq:text property="jcr:description" placeholder="" tagName="small" escapeXml="true"/>
    
    <%@include file="/libs/foundation/components/image/tracking-js.jsp"%>
