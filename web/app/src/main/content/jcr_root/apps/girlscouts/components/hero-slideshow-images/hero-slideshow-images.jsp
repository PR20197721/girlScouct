<%@page import="com.day.cq.wcm.foundation.Image,java.util.List,
java.util.Map,java.util.HashMap,java.util.Iterator,com.day.cq.wcm.api.WCMMode,
org.apache.sling.commons.json.JSONArray,org.apache.sling.commons.json.JSONException,com.day.cq.dam.api.Asset" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%!
    public String getPlaceHolderText(String text, String classes){
    	String placeHolder = "<div style=\"text-align:center; height:500px;\" class=\""+classes+" \" >"+ 
        					 "<p style=\"text-align: center\">"+text+"</p>"+"</div>";
		return placeHolder;
	};

%>  
<%
   Node imageNode = resource.adaptTo(Node.class);
   String largePath = "";
   String smallPath ="";
   String mediumPath ="";
   Asset assets=null;
   String newWindow = "";
   if(imageNode.hasProperty("newWindow")){
   	 newWindow = imageNode.getProperty("newWindow").getString();
   }
   if(null==newWindow || "".equals(newWindow)){
	 newWindow = "false";
   }
   Resource rendition=null;
  
if ((null==imageNode) && WCMMode.fromRequest(request) == WCMMode.EDIT) {
    %><%=getPlaceHolderText("Click edit above and select number of slides. Then click here to add images to slides.","")%>
   <% }
else if(imageNode!=null){
	String spplacement = (String)request.getAttribute("sbplacement");

	Iterator<Resource> images = resource.listChildren();
	String alt = "";
	String linkUrl = "";
	String sortOrder = "";
	if(imageNode.hasProperty("alt")){
			alt = imageNode.getProperty("alt").getString();
	}
	if(imageNode.hasProperty("linkURL")){
			linkUrl = imageNode.getProperty("linkURL").getString();
			if (!linkUrl.contains("://")) { // If it is not external link
			    linkUrl += ".html";
			}
	}
	if(imageNode.hasProperty("sortOrder")){
			sortOrder = imageNode.getProperty("sortOrder").getString();
	}
%>  
  
 <% 
 int i =0;
  String imgPath = "";
  while(images.hasNext()){  
	  
	Node imgNode = images.next().adaptTo(Node.class);
	String width = "960";
	String height="";
	String imgTag = "";
    String classes = "";
	if(imgNode.hasProperty("width")){
		width = imgNode.getProperty("width").getString();
	}
	if(imgNode.hasProperty("height")){
		width = imgNode.getProperty("height").getString();
	}
    if(imgNode.getProperty("imagesize").getString().equalsIgnoreCase("regular")){
        classes = "hide-for-small hide-for-medium";

		if(imgNode.hasProperty("fileReference")){
			largePath = imgNode.getProperty("fileReference").getString();
			%>
			<div>
                <%if(spplacement!=null && spplacement.equalsIgnoreCase("right")){ 
                	imgTag = displayRendition(resourceResolver, largePath, "cq5dam.web.665.365", classes, BREAKPOINT_MAX_LARGE,alt,null);
            	}else{
                	imgTag=displayRendition(resourceResolver, largePath, "cq5dam.web.960.420",classes, BREAKPOINT_MAX_LARGE,alt,null); 
            	}
            	if(imgTag!=null && !imgTag.isEmpty()){
                		if("true".equals(newWindow)){
                            %><a href="<%=linkUrl%>" target="_new"><%=imgTag%></a><%
                        } else{
							%><a href="<%=linkUrl%>"><%=imgTag%></a><%
                        }
            	}else if(WCMMode.fromRequest(request) == WCMMode.EDIT){
                %><%=getPlaceHolderText("Not able to find the image: "+largePath,classes)%>
                <%}%>
			</div> 
		<%}else if(WCMMode.fromRequest(request) == WCMMode.EDIT) {
		%>
                <%=getPlaceHolderText("Please click to add regular sized image.",classes)%>
		<% }
	}
	if(imgNode.getProperty("imagesize").getString().equalsIgnoreCase("medium")){
        classes = "show-for-medium";

		if(imgNode.hasProperty("fileReference")){
			mediumPath = imgNode.getProperty("fileReference").getString();
         %>  
			<div>    
					<%imgTag = displayRendition(resourceResolver, mediumPath, "cq5dam.web.720.420", classes, BREAKPOINT_MAX_MEDIUM,alt,null);
                    if(imgTag!=null && !imgTag.isEmpty()){

                        if("true".equals(newWindow)){
                            %><a href="<%=linkUrl%>" target="_new"><%=imgTag%></a><%
                        } else{
							%><a href="<%=linkUrl%>"><%=imgTag%></a><%
                        }
            		}else if(WCMMode.fromRequest(request) == WCMMode.EDIT){
                        %><%=getPlaceHolderText("Not able to find the image: "+mediumPath,classes)%>
                    <%}%>
			</div> 
	<%}else if(WCMMode.fromRequest(request) == WCMMode.EDIT) {
		%>
			<%=getPlaceHolderText("Please click to add medium screen sized image.",classes)%>
			
		<%}
	}
	if(imgNode.getProperty("imagesize").getString().equalsIgnoreCase("small")){
        classes = "show-for-small";
		if(imgNode.hasProperty("fileReference")){
			smallPath = imgNode.getProperty("fileReference").getString();
		%>  
			 <div>
					<%imgTag = displayRendition(resourceResolver, smallPath, "cq5dam.web.320.400", classes, BREAKPOINT_MAX_SMALL,alt,null);
                    if(imgTag!=null && !imgTag.isEmpty()){
                        if("true".equals(newWindow)){
                            %><a href="<%=linkUrl%>" target="_new"><%=imgTag%></a><%
                        } else{
							%><a href="<%=linkUrl%>"><%=imgTag%></a><%
                        }
            		}else if(WCMMode.fromRequest(request) == WCMMode.EDIT){
                        %><%=getPlaceHolderText("Not able to find the image: "+smallPath, classes)%>
                    <%}%>
			</div>  
		<%}else if(WCMMode.fromRequest(request) == WCMMode.EDIT) {
			%>
 				<%=getPlaceHolderText("Please click to add small screen sized image.", classes)%>
		<% }
	}
%>   
<% }  
   }//else
    %>
  
