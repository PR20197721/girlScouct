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
	
	DateFormat calendarFormat = new SimpleDateFormat("M-yyyy");
	java.util.List<String> results = srchInfo.getResults();
	
	System.out.println("#########################List" +results.size());
	
	
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
    
    Date today = new Date();
    Calendar cal1 = Calendar.getInstance();
    cal1.setTime(today);
%>

<div class="small-24 medium-24 large-24 columns">
	<div class="row">
		<div class="hide-for-small hide-for-medium large-24 columns">
			<div class="feature-icon">
				<img src="<%= iconImg %>" width="50" height="50"/>
			</div>
			<div class="feature-title">
				<h2><a href="<%= eventsLink %>"><%= featureTitle %></a></h2>
			</div>
		</div>
		<div class="medium-8 show-for-medium columns">&nbsp;</div>
		<div class="small-24 medium-24 hide-for-large  hide-for-xlarge hide-for-xxlarge columns">
			<div class="feature-icon">
				<img src="<%= iconImg %>" width="50" height="50"/>
			</div>
			<div class="feature-title">
				<h2><a href="<%= eventsLink %>"><%= featureTitle %></a></h2>
			</div>
		</div>
		<div class="medium-4 show-for-medium columns">&nbsp;</div>
	</div>
</div>
<ul class="small-block-grid-1 medium-block-grid-1  large-block-grid-2 content" style="
    padding-right: 12px;
    margin-left: 20px;
    margin-right: 20px;
    padding-left: 12px;
">
<%
	com.day.cq.wcm.foundation.List elist= (com.day.cq.wcm.foundation.List)request.getAttribute("elist");

	if (!elist.isEmpty()){
		Iterator<Page> items = elist.getPages();
		
		while (items.hasNext()){
			Page item = (Page)items.next();
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
     for(String result: results){
    	 Node node = resourceResolver.getResource(result).adaptTo(Node.class);
    	 try{
			Node propNode = node.getNode("jcr:content/data");
 		 	String fromdate = propNode.getProperty("start").getString();
  			title = propNode.getProperty("../jcr:title").getString();
  			Date fdt = fromFormat.parse(fromdate);
 			href = result+".html";
  			String time = "";
  			String todate="";
 		    String toDate="";
			dateStr="";
			imgPath="";
			iconPath="";  		 
			Date tdt = null;
  			locationLabel = "";
         	if(fdt.equals(today) || fdt.after(today)){
        	 startDate = propNode.getProperty("start").getDate().getTime(); 
        	 startDateStr = dateFormat.format(startDate);
             startTimeStr = timeFormat.format(startDate);
             dateStr = startDateStr + ", " +startTimeStr;
        	 time = startTimeStr;
             
        	 if(propNode.hasProperty("locationLabel")){
                 locationLabel=propNode.getProperty("locationLabel").getString();
             }
        	 
        	 if (propNode.hasProperty("end"))
        	   {
        		    Date endDate = propNode.getProperty("end").getDate().getTime();
        		    dateStr = getDateTime(startDate,endDate,dateFormat,timeFormat,dateStr);
        		    
        	    }
             
             boolean hasImage = false;
             String fileReference = null;
             imgPath = node.getPath()+"/jcr:content/data/image";
            
             iconPath=node.hasProperty("jcr:content/data/image/fileReference") ? node.getProperty("jcr:content/data/image/fileReference").getString() : "";
             count++;
             request.setAttribute("href", href);
             request.setAttribute("title", title);
             request.setAttribute("dateStr", dateStr);
             request.setAttribute("locationLabel", locationLabel);
             request.setAttribute("iconPath", iconPath);
             request.setAttribute("imgPath", imgPath);
               
           %> 
          <cq:include script="event-render.jsp"/>
     <%}
      if(eventcounts==count)
      {
        	 break;
         }
    	 }catch(Exception e){} 
     }
%>
</ul>
<%


if (!elist.isEmpty()){
	   Iterator<Page> itemslist = elist.getPages();
	   while(itemslist.hasNext()){
		   Page pg = itemslist.next();
		   if(pg.getProperties().containsKey("isFeature")){
			    Node node = pg.getContentResource().adaptTo(Node.class);
			    node.setProperty("isFeature", false);
			    node.save();
			 }
		}
}  


%>

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
