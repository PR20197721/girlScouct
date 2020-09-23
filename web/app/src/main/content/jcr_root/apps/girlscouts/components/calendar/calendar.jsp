
<%@include file="/libs/foundation/global.jsp"%>
<%@ page import="com.day.cq.tagging.TagManager,
                org.apache.sling.commons.json.*,
                java.util.ArrayList,
                java.util.HashSet,
                java.util.Locale,
                java.util.Arrays,
                java.util.Iterator,
                java.util.List,
                java.util.Set,
                com.google.gson.*,
                org.slf4j.Logger,
                org.slf4j.LoggerFactory,
                com.day.cq.search.result.SearchResult,
                java.util.ResourceBundle,
                com.day.cq.search.QueryBuilder,
                javax.jcr.PropertyIterator,
                com.day.cq.i18n.I18n,
                org.apache.sling.api.resource.ResourceResolver,
                org.joda.time.DateTime,
                java.util.Calendar,
org.girlscouts.common.events.search.*, javax.jcr.Node"%>


<%@include file="/libs/foundation/global.jsp"%>

<cq:includeClientLib categories="apps.girlscouts" />
<cq:includeClientLib categories="apps.girlscouts.components.calendar" />
<cq:defineObjects />
<%!

    private String getJsonEvents(List<String> eventsPath, ResourceResolver resourceResolver, Node calNode){
        Logger log = LoggerFactory.getLogger(this.getClass().getName());
        JsonArray jsonArr = new JsonArray();
        Gson gson = new Gson();
        List<JSONObject> eventList = new ArrayList<JSONObject>();
        GSDateTimeFormatter dtfIn = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        GSDateTimeFormatter dateFormat = GSDateTimeFormat.forPattern("EEE, MMM d, yyyy");
        GSDateTimeFormatter timeFormat = GSDateTimeFormat.forPattern("h:mm a");
        GSDateTimeFormatter fromFormat = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        DateTime dateTime = new DateTime();
        dateTime = dateTime.withTimeAtStartOfDay();
        GSDateTime today = new GSDateTime(dateTime.getMillis());
        GSDateTimeFormatter formatter = GSDateTimeFormat.forPattern("yyyy-MM-dd");
        String evtStartDt = formatter.print(today);
        // Do not reset to midnight. GSWP-1255
        //try{
        //	today = GSDateTime.parse(evtStartDt,formatter);
        //
        //}catch(Exception e){
        //	e.printStackTrace();
        //}
        String end ="";
        String location="";
        String detail="";
        GSDateTime eventDate = null;
        GSDateTime startDate = null;
        GSDateTime endDate = null;
        GSDateTimeFormatter dateFt = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
        GSDateTimeFormatter dateFtPrint = GSDateTimeFormat.forPattern("MMM d, yyyy");
        String jsonEvents="";
        for(String path: eventsPath){
            String color = "#00AE58";
            String eventDt = "";
            try{
                Node node =   resourceResolver.getResource(path).adaptTo(Node.class);
                if(node.hasNode("jcr:content/data")) {
                    Node propNode = node.getNode("jcr:content/data");
                    if(propNode.hasProperty("visibleDate")){
                        String visibleDate = propNode.getProperty("visibleDate").getString();

                        GSDateTime vis = GSDateTime.parse(visibleDate,dtfIn);
                        if(vis.isAfter(today)){
                            continue;
                        }
                    }

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
                    GSLocalDateTime localStartDate = null;
                    GSLocalDateTime localEndDate = null;

                    String start = "";
                    String time = "";
                    String dateInCalendar = "";
                    String startTimeStr = "";
                    String endDateStr = "";
                    String endTimeStr = "";
                    Boolean useRaw = timeZoneLabel.length() < 4;

                    if(!timeZoneLabel.isEmpty() && !useRaw){
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
                                start = dateFt.print(startDate);
                                time = timeFormat.print(startDate);
                                dateInCalendar = dateFormat.print(startDate);
                                startTimeStr = timeFormat.print(startDate);
                            }
                            if(endDate != null){
                                endDate = endDate.withZone(dtz);
                                end = dateFt.print(endDate);
                                endDateStr = dateFormat.print(endDate);
                                endTimeStr = timeFormat.print(endDate);
                            }
                            timeZoneLabel = dtz.getShortName(GSDateTimeUtils.currentTimeMillis());
                        }catch(Exception e){
                            useRaw = true;
                            e.printStackTrace();
                        }
                    }
                    if(timeZoneLabel.isEmpty() || useRaw){
                        if(startDate != null){
                            localStartDate = GSLocalDateTime.parse(propNode.getProperty("start").getString(),fromFormat);
                            start = dateFt.print(localStartDate);
                            time = timeFormat.print(localStartDate);
                            dateInCalendar = dateFormat.print(localStartDate);
                            startTimeStr = timeFormat.print(localStartDate);
                        }
                        if(endDate != null){
                            localEndDate = GSLocalDateTime.parse(propNode.getProperty("end").getString(),fromFormat);
                            end = dateFt.print(localEndDate);
                            endDateStr = dateFormat.print(localEndDate);
                            endTimeStr = timeFormat.print(localEndDate);
                        }
                    }

                    try{
                    	eventDt = formatter.print(eventDate);
                        eventDate = GSDateTime.parse(eventDt,formatter);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    boolean showPastEvents;
                    String show= "false";
                    if(calNode.hasProperty("pastevents")) {
                        show = calNode.getProperty("pastevents").getString();
                        if("true".equals(show)) {
                            showPastEvents = true;
                        } else {
                            showPastEvents = eventDate.isAfter(today) || eventDate.isEqual(today);
                        }
                    } else {
                        showPastEvents = eventDate.isAfter(today) || eventDate.isEqual(today);
                    }

                    if(showPastEvents){
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

                        String dateStr = dateInCalendar + ", " +startTimeStr;


                        if(propNode.hasProperty("end")){
                            //End is need for the calendar to display right end date of an event on Calendar
                            boolean sameDay = startDate.year() == endDate.year() &&
                                    startDate.dayOfMonth() == endDate.dayOfMonth() && startDate.monthOfYear() == endDate.monthOfYear();
                            boolean sameTime = startDate.hourOfDay() == endDate.hourOfDay() &&
                                    startDate.minuteOfHour() == endDate.minuteOfHour();

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

                        JsonObject jsonObj = new JsonObject();


                        String url = path+".html";
                        jsonObj.addProperty("displayDate", dateStr);
                        jsonObj.addProperty("location", location);
                        jsonObj.addProperty("color", color);
                        jsonObj.addProperty("description", detail);
                        jsonObj.addProperty("start", start);
                        jsonObj.addProperty("path", url);
                        jsonObj.addProperty("title", title);
                        jsonObj.addProperty("show", show);
                        if(!end.isEmpty()) {
                            jsonObj.addProperty("end", end);
                        }


                        jsonArr.add(jsonObj);
                    }

                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        String jsonStr = "";
        try{
            jsonStr = gson.toJson(jsonArr);
        }catch(Exception je){
            jsonStr = "";
            je.printStackTrace();
        }
        return jsonStr;
    }
%>

<%
    String month = null;
    String year = null;
    String calDate = null;
    String eventSuffix = slingRequest.getRequestPathInfo().getSuffix();
    if(null!=eventSuffix) {
        String temp = eventSuffix.substring(eventSuffix.indexOf("/")+1, eventSuffix.length());
        String[] my = temp.split("-");
        calDate = my[1] + "-" + my[0];
    }
%>
<cq:include path="content/middle/par/event-search" resourceType="girlscouts/components/event-search" />
<%

    SearchResultsInfo srchInfo = (SearchResultsInfo)request.getAttribute("eventresults");
    List<String> searchResults = srchInfo.getResults();
    Node listNode = resource.adaptTo(Node.class);

    if(null != searchResults) {
        String jsonEvents = getJsonEvents(searchResults,resourceResolver, listNode);
        if(null!=eventSuffix) {
%>
<div id="calendar-events" data-date="<%=calDate%>"  data-event='<%=jsonEvents%>'></div>
<%
} else {
%>
<div id="calendar-events" data-event='<%=jsonEvents%>'></div>
<%
    }
%>
<div id="fullcalendar"></div>
<%} %>