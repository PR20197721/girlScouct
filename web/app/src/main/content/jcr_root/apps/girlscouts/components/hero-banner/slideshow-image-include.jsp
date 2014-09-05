 <%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
 <%!
   int slideShowCount=0;
   //int timer = 0;
  
  
%>
<%
	slideShowCount = Integer.parseInt(properties.get("slideshowcount", "1"));
	for(int i=1; i<slideShowCount+1;i++){
          String path = "./"+"Image_"+i;
         %>
          <div>        
            <cq:include path="<%=path%>" resourceType="girlscouts/components/hero-slideshow-images"/>  
          </div> 
		<%}%>

