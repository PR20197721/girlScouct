<%@ page import="com.day.cq.tagging.TagManager,com.day.cq.dam.api.Asset,java.util.ArrayList,java.util.HashSet,java.text.DateFormat,java.text.SimpleDateFormat,java.util.Date, java.util.Locale,java.util.Map,java.util.Iterator,java.util.HashMap,java.util.List,java.util.Set,com.day.cq.search.result.SearchResult, java.util.ResourceBundle,com.day.cq.search.QueryBuilder,javax.jcr.PropertyIterator,org.girlscouts.web.events.search.SearchResultsInfo, com.day.cq.i18n.I18n,org.apache.sling.api.resource.ResourceResolver,org.girlscouts.web.events.search.EventsSrch,org.girlscouts.web.events.search.FacetsInfo,java.util.Calendar,java.util.TimeZone" %>
<%@include file="/libs/foundation/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects/>
<% 
DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.S");
fromFormat.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("GMT")));
DateFormat toFormat = new SimpleDateFormat("EEE dd MMM yyyy");
SearchResultsInfo srchInfo = (SearchResultsInfo)request.getAttribute("eventresults");
if(null==srchInfo) {
%>
<cq:include path="content/middle/par/event-search" resourceType="girlscouts/components/event-search" />
<%  
}
srchInfo =  (SearchResultsInfo)request.getAttribute("eventresults");
List<String> results = srchInfo.getResults();
long hitCounts = srchInfo.getHitCounts();
SearchResult searchResults = (SearchResult)request.getAttribute("searchResults");
String q = request.getParameter("q");
if(properties.containsKey("isfeatureevents") && properties.get("isfeatureevents").equals("on") ){
%> 
<cq:include script="feature-events.jsp"/>
<%   
} else{
%> 

<%

    int tempMonth =0;
    System.out.println("This is the event::::" +results.size());
    for(String result: results){
		Node node =  resourceResolver.getResource(result).adaptTo(Node.class);
		Node propNode = node.getNode("jcr:content/data");
		String fromdate = propNode.getProperty("start").getString();
		String title = propNode.getProperty("../jcr:title").getString();
		String href = result+".html";
		String time = "";
		String todate="";
		Date tdt = null;
		String locationLabel = "";
		Date today = new Date();
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(today);
		cal1.add(Calendar.DAY_OF_MONTH, +61);
		Date after60days = cal1.getTime();
		if(propNode.hasProperty("time"))
		{
		   time = propNode.getProperty("time").getString();	
		}
		if(propNode.hasProperty("locationLabel")){
			locationLabel=propNode.getProperty("locationLabel").getString();
		}
		if(propNode.hasProperty("end")){
			todate = propNode.getProperty("end").getString();
			tdt = fromFormat.parse(todate);
		}
		String details = propNode.getProperty("details").getString();
		Date fdt = fromFormat.parse(fromdate);
		Calendar cal = Calendar.getInstance();
		cal.setTime(fdt);
		int month = cal.get(Calendar.MONTH);
	    System.out.println("What is today" +today+ "after60days" +after60days);
	    System.out.println("What is fdt  ----->" +fdt +result);
	    
		if(today.after(fdt) && fdt.before(after60days))
		{
			if(tempMonth!=month)
			  {
				Date d = new Date(cal.getTimeInMillis());
		        String monthName = new SimpleDateFormat("MMMM").format(d);
	    	    String yr = new SimpleDateFormat("yyyy").format(d);
		        tempMonth = month;
		      %>	
	           <%=monthName %>  <%=yr %><hr/>
        <% }
			  // Image
			  boolean hasImage = propNode.hasNode("image");
			  String fileReference = null;
		      String imgWidth = null;
		      String imgHeight = null;
		      String imgAlt = null;
	    	  if (hasImage) {
			        ValueMap imageProps = resourceResolver.resolve(propNode.getPath() + "/image").adaptTo(ValueMap.class);
			        fileReference = imageProps.get("fileReference", "");
			        try{
			        Asset assets = resource.getResourceResolver().getResource(fileReference).adaptTo(Asset.class);
			        Resource rendition =  assets.getRendition("cq5dam.thumbnail.120.80.png");
			        fileReference = rendition.getPath();
			        }catch(Exception e){}
			        imgWidth = imageProps.get("width", "");
			        if (!imgWidth.isEmpty()) imgWidth = "width=\"" + imgWidth + "\"";
			        imgHeight = imageProps.get("height", "");
			        if (!imgHeight.isEmpty()) imgHeight = "height=\"" + imgHeight + "\"";
			        imgAlt = imageProps.get("alt", "");
			        if (!imgAlt.isEmpty()) imgAlt = "alt=\"" + imgAlt + "\"";
			    } 
		%>
<div class="row">
    <div class="small-24 large-24 medium-24 columns">
    <%if(hasImage) {%>
      <div class="small-4 large-4 medium-4 columns left">
          <img src="<%= fileReference %>" <%= imgWidth %> <%= imgHeight %> <%= imgAlt %> />
       </div>  
      <%} %> 
       <div class="small-20 large-20 medium-20 columns right">
          <h4><a href="<%=href%>"><%=title %></a></h4>
         <div class="small-10 large-10 medium-10 columns time">
            <b>Time:</b> <%= time %>
         </div>
         <div class="small-10 large-10 medium-10 columns date">
             <b>Date :</b> <%=toFormat.format(fdt)%> <%if(propNode.hasProperty("end")) {%> to <%=toFormat.format(tdt) %> <%}%>
         </div>
         <%if(!locationLabel.isEmpty()){ %>
           <div class="locationLabel">
              <b>Location: </b><%=locationLabel %>
           </div>
         <%} %>
          <%=details%>
       </div>
       
    </div>  
</div>

<style>
.left{
  align:left;
  
  
}

.right{
 
  padding-left:0px;
  vertical-align: text-top;
}
.date{
  width:50%;
  align:left;
  
}
.time{
  width:50%;
  align:left;
  padding-left:0px;
}

</style>  

  
<%
   }//if
 }//else
%>

<%
}//for
%>
