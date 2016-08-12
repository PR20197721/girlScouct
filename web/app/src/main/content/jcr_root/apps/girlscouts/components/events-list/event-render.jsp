<%@ page import="org.girlscouts.web.events.search.*"%>
				
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
try{
	Node propNode = (Node)request.getAttribute("propNode");
	Node node = (Node)request.getAttribute("node");
	
	GSDateTime startDate = null;
	GSDateTime endDate = null;
	String dateStart = "";
	String dateEnd = "";
	String startDateStr = "";
	String startTimeStr = "";
	String endDateStr = "";
	String endTimeStr = "";

	String locationLabel = "";
	String imgPath="";
	String iconPath="";
	GSDateTimeFormatter fromFormat = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	GSDateTimeFormatter dateFormat = GSDateTimeFormat.forPattern("EEE MMM dd yyyy");
	GSDateTimeFormatter timeFormat = GSDateTimeFormat.forPattern("h:mm aa");
	
	startDate = GSDateTime.parse(propNode.getProperty("start").getString(),fromFormat);
	GSLocalDateTime localStartDate = null;
	GSLocalDateTime localEndDate = null;
	
    //Add time zone label to date string if event has one
   	String timeZoneLabel = null;
    String timeZoneShortLabel = "";
	GSDateTimeZone dtz = null;
    if(propNode.hasProperty("timezone")){
    	timeZoneLabel = propNode.getProperty("timezone").getString();
		int openParen1 = timeZoneLabel.indexOf("(");
		int openParen2 = timeZoneLabel.indexOf("(",openParen1+1);
		int closeParen = timeZoneLabel.indexOf(")",openParen2);
		if(closeParen != -1 && openParen2 != -1 && timeZoneLabel.length() > openParen2){
			timeZoneLabel = timeZoneLabel.substring(openParen2+1,closeParen);
		}
		try{
			dtz = GSDateTimeZone.forID(timeZoneLabel);
			startDate = startDate.withZone(dtz);
			timeZoneShortLabel = dtz.getShortName(startDate.getMillis());
			startDateStr = dateFormat.print(startDate);
			startTimeStr = timeFormat.print(startDate) + " " + timeZoneShortLabel;
		}catch(Exception e){
			e.printStackTrace();
		}
    }else{
		localStartDate = GSLocalDateTime.parse(propNode.getProperty("start").getString(),fromFormat);
		startDateStr = dateFormat.print(localStartDate);
		startTimeStr = timeFormat.print(localStartDate);
    }
	
	dateStart = startDateStr + ", " + startTimeStr;	
	
	if (propNode.hasProperty("end")){
		endDate = GSDateTime.parse(propNode.getProperty("end").getString(),fromFormat);
		if(dtz != null){
			endDate = endDate.withZone(dtz);
			endDateStr = dateFormat.print(endDate);
			endTimeStr = timeFormat.print(endDate) + " " + timeZoneShortLabel;
		}else{
			localEndDate = GSLocalDateTime.parse(propNode.getProperty("end").getString(),fromFormat);
			endDateStr = dateFormat.print(localEndDate);
			endTimeStr = timeFormat.print(localEndDate);
		}		
 	}
	
	dateEnd = endDateStr + ", " + endTimeStr;

	if(propNode.hasProperty("locationLabel")){
		locationLabel=propNode.getProperty("locationLabel").getString();
	}
	
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
       <p>Start: <%= dateStart %></p>
       <% if (propNode.hasProperty("end")) { %>
       <p>End: <%= dateEnd %></p>
       <% } %>
       <p>Location: <%= locationLabel %></p>
    </div>
  </div>
</li> 
<% }catch(Exception e){
	e.printStackTrace();
}%> 
