<%@ page import="java.util.Map,org.girlscouts.web.events.search.SearchResultsInfo" %>

<%@include file="/libs/foundation/global.jsp"%>

<%
  SearchResultsInfo srchInfo = (SearchResultsInfo)request.getAttribute("results");
  Map<String, String> results = srchInfo.getResults();
  long hitCounts = srchInfo.getHitCounts();
 %>
Total Count <%=hitCounts%>
 <%
    
     for(Map.Entry<String,String> result: results.entrySet()){
    %>
    <div>
      <a href="<%=result.getValue()%>"><%=result.getKey() %></a>    
   </div>    
     <%}%>