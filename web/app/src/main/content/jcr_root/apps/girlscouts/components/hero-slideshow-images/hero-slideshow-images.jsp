<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@page import="java.util.Map" %>
<%@page import="java.util.HashMap" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="org.apache.sling.commons.json.JSONArray" %>
<%@ page import="org.apache.sling.commons.json.JSONException" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/component_global.jsp"%>
<%
   Node imageNode = resource.adaptTo(Node.class);

if ((null==imageNode) && WCMMode.fromRequest(request) == WCMMode.EDIT) {
    %>
    <div style="text-align:center; height:500px;"> 
           <p style="text-align: center"> #####  Images ##### </p>
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
       linkUrl = imageNode.getProperty("linkURL").getString();
   }
  
 %>  
  
 <%   
   while(images.hasNext())
       {
          
    	 //Image image =  scrSizeImages.next().adaptTo(Image.class);
           //Resource resource = sc
           Node imgNode = images.next().adaptTo(Node.class);
           String width = "";
           String height="";
           if(imgNode.hasProperty("width")){
               width = imgNode.getProperty("width").getString();
           }
           if(imgNode.hasProperty("height")){
               width = imgNode.getProperty("height").getString();
           }
           //imgNode.getProperty("fileReference").getString();
           String imgPath = imgNode.getProperty("fileReference").getString();
           if(imgNode.getProperty("imagesize").getString().equalsIgnoreCase("regular"))
           {%><a href="<%=linkUrl%>">  
               <img class="hide-for-small hide-for-medium" src="<%=imgPath %>"<%if(!width.isEmpty()){%> width="<%=width%>"<%}%><%if(!height.isEmpty()){%>width="<%=height%>"<%}%><%if(!alt.isEmpty()){%>alt="<%=alt%>"<%}%>>
              </a> 
          <%
           }
           if(imgNode.getName().equalsIgnoreCase("medium")){%>
               <a href="<%=linkUrl%>"> 
                <img class="show-for-medium" src="<%=imgPath %>"<%if(!width.isEmpty()){%> width="<%=width%>"<%}%><%if(!height.isEmpty()){%>width="<%=height%>"<%}%><%if(!alt.isEmpty()){%>alt="<%=alt%>"<%}%>>
               </a>  
          <%   }
           if(imgNode.getName().equalsIgnoreCase("small")){%>
              <a href="<%=linkUrl%>">  
               <img class="show-for-small" src="<%=imgPath %>"<%if(!width.isEmpty()){%> width="<%=width%>"<%}%><%if(!height.isEmpty()){%>width="<%=height%>"<%}%><%if(!alt.isEmpty()){%>alt="<%=alt%>"<%}%>>
              </a> 
          <%  }
      %>   
          
    <% }  
   }
    %>
 
   
   
  