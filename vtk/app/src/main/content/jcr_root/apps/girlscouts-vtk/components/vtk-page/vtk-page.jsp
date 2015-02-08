<%@page session="false" contentType="text/html; charset=utf-8" import="com.day.cq.commons.Doctype, com.day.cq.wcm.api.WCMMode, com.day.cq.wcm.foundation.ELEvaluator" %><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %><cq:defineObjects/><%

boolean isAutoLogin =false;

HttpSession session = request.getSession();
if( !isAutoLogin ){
    
	org.girlscouts.vtk.auth.models.ApiConfig apiConfig= null;
	try{
		apiConfig = (org.girlscouts.vtk.auth.models.ApiConfig)
		session.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName());
	} catch (ClassCastException exc) { 
		session.invalidate();
		apiConfig=null; 
	} catch (Exception e) {
		e.printStackTrace();
	}
	if( apiConfig==null ){
		response.sendRedirect("/content/girlscouts-vtk/controllers/auth.sfauth.html?action=signin");
		return;
	}
	
}else{
	
	final org.girlscouts.vtk.ejb.TroopUtil troopUtil = sling.getService(org.girlscouts.vtk.ejb.TroopUtil.class);
	troopUtil.autoLogin(session);
	
}
	
	
	

	// read the redirect target from the 'page properties' and perform the
	// redirect if WCM is disabled.
	String location = properties.get("redirectTarget", "");

	// resolve variables in path
	location = ELEvaluator.evaluate(location, slingRequest, pageContext);
	boolean wcmModeIsDisabled = WCMMode.fromRequest(request) == WCMMode.DISABLED;
	boolean wcmModeIsPreview = WCMMode.fromRequest(request) == WCMMode.PREVIEW;

	if ( (location.length() > 0) && ((wcmModeIsDisabled) || (wcmModeIsPreview)) ) {
		// check for recursion
		if (currentPage != null && !location.equals(currentPage.getPath()) && location.length() > 0) {
			// check for absolute path
			final int protocolIndex = location.indexOf(":/");
			final int queryIndex = location.indexOf('?');
			final String redirectPath;	
			if ( protocolIndex > -1 && (queryIndex == -1 || queryIndex > protocolIndex) ) {
				redirectPath = location;
			} else {
				redirectPath = slingRequest.getResourceResolver().map(request, location) + ".html";
			}
			
			
			response.sendRedirect(redirectPath);
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		return;
	}
	// set doctype
	if (currentDesign != null) {
		currentDesign.getDoctype(currentStyle).toRequest(request);
	}
	
	request.setAttribute("PAGE_CATEGORY", "VTK");
	
	
	
	
	
	
%><%= Doctype.fromRequest(request).getDeclaration() %>
<html <%= wcmModeIsPreview ? "class=\"preview\"" : ""%>>
<cq:include script="head.jsp"/>
<cq:include script="body.jsp"/>

<% 

String thisFooterScript = (String)request.getAttribute("footerScript") ;
if (thisFooterScript!= null) {
	out.println(thisFooterScript);
}else{
	out.println("");
}
%>

</html>
