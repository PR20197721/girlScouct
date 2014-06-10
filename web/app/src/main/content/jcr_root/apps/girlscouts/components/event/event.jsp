<%@ page
	import="java.text.Format,
	java.text.ParseException,
	java.lang.Exception,
	java.text.SimpleDateFormat,
	java.text.DateFormat,
	java.util.Date,
	java.util.Calendar,
	java.util.Map,
	java.util.HashMap,
	java.util.List,
	java.util.ArrayList,
	java.util.Iterator,
	com.day.cq.tagging.TagManager,
	com.day.cq.tagging.Tag,
	com.day.cq.dam.api.Asset
	"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<cq:defineObjects />
<!-- apps/girlscouts/components/components/event/event.jsp -->
<%
	String currentPath = currentPage.getPath() + ".html";
   
	// date and time
    DateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
	DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
    DateFormat calendarFormat = new SimpleDateFormat("M-yyyy");
	Date startDate = properties.get("start", Date.class); 
	
	String startDateStr = dateFormat.format(startDate);
	String startTimeStr = timeFormat.format(startDate);
	
	//Calendar Date and Month
	
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(startDate);
    int month = calendar.get(Calendar.MONTH)+1;
    int year = calendar.get(Calendar.YEAR);
    String combineMonthYear = month+"-"+year;
    String calendarUrl = currentSite.get("calendarPath",String.class)+".html/"+combineMonthYear; 
    String dateStr = startDateStr;
    String time = startTimeStr;
	Date endDate = properties.get("end", Date.class); 

	if (endDate != null) {
	    Calendar cal1 = Calendar.getInstance();
	    Calendar cal2 = Calendar.getInstance();
	    cal1.setTime(startDate);
	    cal2.setTime(endDate);
	    boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
	                      cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
		String endDateStr = dateFormat.format(endDate);
		String endTimeStr = timeFormat.format(endDate);
		if (!sameDay) {
	    	dateStr += " to " + endDateStr;
		} 
	    time += " to " + endTimeStr;
	}
	Map<String,List<String>> tags= new HashMap<String,List<String>>() ;
	if(currentNode.getParent().hasProperty("cq:tags")){
		
		
		ValueMap jcrProps = resourceResolver.getResource(currentNode.getParent().getPath()).adaptTo(ValueMap.class);
		String[] cqTags = jcrProps.get("cq:tags", String[].class);
	    TagManager tagManager = resourceResolver.adaptTo(TagManager.class);

	    for(String str:cqTags)
	    {
	    	
	    	Tag tag  = tagManager.resolve(str);
	    	
	    	if(tags.containsKey(tag.getParent().getTitle()))
	    	{
	    		tags.get(tag.getParent().getTitle()).add(tag.getTitle());
	    	}else{
	    		List<String> temp = new ArrayList<String>();
	    		temp.add(tag.getTitle());
	    		tags.put(tag.getParent().getTitle(),temp);
	    	}
	    }
	}
    // content
    String title = currentPage.getTitle();
    String details = properties.get("details", " ");
   
   // address 
   String address = properties.get("address", "");

    //Region
    String region = properties.get("region", "");
    
    //Location Label
    String locationLabel = properties.get("locationLabel","");
    
%>

<!-- TODO: fix the h2 color in CSS -->
<div class="row">
   <div class="large-17 medium-17 columns">
        <h2 style="color: green;"><%= title %></h2>
   </div>
   <div class="medium-7 columns small-24">
        <a href="<%=calendarUrl%>">View event on calendar</a>
   </div>

</div>

<div>
<p>	
	<%  
		try {
		    String imgPath = resource.getPath() + "/image";
%>
<%= displayRendition(resourceResolver, imgPath, "cq5dam.web.520.520") %>
<%
		} catch (Exception e) {}
	%>
</p>
</div>
 <div>
   <div class="inner">
       <b>Time:</b> <%= time %><br/>
       <b>Date:</b> <%= dateStr %> <br/>
       <% if (!locationLabel.isEmpty()) { %>
            <b>Location:</b> <%= locationLabel %>
        <% } %>
        <% if(!address.isEmpty()){ %>
        
            <a href="/content/girlscouts-usa/en/map.html?address=<%=address%>">Directions</a>
  
        <%} %>
    </div>
   <div class="inner">
      <%
         Iterator<String> str = tags.keySet().iterator();
         while(str.hasNext()){
        	 String categoryTitle = str.next();
        	 
        %>
          <b> <%=categoryTitle %>: </b>
        <%	 
        	Iterator<String> tagValue = tags.get(categoryTitle).iterator();
           while(tagValue.hasNext()){
        	%>   
        	   <%=tagValue.next()%><% if(tagValue.hasNext()){ %>,<%} %>   
          <% }%>
            <br/>
          <%
        	 
         }
      
      %>
       
       <%if(!region.isEmpty()){ %>
           <b>Region: </b><%=region %>
       
       <%} %>
      
        
        
    </div>
 </div>
<div>
   <%=details %>

</div>   


<style>
.inner{
    float:left; 
    width:50%;
    height:75px;
    margin:1px auto;
    padding:10px,5px,0,0;
}

</style>
