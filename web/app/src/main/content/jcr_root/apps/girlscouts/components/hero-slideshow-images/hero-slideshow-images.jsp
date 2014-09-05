<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@page import="java.util.Map" %>
<%@page import="java.util.HashMap" %>
<%@ page import="java.util.Iterator" %>
<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%@ page import="org.apache.sling.commons.json.JSONArray" %>
<%@ page import="org.apache.sling.commons.json.JSONException,com.day.cq.dam.api.Asset" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>

<%
   Node imageNode = resource.adaptTo(Node.class);
   String largePath = "";
   String smallPath ="";
   String mediumPath ="";
   Asset assets=null;
   Resource rendition=null;
  
if ((null==imageNode) && WCMMode.fromRequest(request) == WCMMode.EDIT) {
    %>
    <div style="text-align:center; height:500px;"> 
           <p style="text-align: center">Click edit above and select number of slides. Then click here to add images to slides.</p>
    </div>
   <% }
else if(imageNode!=null){
	String spplacement = (String)request.getAttribute("sbplacement");
	
	Iterator<Resource> images = resource.listChildren();
	String alt = "";
	String linkUrl = "";
	if(imageNode.hasProperty("alt")){
			alt = imageNode.getProperty("alt").getString();
	}
	if(imageNode.hasProperty("linkURL")){
			linkUrl = imageNode.getProperty("linkURL").getString()+".html";
	}
%>  
  
 <% 
  String imgPath = "";
  while(images.hasNext()){ 
	Node imgNode = images.next().adaptTo(Node.class);
	String width = "960";
	String height="";
    if(imgNode.hasProperty("width")){
        width = imgNode.getProperty("width").getString();
    	}
	if(imgNode.hasProperty("height")){
          width = imgNode.getProperty("height").getString();
        }
    if(imgNode.getProperty("imagesize").getString().equalsIgnoreCase("regular")){
		if(imgNode.hasProperty("fileReference")){
            	  largePath = imgNode.getProperty("fileReference").getString();
             }
           %>
           <div>
           <a href="<%=linkUrl%>">
            <%if(spplacement!=null && spplacement.equalsIgnoreCase("right")){ %> 
				<%= displayRendition(resourceResolver, largePath, "cq5dam.web.665.365", "hide-for-small hide-for-medium", BREAKPOINT_MAX_LARGE) %>
             <%}else{%>
            	 <%= displayRendition(resourceResolver, largePath, "cq5dam.web.960.420", "hide-for-small hide-for-medium", BREAKPOINT_MAX_LARGE) %>
             <%} %> </a>
           </div>    
          <%
           }
    if(imgNode.getProperty("imagesize").getString().equalsIgnoreCase("medium")){
    	if(imgNode.hasProperty("fileReference")){
    		mediumPath = imgNode.getProperty("fileReference").getString();
           } 
       %>  
              <div>    
               <a href="<%=linkUrl%>"> 
				<%= displayRendition(resourceResolver, mediumPath, "cq5dam.web.720.420", "show-for-medium", BREAKPOINT_MAX_MEDIUM) %>
               </a>  
              </div> 
     <%}
     if(imgNode.getProperty("imagesize").getString().equalsIgnoreCase("small")){
		if(imgNode.hasProperty("fileReference")){
			smallPath = imgNode.getProperty("fileReference").getString();
        	} 
		%>  
             <div>
              <a href="<%=linkUrl%>">  
				<%= displayRendition(resourceResolver, smallPath, "cq5dam.web.320.400", "show-for-small", BREAKPOINT_MAX_SMALL) %>
              </a>
             </div>  
          <%  }
      %>   
          
    <% }  
   }//else
    %>
