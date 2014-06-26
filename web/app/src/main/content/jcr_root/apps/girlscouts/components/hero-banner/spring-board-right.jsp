<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
 <%
 // Design 
	String designPath = currentDesign.getPath();
   //Spring Board First 
    String FsbDesc = (String)request.getAttribute("FsbDesc");
    String FsbTitle = (String)request.getAttribute("FsbTitle");
    String FsbUrl = (String)request.getAttribute("FsbUrl");
    String FsbButton = (String)request.getAttribute("FsbButton");
    
    if(!FsbUrl.isEmpty())
        FsbUrl = genLink(resourceResolver, FsbUrl);
    
    //Spring Board Second
    String SsbDesc = (String)request.getAttribute("SsbDesc");
    String SsbTitle = (String)request.getAttribute("SsbTitle");
    String SsbUrl = (String)request.getAttribute("SsbUrl");
    String SsbButton = (String)request.getAttribute("SsbButton");
    if(!SsbUrl.isEmpty()){
    	SsbUrl = genLink(resourceResolver, SsbUrl);
    	}
 %>

<div class="view-c">
  <!-- hide-for-medium-down -->
 	<div id="heroBanner" class="row collapse">
    	<div class="large-18 medium-24 columns">
    	   <div class="meow">
    	 	  <cq:include script="slideshow-image-include.jsp"/> 
    	   </div>
       	</div>  
		<div class="large-6 medium-11 columns">
      	 	<div class="row collapse">
        		<div class="columns">
          			<p><%=FsbDesc %></p>
          			<p><a href="<%=FsbUrl%>" class="button"><%=FsbTitle%></a></p>
        		</div>
      		</div>
      	</div>
      	<div class="large-6 medium-11 columns">
        	<div class="row collapse">
        		<div class="columns">
        			<p><%=SsbDesc %></p>
        	  		<p><a href="<%=SsbUrl%>" class="button"><%=SsbTitle%></a></p>
          		</div>
        	</div>
     	 </div>
      </div>
</div><!-- end view-c -->

