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
	for(int i=1; i<slideShowCount+1;i++){
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
			if(!imgName.isEmpty()){
				number_of_children = Integer.parseInt(imgName.substring(6,path.length())) + 1;
				path = "Image_"+number_of_children;
			}else{
				number_of_children = number_of_children+1;
				path = "Image_"+number_of_children;
			}
			%>
			<div>        
				<cq:include path="<%=path%>" resourceType="girlscouts/components/hero-slideshow-images"/>  
			</div> 
		<%  path = "";
			imgName="";
		}%>
		
	<%}%>
