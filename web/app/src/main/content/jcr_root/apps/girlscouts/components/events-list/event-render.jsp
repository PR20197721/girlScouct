<%@ page import="org.girlscouts.web.events.search.*"%>
				
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
try{
	Node propNode = (Node)request.getAttribute("propNode");
	Node node = (Node)request.getAttribute("node");
	GSDateTime startDate = null; 
	String startDateStr = "";
	String startTimeStr = "";
	String time = "";
	String locationLabel = "";
	String imgPath="";
	String iconPath="";
	GSDateTimeFormatter fromFormat = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	GSDateTimeFormatter dateFormat = GSDateTimeFormat.forPattern("EEE MMM dd yyyy");
	GSDateTimeFormatter timeFormat = GSDateTimeFormat.forPattern("hh:mm aa");
	
	startDate = GSDateTime.parse(propNode.getProperty("start").getString(),fromFormat);
	
    //Add time zone label to date string if event has one
   	String timeZoneLabel = null;
    String timeZoneShortLabel = "";
	GSDateTimeZone dtz = null;
    if(propNode.hasProperty("timezone")){
    	timeZoneLabel = propNode.getProperty("timezone").getString();
		//dateStr = dateStr + " " + timeZoneLabel;
		int openParen1 = timeZoneLabel.indexOf("(");
		int openParen2 = timeZoneLabel.indexOf("(",openParen1+1);
		int closeParen = timeZoneLabel.indexOf(")",openParen2);
		if(closeParen != -1 && openParen2 != -1 && timeZoneLabel.length() > openParen2){
			timeZoneLabel = timeZoneLabel.substring(openParen2+1,closeParen);
		}
		try{
			dtz = GSDateTimeZone.forID(timeZoneLabel);
			startDate = startDate.withZone(dtz);
			timeZoneShortLabel = dtz.getShortName(GSDateTimeUtils.currentTimeMillis());
		}catch(Exception e){
			e.printStackTrace();
		}
    }
	
	startDateStr = dateFormat.print(startDate);
	startTimeStr = timeFormat.print(startDate);

	
	
	String dateStr="";
	dateStr = startDateStr + ", " +startTimeStr;
	time = startTimeStr;
	
	if(propNode.hasProperty("locationLabel")){
		locationLabel=propNode.getProperty("locationLabel").getString();
	}
	if (propNode.hasProperty("end")){
		GSDateTime endDate = GSDateTime.parse(propNode.getProperty("end").getString(),fromFormat);
		if(dtz != null){
			endDate = endDate.withZone(dtz);
		}
		dateStr = getDateTime(startDate,endDate,dateFormat,timeFormat,dateStr,timeZoneShortLabel);
 	}
 
	boolean hasImage = false;
	String fileReference = null;
	imgPath = node.getPath()+"/jcr:content/data/image";
	boolean hasThumb = false;
	if (propNode.hasProperty("thumbImage")){
		iconPath = propNode.getProperty("thumbImage").getString();
		hasThumb = true;
	} else {
		iconPath=node.hasProperty("jcr:content/data/image/fileReference") ? node.getProperty("jcr:content/data/image/fileReference").getString() : "";
	}
	String title = (String)request.getAttribute("title");
	String href = (String)request.getAttribute("href");
%>
 <li class="eventsListItem">
  <div class="row collapse">  
    <div class="medium-6 large-6 small-8 columns lists-image">
      <% 
      	if(hasThumb){
      		%> <img src="<%= iconPath %>"/> <%
      	} else if(!iconPath.isEmpty()) { /*if there is image*/ %>
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
<% }catch(Exception e){
	e.printStackTrace();
}%> 
