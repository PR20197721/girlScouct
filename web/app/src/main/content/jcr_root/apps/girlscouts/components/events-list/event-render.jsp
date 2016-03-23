<%@ page import="java.util.Date,
				java.text.DateFormat,
				java.text.SimpleDateFormat,
				java.util.Calendar,
				org.joda.time.DateTimeZone,
				org.joda.time.format.DateTimeFormatter,
				org.joda.time.format.DateTimeFormat,
				org.joda.time.DateTime"
%>
				
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
	Node propNode = (Node)request.getAttribute("propNode");
	Node node = (Node)request.getAttribute("node");
	Date startDate = null; 
	String startDateStr = "";
	String startTimeStr = "";
	String time = "";
	String locationLabel = "";
	String imgPath="";
	String iconPath="";
	DateTimeFormatter dtfnotimezone = DateTimeFormat.forPattern("EEE MMM d yyyy, HH:MM"); 
    DateTimeFormatter dtfTimezone = DateTimeFormat.forPattern("EEE MMM d yyyy, HH:MM zzz"); 
	DateTimeFormatter dtftimeonly = DateTimeFormat.forPattern("HH:MM zzz");
	DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");
	String inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";
	String formatedStartDateStr = null;
	String formatedEndDateStr = null;
	
	//DateFormat dateFormat = new SimpleDateFormat("EEE MMM d yyyy");
	//DateFormat timeFormat = new SimpleDateFormat("h:mm a");
	
	
//	Calendar cal =  Calendar.getInstance();
//	cal.setTime(fromFormat.parse(propNode.getProperty("start").getString()));
//	startDate = cal.getTime(); 
//	startDateStr = dateFormat.format(startDate);
//	startTimeStr = timeFormat.format(startDate);

//process the start date
	DateTimeFormatter formatter = DateTimeFormat.forPattern(inputPattern);
	DateTime Startdt = formatter.parseDateTime(propNode.getProperty("start").getString());
	DateTime startdtCouncil = Startdt.withZone(DateTimeZone.forID(councilTimeZone));
	if (propNode.hasProperty("end")) {
		formatedStartDateStr = dtfTimezone.print(startdtCouncil);
	} else {
		formatedStartDateStr = dtfnotimezone.print(startdtCouncil);
	}

	
	
	String dateStr="";
	dateStr = formatedStartDateStr;
	time = startTimeStr;
	
	if(propNode.hasProperty("locationLabel")){
		locationLabel=propNode.getProperty("locationLabel").getString();
	}
	if (propNode.hasProperty("end")) {
		//consider timezone with joda
		
		DateTime endDateTime = formatter.parseDateTime(propNode.hasProperty("end").getString());
		DateTime endDateTimeCouncil = endDateTime.withZone(DateTimeZone.forID(councilTimeZone));
		if (!isSameDate(endDateTimeCouncil, startdtCouncil)) {
			formatedEndDateStr = dtfTimezone.print(endDateTimeCouncil);			
		} else {
			formatedEndDateStr =  dtftimeonly.print(endDateTimeCouncil);
		}
		dateStr += formatedEndDateStr;
		//cal.setTime(fromFormat.parse(propNode.getProperty("end").getString()));
		//Date endDate = cal.getTime();
		//dateStr = getDateTime(startDate,endDate,dateFormat,timeFormat,dateStr);
 	}
 
	boolean hasImage = false;
	String fileReference = null;
	imgPath = node.getPath()+"/jcr:content/data/image";
	iconPath=node.hasProperty("jcr:content/data/image/fileReference") ? node.getProperty("jcr:content/data/image/fileReference").getString() : "";
	


	//Add time zone label to date string if event has one
	/* String timeZoneLabel = propNode.hasProperty("timezone") ? propNode.getProperty("timezone").getString() : "";
	if(!timeZoneLabel.isEmpty()){
		dateStr = dateStr + " " + timeZoneLabel;
	} */

	String title = (String)request.getAttribute("title");
	String href = (String)request.getAttribute("href");
%>
 <li class="eventsListItem">
  <div class="row collapse">  
    <div class="medium-6 large-6 small-8 columns lists-image">
      <% if(!iconPath.isEmpty()) { /*if there is image*/ %>
        <%= displayRendition(resourceResolver, imgPath, "cq5dam.web.120.80") %>
      <%} else { /*if there is no image*/ %>
        <img src="/content/dam/girlscouts-shared/images/Icons/jolly-icons-64/events_icon.jpg" alt="events icon"/>
      <% } %>
    </div>
    <div class="medium-16 large-16 columns small-15">
       <p><a href="<%= href %>" title="<%= title %>"><%= title %></a></p>
       <p>Date: <%= dateStr %></p>
       <p>Location: <%= locationLabel %></p>
    </div>
  </div>
</li>  
