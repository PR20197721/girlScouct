<%@ page
	import="java.text.Format,
	java.text.ParseException,
	java.lang.Exception,
	java.util.Map,
	java.util.HashMap,
	java.util.List,
	java.util.ArrayList,
	java.util.Iterator,
	com.day.cq.tagging.TagManager,
	com.day.cq.tagging.Tag,
	com.day.cq.dam.api.Asset,
	org.girlscouts.vtk.utils.VtkUtil,
	org.girlscouts.vtk.models.User,
	javax.servlet.http.HttpSession,
	org.girlscouts.web.events.search.*
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
    //String locale =  currentSite.get("locale", "America/Chicago");
	//TimeZone tZone = TimeZone.getTimeZone(locale);
	
	GSDateTime today = new GSDateTime();
	GSDateTimeFormatter dtfIn = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	GSDateTimeFormatter dtfOutDate = GSDateTimeFormat.forPattern("EEE MMM dd");
	GSDateTimeFormatter dtfOutTime = GSDateTimeFormat.forPattern("h:mm aa");
	GSDateTimeFormatter dtfOutMY = GSDateTimeFormat.forPattern("MMMM yyyy");
	GSDateTimeFormatter dtfOutMYCal = GSDateTimeFormat.forPattern("MM'-'yyyy");
	GSDateTimeFormatter dtUTF = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm");
	
	String visibleDate = properties.get("visibleDate","");
	GSDateTime vis = null;
	if(!visibleDate.isEmpty()){
		try{
			vis = GSDateTime.parse(visibleDate,dtfIn);
			if(vis.isAfter(today)){
				%> 
					<script type="text/javascript">
		   			window.location = "/404.html";
					</script>
			 	<%
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	} 

	String stringStartDate = properties.get("start","");
	GSDateTime startDate = GSDateTime.parse(stringStartDate,dtfIn);
	GSLocalDateTime localStartDate = null;
	
    //Add time zone label to date string if event has one
    String timeZoneLabel = properties.get("timezone","");
    String timeZoneShortLabel = "";
    GSDateTimeZone dtz = null;
    String startDateStr = "";
    String startTimeStr = "";
    Boolean useRaw = timeZoneLabel.length() < 4;
    
	if(!timeZoneLabel.isEmpty() && !useRaw){
		int openParen1 = timeZoneLabel.indexOf("(");
		int openParen2 = timeZoneLabel.indexOf("(",openParen1+1);
		int closeParen = timeZoneLabel.indexOf(")",openParen2);
		if(closeParen != -1 && openParen2 != -1 && timeZoneLabel.length() > openParen2){
			timeZoneLabel = timeZoneLabel.substring(openParen2+1,closeParen);
		}
		try{
			dtz = GSDateTimeZone.forID(timeZoneLabel);
			startDate = startDate.withZone(dtz);
			timeZoneShortLabel = dtz.getShortName(startDate.getMillis());
			startDateStr = dtfOutDate.print(startDate);
			startTimeStr = dtfOutTime.print(startDate);
		}catch(Exception e){
			useRaw = true;
			e.printStackTrace();
		}
		//startDate = new GSDateTime(startDate.getMillis());
	}
	if(timeZoneLabel.isEmpty() || useRaw){
		timeZoneShortLabel = timeZoneLabel;
		localStartDate = GSLocalDateTime.parse(stringStartDate,dtfIn);
		startDateStr = dtfOutDate.print(localStartDate);
		startTimeStr = dtfOutTime.print(localStartDate);
	}

	String formatedStartDateStr = startDateStr + ", " +startTimeStr;

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

	String formatedEndDateStr="";
	GSDateTime endDate =null;
	String endDateStr = "";
	String endTimeStr = "";
	GSLocalDateTime localEndDate = null;
	if(!"".equals(properties.get("end",""))){
		endDate = GSDateTime.parse(properties.get("end",""),dtfIn);
		if(dtz != null){
			endDate = endDate.withZone(dtz);
			endDateStr = dtfOutDate.print(endDate);
			endTimeStr = dtfOutTime.print(endDate);
		} else{
			localEndDate = GSLocalDateTime.parse(properties.get("end",""),dtfIn);
			endDateStr = dtfOutDate.print(localEndDate);
			endTimeStr = dtfOutTime.print(localEndDate);
		}
		boolean sameDay = startDate.year() == endDate.year() && startDate.dayOfYear() == endDate.dayOfYear();
		if (!sameDay) {
			//dateStr += " - " + endDateStr +", " + endTimeStr;
			formatedEndDateStr= " - " + endDateStr +", " + endTimeStr;
		}else {
			//dateStr += " - " + endTimeStr;
			formatedEndDateStr= " - " + endTimeStr;
		}
	}
	
	formatedEndDateStr = formatedEndDateStr + " " + timeZoneShortLabel;


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
	if(address.indexOf("'",0) > 0) {
    	if(address.indexOf("\\'",0) <= 0) {
	   		address = address.replace("'","\\'");
    	}
    }
    
    //Region
    String region = properties.get("region", "");

    //Location Label
    String locationLabel = properties.get("locationLabel","");
    
    String monthYr = dtfOutMYCal.print(startDate);
    String calendarUrl = currentSite.get("calendarPath",String.class)+".html/"+monthYr;

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
						<span itemprop="startDate" itemscope itemtype="http://schema.org/Event" content="<%=dtUTF.withZone(GSDateTimeZone.UTC).print(startDate)%>"><%=formatedStartDateStr%></span>
                        <% if(formatedEndDateStr!=null && !formatedEndDateStr.equals("")){ %>
                            <span itemprop="stopDate" itemscope itemtype="http://schema.org/Event" content="<%=(endDate==null ? "" : dtUTF.withZone(GSDateTimeZone.UTC).print(endDate))%>"><%=formatedEndDateStr %></span>
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
				<b><%= locationLabel %></b> <%--if(address!=null && !address.isEmpty()){--%>
				<%-- Not all addresses are valid addresses. We use Google Maps API to validate and show "maps" link on event list
    				but however there's a quota of 2500/day. If there are two commas, it's likely a valid address and check
    				otherwise do not show "map" link --%>
					<% if ((address.indexOf(",") != -1) && (address.indexOf(",") != address.lastIndexOf(","))) {%>
						<a href="javascript:void(0)" onclick="showMap('<%=address%>')">Map</a>
					<%} %>
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
    	 	<a class="button" onclick="addToCart('<%= title.replaceAll("'","\\\\'").replaceAll("\"","&quot") %>', '<%= eventID %>', '<%= currentPage.getPath() %>'); return false;">Add to MyActivities</a>
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
	//window.open('/en/map.html?address='+address);
	window.open('http://www.google.com/maps/search/' + address);
}
</script>
