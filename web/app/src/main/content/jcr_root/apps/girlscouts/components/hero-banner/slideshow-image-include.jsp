 <%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page import="java.util.Random,java.util.List,java.util.ArrayList,java.util.Date,
java.util.Collections,java.util.Comparator" %>
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


  // reiterate over and add the items into the slots
  ArrayList<Resource> imageList = new ArrayList<Resource>();
  for(int i=1; i<slideShowCount+1;i++){
    if(images.hasNext()) imageList.add(images.next());
  }
  
  // sort
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

  // assign new iterator  
  images = imageList.iterator();

	System.out.println("About to create slideshow images");	
	for(int i=1; i<slideShowCount+1;i++){
        imgName = "";
        System.out.println("Writing hero slideshow image. Slideshow count is: " + slideShowCount + " i is: " + i);
		if(images.hasNext()){
			Resource imgResource = images.next();
			imagePath = imgResource.getPath();
			imgName = imgResource.getName();
			slingRequest.setAttribute("hero-banner-path", imagePath );
			%>
			<div>        
				<cq:include script="/apps/girlscouts/components/hero-banner/slideshow-image.jsp" />  
			</div> 
		<% 	//path = imgName;
		}
		else{
			String placeHolderName = resource.getPath()+"/hero_banner_"+i;
			slingRequest.setAttribute("hero-banner-path", placeHolderName );
			%>
			<div>        
				<cq:include script="/apps/girlscouts/components/hero-banner/slideshow-image.jsp" />  
			</div> 
			<%
		}%>
		
	<%}%>

	<div class="slide-show-target"></div>
	<script>
		SlideShowManager.init("slide-show-target");
	</script>