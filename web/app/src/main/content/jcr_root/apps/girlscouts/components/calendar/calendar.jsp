
<%@include file="/libs/foundation/global.jsp"%>
<%@ page import="com.day.cq.tagging.TagManager,org.apache.sling.commons.json.*,java.util.ArrayList,java.util.HashSet, java.util.Locale,java.util.Arrays,java.util.Iterator,java.util.List,java.util.Set,com.day.cq.search.result.SearchResult, java.util.ResourceBundle,com.day.cq.search.QueryBuilder,javax.jcr.PropertyIterator,org.girlscouts.web.events.search.SearchResultsInfo, com.day.cq.i18n.I18n,org.apache.sling.api.resource.ResourceResolver,org.girlscouts.web.events.search.EventsSrch,org.girlscouts.web.events.search.FacetsInfo,java.util.Calendar,
org.girlscouts.web.events.search.*"%>


<%@include file="/libs/foundation/global.jsp"%>

<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects />
<%!
  
  private String getJsonEvents(List<String> eventsPath, ResourceResolver resourceResolver){    
    List<JSONObject> eventList = new ArrayList<JSONObject>();
    GSDateTimeFormatter dateFormat = GSDateTimeFormat.forPattern("EEE, MMM d, yyyy");
    GSDateTimeFormatter timeFormat = GSDateTimeFormat.forPattern("h:mm a");
    GSDateTimeFormatter fromFormat = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");   
    
	GSDateTime today = new GSDateTime();
	GSDateTimeFormatter formatter = GSDateTimeFormat.forPattern("yyyy-MM-dd");
	String evtStartDt = formatter.print(today);
	try{
		today = GSDateTime.parse(evtStartDt,formatter);
		
	}catch(Exception e){
		e.printStackTrace();
	}
	String end ="";
    String location="";
    String detail="";
    GSDateTime eventDate = null;
    GSDateTime startDate = null;
    GSDateTime endDate = null;
    GSDateTimeFormatter dateFt = GSDateTimeFormat.forPattern("MMM d, yyyy");
    String jsonEvents="";
	for(String path: eventsPath){
		String color = "#00AE58";
		String eventDt = "";
       	try{ 
			Node node =   resourceResolver.getResource(path).adaptTo(Node.class);
			if(node.hasNode("jcr:content/data")) {
				Node propNode = node.getNode("jcr:content/data");

				//End should never be null. If end is null, the event may be stretched
                if(!propNode.hasProperty("end")){
					propNode.setProperty("end",propNode.getProperty("start").getString());
                }

				JSONObject obj = new JSONObject();
				
				if(propNode.hasProperty("start")){
					startDate = GSDateTime.parse(propNode.getProperty("start").getString(),fromFormat);
					eventDate = startDate;
				}
				if(propNode.hasProperty("end")){
					endDate = GSDateTime.parse(propNode.getProperty("end").getString(),fromFormat);
					eventDate = endDate;
				}
				
           		String timeZoneLabel = propNode.hasProperty("timezone") ? propNode.getProperty("timezone").getString() : "";
				if(!timeZoneLabel.isEmpty()){
					int openParen1 = timeZoneLabel.indexOf("(");
					int openParen2 = timeZoneLabel.indexOf("(",openParen1+1);
					int closeParen = timeZoneLabel.indexOf(")",openParen2);
					if(closeParen != -1 && openParen2 != -1 && timeZoneLabel.length() > openParen2){
						timeZoneLabel = timeZoneLabel.substring(openParen2+1,closeParen);
					}
					try{
						GSDateTimeZone dtz = GSDateTimeZone.forID(timeZoneLabel);
						if(startDate != null){
							startDate = startDate.withZone(dtz);
						}
						if(endDate != null){
							endDate = endDate.withZone(dtz);
						}
						timeZoneLabel = dtz.getShortName(GSDateTimeUtils.currentTimeMillis());
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				
				try{
					eventDt = formatter.print(eventDate);
					eventDate = GSDateTime.parse(eventDt,formatter);
				}catch(Exception e){
					e.printStackTrace();
				}
				if(eventDate.isAfter(today) || eventDate.isEqual(today)){
					String title = propNode.getProperty("../jcr:title").getString();
					detail = "";
					location="";
					if(propNode.hasProperty("srchdisp")){
						 detail = propNode.getProperty("srchdisp").getString();
					}
					if(propNode.hasProperty("locationLabel")){
						 location = propNode.getProperty("locationLabel").getString();
	 				}

					//Start is need for the calendar to display right event on Calendar
	             	
					String start = dateFt.print(startDate);
	      			String time = timeFormat.print(startDate);
	             	String dateInCalendar = dateFormat.print(startDate);
	             	String startTimeStr = timeFormat.print(startDate);
	             	String dateStr = dateInCalendar + ", " +startTimeStr;
	             	
	             	
	              	if(propNode.hasProperty("end")){
					     //End is need for the calendar to display right end date of an event on Calendar
						end = dateFt.print(endDate);
						boolean sameDay = startDate.year() == endDate.year() &&
					                  startDate.dayOfMonth() == endDate.dayOfMonth() && startDate.monthOfYear() == endDate.monthOfYear();
                        boolean sameTime = startDate.hourOfDay() == endDate.hourOfDay() && 
                            		  startDate.minuteOfHour() == endDate.minuteOfHour();
						String endDateStr = dateFormat.print(endDate);
						String endTimeStr = timeFormat.print(endDate);

                        if (!sameDay && !sameTime) {
					 	  	dateStr += " - " + endDateStr +", " + endTimeStr;
						}else if(!sameTime){
							dateStr += " - " + endTimeStr;
						}
	            	  }
                    
					if(!timeZoneLabel.isEmpty()){
						dateStr = dateStr + " " + timeZoneLabel;
					}

					if(propNode.hasProperty("color")){
	            		 color = propNode.getProperty("color").getString();
	            	}
					String url = path+".html";
					obj.put("title", title);
					obj.put("displayDate", dateStr);
					obj.put("location",location);
					obj.put("color",color);
					obj.put("description", detail);
					obj.put("start",start);
					if(!end.isEmpty())
					  	obj.put("end", end);
					obj.put("path", url);
					eventList.add(obj);
		        }
	            
			}  
        }catch(Exception e){
        	e.printStackTrace();
        }
	}
	try{
		JSONArray eventArray = new JSONArray(eventList);
		jsonEvents = eventArray.toString();
	}catch(Exception je){
		je.printStackTrace();
	}  
     return jsonEvents;
}
%>

<%
   String month = null;
   String year = null;
   String eventSuffix = slingRequest.getRequestPathInfo().getSuffix();
   if(null!=eventSuffix) {
	String temp = eventSuffix.substring(eventSuffix.indexOf("/")+1, eventSuffix.length());
	String[] my = temp.split("-");
	month = String.valueOf(Integer.parseInt(my[0])-1);
	year = my[1];
   }

SearchResultsInfo srchInfo = (SearchResultsInfo)request.getAttribute("eventresults");
if(null==srchInfo) {
%>
<cq:include path="content/middle/par/event-search" resourceType="girlscouts/components/event-search" />
<%  
}
srchInfo =  (SearchResultsInfo)request.getAttribute("eventresults");
String jsonEvents = getJsonEvents(srchInfo.getResults(),resourceResolver);
%>


<div id="fullcalendar"></div>
<script>
$(document).ready(function(){
	calendarDisplay(<%=month%>,<%=year%>,<%=jsonEvents%>);

	// iOS touch fix
	var plat = navigator.platform;
	if( plat.indexOf("iPad") != -1 || plat.indexOf("iPhone") != -1 || plat.indexOf("iPod") != -1 ) {
		$(".fc-event-title").bind('touchend', function() {
			$(this).click();
		});
	}
}); 
</script>
