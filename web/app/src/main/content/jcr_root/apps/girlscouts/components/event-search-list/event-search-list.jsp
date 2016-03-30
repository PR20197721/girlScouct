<%@ page import="com.day.cq.tagging.TagManager,com.day.cq.dam.api.Asset,java.util.ArrayList,java.util.HashSet,java.text.DateFormat,java.text.SimpleDateFormat,java.util.Date, java.util.Locale,java.util.Map,java.util.Iterator,java.util.HashMap,java.util.List,java.util.Set,com.day.cq.search.result.SearchResult, java.util.ResourceBundle,com.day.cq.search.QueryBuilder,javax.jcr.PropertyIterator,org.girlscouts.web.events.search.SearchResultsInfo, com.day.cq.i18n.I18n,org.apache.sling.api.resource.ResourceResolver,org.girlscouts.web.events.search.EventsSrch,org.girlscouts.web.events.search.FacetsInfo,java.util.Calendar,java.util.TimeZone,
    org.girlscouts.vtk.utils.VtkUtil,
	org.girlscouts.vtk.models.User,
	javax.servlet.http.HttpSession,
	com.day.text.Text,
	org.joda.time.DateTimeZone,
	org.joda.time.format.DateTimeFormatter,
	org.joda.time.format.DateTimeFormat,
	org.joda.time.DateTime,
	org.joda.time.DateTimeZone,
	org.joda.time.DateTimeUtils,
	java.util.TimeZone" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects/>
<%

Boolean includeCart = false;
if(homepage.getContentResource().adaptTo(Node.class).hasProperty("event-cart")){
	if(homepage.getContentResource().adaptTo(Node.class).getProperty("event-cart").getString().equals("true")){
		includeCart = true;
	}
}

DateTime today = new DateTime();
DateTimeFormatter dtfIn = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
DateTimeFormatter dtfOutDate = DateTimeFormat.forPattern("EEE MMM dd");
DateTimeFormatter dtfOutTime = DateTimeFormat.forPattern("HH:mm aa");
DateTimeFormatter dtfOutMY = DateTimeFormat.forPattern("MMMM yyyy");
String register = "";
String membersOnly = "false";
String eventID = "-1";

//User user = VtkUtil.getUser(request.getSession());
//Boolean isMember = (user != null);

//IF there is no configuration, we set it ourselves using event-search
//We will actually search the events too 
SearchResultsInfo srchInfo = (SearchResultsInfo)request.getAttribute("eventresults");
if(null==srchInfo) {
%>
<cq:include path="content/middle/par/event-search" resourceType="girlscouts/components/event-search" />
<%
}
//The results are stored in srchInfo
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
	<p>No event search results for &quot;<i class="error"><%= Text.escapeXml(q) %></i>&quot;.</p>
