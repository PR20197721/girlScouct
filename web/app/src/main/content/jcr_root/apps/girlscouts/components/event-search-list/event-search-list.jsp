
<%@ page import="com.day.cq.tagging.TagManager,java.util.ArrayList,java.util.HashSet,java.text.DateFormat,java.text.SimpleDateFormat,java.util.Date,
                 java.util.Locale,java.util.Map,java.util.Iterator,java.util.HashMap,java.util.List,java.util.Set,com.day.cq.search.result.SearchResult,
                 java.util.ResourceBundle,com.day.cq.search.QueryBuilder,javax.jcr.PropertyIterator,org.girlscouts.web.events.search.SearchResultsInfo,
                 com.day.cq.i18n.I18n,org.apache.sling.api.resource.ResourceResolver,org.girlscouts.web.events.search.EventsSrch,org.girlscouts.web.events.search.FacetsInfo,java.util.Calendar,java.util.TimeZone" %>

<%@include file="/libs/foundation/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects/>
 
<% 
  DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.S");
  fromFormat.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("GMT")));
  DateFormat toFormat = new SimpleDateFormat("EEE dd MMM yyyy");
  SearchResultsInfo srchInfo = (SearchResultsInfo)request.getAttribute("eventresults");
  if(null==srchInfo)
  {
%>
    <cq:include path="content/middle/par/event-search" resourceType="girlscouts/components/event-search" />
<%  
      
  }
  srchInfo =  (SearchResultsInfo)request.getAttribute("eventresults");
    

%>

<%


    Map<String, String> results = srchInfo.getResults();
    long hitCounts = srchInfo.getHitCounts();
    SearchResult searchResults = (SearchResult)request.getAttribute("searchResults");
    String q = request.getParameter("q");

%>
<%
    if(properties.containsKey("isfeatureevents") && properties.get("isfeatureevents").equals("on") ){
    
%> 
      <cq:include script="feature-events.jsp"/>

<%   

    } else{

%> 



    

<div>
     Total Counts <%=hitCounts%> 
   <%
      for(Map.Entry<String,String> result: results.entrySet()){
    	 Node node =  resourceResolver.getResource(result.getValue()).adaptTo(Node.class);
    	 Node propNode = node.getNode("jcr:content/data");
    	 String title = propNode.getProperty("jcr:title").getString();
    	 String href = result.getValue()+".html";
    	 String fromdate = propNode.getProperty("start").getString();
    	 String todate="";
    	 Date tdt = null;
    	 if(propNode.hasProperty("end")){
    	 	 todate = propNode.getProperty("end").getString();
    	 	 tdt = fromFormat.parse(todate);
    	 }
    	 String details = propNode.getProperty("srchdisp").getString();
    	 Date fdt = fromFormat.parse(fromdate);
    	
    %>
      <div>
            <a href="<%=href%>"><%=title %></a>
            <p><%=details%></p>
            <p>Date : <%=toFormat.format(fdt)%> <%if(propNode.hasProperty("end")) {%> to <%=toFormat.format(tdt) %> <%}%></p>   
      </div>    
     <%}%>
</div>
    
<div id="pagination">
   <%if(searchResults.getResultPages().size() > 1) 
      { 
	    for(int i=0;i<searchResults.getResultPages().size(); i++)
         {
	    	long offst = i*10;
            if(searchResults.getResultPages().get(i).isCurrentPage()){%>
                 <%=i+1 %>
             <%}
            else{
             if(q!=null){%>
                <a x-cq-linkchecker="skip" href="<%=currentPage.getPath()%>.html?q=<%=q%>&offset=<%=offst%>"><%=i+1%></a>
              <%}else{%>
                 <a x-cq-linkchecker="skip" href="<%=currentPage.getPath()%>.html?offset=<%=offst%>"><%=i+1%></a>
              <%}%>
                  
                       
                <%}
              }
            
          }  
        %>
 </div>
<%} %>
