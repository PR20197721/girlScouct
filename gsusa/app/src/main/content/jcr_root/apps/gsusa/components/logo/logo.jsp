<%@ page import="org.apache.sling.api.resource.ResourceUtil,com.day.cq.commons.Doctype,com.day.cq.wcm.api.components.DropTarget,com.day.cq.wcm.foundation.Image, com.day.cq.wcm.foundation.Placeholder" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%@ taglib prefix="gsusa" uri="https://girlscouts.org/gsusa/taglib" %>
<%
	String imgAlt = properties.get("imageAlt", "");
	Resource regularLogo = resource.getChild("image");	
	if(!ResourceUtil.isNonExistingResource(regularLogo)){
		try{
			Image image = new Image(regularLogo);
			image.setSrc(gsImagePathProvider.getImagePathByLocation(image));			    	  	
		    image.setIsInUITouchMode(Placeholder.isAuthoringUIModeTouch(slingRequest));		   
		    //drop target css class = dd prefix + name of the drop target in the edit config
		    image.setAlt(imgAlt);
		    image.setTitle(imgAlt);
		    image.loadStyleData(currentStyle);
		    image.setSelector(".img"); // use image script
		    image.setDoctype(Doctype.fromRequest(request));	
		    image.addAttribute("id", "mainGSLogo");
			image.addCssClass("mainGSLogo");
			image.draw(out); 
			try {
				String headerNavPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header/header-nav";   
				ValueMap headerNavProps = resourceResolver.resolve(headerNavPath).adaptTo(ValueMap.class);
			    if (headerNavProps.get("displayStickyNav", false)) {
			    	Resource stickyNavLogo = resource.getChild("stickyNavImage");
			    	Image image2 = new Image(stickyNavLogo);
					image2.setSrc(gsImagePathProvider.getImagePathByLocation(image2));
					image2.setIsInUITouchMode(Placeholder.isAuthoringUIModeTouch(slingRequest));
				    image2.setAlt(imgAlt);
				    image2.setTitle(imgAlt);
				    image2.loadStyleData(currentStyle);
				    image2.setSelector(".img"); // use image script
				    image2.setDoctype(Doctype.fromRequest(request));
					image2.addCssClass("sticky-nav-GS-logo");
					%><div class="logo"><%image2.draw(out);%></div><%
			    }
			} catch(Exception e) {}
	  	}catch (Exception e){}
	}
%>