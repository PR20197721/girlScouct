<%@ page import="org.girlscouts.web.constants.*,com.day.cq.wcm.api.WCMMode" %>


<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects/>
<%

   
   String title = properties.get("title","");
   String linkTitle = properties.get("pathfield","");
   String shortDesc = properties.get("shortdesc","");
   String isSpringBoard = properties.get("isspringboard", "");
   String fileReference = properties.get("fileReference", "");
   String imgWidth = properties.get("width", "");
   String imgHeight = properties.get("height", "");
   String imgAlt = properties.get("alt", "");
   String imgTitle = properties.get("imgtitle", "");
   String iconImage = properties.get("featureiconimage","");
 
   if (title.isEmpty() && WCMMode.fromRequest(request) == WCMMode.EDIT) {
		%>#### Double click here to configure #####<%
	}

%>  


 <div id="homeBlockGrid" class="large-8 medium-8 small-12 columns">
   <%if(iconImage!=null && iconImage.length() >0){ %>
        <br/> <img src="<%=iconImage%>"/>
    <%} %>
	<%if(title!=null && title.length() >0){ %>
	    <br/> <a href="<%=linkTitle%>"><%=title %></a>
	<%} %>
	<%if(shortDesc!=null && shortDesc.length() >0){ %>
	    <br/> <%=shortDesc %>
	<%} %>
	<%if(fileReference!=null && fileReference.length()>0){ %>
	   <img src="<%=fileReference%>" alt="<%if(imgAlt!=null && imgAlt.length()>0){%><%=imgAlt%><%}%>" width=<%=imgWidth%> height="<%=imgHeight%>"/>

	<%} %>
</div>