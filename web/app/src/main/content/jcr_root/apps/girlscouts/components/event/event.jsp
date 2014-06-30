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
   
    // Defining a hashMap for the Program Level - Level and Categories -> Category
    Map<String,String> map = new HashMap<String,String>();
    map.put("Program Level", "Level");
    map.put("Categories", "Category");		
    
	// date and time
    DateFormat dateFormat = new SimpleDateFormat("EEE MMM d yyyy");
	DateFormat timeFormat = new SimpleDateFormat("h:mm a");
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
   
    String time = startTimeStr;
	Date endDate = properties.get("end", Date.class); 
	
	String register = properties.get("register", String.class);
	
	//Start Time : startTimeStr var called time
	
	String dateStr = startDateStr + ", " +startTimeStr;

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
	    	dateStr += " - " + endDateStr +", " + endTimeStr;
		    
		}else{
			dateStr += " - " + endTimeStr;
			
		}
	       
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
   <div class="small-14 large-14 medium-14 columns">
      &nbsp;
   </div>
   <div class="small-10 large-10 medium-10 columns">
      <div id="calendar">
        <a href="<%=calendarUrl%>">View event on calendar</a>
      </div>  
   </div>
   <div class="small-24 large-24 medium-24 columns">
        <h2><%= title %></h2>
   </div>

</div>
<%  
	try {
	    String imgPath = resource.getPath() + "/image";
	    Node imgNode = resourceResolver.getResource(imgPath).adaptTo(Node.class);
	   
	    if( imgNode.hasProperty("fileReference")){
	%>   <div>
			<p>	
			<%= displayRendition(resourceResolver, imgPath, "cq5dam.web.520.520") %>
			</p>
		</div>	
<%
		    }
	} catch (Exception e) {}
	%>

 <div class="row">
   <div class="small-12 large-12 medium-12 columns">
       <div class="row">
         <div class="small-8 large-8 medium-8 columns lineHeight">
             <b>Date:</b> 
            
         </div>
         <div class="small-16 large-16 medium-16 columns lineHeight">
            <%= dateStr %>
         </div>
       </div>
       
       <div class="row">
         <div class="small-8 large-8 medium-8 columns lineHeight">
             <b>Locations:</b> 
            
         </div>
         <div class="small-16 large-16 medium-16 columns lineHeight">
           <%= locationLabel %> 
         </div>
       </div>
       
    </div>
    <div class="row">
     <div class="small-12 large-12 medium-12 columns">
        
        <%
         Iterator<String> str = tags.keySet().iterator();
         while(str.hasNext()){
             String categoryTitle = str.next();
             
        %>
         <div class="row">
         <div class="small-8 large-8 medium-8 columns lineHeight">
                  <b> <%=map.get(categoryTitle)%>: </b>
         </div>
        <%   
            Iterator<String> tagValue = tags.get(categoryTitle).iterator();
            %>
              <div class="small-16 large-16 medium-16 columns lineHeight"> 
            
           <%  
           while(tagValue.hasNext()){
            %> 
            
               <%=tagValue.next()%><% if(tagValue.hasNext()){ %>,<%} %> 
                 
          <% }%>
            </div>
          </div>
          <div class="row">
              <div class="small-24 large-24 medium-24 columns">&nbsp;</div>
          </div>  
          <%
          }
      
      %>
        <div class="row">
           <div class="small-10 large-10 medium-10 columns">
            <%if(!region.isEmpty()){ %>
                 <b>Region: </b>
             <%} %>
           </div>
            <div class="small-14 large-14 medium-14 columns"> 
                <%=region %>
            </div>
          
         </div>
       </div>
     </div>
    </div>
    
    
<div class="row">
  <div class="small-16 large-16 medium-16 columns">
     &nbsp;
  </div>
   <div class="small-8 large-8 medium-8 columns">
     <%if(register!=null && !register.isEmpty()){%>
        <div class="register">
    	 <a href="<%=genLink(resourceResolver, register)%>">Register for this event</a>
    	</div>   
     <%} %>
  </div>
</div>    
<div class="row">
   <div class="small-24 large-24 medium-24 columns">&nbsp;</div>
</div>  
   
<div class="row">
  <div class="small-24 large-24 medium-24 columns">
   <%=details %>
  </div>
</div>      
    
  

