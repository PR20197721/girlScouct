<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@page import="java.util.Random,java.util.List,java.util.ArrayList,java.util.Date,
java.util.Collections,java.util.Comparator" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder"></div>
<%
String cssClasses = properties.get("cssClasses", "");

 //Spring Board First 
 request.setAttribute("FsbDesc", properties.get("firstsbdesc","")); 
 request.setAttribute("FsbTitle",properties.get("firstsbtitle","")); 
 request.setAttribute("FsbButton", properties.get("firstsbbutton",""));
 request.setAttribute("FsbUrl", properties.get("firstsburl",""));
 request.setAttribute("FsbNewWindow", properties.get("firstsbnewwindow","false"));
 

 
 request.setAttribute("SsbDesc", properties.get("secondsbdesc","")); 
 request.setAttribute("SsbTitle",properties.get("secondsbtitle","")); 
 request.setAttribute("SsbUrl", properties.get("secondsburl",""));
 request.setAttribute("SsbButton", properties.get("secondsbbutton",""));
 request.setAttribute("SsbNewWindow", properties.get("secondsbnewwindow","false"));
 request.setAttribute("sbplacement",properties.get("spplacement",""));




%>
<%!
  int timer = 0;
%>
<%
String editFlag = "true";
int slideShowCount=0;
Iterator<Resource> images = resource.listChildren();
slideShowCount = Integer.parseInt(properties.get("slideshowcount", "1"));
ArrayList<Resource> imageList = new ArrayList<Resource>();
for(int i=1; i<slideShowCount+1;i++){
  if(images.hasNext()) imageList.add(images.next());
}
Collections.sort(imageList, new Comparator<Resource>(){
    public int compare(Resource image1, Resource image2) {
      String sortOrder1 = "";
      String sortOrder2 = "";
      try {
      	Node node1 = image1.adaptTo(Node.class);
        if(node1.hasProperty("sortOrder")){
          sortOrder1 = node1.getProperty("sortOrder").getString();
        }
      	Node node2 = image2.adaptTo(Node.class);
        if(node2.hasProperty("sortOrder")){
          sortOrder2 = node2.getProperty("sortOrder").getString();
        }
      } catch (Exception e) {
    	e.printStackTrace(); 
      } 
      return sortOrder1.compareToIgnoreCase(sortOrder2);
    }
  });
images = imageList.iterator();
for(int i=1; i<slideShowCount+1;i++){
	if(images.hasNext()){
		Resource imgResource = images.next();
		String imagePath = imgResource.getPath();
		%>     
		<sling:include path="<%=imagePath%>" resourceType="girlscouts/components/hero-slideshow-images" addSelectors="placeholder"/>
		<% 
	}
	else{
		String placeHolderName = resource.getPath()+"/placeholder-image_"+i;
		%>      
		<sling:include path="<%=placeHolderName%>" resourceType="girlscouts/components/hero-slideshow-images"  addSelectors="placeholder"/> 
		<%
	}
}
if (WCMMode.fromRequest(request) == WCMMode.EDIT){
    editFlag ="false"; 
    request.setAttribute("editFlag",editFlag); 
}
   String sbplacement = properties.get("spplacement","");
   timer = Integer.parseInt(properties.get("slideshowtimer", "6000"));
if(sbplacement.equals("right")){
    %>
	  <cq:include script="spring-board-right.jsp"/>
<% }else{%>
	 <cq:include script="default-sboard-rendition.jsp"/>
<%} %>
<script>
 $(document).ready(function(){
	     setTimer("<%=timer%>","<%=editFlag%>");
	});
 </script>
