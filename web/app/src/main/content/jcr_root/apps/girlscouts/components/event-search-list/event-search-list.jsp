<%@ page import="com.day.cq.tagging.TagManager,com.day.cq.dam.api.Asset,java.util.ArrayList,java.util.HashSet,java.text.DateFormat,java.text.SimpleDateFormat,java.util.Date, java.util.Locale,java.util.Map,java.util.Iterator,java.util.HashMap,java.util.List,java.util.Set,com.day.cq.search.result.SearchResult, java.util.ResourceBundle,com.day.cq.search.QueryBuilder,javax.jcr.PropertyIterator,org.girlscouts.web.events.search.SearchResultsInfo, com.day.cq.i18n.I18n,org.apache.sling.api.resource.ResourceResolver,org.girlscouts.web.events.search.EventsSrch,org.girlscouts.web.events.search.FacetsInfo,java.util.Calendar,java.util.TimeZone" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects/>
<% 
DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.S");
fromFormat.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("GMT")));
DateFormat dateFormat = new SimpleDateFormat("EEE MMM d yyyy");
DateFormat timeFormat = new SimpleDateFormat("h:mm a");
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
 <div class="row">
	<div class="small-24 large-24 medium-24 columns">&nbsp;</div>
</div>  
<%

    int tempMonth =0;
    for(String result: results)
    {
		Node node =  resourceResolver.getResource(result).adaptTo(Node.class);
		try
		{
			  Node propNode = node.getNode("jcr:content/data");
			  Date startDate = propNode.getProperty("start").getDate().getTime();
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
		
			  String startDateStr = dateFormat.format(startDate);
			  String startTimeStr = timeFormat.format(startDate);
			  String dateStr = startDateStr + ", " +startTimeStr;
		
		
			  if(propNode.hasProperty("locationLabel")){
				    locationLabel=propNode.getProperty("locationLabel").getString();
				    }
			  if(propNode.hasProperty("end")){
				  	Date endDate = propNode.getProperty("end").getDate().getTime();
				   	Calendar cal2 = Calendar.getInstance();
				    Calendar cal3 = Calendar.getInstance();
				     cal2.setTime(startDate);
				      cal3.setTime(endDate);
				      boolean sameDay = cal2.get(Calendar.YEAR) == cal3.get(Calendar.YEAR) &&
	                          cal2.get(Calendar.DAY_OF_YEAR) == cal3.get(Calendar.DAY_OF_YEAR);
				      String endDateStr = dateFormat.format(endDate);
				      String endTimeStr = timeFormat.format(endDate);
				      if (!sameDay) {
				    	      dateStr += " - " + endDateStr +", " + endTimeStr;
				    	   }else
				    	   {
				    		   dateStr += " - " + endTimeStr;
	            
				    		}
	    	todate = propNode.getProperty("end").getString();
			tdt = fromFormat.parse(todate);
		}
		String details = propNode.getProperty("details").getString();
		//Date fdt = fromFormat.parse(startDate);
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		int month = cal.get(Calendar.MONTH);
	
		if(startDate.after(today) && startDate.before(after60days))
		{
			if(tempMonth!=month)
			  {
				Date d = new Date(cal.getTimeInMillis());
		        String monthName = new SimpleDateFormat("MMMM").format(d);
	    	    String yr = new SimpleDateFormat("yyyy").format(d);
		        tempMonth = month;
		      %>
		    
		    <div class="row">
		        <div class="small-24 large-24 medium-24 columns event-table">
		       <table width="100%">
		        <tr>
		          <td width="25% nowrap"> <b> <%=monthName.toUpperCase() %>  <%=yr %></b></td>
		          <td class="hrStyle"><hr/></td>
		        </tr>
		      </table>
		      </div>
		    </div>   
        <% } %>

  <div class="events-lists">
    <div class=row>
      <div class="small-4 medium-4 large-4 columns">
           <div id="paddingTop"> 
              	<%
              		String imgPath = propNode.getPath() + "/image";
%>
<%= displayRendition(resourceResolver, imgPath, "cq5dam.web.120.80") %>
<%
              	%>
             </div>  
     </div>
      
       <div class="small-20 large-20 medium-20 columns" style="padding-left:0px">
         <div class="row">
           <div class="small-24 large-24 medium-24 columns event-title">
               <h6><a href="<%=href%>"><%=title %></a></h6>
           </div>
         
         </div>
        <div class="row">
             <div class="small-24 large-24 medium-24 columns lineHeight">
                 <b>Date: </b> <%=dateStr%>
           </div>
        </div>
          <div class="row">
              <div class="small-24 large-24 medium-24 columns lineHeight">
                <%if(!locationLabel.isEmpty()){ %>
                    <div class="locationLabel">
                      <b>Location: </b><%=locationLabel %>
                  </div>
                <%} %>
                </div>
           </div>
          <div class="row">
              <div class="small-24 large-24 medium-24 columns lineHeight">
                <%if(propNode.hasProperty("srchdisp")){ %>
                 <%=propNode.getProperty("srchdisp").getString()%>
                 <%} %>
            </div>
          </div> 
       </div>
    </div> 
    
    
    
    
  </div>
  
    
<%
   }//if
   }catch(Exception e){}
 }//for
%>

<%
 
}//else
%>

