<%@ page import="com.day.cq.tagging.TagManager,com.day.cq.dam.api.Asset,java.util.ArrayList,java.util.HashSet,java.text.DateFormat,java.text.SimpleDateFormat,java.util.Date, java.util.Locale,java.util.Map,java.util.Iterator,java.util.HashMap,java.util.List,java.util.Set,com.day.cq.search.result.SearchResult, java.util.ResourceBundle,com.day.cq.search.QueryBuilder,javax.jcr.PropertyIterator,org.girlscouts.web.events.search.SearchResultsInfo, com.day.cq.i18n.I18n,org.apache.sling.api.resource.ResourceResolver,org.girlscouts.web.events.search.EventsSrch,org.girlscouts.web.events.search.FacetsInfo,java.util.Calendar,java.util.TimeZone" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects/>
<% 
DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.S");
fromFormat.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("GMT")));
DateFormat toFormat = new SimpleDateFormat("EEE dd MMM yyyy");
SearchResultsInfo srchInfo = (SearchResultsInfo)request.getAttribute("eventresults");
if(null==srchInfo) {
%>
<cq:include path="content/middle/par/event-search" resourceType="girlscouts/components/event-search" />
<%  
}
srchInfo =  (SearchResultsInfo)request.getAttribute("eventresults");
List<String> results = srchInfo.getResults();
long hitCounts = srchInfo.getHitCounts();
SearchResult searchResults = (SearchResult)request.getAttribute("searchResults");
String q = request.getParameter("q");
if(properties.containsKey("isfeatureevents") && properties.get("isfeatureevents").equals("on") ){
%> 
<cq:include script="feature-events.jsp"/>
<%   
} else{
%> 

<%

    int tempMonth =0;
    for(String result: results)
    {
		Node node =  resourceResolver.getResource(result).adaptTo(Node.class);
		Node propNode = node.getNode("jcr:content/data");
		String fromdate = propNode.getProperty("start").getString();
		String title = propNode.getProperty("../jcr:title").getString();
		String href = result+".html";
		String time = "";
		String todate="";
		Date tdt = null;
		String locationLabel = "";
		Date today = new Date();
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(today);
		cal1.add(Calendar.DAY_OF_MONTH, +61);
		Date after60days = cal1.getTime();
		if(propNode.hasProperty("time"))
		{
		   time = propNode.getProperty("time").getString();	
		}
		if(propNode.hasProperty("locationLabel")){
			locationLabel=propNode.getProperty("locationLabel").getString();
		}
		if(propNode.hasProperty("end")){
			todate = propNode.getProperty("end").getString();
			tdt = fromFormat.parse(todate);
		}
		String details = propNode.getProperty("details").getString();
		Date fdt = fromFormat.parse(fromdate);
		Calendar cal = Calendar.getInstance();
		cal.setTime(fdt);
		int month = cal.get(Calendar.MONTH);
	
		if(fdt.after(today) && fdt.before(after60days))
		{
			if(tempMonth!=month)
			  {
				Date d = new Date(cal.getTimeInMillis());
		        String monthName = new SimpleDateFormat("MMMM").format(d);
	    	    String yr = new SimpleDateFormat("yyyy").format(d);
		        tempMonth = month;
		      %>
		      
		    <div class="row">
		       <div class="sma11-24 large-24 medium-24 column" style="padding:15px 0px 0px 10px">
		            <%=monthName %>  <%=yr %><hr/>
		       </div>
		       
		       
		      
		    </div>   
        <% } %>

    <div class=row>
      <div class="small-6 medium-6 large-6 columns">
              <div id="left">
              	<%
              		String imgPath = propNode.getPath() + "/image";
              		displayRendition(resourceResolver, imgPath, "cq5dam.web.120.80", pageContext); 
              	%>
             </div>  
     </div>
      
       <div class="small-18 medium-18 large-18 columns">
          <h4><a href="<%=href%>"><%=title %></a></h4>
         <div class="date">
             <b>Date :</b> <%=toFormat.format(fdt)%> <%if(propNode.hasProperty("end")) {%> to <%=toFormat.format(tdt) %> <%}%>
         </div>
         <%if(!locationLabel.isEmpty()){ %>
           <div class="locationLabel">
              <b>Location: </b><%=locationLabel %>
           </div>
         <%} %>
       </div>
    </div> 
   <div class="row">
         <div class="small-24 large-24 medium-24 columns">
          <p><%=details%></p>
        </div>
     </div>     

  

  
    
<%
   }//if
 }//else
%>

<%
}//for
%>