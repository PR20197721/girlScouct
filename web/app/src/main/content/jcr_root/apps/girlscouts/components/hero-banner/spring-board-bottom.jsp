<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
 
 <%
 // Design 
	String designPath = currentDesign.getPath();
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
 <!-- display for medium-up -->
 <div class="row collapse panels-row">
 	<div class="large-12 medium-12 columns">
		<div id="SUP1" class="panel text-center toggled-off">
           <img id="rotate-img" src="<%= designPath %>/images/arrow-down.png">
            <h2><%=FsbTitle%></h2>
            <p><%=FsbDesc%></p>
            <a href="<%=FsbUrl %>" class="button"><%=FsbButton%></a>
         </div>
      </div>
      <div class="large-12 medium-12 columns">
         <div id="SUP2" class="panel text-center toggled-off">
            <img id="rotate-img" src="<%= designPath %>/images/arrow-down.png">
            <h2><%=SsbTitle%></h2>
            <p><%=SsbDesc%></p>
            <a href="<%=SsbUrl %>" class="button"><%=SsbButton%></a>
          </div>
      </div>
</div>