
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
  
    String end ="";
    
    DateFormat dateFt = new SimpleDateFormat("MMM d, yyyy");
        String jsonEvents="";
        for(String path: eventsPath){
        	String color = "#00AE58";
         try
         {   Node node =   resourceResolver.getResource(path).adaptTo(Node.class);
             Node propNode = node.getNode("jcr:content/data");
             JSONObject obj = new JSONObject();
             String title = propNode.getProperty("../jcr:title").getString();
             String detail = propNode.getProperty("details").getString();
             
             Calendar startDt = propNode.getProperty("start").getDate();
             //Calendar endDt = propNode.getProperty("end").getDate();
             String start = dateFt.format(startDt.getTime());
             String time = timeFormat.format(propNode.getProperty("start").getDate().getTime());
             //String end = dateFormat.format(endDt.getTime());
             
             if(propNode.hasProperty("end")){
            	 String endTimeStr = timeFormat.format(propNode.getProperty("end").getDate().getTime());
                 time += " to " + endTimeStr;
                 Calendar endDt = propNode.getProperty("end").getDate();
                 end = dateFormat.format(endDt.getTime());
             }
             
            
             if(propNode.hasProperty("color")){
            	 color = propNode.getProperty("color").getString();
            	 
             }
             String url = path+".html";
             obj.put("title", title);
             
             obj.put("color",color);
             obj.put("description", detail);
             obj.put("start",start);
             if(!end.isEmpty())
                obj.put("end", end);
             obj.put("path", url);
            
              obj.put("time", time);
             eventList.add(obj); 
         }catch(Exception e){
            
         
         }
        }
        try{
            JSONArray eventArray = new JSONArray(eventList);
            jsonEvents = eventArray.toString();
           
           }catch(Exception je){
               System.out.println("Exception" +je.getStackTrace());
           }  
     return jsonEvents;
}
%>

<%
   String month = null;
   String year = null;
   String eventSuffix = slingRequest.getRequestPathInfo().getSuffix();
   if(null!=eventSuffix)
   {
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
}); 

</script>







