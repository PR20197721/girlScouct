<%@ page import="com.day.cq.tagging.TagManager,java.util.ArrayList,
            java.util.HashSet,java.text.DateFormat,
            java.text.SimpleDateFormat,java.util.Date,
            java.util.Locale,java.util.Arrays,
            java.util.Iterator,
            java.util.Set,com.day.cq.search.result.SearchResult,
            java.util.ResourceBundle,com.day.cq.search.QueryBuilder,
            javax.jcr.PropertyIterator,org.girlscouts.web.events.search.SearchResultsInfo,
            com.day.cq.i18n.I18n,org.apache.sling.api.resource.ResourceResolver,
            org.girlscouts.web.events.search.EventsSrch,org.girlscouts.web.events.search.FacetsInfo,java.util.Calendar,java.util.TimeZone,com.day.cq.dam.api.Asset"%>

<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<!-- apps/girlscouts/components/events-list/events-list.jsp -->
<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects />

<cq:include script="feature-include.jsp"/>
<%
	SearchResultsInfo srchInfo = (SearchResultsInfo) request.getAttribute("eventresults");
	if (null == srchInfo) {
%>
<cq:include script="/apps/girlscouts/components/event-search/event-search.jsp" />
<%
		srchInfo = (SearchResultsInfo) request.getAttribute("eventresults");
}
	
%>	
	
<% 	
	DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.S");
	fromFormat.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("GMT")));
	DateFormat dateFormat = new SimpleDateFormat("EEE MMM d yyyy");
	DateFormat timeFormat = new SimpleDateFormat("h:mm a");
	
	Date today = new Date();
	DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	String evtStartDt = formatter.format(today);
	try{
		today = formatter.parse(evtStartDt);
		
	}catch(Exception e){}

	
	DateFormat calendarFormat = new SimpleDateFormat("M-yyyy");
	java.util.List<String> results = srchInfo.getResults();
	int eventcounts = 0;
	String key = "";
	String value = "";
	if (properties.containsKey("eventcount")) {
		eventcounts = Integer.parseInt(properties.get("eventcount", String.class));
		if (eventcounts > results.size()) {
			eventcounts = results.size();
		}
	}
	String designPath = currentDesign.getPath();
	String iconImg = properties.get("fileReference", String.class);
	String eventsLink = properties.get("urltolink", "") + ".html";
	String featureTitle = properties.get("featuretitle", "UPCOMING EVENTS");
	Date startDate = null; 
      
    String startDateStr = "";
    String startTimeStr = "";
    String imgPath="";
    String iconPath="";
    String href="";
    String title="";
    String dateStr="";
    String locationLabel="";
    
    /*Date today = new Date();
    Calendar cal1 = Calendar.getInstance();
    cal1.setTime(today);
    cal1.add(Calendar.DAY_OF_MONTH, -1);
	today = cal1.getTime();*/
%>

<div class="small-24 medium-24 large-24 columns events-section">
	<div class="row">
		<div class="hide-for-small hide-for-medium large-24 columns featureHeader">
			<div class="feature-icon">
				<img src="<%= iconImg %>" width="50" height="50" alt="feature icon"/>
			</div>
			<div class="feature-title">
				<h2><a href="<%= eventsLink %>"><%= featureTitle %></a></h2>
			</div>
		</div>
		<div class="small-24 medium-24 hide-for-large  hide-for-xlarge hide-for-xxlarge columns featureSmallHeader">
			<div class="feature-icon">
				<img src="<%= iconImg %>" width="50" height="50" alt="feature icon"/>
			</div>
			<div class="feature-title">
				<h2><a href="<%= eventsLink %>"><%= featureTitle %></a></h2>
			</div>
		</div>
	</div>
