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
%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%><%
	String divId = "cq-image-jsp-" + resource.getPath();
	%><div id="<%= divId %>"><%
	    Image image = new Image(resource);
	    image.setSrc(gsImagePathProvider.getImagePathByLocation(image));
	    String width = properties.get("./width", "0");
	    String height = properties.get("./height", "0");
	    
	  	try{
		    image.setIsInUITouchMode(Placeholder.isAuthoringUIModeTouch(slingRequest));
		
		    //drop target css class = dd prefix + name of the drop target in the edit config
		    image.addCssClass(DropTarget.CSS_CLASS_PREFIX + "image");
		    image.loadStyleData(currentStyle);
		    image.setSelector(".img"); // use image script
		    image.setDoctype(Doctype.fromRequest(request));
		    if (!"0".equals(width)) {
	        	image.addAttribute("width", width + "px");
	    	}
	        if (!"0".equals(height)) {
	        	image.addAttribute("height", height + "px");
	    	}
		    
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
	</div>
	<cq:text property="jcr:description" placeholder="" tagName="small" escapeXml="true"/>
    <%@include file="/libs/foundation/components/image/tracking-js.jsp"%>
