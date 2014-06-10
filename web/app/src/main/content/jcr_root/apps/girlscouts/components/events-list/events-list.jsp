<%@ page import="com.day.cq.tagging.TagManager,java.util.ArrayList,
            java.util.HashSet,java.text.DateFormat,
            java.text.SimpleDateFormat,java.util.Date,
            java.util.Locale,java.util.Arrays,
            java.util.Iterator,java.util.List,
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

<%
	SearchResultsInfo srchInfo = (SearchResultsInfo) request.getAttribute("eventresults");
	if (null == srchInfo) {
%>
<cq:include script="/apps/girlscouts/components/event-search/event-search.jsp" />
<%
		srchInfo = (SearchResultsInfo) request.getAttribute("eventresults");
	}
	DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.S");
	fromFormat.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("GMT")));
	DateFormat dateFormat = new SimpleDateFormat("EEE dd MMM yyyy");
	DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
	List<String> results = srchInfo.getResults();
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
		<div class="small-24 medium-12 hide-for-large  hide-for-xlarge hide-for-xxlarge columns">
			<div class="feature-icon">
				<img src="<%= designPath %>/images/arrow-down.png" width="30" height="30"/>
			</div>
			<div class="feature-title">
				<h2><a href="<%= eventsLink %>"><%= featureTitle %></a></h2>
			</div>
		</div>
		<div class="medium-4 show-for-medium columns">&nbsp;</div>
	</div>
</div>
<ul class="small-block-grid-1 medium-block-grid-1  large-block-grid-2 content">
<%
     Date today = new Date();
     Calendar cal1 = Calendar.getInstance();
     cal1.setTime(today);
     int count = 0;
    // DateFormat timeFormat = new SimpleDateFormat("KK:mm a");
     
     for(String result: results){
    	 Node node = resourceResolver.getResource(result).adaptTo(Node.class);
    	 Node propNode = node.getNode("jcr:content/data");
         String fromdate = propNode.getProperty("start").getString();
         String title = propNode.getProperty("../jcr:title").getString();
         Date fdt = fromFormat.parse(fromdate);
         String href = result+".html";
         String time = "";
         String todate="";
         String toDate="";
         Date tdt = null;
         String locationLabel = "";
         if(fdt.equals(today) || fdt.after(today)){
        	 
        	 time = timeFormat.format(propNode.getProperty("start").getDate().getTime());
        	 
        	 if(propNode.hasProperty("locationLabel")){
                 locationLabel=propNode.getProperty("locationLabel").getString();
             }
             if(propNode.hasProperty("end")){
                 //toDate = dateFormat.format(propNode.getProperty("end").getDate());
                 tdt = fromFormat.parse(propNode.getProperty("end").getString());
                 toDate = dateFormat.format(tdt);
                 time+= " to " + timeFormat.format(propNode.getProperty("end").getDate().getTime());
             }
             
             String fromDate = dateFormat.format(fdt);
            
             boolean hasImage = false;
             String fileReference = null;
             count++;
           %> 
     <li>
        <div class="row">
            <div class="small-24 medium-12 large-8 columns">
            <%
            	String imgPath = node.getPath() + "/jcr:content/data/image";
%>
<%= displayRendition(resourceResolver, imgPath, "cq5dam.web.120.80")%>
            </div>
            <div class="small-24 medium-12 large-16 columns">
                <h3><a href="<%= href %>"><%= title %></a></h3>
                <p>Date: <%= fromDate %> <% if (!toDate.isEmpty()) { %> to <%= toDate %> <% } %> </p>
                <p>Location: <%= locationLabel %></p>
            </div>
        </div>
    </li>   
     <%}
      if(eventcounts==count)
      {
        	 break;
         }
     }
%>
</ul>