<ul class="small-block-grid-1 medium-block-grid-2  large-block-grid-2 content events-block">
<%
	com.day.cq.wcm.foundation.List elist= (com.day.cq.wcm.foundation.List)request.getAttribute("elist");
    Set<String> featureEvents =(HashSet) request.getAttribute("featureEvents");
    


	if (!elist.isEmpty()){
	   
	
		Iterator<Page> items = elist.getPages();
		
		while (items.hasNext()){
			Page item = (Page)items.next();
			href=item.getPath()+".html";
			Node node = item.getContentResource().adaptTo(Node.class);
			try{
				Node propNode = node.getNode("data");
 		 		String fromdate = propNode.getProperty("start").getString();
  				title = propNode.getProperty("../jcr:title").getString();
  				dateStr="";
				imgPath="";
				iconPath=""; 
				startDate = propNode.getProperty("start").getDate().getTime(); 
       			startDateStr = dateFormat.format(startDate);
            	startTimeStr = timeFormat.format(startDate);

            	dateStr = startDateStr + ", " +startTimeStr;
            	
            	imgPath = node.getPath()+"/data/image";
                iconPath=propNode.hasProperty("image/fileReference") ? propNode.getProperty("image/fileReference").getString() : "";
				if(propNode.hasProperty("locationLabel")){
                	locationLabel=propNode.getProperty("locationLabel").getString();
           		 }
       	 
       	 		if (propNode.hasProperty("end")){
       		    	Date endDate = propNode.getProperty("end").getDate().getTime();
       		    	dateStr = getDateTime(startDate,endDate,dateFormat,timeFormat,dateStr);
       		  	}
       	 		
       	 	request.setAttribute("href", href);
            request.setAttribute("title", title);
            request.setAttribute("dateStr", dateStr);
            request.setAttribute("locationLabel", locationLabel);
            request.setAttribute("iconPath", iconPath);
            request.setAttribute("imgPath", imgPath);
       	 		
       	 	%>	<cq:include script="event-render.jsp"/>
			<%}catch(Exception e){
			}
		}
	}


%>
<%
    
     int count = 0;
if(eventcounts>0){
  
	for(String result: results){
	Node node = resourceResolver.getResource(result).adaptTo(Node.class);
	
	Date fromdate = null;
    try{
		Node propNode = node.getNode("jcr:content/data");
		if(propNode.hasProperty("end")){
			fromdate = propNode.getProperty("end").getDate().getTime();
			
		}else if(propNode.hasProperty("start")){
			fromdate = propNode.getProperty("start").getDate().getTime();
			
		}
 	 	
  		title = propNode.getProperty("../jcr:title").getString();
  		try{
			String eventDt = formatter.format(fromdate);
			fromdate = formatter.parse(eventDt);
		}catch(Exception e){}
		href = result+".html";
		String time = "";
		String todate="";
    	String toDate="";
		dateStr="";
		imgPath="";
		iconPath="";  		 
		Date tdt = null;
		locationLabel = "";
      	if(fromdate.after(today) || fromdate.equals(today)){
			startDate = propNode.getProperty("start").getDate().getTime(); 
     	 	startDateStr = dateFormat.format(startDate);
          	startTimeStr = timeFormat.format(startDate);
          	dateStr = startDateStr + ", " +startTimeStr;
     	 	time = startTimeStr;
            if(propNode.hasProperty("locationLabel")){
                 locationLabel=propNode.getProperty("locationLabel").getString();
             }
        	if (propNode.hasProperty("end")){
        		 Date endDate = propNode.getProperty("end").getDate().getTime();
        		 dateStr = getDateTime(startDate,endDate,dateFormat,timeFormat,dateStr);
        		    
        	 }
             
             boolean hasImage = false;
             String fileReference = null;
             imgPath = node.getPath()+"/jcr:content/data/image";
            
             iconPath=node.hasProperty("jcr:content/data/image/fileReference") ? node.getProperty("jcr:content/data/image/fileReference").getString() : "";
            
             request.setAttribute("href", href);
             request.setAttribute("title", title);
             request.setAttribute("dateStr", dateStr);
             request.setAttribute("locationLabel", locationLabel);
             request.setAttribute("iconPath", iconPath);
             request.setAttribute("imgPath", imgPath);
             if(!featureEvents.contains(result)){   
            	 count++;
           %> 
          		<cq:include script="event-render.jsp"/>
     <%
     		}
        }
      if(eventcounts==count)
      {
        	 break;
         }
    	 }catch(Exception e){} 
     }
 }  
%>
</ul>
</div>


<%!
public static String getDateTime(Date startDate, Date endDate,DateFormat dateFormat,DateFormat timeFormat,String dateStr){
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
 	   }else
 	   {
 		   dateStr += " - " + endTimeStr;

 		}
	return dateStr;
}


%>
