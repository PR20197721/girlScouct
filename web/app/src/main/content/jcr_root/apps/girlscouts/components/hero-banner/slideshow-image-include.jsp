 <%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page import="java.util.Random,
				java.util.List,
				java.util.ArrayList,
				java.util.Date,
				java.util.Collections,
				java.util.Comparator,
				com.day.cq.wcm.api.WCMMode" %>
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
%>
	<script>
		SlideShowManager.removeAll();
	</script>
<%
  // assign new iterator  
  images = imageList.iterator();
	for(int i=1; i<slideShowCount+1;i++){
        imgName = "";
		if(images.hasNext()){
			Resource imgResource = images.next();
			imagePath = imgResource.getPath();
			imgName = imgResource.getName();
			%>
			<div>
				<div>
					<cq:include path="<%=imagePath%>" resourceType="girlscouts/components/hero-slideshow-images"/>  
				</div>
			</div> 
		<% 	//path = imgName;
		}
		else{
           	String path = "Image_" + new Date().getTime()+blank_number;//new slide-show-image component created with empty image.
            blank_number++;
			%>
			<div>        
				<div>
					<cq:include path="<%=path%>" resourceType="girlscouts/components/hero-slideshow-images"/>  
				</div>
			</div> 
			<%  //path = "";
			imgName="";
		}%>
		
	<%}%>
   <div class="slide-show-target"></div>
	<script>
		SlideShowManager.init("slide-show-target", <%= WCMMode.fromRequest(request) == WCMMode.EDIT %>);
	</script>