<%@ page import="java.util.Date,
				java.text.DateFormat,
				java.text.SimpleDateFormat,
				java.util.TimeZone"%>
				
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
	
	
	String locale =  currentSite.get("locale", "America/New_York");
    TimeZone tZone = TimeZone.getTimeZone(locale);
	
	
	DateFormat dateFormat = new SimpleDateFormat("EEE MMM d yyyy");
	DateFormat timeFormat = new SimpleDateFormat("h:mm a");
	timeFormat.setTimeZone(tZone);
	
	startDate = propNode.getProperty("start").getDate().getTime(); 
	startDateStr = dateFormat.format(startDate);
	startTimeStr = timeFormat.format(startDate);
	String dateStr="";
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
	
	if (dateStr != null) {
		dateStr =  dateStr.replaceAll(":00","").replaceAll(" ([AP]M)", "&nbsp;$1");
	}
	
	String title = (String)request.getAttribute("title");
	String href = (String)request.getAttribute("href");
%>
 <li class="eventsListItem">
   <div class="row">
     <div class="small-8 medium-8 large-8 columns events-image">
         <%
			if(!iconPath.isEmpty()){ %>
          		<%= displayRendition(resourceResolver, imgPath, "cq5dam.web.120.80") %>
             <%} else if(iconPath.isEmpty()) { %>
					<img src="/content/dam/all_icons/icons_64/events_icon.jpg" alt="events icon"/>
             <% } %>
     </div>
      <div class="small-16 medium-16 large-16 columns events-data">
         <p><a href="<%= href %>"><%= title %></a></p>
         <p>Date: <%= dateStr %></p>
         <p>Location: <%= locationLabel %></p>
      </div>
   </div>
</li>  
