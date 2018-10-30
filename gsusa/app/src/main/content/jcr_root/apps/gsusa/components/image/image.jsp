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
	
	String additionalCssStyle = properties.get("./additionalCssStyle", "");
	String style = "";
	if(additionalCssStyle.length() > 0) {
		style = "style=\""+additionalCssStyle+"\"";
	}
	
	String styleClass = DropTarget.CSS_CLASS_PREFIX + "image " + properties.get("./additionalCss", "");
	
    // add design information if not default (i.e. for reference paras)
    String suffix = null;
    if (!currentDesign.equals(resourceDesign)) {
    		suffix = currentDesign.getId();
    }
%>

<div id="<%= "cq-image-jsp-" + resource.getPath() %>" <%=style%> >
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
				image.draw(out);
			}
	  	}catch (Exception e){
	  		
	  	}
%>	
</div>
<cq:text property="jcr:description" placeholder="" tagName="small" escapeXml="true"/>
