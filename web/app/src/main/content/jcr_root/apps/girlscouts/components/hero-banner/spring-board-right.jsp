<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
 
   //Spring Board First 
    String FsbDesc = properties.get("firstsbdesc","");
    String FsbTitle = properties.get("firstsbtitle","");
    String FsbUrl = properties.get("firstsburl","");
    String FsbButton = properties.get("firstsbbutton","");
    
    if(!FsbUrl.isEmpty())
        FsbUrl = genLink(resourceResolver, FsbUrl);
    
    //Spring Board Second
    String SsbDesc = properties.get("secondsbdesc","");
    String SsbTitle = properties.get("secondsbtitle","");
    String SsbUrl = properties.get("secondsburl","");
    String SsbButton = properties.get("secondsbbutton","");
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
