 <%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page import="java.util.Random,java.util.List,java.util.ArrayList,java.util.Date" %>
 <%!
   int slideShowCount=0;
   //int timer = 0;
  
  
%>
<%
	int slide_number = 1;
	slideShowCount = Integer.parseInt(properties.get("slideshowcount", "1"));
	
	int number_of_children=0;
	Iterator<Resource> images = resource.listChildren();
	String imagePath = "";
	String imgName = "";
//	String path = "Image_"+number_of_children;
	List<String> nameImage = new ArrayList<String>(); 
	int blank_number = 0;
	for(int i=1; i<slideShowCount+1;i++){
        imgName = "";
		if(images.hasNext()){
			Resource imgResource = images.next();
			imagePath = imgResource.getPath();
			imgName = imgResource.getName();
			%>
			<div>        
				<cq:include path="<%=imagePath%>" resourceType="girlscouts/components/hero-slideshow-images"/>  
			</div> 
		<% 	//path = imgName;
		}
		else{
           	String path = "Image_" + new Date().getTime()+blank_number;//new slide-show-image component created with empty image.
            blank_number++;
			%>
			<div>        
				<cq:include path="<%=path%>" resourceType="girlscouts/components/hero-slideshow-images"/>  
			</div> 
			<%  //path = "";
			imgName="";
		}%>
		
	<%}%>