<%
	} else {
		for(String result: results) {
			register = "";
			DateTime evntComparsion = null;
			Node node =  resourceResolver.getResource(result).adaptTo(Node.class);
			try {
				Node propNode = node.getNode("jcr:content/data");
				if(propNode.hasProperty("visibleDate")){
					String visibleDate = propNode.getProperty("visibleDate").getString();

					DateTime vis = DateTime.parse(visibleDate,dtfIn);
					if(vis.isAfter(today)){
						continue;
					}
				}
				String stringStartDate = propNode.getProperty("start").getString();
				DateTime startDate = DateTime.parse(stringStartDate,dtfIn);
				eventID = "-1";
				
                //Add time zone label to date string if event has one
                String timeZoneLabel = propNode.hasProperty("timezone") ? propNode.getProperty("timezone").getString() : "";
                String timeZoneShortLabel = "";
				if(!timeZoneLabel.isEmpty()){
					//dateStr = dateStr + " " + timeZoneLabel;
					int openParen1 = timeZoneLabel.indexOf("(");
					int openParen2 = timeZoneLabel.indexOf("(",openParen1+1);
					int closeParen = timeZoneLabel.indexOf(")",openParen2);
					if(closeParen != -1 && openParen2 != -1 && timeZoneLabel.length() > openParen2){
						timeZoneLabel = timeZoneLabel.substring(openParen2+1,closeParen);
					}
					DateTimeZone dtz = DateTimeZone.forID(timeZoneLabel);
					timeZoneShortLabel = dtz.getShortName(DateTimeUtils.currentTimeMillis());
					startDate = new DateTime(startDate.getMillis(),dtz);
				}
				
				DateTime evntComparison = startDate;
				
				if(propNode.hasProperty("eid")){
					eventID = propNode.getProperty("eid").getString();
				}
				if(propNode.hasProperty("register") && !propNode.getProperty("register").getString().equals("")){
					register = propNode.getProperty("register").getString();
				}
				if(propNode.hasProperty("memberOnly")){
					membersOnly = propNode.getProperty("memberOnly").getString();
				}

				String title = propNode.getProperty("../jcr:title").getString();
				String href = result+".html";
				String time = "";
				String locationLabel = "";

				String startDateStr = dtfOutDate.print(startDate);
				String startTimeStr = dtfOutTime(startDate);
				String formatedStartDateStr = startDateStr + ", " +startTimeStr;

				if(propNode.hasProperty("locationLabel")){
					locationLabel=propNode.getProperty("locationLabel").getString();
				}

				String formatedEndDateStr="";
				DateTime endDate =null;
				if(propNode.hasProperty("end")){
					endDate = DateTime.parse(formatedEndDateStr,dtfIn);
					evntComparison = endDate;
					boolean sameDay = startDate.year().get() == endDate.year().get() && startDate.dayOfYear().get() == endDate.dayOfYear().get();
					String endDateStr = dtfOutDate.parse(endDate);
					String endTimeStr = dtfOutTime.parse(endDate);
					if (!sameDay) {
						//dateStr += " - " + endDateStr +", " + endTimeStr;
						formatedEndDateStr= endDateStr +", " + endTimeStr;
					}else {
						//dateStr += " - " + endTimeStr;
						formatedEndDateStr= endTimeStr;
					}
				}
				String details = propNode.getProperty("details").getString();
				int month = startDate.monthOfYear().get();
				
				formatedEndDateStr = formatedEndDateStr + " " + timeZoneShortLabel;
				
				if(evntComparison.year().get() > today.year().get() || (evntComparison.year().get() == today.year().get() && (evntComparsion.dayOfYear().get() >= today.dayOfYear().get()))) {

					if(tempMonth!=month) {
						String monthYr = dtfOutMY.print(startDate);
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

		<div class="eventsList eventSection" itemtype="http://schema.org/ItemList">
			<div class="leftCol" itemprop="image">
<%
				String imgPath;
				if(propNode.hasProperty("thumbImage")){
					imgPath = propNode.getProperty("thumbImage").getString();
					%> <img src="<%= imgPath %>" /> <%
				}
				else if(propNode.hasProperty("image")){
					imgPath = propNode.getProperty("image").getString();
					%> <img src="<%= imgPath %>" /> <%
				} else{
					imgPath = propNode.getPath() + "/image";
%>
<%= displayRendition(resourceResolver, imgPath, "cq5dam.web.120.80") %>
<% } %>
			</div>
			<div class="rightCol">
				<h6>

				<a class="bold" href="<%=href%>" itemprop="name"><%=title %></a></h6>
				<% if(membersOnly.equals("true")){
					  %> <p class="bold">MEMBERSHIP REQUIRED</p> <%
				   }%>
				<p class="bold">Date:
				    <%try{%>
                        <span itemprop="startDate" itemscope itemtype="http://schema.org/Event" content="<%=utcFormat.format(startDate)%>"><%=formatedStartDateStr%></span>
                        <% if(formatedEndDateStr!=null && !formatedEndDateStr.equals("")){ %>
                            - <span itemprop="stopDate" itemscope itemtype="http://schema.org/Event" content="<%=(endDate==null ? "" : utcFormat.format(endDate))%>"><%=formatedEndDateStr %></span>
                        <%
                        }
                     }catch(Exception eDateStr){eDateStr.printStackTrace();}
                    %>
				</p>
<%if(!locationLabel.isEmpty()){ %>
				<p class="bold" itemprop="location" itemscope itemtype="http://schema.org/Place">Location:  <span itemprop="name"><%=locationLabel %></span></p>
<% } %>
<% if(propNode.hasProperty("srchdisp")){ %>
				<p itemprop="description"><%=propNode.getProperty("srchdisp").getString()%></p>
<% } %>
	<%if(includeCart && register!=null && !register.isEmpty() && !eventID.equals("-1")){%>
        <div class="eventDetailsRegisterLink">
    	 	<a href="<%=genLink(resourceResolver, register)%>">Register Now</a>
    	 	<a onclick="addToCart('<%= title.replace("'","\\'") %>', '<%= eventID %>', '<%= href %>'); return false;">Add to MyActivities</a>
    	</div>
     <%} %>

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

