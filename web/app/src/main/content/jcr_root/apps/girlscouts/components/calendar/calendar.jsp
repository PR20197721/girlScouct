
<%@include file="/libs/foundation/global.jsp"%>
<%@ page import="com.day.cq.tagging.TagManager,org.apache.sling.commons.json.*,java.util.Calendar,java.util.ArrayList,java.util.HashSet,java.text.DateFormat,java.text.SimpleDateFormat,java.util.Date, java.util.Locale,java.util.Arrays,java.util.Iterator,java.util.List,java.util.Set,com.day.cq.search.result.SearchResult, java.util.ResourceBundle,com.day.cq.search.QueryBuilder,javax.jcr.PropertyIterator,org.girlscouts.web.events.search.SearchResultsInfo, com.day.cq.i18n.I18n,org.apache.sling.api.resource.ResourceResolver,org.girlscouts.web.events.search.EventsSrch,org.girlscouts.web.events.search.FacetsInfo,java.util.Calendar,java.util.TimeZone"%>


<%@include file="/libs/foundation/global.jsp"%>

<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects />
<%!
  
  private String getJsonEvents(List<String> eventsPath, ResourceResolver resourceResolver){    
    List<JSONObject> eventList = new ArrayList<JSONObject>();
    DateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
    DateFormat timeFormat = new SimpleDateFormat("h:mm a");
    Date startDate = null; 
    /*Date today = new Date();
    Calendar cal = Calendar.getInstance();
    cal.setTime(today);
    cal.add(Calendar.DAY_OF_MONTH, -1);
	today = cal.getTime();
	*/
	Date today = new Date();
	DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	String evtStartDt = formatter.format(today);
	try{
		today = formatter.parse(evtStartDt);
		System.out.println("today" +today);
	}catch(Exception e){
		System.out.println("Exception today" +today);
	}
	String end ="";
    String location="";
    String detail="";
    Date eventDate = null;
    DateFormat dateFt = new SimpleDateFormat("MMM d, yyyy");
    String jsonEvents="";
	for(String path: eventsPath){
		String color = "#00AE58";
		String eventDt = "";
       	try{ 
			Node node =   resourceResolver.getResource(path).adaptTo(Node.class);
			if(node.hasNode("jcr:content/data")) {
				Node propNode = node.getNode("jcr:content/data");
	            JSONObject obj = new JSONObject();
	            if(propNode.hasProperty("start")){
	            	eventDate = propNode.getProperty("start").getDate().getTime();
	            	eventDt = formatter.format(eventDate);
					try{
						eventDate = formatter.parse(eventDt);
						
					}catch(Exception e){}
					if(eventDate.after(today) || eventDate.equals(today)){
						String title = propNode.getProperty("../jcr:title").getString();
						detail = "";
						location="";
						if(propNode.hasProperty("srchdisp")){
							 detail = propNode.getProperty("srchdisp").getString();
						}
						if(propNode.hasProperty("locationLabel")){
							 location = propNode.getProperty("locationLabel").getString();
		 				}
						Calendar startDt = propNode.getProperty("start").getDate();
		             //Start is need for the calendar to display right event on Calendar
		             	String start = dateFt.format(startDt.getTime());
		      			String time = timeFormat.format(propNode.getProperty("start").getDate().getTime());
		             	String dateInCalendar = dateFormat.format(startDt.getTime());
		             	String startTimeStr = timeFormat.format(propNode.getProperty("start").getDate().getTime());
		             	String dateStr = dateInCalendar + ", " +startTimeStr;
		              	if(propNode.hasProperty("end")){
							Calendar endDt = propNode.getProperty("end").getDate();
						     //End is need for the calendar to display right end date of an event on Calendar
							end = dateFt.format(endDt.getTime());
							Calendar cal1 = Calendar.getInstance();
							Calendar cal2 = Calendar.getInstance();
							Calendar endDate = propNode.getProperty("end").getDate();
							cal1.setTime(startDt.getTime());
							cal2.setTime(endDate.getTime());
							boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
						                  cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
							String endDateStr = dateFormat.format(endDt.getTime());
							String endTimeStr = timeFormat.format(propNode.getProperty("end").getDate().getTime());
							
							if (!sameDay) {
						 	  	dateStr += " - " + endDateStr +", " + endTimeStr;
							}else{
								dateStr += " - " + endTimeStr;
							}
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
			}  
        }catch(Exception e){}
	}
	try{
		JSONArray eventArray = new JSONArray(eventList);
		jsonEvents = eventArray.toString();
	}catch(Exception je){}  
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
