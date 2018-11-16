<%--
  ADOBE CONFIDENTIAL

  Copyright 2012 Adobe Systems Incorporated
  All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and may be covered by U.S. and Foreign Patents,
  patents in process, and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.
--%><%
%><%@page session="false"%>
<%@page import=" org.apache.sling.api.resource.Resource,
                 org.apache.sling.tenant.Tenant,
                 org.apache.sling.api.SlingHttpServletRequest,
                 javax.jcr.Node,
                 java.net.URLEncoder,
                 com.adobe.granite.i18n.LocaleUtil,
                 com.day.cq.dam.api.DamConstants"%><%
%><%@include file="/libs/granite/ui/global.jsp"%><%
	final String VTK_MOUNTPOINT_ASSETS = "/content/dam-resources2";
    String assetsVanity = "/vtk-assets.html";
    String assetDetailsVanity = "/assetdetails.html";
    Tenant tenant = resourceResolver.adaptTo(Tenant.class);
    String mountPoint = (null != tenant)? (String)tenant.getProperty("dam:assetsRoot") : VTK_MOUNTPOINT_ASSETS;
    if(null == mountPoint || mountPoint.trim().isEmpty()){
        mountPoint = VTK_MOUNTPOINT_ASSETS;
    }
    String contextPath = request.getContextPath();
    String contentPath = slingRequest.getRequestPathInfo().getSuffix();
    String uri = slingRequest.getRequestURI();
    Resource res = resourceResolver.getResource(contentPath);
    // if the resource is not valid
    if (contentPath == null || !contentPath.contains(mountPoint) || res == null) {
        // redirect to assets root path if permission is there
        if (resourceResolver.getResource(mountPoint) != null) {
            response.sendRedirect(contextPath + assetsVanity + mountPoint);    
        }        
        return;
    } else if(res.isResourceType(DamConstants.NT_DAM_ASSET) && uri.startsWith(contextPath +assetsVanity) && !isColumnView(slingRequest)) {
        //using assets vanity for dam:Asset, then redirect to assetdetails vanity properly
        contentPath = getEncodedValue(contentPath);
        response.sendRedirect(contextPath + assetDetailsVanity + contentPath);
    } else if(res.adaptTo(Node.class).isNodeType("nt:folder") && uri.startsWith(contextPath +assetDetailsVanity)) {
      //using assetdetails vanity for folder, then redirect to assets vanity properly
      contentPath = getEncodedValue(contentPath);
        response.sendRedirect(contextPath + assetsVanity + contentPath);
    }
    String headIncludePath =  resource.getPath() + "/head";

%>
<%!
String getEncodedValue(String str) {
	try {
	str =  URLEncoder.encode(str, "UTF-8")
    .replaceAll("\\%2F", "/")		
    .replaceAll("\\+", "%20")
    .replaceAll("\\%21", "!")
    .replaceAll("\\%27", "'")
    .replaceAll("\\%28", "(")
    .replaceAll("\\%29", ")")
    .replaceAll("\\%7E", "~");
	} catch (Exception e) {
		log("Error while encoding the contentPath "+str, e);
	}
	return str;
}

boolean isColumnView(SlingHttpServletRequest slingRequest) {
    Cookie childAssetsLayout = slingRequest.getCookie("cq-assets-files");
    if (childAssetsLayout != null && "column".equals(childAssetsLayout.getValue())) {
       return true;
    }

 return false;
}
%>