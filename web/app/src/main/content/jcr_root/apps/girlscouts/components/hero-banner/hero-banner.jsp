 <%@ page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@page import="java.util.Map" %>
<%@page import="java.util.HashMap" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="org.apache.sling.commons.json.JSONArray" %>
<%@ page import="org.apache.sling.commons.json.JSONException" %>
<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
String cssClasses = properties.get("cssClasses", "");
%>
<%!
   int slideShowCount=0;
   int timer = 0;
  
%>
<%
  boolean editFlag = false;

if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
    editFlag =true;  
    %>
    
   <% }
%>
<div id="heroBanner" class="large-24 medium-24 small-24 columns" style="height:650;">

  <div class="jcarousel-wrapper">
     <div class="jcarousel">
        <ul class="clearfix">
        <%
             slideShowCount = Integer.parseInt(properties.get("slideshowcount", "1"));
             timer = Integer.parseInt(properties.get("slideshowtimer", "6000"));
             for(int i=1; i<slideShowCount+1;i++){
            	  String path = "./"+"Image_"+i;
            	 
            	     %>
           <li> 	     
            <cq:include path="<%=path%>" resourceType="girlscouts/components/hero-slideshow-images"/>  
           </li> 
        <%}
        	
%>
         </ul>   
        </div>
        
        <%if(slideShowCount >1){ %>
        <a href="#" class="jcarousel-control-prev">&lsaquo;</a>
        <a href="#" class="jcarousel-control-next">&rsaquo;</a>
        <p class="jcarousel-pagination"></p>
        <%} %>
 </div>
</div>
<script>
 $(document).ready(function(){
	     displaySlideShow("<%=timer%>","<%=editFlag%>");
	});
 </script>  
  
  
