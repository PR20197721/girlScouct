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
   else{  
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
   while(images.hasNext())
       {
	       
    	 //Image image =  scrSizeImages.next().adaptTo(Image.class);
           //Resource resource = sc
           Node imgNode = images.next().adaptTo(Node.class);
           String width = "1120";
           String height="";
           if(imgNode.hasProperty("width")){
               width = imgNode.getProperty("width").getString();
           }
           if(imgNode.hasProperty("height")){
               width = imgNode.getProperty("height").getString();
           }
          
           
               
           if(imgNode.getProperty("imagesize").getString().equalsIgnoreCase("regular"))
           {
        	 //String fileReference = imgNode.getProperty("fileReference").getString(); 
             if(imgNode.hasProperty("fileReference")){
            	  largePath = imgNode.getProperty("fileReference").getString();
             }
           
           %><a href="<%=linkUrl%>">  
					<% displayRendition(resourceResolver, largePath, "cqdam.web.1120.490", pageContext, "hide-for-small hide-for-medium"); %>
              </a> 
          <%
           }
           if(imgNode.getProperty("imagesize").getString().equalsIgnoreCase("medium")){
              if(imgNode.hasProperty("fileReference")){
                  mediumPath = imgNode.getProperty("fileReference").getString();
              } 
                  %>  
               <a href="<%=linkUrl%>"> 
				<% displayRendition(resourceResolver, mediumPath, "cqdam.web.720.420", pageContext, "show-for-medium"); %>
               </a>  
          <%   }
           if(imgNode.getProperty("imagesize").getString().equalsIgnoreCase("small")){
        	   if(imgNode.hasProperty("fileReference")){
                   smallPath = imgNode.getProperty("fileReference").getString();
        	     }  
                   %>  
           
              <a href="<%=linkUrl%>">  
				<% displayRendition(resourceResolver, smallPath, "cqdam.web.320.400", pageContext, "show-for-small"); %>
              </a> 
          <%  }
      %>   
          
    <% }  
   }
    %>
