 <%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page import="java.util.Random,java.util.List,java.util.ArrayList" %>
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
	String path = "Image_"+number_of_children;
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
		<% 	path = imgName;
		}
		else{
           	path = "Image_empty_" + blank_number;
            blank_number++;
			%>
			<div>        
				<cq:include path="<%=path%>" resourceType="girlscouts/components/hero-slideshow-images"/>  
			</div> 
			<%  path = "";
			imgName="";
		}%>
		
	<%}%>
