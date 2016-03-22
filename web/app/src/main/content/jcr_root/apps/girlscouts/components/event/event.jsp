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
	java.util.Set,
	java.util.ArrayList,
	java.util.Iterator,
	java.util.TimeZone,
	com.day.cq.tagging.TagManager,
	com.day.cq.tagging.Tag,
	com.day.cq.dam.api.Asset,
	org.girlscouts.vtk.utils.VtkUtil,
	org.girlscouts.vtk.models.User,
	javax.servlet.http.HttpSession,
	org.joda.time.DateTimeZone,
	org.joda.time.format.DateTimeFormatter,
	org.joda.time.format.DateTimeFormat,
	org.joda.time.DateTime
	"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<cq:defineObjects />
<!-- apps/girlscouts/components/components/event/event.jsp -->
<%

Boolean includeCart = false;

if(homepage.getContentResource().adaptTo(Node.class).hasProperty("event-cart")){
	if(homepage.getContentResource().adaptTo(Node.class).getProperty("event-cart").getString().equals("true")){
		includeCart = true;
	}
}
	
	String currentPath = currentPage.getPath() + ".html";

    // Defining a hashMap for the Program Level - Level and Categories -> Category
    Map<String,String> map = new HashMap<String,String>();
    map.put("Program Level", "Level");
    map.put("Categories", "Category");
	
	// date and time
	String councilTimeZone = homepage.getContentResource().adaptTo(Node.class).getProperty("timezone").getString();
    DateFormat dateFormat = new SimpleDateFormat("EEE MMM d yyyy");
	DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	DateFormat utcFormat =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");//2015-05-31T12:00
	DateFormat formatWTZone = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
	utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	DateFormat timeFormat = new SimpleDateFormat("h:mm a");
    DateFormat calendarFormat = new SimpleDateFormat("M-yyyy");
	
	String visibleDate = properties.get("visibleDate","");
	Date vis;
	if(!visibleDate.isEmpty()){
		try{
			vis = formatWTZone.parse(visibleDate);
		}catch(Exception e){
			formatWTZone = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			vis = formatWTZone.parse(visibleDate);
		}
		
		long visLong = vis.getTime();
		long currentTime = Calendar.getInstance().getTime().getTime();
		if(visLong > currentTime){
			%> 
				<script type="text/javascript">
	   			window.location = "/404.html";
				</script>
		 	<%
		}
	} 

	Calendar startDateCl = properties.get("start", Calendar.class);

	String edtTime = properties.get("start", "");
    Date basedOnTimeZone = dateFormat1.parse(edtTime);
    Calendar cale =  Calendar.getInstance();
    cale.setTime(basedOnTimeZone);
	Date startDate = cale.getTime();

	String startDateStr = dateFormat.format(cale.getTime());
	String startTimeStr = timeFormat.format(cale.getTime());
	
	//Calendar Date and Month

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(cale.getTime());
    int month = calendar.get(Calendar.MONTH)+1;
    int year = calendar.get(Calendar.YEAR);
    String combineMonthYear = month+"-"+year;
    String calendarUrl = currentSite.get("calendarPath",String.class)+".html/"+combineMonthYear;

    String time = startTimeStr;

    String endDateSt = properties.get("end", "");
	// Member type true means it's members only. False means it's public. This was done because salesforce is currently sending us boolean data,
	// but there's a possibility that more member types will be added in the future, and using strings means less of a transition when that happens
	String membersOnly = properties.get("memberOnly","false");
	String eventID = properties.get("eid", "-1");
	String register;
	if(includeCart && !eventID.equals("-1")){
		register = properties.get("register", "https://gsmembers.force.com/members/Event_join?EventId=" + eventID);
	}
	else{
		register = properties.get("register", String.class);
	}

    DateTimeFormatter dtfnotimezone = DateTimeFormat.forPattern("EEE MMM d yyyy, HH:MM"); 
    DateTimeFormatter dtfTimezone = DateTimeFormat.forPattern("EEE MMM d yyyy, HH:MM zzz"); 
	DateTimeFormatter dtftimeonly = DateTimeFormat.forPattern("HH:MM zzz");

	String inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";
	DateTimeFormatter formatter = DateTimeFormat.forPattern(inputPattern);
	DateTime datetime = formatter.parseDateTime(edtTime);
	DateTime dtCouncil = datetime.withZone(DateTimeZone.forID(councilTimeZone));
	String formatedStartDateStr = null;
	if ("".equals(endDateSt)) {
		formatedStartDateStr = dtfTimezone.print(dtCouncil);
	} else {
		formatedStartDateStr = dtfnotimezone.print(dtCouncil);
	}
    
    String formatedEndDateStr="";
    Date endDate=null;
	if (endDateSt != null && !endDateSt.isEmpty()) {
	    Calendar cal1 = Calendar.getInstance();
	    Calendar cal2 = Calendar.getInstance();
	    endDate = dateFormat1.parse(endDateSt);
	    cal2.setTime(endDate);
	    cal1.setTime(startDate);
	    boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
	                      cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
		String endDateStr = dateFormat.format(endDate);
		String endTimeStr = timeFormat.format(endDate);
		
		//consider timezone with joda
		DateTime endDateTime = formatter.parseDateTime(endDateSt);
		DateTime endDateTimeCouncil = endDateTime.withZone(DateTimeZone.forID(councilTimeZone));
		if (!sameDay) {
			formatedEndDateStr = dtfTimezone.print(endDateTimeCouncil);			
		}else{
			formatedEndDateStr =  dtftimeonly.print(endDateTimeCouncil);
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

			try {
	    		if(tags.containsKey(tag.getParent().getTitle())){
	    			tags.get(tag.getParent().getTitle()).add(tag.getTitle());
	    		}else{
	    			List<String> temp = new ArrayList<String>();
	    			temp.add(tag.getTitle());
	    			tags.put(tag.getParent().getTitle(),temp);
	    		}
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	}
    // content
    String title = currentPage.getTitle();
    String details = properties.get("details", " ");

   // address
   String address = properties.get("address", "");
   address = address.replaceAll("[\\n\\r]", " ");

    //Region
    String region = properties.get("region", "");

    //Location Label
    String locationLabel = properties.get("locationLabel","");

%>

<!-- TODO: fix the h2 color in CSS -->
<div class="row">
   <div class="small-24 large-24 medium-24 columns">
      &nbsp;
   </div>

   <div class="small-24 large-24 medium-24 columns">
        <h2  itemprop="name"><%= title %></h2>
   </div>

   <div class="small-24 large-24 medium-24 columns">
      <div id="calendar" style="padding-bottom:10px;">
        <a href="<%=calendarUrl%>">View event on calendar</a>
      </div>
   </div>

</div>
<%
	try {
		String imgPath = properties.get("image","");
		if(!imgPath.isEmpty()){
			%> <img src="<%= imgPath %>" /> <%
		}
		else{
	    imgPath = resource.getPath() + "/image";
	    Node imgNode = resourceResolver.getResource(imgPath).adaptTo(Node.class);

	    if( imgNode.hasProperty("fileReference")){
	%>   <div>
			<p>
			<%= displayRendition(resourceResolver, imgPath, "cq5dam.web.520.520") %>
			</p>
		</div>
<%}
		    }
	} catch (Exception e) {}
	%>

 <div class="row eventListDetail">
	<div class="small-24 medium-12 large-12 columns">
		<div class="row">
			<div class="small-8 medium-8 large-8 columns">
             <b>Date:</b>
			</div>
                        <div class="small-16 medium-16 large-16 columns">
           <b>
           <%try{%>
                        <span itemprop="startDate" itemscope itemtype="http://schema.org/Event" content="<%=utcFormat.format(startDate)%>"><%=formatedStartDateStr%></span>
                        <% if(formatedEndDateStr!=null && !formatedEndDateStr.equals("")){ %>
                            - <span itemprop="stopDate" itemscope itemtype="http://schema.org/Event" content="<%=(endDate==null ? "" : utcFormat.format(endDate))%>"><%=formatedEndDateStr %></span>
                        <%
                        }
                     }catch(Exception eDateStr){eDateStr.printStackTrace();}
                    %>
           </b>
                        </div>
		</div>
                <div class="row">
                        <div class="small-8 medium-8 large-8 columns">
             <b>Location:</b>
                        </div>
                        <div class="small-16 medium-16 large-16 columns" itemprop="location" itemscope itemtype="http://schema.org/Place">
           <b><%= locationLabel %></b> <%if(address!=null && !address.isEmpty()){%><a href="javascript:void(0)" onclick="showMap('<%=address%>')">Map</a><%} %>
                        </div>
                </div>
<%String priceRange = properties.get("priceRange","");
	if (!"".equals(priceRange)) {%>                
                <div class="row">
                	<div class="small-8 medium-8 large-8 columns">
                		<b>Price:</b>
                	</div>
                	<div class="small-16 medium-16 large-16 columns">
                		<b><%= priceRange %></b>
                	</div>
				</div>
	<% } %>
	</div>
        <div class="small-24 medium-12 large-12 columns">
                <div class="row">
             	<%
                 	Iterator<String> str = tags.keySet().iterator();
                	 while(str.hasNext()){
                     String categoryTitle = str.next();
                     if(map.get(categoryTitle)!=null){
               	%>
               		<div class="small-8 medium-8 large-8 columns">
 						<b> <%=map.get(categoryTitle)%>: </b>
               		</div>
          	    <%
               		  Iterator<String> tagValue = tags.get(categoryTitle).iterator();
            	%>
                    <div class="small-16 medium-16 large-16 columns">
				<%
						while(tagValue.hasNext()){
           		 %>
	            	<b> <%=tagValue.next()%><% if(tagValue.hasNext()){ %>,<%} %> </b>

          		<% }%>
                  </div>
                  <% }} %>
                </div>
                <div class="row">
                     <div class="small-8 medium-8 large-8 columns">
 						<%if(!region.isEmpty()){ %>
                			<b>Region: </b>
            			<%} %>
                     </div>
                     <div class="small-16 medium-16 large-16 columns">
							<b><%=region %></b>
                     </div>
                </div>
        </div>
</div>
     <%if(register!=null && !register.isEmpty()){%>
        <div class="eventDetailsRegisterLink">
    	 	<a class="button" href="<%=genLink(resourceResolver, register)%>">REGISTER NOW</a>
    	 	<% if(includeCart && !eventID.equals("-1")){ %>
    	 	<a class="button" onclick="addToCart('<%= title.replace("'","\\'") %>', '<%= eventID %>', '<%= currentPage.getPath() %>'); return false;">Add to MyActivities</a>
    		<%}
    		%>

    	</div>
     <%} %>
<div class="row">
   <div class="small-24 large-24 medium-24 columns">&nbsp;</div>
</div>

<div class="row">
  <div class="small-24 large-24 medium-24 columns" itemprop="description">
   <%=details %>
  </div>
</div>
<div class="row">
   <div class="small-24 large-24 medium-24 columns">&nbsp;</div>
</div>

<script>
function showMap(address){
	window.open('/en/map.html?address='+address);
}
</script>
