<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
 
 <%
 // Design 
    final String designPath = "/etc/designs/girlscouts";
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
 <!-- display for medium-up -->
 <div class="row collapse panels-row">
 	<div class="large-12 medium-12 columns">
		<div id="SUP1" class="panel text-center toggled-off">
           <img class="rotate-img" src="<%= designPath %>/images/arrow-down.png" alt="springboard rotate image down"/>
            <h2><%=FsbTitle%></h2>
            <p><%=FsbDesc%></p>
            <a href="<%=FsbUrl %>" <%=FsbNewWindow %> class="button"><%=FsbButton%></a>
         </div>
      </div>
      <div class="large-12 medium-12 columns">
         <div id="SUP2" class="panel text-center toggled-off">
            <img class="rotate-img" src="<%= designPath %>/images/arrow-down.png" alt="springboard rotate image up" />
            <h2><%=SsbTitle%></h2>
            <p><%=SsbDesc%></p>
            <a href="<%=SsbUrl %>" <%=SsbNewWindow %> class="button"><%=SsbButton%></a>
          </div>
      </div>
</div>
