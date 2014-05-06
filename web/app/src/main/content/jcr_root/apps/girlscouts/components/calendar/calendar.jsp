
<%@include file="/libs/foundation/global.jsp"%>
<%@ page import="com.day.cq.tagging.TagManager,org.apache.sling.commons.json.*,java.util.ArrayList,java.util.HashSet,java.text.DateFormat,java.text.SimpleDateFormat,java.util.Date, java.util.Locale,java.util.Arrays,java.util.Iterator,java.util.List,java.util.Set,com.day.cq.search.result.SearchResult, java.util.ResourceBundle,com.day.cq.search.QueryBuilder,javax.jcr.PropertyIterator,org.girlscouts.web.events.search.SearchResultsInfo, com.day.cq.i18n.I18n,org.apache.sling.api.resource.ResourceResolver,org.girlscouts.web.events.search.EventsSrch,org.girlscouts.web.events.search.FacetsInfo,java.util.Calendar,java.util.TimeZone"%>


<%@include file="/libs/foundation/global.jsp"%>

<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects />
<%!

  private String getJsonEvents(List<String> eventsPath, ResourceResolver resourceResolver){    
    List<JSONObject> eventList = new ArrayList<JSONObject>();
        String jsonEvents="";
        for(String path: eventsPath){
        
         try
         {   Node node =   resourceResolver.getResource(path).adaptTo(Node.class);
             Node propNode = node.getNode("jcr:content/data");
             JSONObject obj = new JSONObject();
             String title = propNode.getProperty("../jcr:title").getString();
             String detail = propNode.getProperty("details").getString();
             String time =  propNode.getProperty("time").getString();
             String start = propNode.getProperty("start").getString();
             System.out.println(start);
             String end = propNode.getProperty("end").getString();
             String url = path+".html";
             obj.put("allday","false");
             obj.put("borderColor" , "#1587bd");
             obj.put("color","#9fc6e7");
             obj.put("title", title);
             obj.put("description", detail);
             obj.put("start",start);
             obj.put("end", end);
             obj.put("url", url);
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
SearchResultsInfo srchInfo = (SearchResultsInfo)request.getAttribute("eventresults");
if(null==srchInfo) {
%>
<cq:include path="content/middle/par/event-search" resourceType="girlscouts/components/event-search" />
<%  
}
srchInfo =  (SearchResultsInfo)request.getAttribute("eventresults");
  System.out.println("searchInfo Size:" + srchInfo.getResults().size());
  String jsonEvents = getJsonEvents(srchInfo.getResults(),resourceResolver);
%>
<div id="fullcalendar"></div>
<!--  <div id="eventContent" title="Event Details" style="display:none;">
   <a id="eventLink" href="" target="_blank">Read More</a></strong></p>
   <div class="tooltiptext">
    <span id="startTime"></span>
    <span id="endTime"></span>
    <div id="eventInfo">
    </div>
    <p><strong><a href="" target="_blank">Read More</a></strong></p>
  </div>

</div> -->
   
<script>
 
  //  $(document).ready(loadCal(<%=jsonEvents%>));
  
  // Setup FullCalendar
(function() {
    var date = new Date();
    var d = date.getDate();
    var m = date.getMonth();
    var y = date.getFullYear();

    var tooltip = $('<div/>').qtip({
        id: 'fullcalendar',
        prerender: true,
        content: {
            text: ' '
            //title: {
             //   button: true
           // }
        },
        position: {
            my: 'top center',
            at: 'top center',
            target: 'mouse',
            viewport: $('#fullcalendar'),
            adjust: {
                resize:true,
                mouse: false,
                scroll: false,
                x:-20,
                y:80
            }
        },
        show: false,
        hide: false,
        style: {
           classes:'qtip-light',
           tip:{
              width:40,
              border:1
              
            }
         }

        
    }).qtip('api');

    $('#fullcalendar').fullCalendar({
        height: 300,
        header: {
            left: 'title',
            center: '',
            right: 'today prev,next'  
        },
        eventClick: function(data, event, view) {
            var content = '<a href="'+data.url+'">'+data.title+'</a>' + 
                '<p><b>Start:</b> '+data.start+'<br />' + 
                (data.end && '<p><b>End:</b> '+data.end+'</p>' || '');

            tooltip.set({
                'content.text': content
            })
            .reposition(event).show(event);
        },
        dayClick: function() { tooltip.hide() },
       // eventResizeStart: function() { tooltip.hide() },
       // eventDragStart: function() { tooltip.hide() },
       // viewDisplay: function() { tooltip.hide() },
        events: <%=jsonEvents%>
    });
   $('a.fc-event').click(function(e){
        e.preventDefault();
	   });
}());
</script>







