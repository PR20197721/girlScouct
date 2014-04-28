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

<%!
    public static class SlideShowImage extends Image{
        public SlideShowImage(Resource resource) {
            super(resource);
        }
    }

    public static List<SlideShowImage> getImages(Resource resource, String name) {
        List<SlideShowImage> images = new ArrayList<SlideShowImage>();
        Resource imagesResource = resource.getChild(name);
        if (imagesResource == null) {
            return images;
        }
        ValueMap map = imagesResource.adaptTo(ValueMap.class);
        String order = map.get("order", String.class);
        if (order == null) {
            return images;
        }
        JSONArray array; 
        ValueMap vMap;
        try {
            array = new JSONArray(order);
        } catch (JSONException e)
        {
            array = new JSONArray();
        }
        for (int i = 0; i < array.length(); i++) {
            String imageResourceName;
            try {
                imageResourceName = array.getString(i);
            } catch (JSONException e) {
                imageResourceName = null;
            }

            if (imageResourceName != null) {
                Resource imageResource = imagesResource.getChild(imageResourceName);
                if (imageResource != null) {
                    SlideShowImage img = new SlideShowImage(imageResource);
                    images.add(img);
                }
            }
        }

        return images;
    }

%>
<%
    pageContext.setAttribute("images", getImages(resource, "images"));
%>
<cq:includeClientLib categories="hero-slideshow.components"/>
<c:choose>
    <c:when test="${empty images}">
        <%
            if(WCMMode.fromRequest(request) != WCMMode.PREVIEW){
        %>
                <BR>Add images using component dialog<BR><BR>
        <%
            }
        %>
    </c:when>
    <c:otherwise>
       <div>
            <div class="heroSlideShow">
               <%
               List<SlideShowImage> images =getImages(resource,"images");
               for(SlideShowImage slideShowImage : images){
            	   String src = slideShowImage.getSrc();
            	 %>
            	   <img src="<%=src %>" class="hide-for-small hide-for-medium"/>
                     <%   
                     Iterator<Resource> scrSizeImages = slideShowImage.listChildren();
            	     //Medium or Small Image
            	   while(scrSizeImages.hasNext())
            	   {
            		   Node srcSizeImage = scrSizeImages.next().adaptTo(Node.class);
                       try{
                    	   if(srcSizeImage.getName().equalsIgnoreCase("medium-image"))
                    		 %>  
                    		   <img src="<%=srcSizeImage.getProperty("fileReference").getString() %>" class="show-for-medium"/>
                    		<%
                    		if(srcSizeImage.getName().equalsIgnoreCase("small-image"))
                    			{
                    		%>
                    			<img src="<%=srcSizeImage.getProperty("fileReference").getString() %>" class="show-for-small"/>
                    			
                    		<%}
                    	 }catch(Exception e){}
            	   }
               } 
               %>
            </div>
        </div>
    </c:otherwise>
</c:choose>