<%@include file="/libs/foundation/global.jsp"%>
<%
String[] links = properties.get("links", String[].class);
request.setAttribute("links", links);

%>
       <cq:include script="main.jsp"/>
    
   




