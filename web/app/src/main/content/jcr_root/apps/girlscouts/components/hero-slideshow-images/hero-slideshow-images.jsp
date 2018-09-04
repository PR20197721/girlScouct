<%@page import="com.day.cq.wcm.foundation.Image,java.util.List,
				java.util.Map,java.util.HashMap,
				java.util.Iterator,com.day.cq.wcm.api.WCMMode,
				java.util.List,
				java.util.ArrayList,
				java.util.Random,
				org.apache.sling.commons.json.JSONArray,
				org.apache.sling.commons.json.JSONException,
				com.google.gson.Gson,
				com.day.cq.dam.api.Asset,
				org.girlscouts.web.dto.SlideShowElement,
				org.girlscouts.web.video.util.VIDEO_TYPE,
				org.girlscouts.web.video.util.VideoUtil" 
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
	String slideShowElementId = "SlideShowElement_" + new Random().nextInt(10000) + 1000;
%>
<div id="<%= slideShowElementId %>"></div>
<%

	List<Object> slideShowElements = new ArrayList<Object>();
   
	Node imageNode = resource.adaptTo(Node.class);
	Asset assets=null;
	String newWindow = "";
		  
	if ((null==imageNode) && WCMMode.fromRequest(request) == WCMMode.EDIT) {
    		slideShowElements.add(new SlideShowElement(getPlaceHolderText("Click edit above and select number of slides. Then click here to add images to slides.",""), "", "", "regular", false));
    		slideShowElements.add(new SlideShowElement(getPlaceHolderText("Click edit above and select number of slides. Then click here to add images to slides.",""), "", "", "medium", false));
	    	slideShowElements.add(new SlideShowElement(getPlaceHolderText("Click edit above and select number of slides. Then click here to add images to slides.",""), "", "", "small", false));

	} else if(imageNode!=null){
		
		String spplacement = (String)request.getAttribute("sbplacement");
	
		Iterator<Resource> images = resource.listChildren();
		String alt = "";
		String linkUrl = "";
		String sortOrder = "";
		boolean hasOneVideoUrl = false;
		
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
		List<String> missingImageSizes = new ArrayList<String>();
		missingImageSizes.add("small");
		missingImageSizes.add("medium");
		missingImageSizes.add("regular");
		while(images.hasNext()){
			Node imgNode = images.next().adaptTo(Node.class);
			String imageSize = imgNode.getProperty("imagesize").getString();
			
			if(imgNode.hasProperty("videoUrl")){
				String videoUrl = imgNode.getProperty("videoUrl").getString();
				hasOneVideoUrl = true;
				// Add videos separate.
				VIDEO_TYPE videoType = VIDEO_TYPE.detect(videoUrl);
				if(videoType != VIDEO_TYPE.NONE){
					slideShowElements.add(VideoUtil.getVideo(videoUrl, imageSize));
					missingImageSizes.remove(imageSize);
					continue;
				}
			}
			
			if(imgNode.hasProperty("fileReference")){
				String fileReference = imgNode.getProperty("fileReference").getString();
				
				Asset imageAsset = getImageAsset(resourceResolver, fileReference);
				rendition = imageAsset != null ? imageAsset.getRendition(new PrefixRenditionPicker("cq5dam.web.1280.1280")) : null;
           		if(rendition != null){
           			slideShowElements.add(new SlideShowElement(rendition.getPath(), linkUrl, alt, imageSize, "true".equals(newWindow)));
           		}else{
           			slideShowElements.add(new SlideShowElement(getPlaceHolderText("Not able to find the image: " + fileReference, ""), "", "", imageSize, false));
           		}
 				missingImageSizes.remove(imageSize);
			}
		}
		if(missingImageSizes.size() == 3 && !hasOneVideoUrl)
		{
            if(WCMMode.fromRequest(request) == WCMMode.EDIT){
				missingImageSizes.forEach(missingSize -> slideShowElements.add(new SlideShowElement("Click edit above and select number of slides. Then click here to add images to slides.", "", "", missingSize, false)));
            }
		} else{
			missingImageSizes.forEach(missingSize -> slideShowElements.add(new SlideShowElement("MISSING", "", "", missingSize, false)));
		}
   }
%>

<script>
	SlideShowManager.addElementSet(<%= new Gson().toJson(slideShowElements) %>, '<%= resource.getParent().getParent().getPath() + "_slideshow" %>', "<%= slideShowElementId %>");
</script>

