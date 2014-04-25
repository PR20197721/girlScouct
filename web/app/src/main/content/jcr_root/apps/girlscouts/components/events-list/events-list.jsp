<%@ page
	import="com.day.cq.tagging.TagManager,java.util.ArrayList,java.util.HashSet,java.text.DateFormat,java.text.SimpleDateFormat,java.util.Date,
                 java.util.Locale,java.util.Arrays,java.util.Iterator,java.util.List,java.util.Set,com.day.cq.search.result.SearchResult,
                 java.util.ResourceBundle,com.day.cq.search.QueryBuilder,javax.jcr.PropertyIterator,org.girlscouts.web.events.search.SearchResultsInfo,
                 com.day.cq.i18n.I18n,org.apache.sling.api.resource.ResourceResolver,org.girlscouts.web.events.search.EventsSrch,org.girlscouts.web.events.search.FacetsInfo,java.util.Calendar,java.util.TimeZone"%>

<%@include file="/libs/foundation/global.jsp"%>
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
    DateFormat toFormat = new SimpleDateFormat("EEE dd MMM yyyy");
    List<String> results = srchInfo.getResults();

    int eventcounts = 0;
    String key = "";
    String value = "";
    if (properties.containsKey("eventcount")) {
		eventcounts = Integer.parseInt(properties.get("eventcount",
			String.class));
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
	</div>
</div>

<ul class="small-block-grid-1 medium-block-grid-1  large-block-grid-2 content">
<%
    for (int i = 0; i < eventcounts; i++) {
		value = results.get(i);
		Node node = resourceResolver.getResource(value).adaptTo( Node.class);
		String eventUrl = node.getPath() + ".html";
		Node propNode = node.getNode("jcr:content/data");
		String title = propNode.getProperty("jcr:title").getString();
		String href = value + ".html";
		String fromdate = propNode.getProperty("start").getString();
		String todate = "";
		Date tdt = null;
		if (propNode.hasProperty("end")) {
		    todate = propNode.getProperty("end").getString();
		    tdt = fromFormat.parse(todate);
		}
		String location = resourceResolver.getResource(propNode.getProperty("location").getString()).adaptTo(Page.class).getTitle();

		Date fdt = fromFormat.parse(fromdate);
		
		String eventTime = propNode.getProperty("time").getString();
		String fromDate = toFormat.format(fdt);
		String toDate = toFormat.format(tdt);
		String eventImg = propNode.getProperty("fileReference").getString();
%>
	<li>
		<div class="row">
			<div class="small-24 medium-12 large-8 columns">
				<img src="<%= eventImg %>" width="483" height="305" />
			</div>
			<div class="small-24 medium-12 large-16 columns">
				<h3>
					<a href="<%= eventUrl %>"><%= title %></a>
				</h3>
				<p>Time: <%= eventTime %></p>
				<p>Date: <%= fromDate %>
					<% if (!toDate.isEmpty()) { %>
						to <%= toDate %>
					<% } %>
				</p>
				<p>Location: <%= location %></p>
			</div>
		</div>
	</li>		
<% } %>
</ul>