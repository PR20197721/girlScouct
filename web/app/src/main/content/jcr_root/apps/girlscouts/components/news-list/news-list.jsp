<%@ page import="com.day.cq.wcm.api.WCMMode,
                   com.day.cq.wcm.api.components.DropTarget,
                   com.day.cq.wcm.foundation.List,
                   java.util.Iterator"%><%
%><%@include file="/libs/foundation/global.jsp"%>
<cq:include script="init.jsp"/> 

<cq:include script="news-search.jsp"/>
    <%
     if(properties.containsKey("isonhomepage") && properties.get("isonhomepage").equals("true")){%>
    	  <cq:include script="feature-news.jsp"/>
    <%}else{
    	%><cq:include script="list-news.jsp"/>
    	
  <%   }   
%>
