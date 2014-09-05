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
    String FsbNewWindow = ((String)request.getAttribute("FsbNewWindow")).equals("true")?"target=_blank":"";
    
    if(!FsbUrl.isEmpty())
        FsbUrl = genLink(resourceResolver, FsbUrl);
    
    //Spring Board Second
    String SsbDesc = (String)request.getAttribute("SsbDesc");
    String SsbTitle = (String)request.getAttribute("SsbTitle");
    String SsbUrl = (String)request.getAttribute("SsbUrl");
    String SsbButton = (String)request.getAttribute("SsbButton");
    String SsbNewWindow = ((String)request.getAttribute("SsbNewWindow")).equals("true")?"target=_blank":"";
    
    if(!SsbUrl.isEmpty()){
    	SsbUrl = genLink(resourceResolver, SsbUrl);
    	}
 %>

<div class="view-c">
  <!-- hide-for-medium-down -->
 	<div id="heroBanner" class="row rightBannerHeight collapse">
		<div class="large-17 medium-24 small-24 columns">
			<div class="meow">
				<cq:include script="slideshow-image-include.jsp"/> 
			</div>
		</div>  
		<div class="large-7 medium-12 small-24 columns spHeight">
			<div class="row collapse">
				<div class="columns">
				 	<div class="call-desc">
			 				<p><%=FsbDesc %></p>
						</div>
					 	<div class="button-position">
							<p><a href="<%=FsbUrl%>" <%=FsbNewWindow %> class="button"><%=FsbTitle%></a></p>
			  			</div>	
					</div>
			</div>
		</div>
		<div class="large-7 medium-12 small-24 columns spHeight">
			<div class="row collapse">
				<div class="columns">
					<div class="call-desc">
						<p><%=SsbDesc %></p>
					</div>
					<div class="button-position">			
						<p><a href="<%=SsbUrl%>" <%=SsbNewWindow%> class="button"><%=SsbTitle%></a></p>
					</div> 		
				</div>
			</div>
		</div>
	</div>
</div><!-- end view-c -->

