package org.girlscouts.web.components.models;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.commons.json.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.search.result.SearchResult;
import java.util.ResourceBundle;
import com.day.cq.search.QueryBuilder;

import javax.inject.Inject;
import javax.jcr.PropertyIterator;
import com.day.cq.i18n.I18n;
import org.apache.sling.api.resource.ResourceResolver;
import org.joda.time.DateTime;
import java.util.Calendar;
import org.girlscouts.common.events.search.*;
import javax.jcr.Node;
import javax.annotation.PostConstruct;

@Model(adaptables = SlingHttpServletRequest.class)
public class CalendarModel {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Self
    private SlingHttpServletRequest request;

    @Inject
    public ResourceResolver resourceResolver;

    private boolean hasSearchInfo;
    private String jsonEvents;

    @PostConstruct
    public void init() {
        String month = null;
        String year = null;
        String eventSuffix = request.getRequestPathInfo().getSuffix();
     //   if(true || null!=eventSuffix) {
        //    String temp = eventSuffix.substring(eventSuffix.indexOf("/") + 1, eventSuffix.length());
          //  String[] my = temp.split("-");
            try {
            //    month = String.valueOf(Integer.parseInt(java.net.URLEncoder.encode(my[0], "UTF-8")) - 1);
              //  year = String.valueOf(Integer.parseInt(java.net.URLEncoder.encode(my[1], "UTF-8")));
            } catch (Exception e) {
            }
            SearchResultsInfo srchInfo = null;
            try{
                srchInfo = (SearchResultsInfo) request.getAttribute("eventresults");
            }catch (Exception e) {
                log.error("Error: ", e);
            }

            if(null != srchInfo) {
                hasSearchInfo = true;
                jsonEvents = getJsonEvents(srchInfo.getResults(),resourceResolver);
            } else {
                hasSearchInfo = false;
            }
        //}

    }
    private String getJsonEvents(List<String> eventsPath, ResourceResolver resourceResolver){
        Logger log = LoggerFactory.getLogger(this.getClass().getName());
       // List<JSONObject> eventList = new ArrayList<JSONObject>();
        JsonArray jsonArr = new JsonArray();
        Gson gson = new Gson();
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
        GSDateTimeFormatter dateFt = GSDateTimeFormat.forPattern("MMM d, yyyy");
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

                        JsonObject jsonOj = new JsonObject();

                        String url = path+".html";

                        jsonOj.addProperty("title", title);
                        jsonOj.addProperty("displayDate", dateStr);
                        jsonOj.addProperty("location", location);
                        jsonOj.addProperty("color", color);
                        jsonOj.addProperty("description", detail);
                        jsonOj.addProperty("start", start);
                        if(!end.isEmpty()) {
                            jsonOj.addProperty("end", end);
                        }
                        jsonOj.addProperty("path", url);

                    
                        jsonArr.add(jsonOj);
                    }

                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        String jsonStr;
        try{
            jsonStr = gson.toJson(jsonArr);
        }catch(Exception je){
            jsonStr = "";
            je.printStackTrace();
        }
        return jsonStr;
    }

    public boolean isHasSearchInfo() {
        return hasSearchInfo;
    }
    public boolean getHasSearchInfo() {
        return hasSearchInfo;
    }

    public void setHasSearchInfo(boolean hasSearchInfo) {
        this.hasSearchInfo = hasSearchInfo;
    }

    public String getJsonEvents() {
        return jsonEvents;
    }

    public void setJsonEvents(String jsonEvents) {
        this.jsonEvents = jsonEvents;
    }
}
