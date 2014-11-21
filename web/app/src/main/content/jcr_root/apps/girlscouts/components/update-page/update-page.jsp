<%@include file="/libs/foundation/global.jsp"%>
<%@ page session="false" %>
<cq:include script="/libs/wcm/core/components/init/init.jsp"/>

<%
    String date = properties.get("date","");
String title = properties.get("jcr:title","title placeholder");
  	String text = properties.get("text","no description");
%>
<%=date %>
<%=title %>
<%=text %>


     
