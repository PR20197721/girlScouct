<%@ page import="com.day.cq.tagging.TagManager,com.day.cq.dam.api.Asset,java.util.ArrayList,java.util.HashSet,java.text.DateFormat,java.text.SimpleDateFormat,java.util.Date, java.util.Locale,java.util.Map,java.util.Iterator,java.util.HashMap,java.util.List,java.util.Set,com.day.cq.search.result.SearchResult, java.util.ResourceBundle,com.day.cq.search.QueryBuilder,javax.jcr.PropertyIterator,org.girlscouts.web.events.search.SearchResultsInfo, com.day.cq.i18n.I18n,org.apache.sling.api.resource.ResourceResolver,org.girlscouts.web.events.search.EventsSrch,org.girlscouts.web.events.search.FacetsInfo,java.util.Calendar,java.util.TimeZone" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects/>
<% 
DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.S");
fromFormat.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("GMT")));
DateFormat dateFormat = new SimpleDateFormat("EEE MMM d yyyy");
DateFormat timeFormat = new SimpleDateFormat("h:mm a");
DateFormat toFormat = new SimpleDateFormat("EEE dd MMM yyyy");

Date today = new Date();
DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
String evtStartDt = formatter.format(today);
try{
	today = formatter.parse(evtStartDt);
}catch(Exception e){}


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
    
	<div id="eventListWrapper">
<%
	int tempMonth =-1;
	if (results == null || results.size() == 0) {
%>
	<p>No event search results for &quot;<i class="error"><%= q %></i>&quot;.</p>
<%
	} else {
		for(String result: results) {
			Date evntComparsion = null;
			Node node =  resourceResolver.getResource(result).adaptTo(Node.class);
			try {
				Node propNode = node.getNode("jcr:content/data");
				Date startDate = propNode.getProperty("start").getDate().getTime();
				if(propNode.hasProperty("end")){
					evntComparsion = propNode.getProperty("end").getDate().getTime();
				}else if(propNode.hasProperty("start")){
					evntComparsion = propNode.getProperty("start").getDate().getTime();
				}
				String title = propNode.getProperty("../jcr:title").getString();
				String href = result+".html";
				String time = "";
				String todate="";
				Date tdt = null;
				String locationLabel = "";
				
				String startDateStr = dateFormat.format(startDate);
				String startTimeStr = timeFormat.format(startDate);
				String dateStr = startDateStr + ", " +startTimeStr;

				if(propNode.hasProperty("locationLabel")){
					locationLabel=propNode.getProperty("locationLabel").getString();
				}
				if(propNode.hasProperty("end")){
					Date endDate = propNode.getProperty("end").getDate().getTime();
					Calendar cal2 = Calendar.getInstance();
					Calendar cal3 = Calendar.getInstance();
					cal2.setTime(startDate);
					cal3.setTime(endDate);
					boolean sameDay = cal2.get(Calendar.YEAR) == cal3.get(Calendar.YEAR) &&
					cal2.get(Calendar.DAY_OF_YEAR) == cal3.get(Calendar.DAY_OF_YEAR);
					String endDateStr = dateFormat.format(endDate);
					String endTimeStr = timeFormat.format(endDate);
					if (!sameDay) {
						dateStr += " - " + endDateStr +", " + endTimeStr;
					}else {
						dateStr += " - " + endTimeStr;
					}
					todate = propNode.getProperty("end").getString();
					tdt = fromFormat.parse(todate);
				}
				String details = propNode.getProperty("details").getString();
				//Date fdt = fromFormat.parse(startDate);
				Calendar cal = Calendar.getInstance();
				cal.setTime(startDate);
				int month = cal.get(Calendar.MONTH);
				
				try{
					String eventDt = formatter.format(evntComparsion);
					evntComparsion = formatter.parse(eventDt);
				}catch(Exception e){}
				if(evntComparsion.after(today) || evntComparsion.equals(today)) {
					
					if(tempMonth!=month) {
						Date d = new Date(cal.getTimeInMillis());
						String monthName = new SimpleDateFormat("MMMM").format(d);
						String yr = new SimpleDateFormat("yyyy").format(d);
						tempMonth = month;
%>
		<div class="eventsList monthSection">
			<div class="leftCol"><b><%=monthName.toUpperCase() %>&nbsp;<%=yr %></b></div>
			<div class="rightCol horizontalRule">&nbsp;</div>
		</div>
		<br/>
		<br/>
<%
					}
%>

		<div class="eventsList eventSection">
			<div class="leftCol">
<%
				String imgPath = propNode.getPath() + "/image";
%>
<%= displayRendition(resourceResolver, imgPath, "cq5dam.web.120.80") %>
			</div>
			<div class="rightCol">
				<h6><a class="bold" href="<%=href%>"><%=title %></a></h6>
				<p class="bold">Date: <%=dateStr%></p>
<%if(!locationLabel.isEmpty()){ %>
				<p class="bold">Location: <%=locationLabel %></p>
<% } %>
<% if(propNode.hasProperty("srchdisp")){ %>
				<p><%=propNode.getProperty("srchdisp").getString()%></p>
<% } %>

			</div>
		</div>
		<div class="eventsList bottomPadding"></div>
<%
				}
			} catch(Exception e){
			}
		}
	}
%>
	</div>
<%
}
%>

