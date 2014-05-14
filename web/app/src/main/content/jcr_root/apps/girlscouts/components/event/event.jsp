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
	DateFormat timeFormat = new SimpleDateFormat("KK:mm a");
    DateFormat calendarFormat = new SimpleDateFormat("M-yyyy");
	Date startDate = properties.get("start", Date.class); 
	
	String startDateStr = dateFormat.format(startDate);
	String startTimeStr = timeFormat.format(startDate);
	
	//Calendar Date and Month
	
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(startDate);
    int month = calendar.get(Calendar.MONTH);
    int year = calendar.get(Calendar.YEAR);
    String combineMonthYear = month+"-"+year;
    String calendarUrl = currentSite.get("calendarPath",String.class)+".html/"+combineMonthYear; 
	Date endDate = properties.get("end", Date.class); 
	String dateStr = startDateStr;
    String time = startTimeStr;
	if (endDate != null) {
		String endDateStr = dateFormat.format(startDate);
		String endTimeStr = timeFormat.format(endDate);
	    dateStr += " to " + endDateStr;
	    time += " to " + endTimeStr;
	}
	String endDateStr = dateFormat.format(endDate);
	Map<String,List<String>> tags= new HashMap<String,List<String>>() ;
	
	if(currentNode.getParent().hasProperty("cq:tags")){
		
		
		ValueMap jcrProps = resourceResolver.getResource(currentNode.getParent().getPath()).adaptTo(ValueMap.class);
		String[] cqTags = jcrProps.get("cq:tags", String[].class);
	    TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
	    
	    
		
		System.out.println(cqTags.length);
	    for(String str:cqTags)
	    {
	    	System.out.println("String" +str);
	    	Tag tag  = tagManager.resolve(str);
	    	
	    	System.out.println("tag" +tag.getTitle() + tag.getParent().getTitle());
	    	
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

    
    // images
    boolean hasImage = currentNode.hasNode("image");
    String fileReference = null;
    String imgWidth = null;
    String imgHeight = null;
    String imgAlt = null;
    if (hasImage) {
		ValueMap imageProps = resourceResolver.resolve(currentNode.getPath() + "/image").adaptTo(ValueMap.class);
		
		fileReference = imageProps.get("fileReference", "");
		Asset assets = resource.getResourceResolver().getResource(fileReference).adaptTo(Asset.class);
	    
	    Resource rendition =  assets.getRendition("cq5dam.thumbnail.520.215.png");
	    
		fileReference = rendition.getPath();
	    imgWidth = imageProps.get("width", "");
	    if (!imgWidth.isEmpty()) imgWidth = "width=\"" + imgWidth + "\"";
	    imgHeight = imageProps.get("height", "");
	    if (!imgHeight.isEmpty()) imgHeight = "height=\"" + imgHeight + "\"";
	    imgAlt = imageProps.get("alt", "");
	    if (!imgAlt.isEmpty()) imgAlt = "alt=\"" + imgAlt + "\"";
    }

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

<% if (hasImage) { %>
<div>
<p>	
	<img src="<%= fileReference %>" <%= imgWidth %> <%= imgHeight %> <%= imgAlt %> />
</p>

<% } %>
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