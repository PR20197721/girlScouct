<%@ page import="com.day.cq.tagging.TagManager,java.util.ArrayList,java.util.HashSet,java.text.DateFormat,java.text.SimpleDateFormat,java.util.Date,
                 java.util.Locale,java.util.Map,java.util.Arrays,java.util.Iterator,java.util.HashMap,java.util.List,java.util.Set,com.day.cq.search.result.SearchResult,
                 java.util.ResourceBundle,com.day.cq.search.QueryBuilder,javax.jcr.PropertyIterator,org.girlscouts.web.events.search.SearchResultsInfo,
                 com.day.cq.i18n.I18n,org.apache.sling.api.resource.ResourceResolver,org.girlscouts.web.events.search.EventsSrch,org.girlscouts.web.events.search.FacetsInfo" %>

<%@include file="/libs/foundation/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects/>
<% 
  DateFormat fromFormat = new SimpleDateFormat("mm/dd/yy");
  DateFormat toFormat = new SimpleDateFormat("EEE dd MMM yyyy");
  SearchResultsInfo srchInfo = (SearchResultsInfo)request.getAttribute("eventresults");
  Map<String, String> results = srchInfo.getResults();

  int eventcounts = 0;
  String key="";
  String value="";
  System.out.println(properties.get("eventcount"));
  if(properties.containsKey("eventcount")){
	  eventcounts =  Integer.parseInt(properties.get("eventcount",String.class));
	  if(eventcounts > results.size()){
		  eventcounts = results.size();
	  }
  }
  System.out.println(eventcounts);
  
  Set<String>set = results.keySet();
  
  Object[] keys = set.toArray();
  
  
  %>
  <div>
            <img src="<%=properties.get("fileReference", String.class)%>"/><%=properties.get("featuretitle",String.class) %>
  </div>
  
  <% 
  
  for(int i=0;i<eventcounts;i++){
	 key =(String) keys[i];
	 value = results.get(key);
	 System.out.println("key"  +key + "value" +value);
	 Node node = resourceResolver.getResource(value).adaptTo(Node.class);
	 Node propNode = node.getNode("jcr:content/content/middle/par/event");
     String title = propNode.getProperty("title").getString();
     String href = value+".html";
     String time = propNode.getProperty("time").getString();
     String fromdate = propNode.getProperty("fromdate").getString();
     String todate="";
     Date tdt = null;
     if(propNode.hasProperty("todate")){
         todate = propNode.getProperty("todate").getString();
         tdt = fromFormat.parse(todate);
     }
     String location = resourceResolver.getResource(propNode.getProperty("location").getString()).adaptTo(Page.class).getTitle();
     
     Date fdt = fromFormat.parse(fromdate);
	  
  
    
%>
  <div>
        <p>
             <a href="<%=href%>"><%=title %></a>
           <br/> Time :<%=time%> 
           <br/> Date : <%=toFormat.format(fdt)%> <%if(propNode.hasProperty("todate")) {%> to <%=toFormat.format(tdt) %> <%}%>  
           <br/> Location : <%=location %>
        </p>
  </div> 
  
     
 <%}%>
  <div>
    <%
      if(properties.containsKey("urltolink")){
    %>	  
    	<a href="<%=properties.get("urltolink")%>.html"><%=properties.get("linktext")%></a>
    	  
     <% }
    
    %>
  
  </div>
  
  





