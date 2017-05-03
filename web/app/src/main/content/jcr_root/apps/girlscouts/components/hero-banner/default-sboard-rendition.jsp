<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>

<% String sbplacement = properties.get("spplacement","");
	request.setAttribute("sbplacement",sbplacement);

%>
<div class="view-b">
	<div id="heroBanner" class="large-24 columns">
   		<div class="meow">
        	<cq:include script="slideshow-display.jsp"/>   
     	</div>
<%if(sbplacement.equals("bottom")) {%>
     <cq:include script="spring-board-bottom.jsp"/>
 <%} %>  
	</div>  
</div>


