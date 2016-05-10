<%@page session="false" contentType="text/html; charset=utf-8" import="com.day.cq.commons.Doctype, com.day.cq.wcm.api.WCMMode, com.day.cq.wcm.foundation.ELEvaluator" %><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %><cq:defineObjects/><%

   HttpSession session = request.getSession();
   final org.girlscouts.vtk.helpers.ConfigManager configManager = sling.getService(org.girlscouts.vtk.helpers.ConfigManager.class);
   String isDemoSite= configManager.getConfig("isDemoSite");
//System.err.println("IsDemoSite: " + isDemoSite);  


System.err.println("isDemo chk: "+ request.getParameter("useAsDemo"));
    if( request.getParameter("useAsDemo")!=null && !request.getParameter("useAsDemo").trim().equals("") ){
    		session.setAttribute("useAsDemo", request.getParameter("useAsDemo"));
    }else{
    	    session.removeAttribute("useAsDemo");
    }
System.err.println("check isDemo : "+ session.getAttribute("useAsDemo") + " : "+  request.getParameter("useAsDemo")  ); 
  
    String myUrl = request.getRequestURL().toString();
       
    if(  myUrl.trim().contains("vtk.demo.index.html") ) {
        
        org.girlscouts.vtk.auth.models.ApiConfig apiConfig=  new org.girlscouts.vtk.auth.models.ApiConfig();
        session.setAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName(), apiConfig);
    }
    
    if( myUrl!=null)
    	myUrl= java.net.URLDecoder.decode( myUrl);
    
  if (myUrl.trim().contains("/en/vtk.home.html") && session.getAttribute("fatalError")!=null)
    	;
    
  else if( myUrl==null || !myUrl.trim().contains("/controllers/vtk.logout.html") ){
    
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
	    
   /*
    try{
		apiConfig = (org.girlscouts.vtk.auth.models.ApiConfig)
		    session.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName());
	} catch (ClassCastException exc) { 
		session.invalidate();
		apiConfig=null; 
	} catch (Exception e) {
		e.printStackTrace();
	}
	*/
    
	if( apiConfig==null ){
	 
	    String redirectTo = "/content/girlscouts-vtk/controllers/auth.sfauth.html?action=signin";
	    if( isDemoSite !=null && isDemoSite.equals("true") ){
	        redirectTo = "/content/girlscouts-demo/en.html";    
	    }
	    
	    // GSWS-190 Add refererCouncil
	    String refererCouncil = request.getParameter("refererCouncil");
	    if (refererCouncil != null && !refererCouncil.isEmpty()) {
	        redirectTo = redirectTo + "&refererCouncil=" + refererCouncil;
	    }


		response.sendRedirect(redirectTo);
		return;		
		/*
	} else if (apiConfig.isFail()) {
		boolean thisIsHome = false;
		for (String selectFragment: slingRequest.getRequestPathInfo().getSelectors()) {
			if ("home".equals(selectFragment)) {
				thisIsHome = true;
			}
		}
		if (!thisIsHome) {
		    		// go to vtk.home.html w/ error message inside apiConfig.getErrorMessage()
		}
	} else {
		   System.out.println("Unhandled ApiConfig State.");
	   */
	}
   System.out.println("ApiConfig not null and not failed.");

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

<%
final org.girlscouts.vtk.ejb.UserUtil userUtilHead = sling.getService(org.girlscouts.vtk.ejb.UserUtil.class); 
String referer= userUtilHead.getCouncilUrlPath((org.girlscouts.vtk.auth.models.ApiConfig)session.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName()), request);
referer= referer +"en/site-search";
request.setAttribute("altSearchPath", referer);
%>


<cq:include script="head.jsp"/>
<cq:include script="body.jsp"/>

</html>
