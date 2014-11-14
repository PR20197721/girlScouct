<%@include file="/libs/foundation/global.jsp"%>
<%@ page session="false" %>

<%
    String date = properties.get("date","");
  	String title = properties.get("title","title placeholder");
  	String text = properties.get("text","no description");
%>
<%=date %>
<%=title %>
<%=text %>


     
