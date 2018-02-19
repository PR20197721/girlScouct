<%@page import="com.day.cq.wcm.foundation.Image,java.util.List,
				java.util.Map,java.util.HashMap,
				java.util.Iterator,com.day.cq.wcm.api.WCMMode,
				java.util.List,
				java.util.ArrayList,
				org.apache.sling.commons.json.JSONArray,
				org.apache.sling.commons.json.JSONException,
				com.google.gson.Gson,
				com.day.cq.dam.api.Asset,
				org.girlscouts.web.dto.SlideShowElement" 
%>

<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>

<%!
    public String getPlaceHolderText(String text, String classes){
    		String placeHolder = "<div style=\"text-align:center; height:500px;\" class=\""+classes+" \" >"+ 
        					 "<p style=\"text-align: center\">"+text+"</p>"+"</div>";
		return placeHolder;
	}

%>  
<%

	List<SlideShowElement> slideShowElements = new ArrayList<SlideShowElement>();
   
	Node imageNode = resource.adaptTo(Node.class);
	Asset assets=null;
	String newWindow = "";
		  
	if ((null==imageNode) && WCMMode.fromRequest(request) == WCMMode.EDIT) {
	    	slideShowElements.add(new SlideShowElement(getPlaceHolderText("Click edit above and select number of slides. Then click here to add images to slides.",""), "", "", "none", false));
	} else if(imageNode!=null){
		
		String spplacement = (String)request.getAttribute("sbplacement");
	
		Iterator<Resource> images = resource.listChildren();
		String alt = "";
		String linkUrl = "";
		String sortOrder = "";
		
		if(imageNode.hasProperty("newWindow")){
			newWindow = imageNode.getProperty("newWindow").getString();
		}
		if(null==newWindow || "".equals(newWindow)){
			newWindow = "false";
		}
		
		if(imageNode.hasProperty("alt")){
			alt = imageNode.getProperty("alt").getString();
		}
		if(imageNode.hasProperty("linkURL")){
			linkUrl = imageNode.getProperty("linkURL").getString();
			if (!linkUrl.contains("://")) { // If it is not external link
			    linkUrl += ".html";
			}
		}
		String imgPath = "";
		Rendition rendition;
		while(images.hasNext()){
			Node imgNode = images.next().adaptTo(Node.class);
			String imageSize = imgNode.getProperty("imagesize").getString();
			if(imgNode.hasProperty("fileReference")){
				rendition = getImageAsset(resourceResolver, imgNode.getProperty("fileReference").getString()).getRendition(new PrefixRenditionPicker("cq5dam.web.1280.1280"));
           		if(rendition != null){
           			slideShowElements.add(new SlideShowElement(rendition.getPath(), linkUrl, alt, imageSize, "true".equals(newWindow)));
           		}else{
           			slideShowElements.add(new SlideShowElement(getPlaceHolderText("Not able to find the image: " + imgNode.getProperty("fileReference").getString(), ""), "", "", imageSize, false));
           		}
			}else if(WCMMode.fromRequest(request) == WCMMode.EDIT) {
				slideShowElements.add(new SlideShowElement(getPlaceHolderText("Please click to add regular sized image.", ""), "", "", imageSize, false));
 			}
		}
   }
%>

<script>
	SlideShowManager.addElementSet(<%= new Gson().toJson(slideShowElements) %>);
</script>

