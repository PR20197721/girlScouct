<%@include file="/libs/foundation/global.jsp"%>
<%
String[] links = properties.get("links", String[].class);
request.setAttribute("links", links);

%>
      
<div id="right-canvas-menu-bottom">
  <ul>
       <cq:include script="main.jsp"/>
 </ul>
</div> 
   




