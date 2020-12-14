<%@ page
	import="java.lang.Exception,
	java.util.Map,
	java.util.HashMap,
	java.util.List,
	java.util.ArrayList,
	java.util.Iterator,
	java.util.stream.Collectors,
	java.util.stream.Stream,
	java.util.Arrays,
	java.util.regex.Matcher,
	java.util.regex.Pattern,
	com.google.gson.Gson,
    com.google.gson.GsonBuilder,
	com.day.cq.tagging.TagManager,
	com.day.cq.tagging.Tag,
	com.day.cq.commons.Doctype,
    com.day.cq.wcm.api.components.DropTarget,
    com.day.cq.wcm.foundation.Image,
    org.girlscouts.common.events.search.*
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
	String stringRegOpenDate = properties.get("regOpen","");
	GSDateTime startDate = GSDateTime.parse(stringStartDate,dtfIn);
	GSDateTime regOpenDate = null;
	try{
		regOpenDate = GSDateTime.parse(stringRegOpenDate,dtfIn);
	}catch(Exception e){}
	GSLocalDateTime localStartDate = null;
	GSLocalDateTime localRegOpenDate = null;
	
    //Add time zone label to date string if event has one
    String timeZoneLabel = properties.get("timezone","");
    String timeZoneShortLabel = "";
    GSDateTimeZone dtz = null;
    String startDateStr = "";
    String startTimeStr = "";
    String regOpenDateStr = "";
    String regOpenTimeStr = "";
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
			if(regOpenDate != null){
				regOpenDate = regOpenDate.withZone(dtz);
				regOpenDateStr = dtfOutDate.print(regOpenDate);
				regOpenTimeStr = dtfOutTime.print(regOpenDate);
			}
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
		if(stringRegOpenDate != null && !"".equals(stringRegOpenDate)){
			localRegOpenDate = GSLocalDateTime.parse(stringRegOpenDate,dtfIn);
			regOpenDateStr = dtfOutDate.print(localRegOpenDate);
			regOpenTimeStr = dtfOutTime.print(localRegOpenDate);
		}
	}

	String formatedStartDateStr = startDateStr + ", " +startTimeStr;
	String formattedRegOpenDateStr = "";

	Node dataNode = currentPage.adaptTo(Node.class);
	try{
        if(dataNode.hasNode("jcr:content")){
            dataNode = dataNode.getNode("jcr:content");
            if(dataNode.hasNode("data")){
                dataNode = dataNode.getNode("data");
            }
        }

	} catch(Exception e){
	    e.printStackTrace();
	}
	String regDisplay = "false";
    if(dataNode.hasProperty("regDisplay")){
        regDisplay = dataNode.getProperty("regDisplay").getString();
    }


	if(!"".equals(regOpenDateStr) && !"".equals(regOpenTimeStr)){
        if("true".equals(regDisplay)){
    	    formattedRegOpenDateStr = regOpenDateStr;
    	}else{
    	    formattedRegOpenDateStr = regOpenDateStr + ", " + regOpenTimeStr;
    	}
    }

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
	String formattedRegCloseDateStr="";
	GSDateTime endDate =null;
	GSDateTime regCloseDate = null;
	String endDateStr = "";
	String endTimeStr = "";
	String regCloseDateStr = "";
	String regCloseTimeStr = "";
	GSLocalDateTime localEndDate = null;
	GSLocalDateTime localRegCloseDate = null;
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
	
	if(!"".equals(properties.get("regClose","")) && null != regOpenDate){
		regCloseDate = GSDateTime.parse(properties.get("regClose",""),dtfIn);
		if(dtz != null){
			regCloseDate = regCloseDate.withZone(dtz);
			regCloseDateStr = dtfOutDate.print(regCloseDate);
			regCloseTimeStr = dtfOutTime.print(regCloseDate);
		}else{
			localRegCloseDate = GSLocalDateTime.parse(properties.get("regClose",""),dtfIn);
			regCloseDateStr = dtfOutDate.print(localRegCloseDate);
			regCloseTimeStr = dtfOutTime.print(localRegCloseDate);
		}
		boolean regSameDay = regOpenDate.year() == regCloseDate.year() && regOpenDate.dayOfYear() == regCloseDate.dayOfYear();
		if(!regSameDay){
		    if("true".equals(regDisplay)){
		        formattedRegCloseDateStr = " - " + regCloseDateStr;
		    } else{
		        formattedRegCloseDateStr = " - " + regCloseDateStr + ", " + regCloseTimeStr;
		    }

		}else{
		     if("true".equals(regDisplay)){
                formattedRegCloseDateStr = " - " + regCloseDateStr;
             } else{
			    formattedRegCloseDateStr = " - " + regCloseTimeStr;
			 }
		}
	}
	
	formatedEndDateStr = formatedEndDateStr + " " + timeZoneShortLabel;
	formattedRegCloseDateStr = formattedRegCloseDateStr + " " + timeZoneShortLabel;


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

	// Image Paths
	List<String> imagePaths = new ArrayList<>();
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
<div>
	<p>
	<%
		try {
			String imgExtPath = properties.get("imagePath","");
            String imgPath = resource.getPath()+"/image";
            if(!imgExtPath.isEmpty()){
				imagePaths.add(imgExtPath);
                %> <img src="<%= imgExtPath %>" /> <%
            }
        	else if (resourceResolver.getResource(imgPath) != null)
            {

                Node imageNode = resourceResolver.getResource(imgPath).adaptTo(Node.class);
                if(imageNode.hasProperty("fileReference")){
					String imageSrc = imageNode.getProperty("fileReference").getString();
					imagePaths.add(imageSrc);
                    %> <img src="<%= imageSrc %>" /> <%
                }
                else if (imageNode.hasNodes()){
                    Image image = new Image(resource.getChild("image"));
					String imageSrc = gsImagePathProvider.getImagePathByLocation(image);
					imagePaths.add(imageSrc);
                    image.setSrc(imageSrc);
                    Node imgNode = resourceResolver.getResource(resource.getChild("image").getPath()).adaptTo(Node.class);
                    String width;
                    String height;
                    if(imgNode.hasProperty("./width")){
                        width = imgNode.getProperty("./width").getString();
                    } else{
                        width = "0";
                    }
                    if(imgNode.hasProperty("./height")){
                        height = imgNode.getProperty("./height").getString();
                    } else{
                        height = "0";
                    }
                    try{

                        //drop target css class = dd prefix + name of the drop target in the edit config
                        image.addCssClass(DropTarget.CSS_CLASS_PREFIX + "image");
                        image.loadStyleData(currentStyle);
                        image.setSelector(".img"); // use image script
                        image.setDoctype(Doctype.fromRequest(request));
                        if (!"0".equals(width)) {
                            image.addAttribute("width", width + "px");
                        }
                        if (!"0".equals(height)) {
                            image.addAttribute("height", height + "px");
                        }

                        Boolean newWindow = properties.get("./newWindow", false);

                        // add design information if not default (i.e. for reference paras)
                        if (!currentDesign.equals(resourceDesign)) {
                            image.setSuffix(currentDesign.getId());
                        }

                        if(!newWindow) {
                           image.draw(out);
                        } else { %>
                            <%= image.getString().replace("<a ", "<a target=\"_blank\"") %>
                            <%
                        }
                    }catch (Exception e){}
                }
			}
		} catch (Exception e) {}
	%>
	</p>
</div>
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

<% if(regOpenDate != null && regCloseDate != null) {%>
	<div class="row">
	<div class="small-10 medium-10 large-10 columns">
    	<b>Registration:</b>
	</div>
    <div class="small-14 medium-14 large-14 columns">
	<b>
		<%try{%>
			<% if(formattedRegCloseDateStr!=null && !formattedRegCloseDateStr.equals("")){ %>
				<span itemprop="startDate" itemscope itemtype="http://schema.org/Event" content="<%=dtUTF.withZone(GSDateTimeZone.UTC).print(regOpenDate)%>"><%=formattedRegOpenDateStr%></span>
                <span itemprop="stopDate" itemscope itemtype="http://schema.org/Event" content="<%=(regCloseDate==null ? "" : dtUTF.withZone(GSDateTimeZone.UTC).print(regCloseDate))%>"><%=formattedRegCloseDateStr %></span>
                <%
                }
			}catch(Exception eDateStr){eDateStr.printStackTrace();}
            %>
	</b>
	</div>
	</div>
	<% } %>
	
	<% String programType = properties.get("progType","");
		if(!"".equals(programType)){%>
		        <div class="row">
                	<div class="small-10 medium-10 large-10 columns">
                		<b>Program Type:</b>
                	</div>
                	<div class="small-14 medium-14 large-14 columns">
                		<b><%= programType %></b>
                	</div>
				</div>
	<% } %>
	
	<% String grades = properties.get("grades","");
		if(!"".equals(grades)){%>
	        <div class="row">
               	<div class="small-10 medium-10 large-10 columns">
               		<b>Grades:</b>
               	</div>
               	<div class="small-14 medium-14 large-14 columns">
               		<b><%= grades %></b>
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
                
                <% String girlFee = properties.get("girlFee","");
					if(!"".equals(girlFee)){%>
				        <div class="row">
		                	<div class="small-8 medium-8 large-8 columns">
		                		<b>Girl Fee:</b>
		                	</div>
		                	<div class="small-16 medium-16 large-16 columns">
		                		<b><%= girlFee %></b>
		                	</div>
						</div>
				<% } %>
				
				<% String adultFee = properties.get("adultFee","");
					if(!"".equals(adultFee)){%>
				        <div class="row">
		                	<div class="small-8 medium-8 large-8 columns">
		                		<b>Adult Fee:</b>
		                	</div>
		                	<div class="small-16 medium-16 large-16 columns">
		                		<b><%= adultFee %></b>
		                	</div>
						</div>
				<% } %>
				
				<% String minAttend = properties.get("minAttend","");
					if(!"".equals(minAttend)){%>
				        <div class="row">
		                	<div class="small-18 medium-18 large-18 columns">
		                		<b>Minimum Attendance:</b>
		                	</div>
		                	<div class="small-6 medium-6 large-6 columns">
		                		<b><%= minAttend %></b>
		                	</div>
						</div>
				<% } %>
				
				<% String maxAttend = properties.get("maxAttend","");
					if(!"".equals(maxAttend)){%>
				        <div class="row">
		                	<div class="small-18 medium-18 large-18 columns">
		                		<b>Maximum Attendance:</b>
		                	</div>
		                	<div class="small-6 medium-6 large-6 columns">
		                		<b><%= maxAttend %></b>
		                	</div>
						</div>
				<% } %>
        </div>
</div>
     <%if(register!=null && !register.isEmpty()){%>
        <div class="eventDetailsRegisterLink">
    	 	<a class="button" href="<%=genLink(resourceResolver, register)%>">REGISTER NOW</a>
    	 	<% if(includeCart && !eventID.equals("-1")){ %>
    	 	<a class="button" onclick="addToCart('<%= title.replaceAll("'","\\\\'").replaceAll("\"","&quot") %>', '<%= eventID %>', '<%= currentPage.getPath() %>', '<%=register %>'); return false;">Add to MyActivities</a>
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

<%!
static final Pattern pricePattern = Pattern.compile("\\d+|\\d+[.]\\d\\d"); // Match 27 or 27.00 format

Boolean strExists (String str) {
	return str != null && !str.isEmpty();
}

String getPriceFromStr(String str) {
	String price = "";
    Matcher m = pricePattern.matcher(str);
	if (m.find()) {
		price = m.group(0);
	}
	return price;
} 

Map<String, String> createAdditionalProperty(String name, String value) {
	Map<String, String> property = new HashMap<>();
	property.put("@type", "PropertyValue");
	property.put("name", name);
	property.put("value", value);
	return property;
}

Map<String, String> createOffer(String name, String price) {
	Map<String, String> offer = new HashMap<>();
	offer.put("@type", "Offer");
	offer.put("name", name);
	offer.put("price", getPriceFromStr(price));
	offer.put("priceCurrency", "USD");
	return offer;
}
%>
<%
String fullUrl = request.getRequestURL().toString();
String host = request.getScheme() + "://" + request.getServerName(); //+ ":" + request.getServerPort() + request.getContextPath();
List<String> onlineLocations = Arrays.asList("virtual", "webinar", "online", "facebook", "your location", "your home", "zoom");
Boolean onlineLocationMatch = onlineLocations.stream().parallel().anyMatch(locationLabel.toLowerCase()::contains); // Look for a single partial match
String onlineLocationURL = strExists(register) ? genLink(resourceResolver, register) : fullUrl;

// https://developers.google.com/search/docs/data-types/event#datatypes
String eventDescription = details.replaceAll("\\<.*?\\>", ""); // Use replaceAll to clean out HTML elements
String eventStartDate = dtfIn.print(startDate);
String eventEndDate = endDate==null ? "" : dtfIn.print(endDate);
String eventAddress = strExists(address) ? address : locationLabel;
String eventAttendanceMode = onlineLocationMatch ? "https://schema.org/OnlineEventAttendanceMode" : "https://schema.org/OfflineEventAttendanceMode";
String eventStatus = title.toLowerCase().startsWith("cancelled") ? "https://schema.org/EventCancelled" : "https://schema.org/EventScheduled";
List<String> eventImages = imagePaths.stream().map(path -> host + path).collect(Collectors.toList());

Map<String, Object> json = new HashMap<>();
Map<String, Object> location = new HashMap<>();
Map<String, Object> image = new HashMap<>();
List<Map<String, String>> offers = new ArrayList<>();
List<Map<String, String>> additionalProperties = new ArrayList<>();

json.put("@context", "https://schema.org");
json.put("@type", "Event");
json.put("name", title); // Required
json.put("startDate", eventStartDate); // Required
if (strExists(eventEndDate)) json.put("endDate", eventEndDate);
json.put("eventAttendanceMode", eventAttendanceMode);
json.put("eventStatus", eventStatus);
if (onlineLocationMatch) {
	location.put("@type", "VirtualLocation");
	location.put("url", onlineLocationURL); // Required
} else {
	location.put("@type", "Place");
	location.put("name", locationLabel); // Required
	location.put("address", eventAddress); // Required
	if (strExists(region)) additionalProperties.add(createAdditionalProperty("Region", region));
	if (!additionalProperties.isEmpty()) location.put("additionalProperty", additionalProperties);
}
json.put("location", location); // Required
if (!eventImages.isEmpty()) json.put("image", eventImages);
json.put("description", eventDescription);
if (strExists(adultFee)) offers.add(createOffer("Adult Fee", adultFee));
if (strExists(girlFee)) offers.add(createOffer("Girl Fee", girlFee));
if (!offers.isEmpty()) json.put("offers", offers);

Gson gson = new GsonBuilder().setPrettyPrinting().create();
String jsonOutput = gson.toJson(json);
%>
<script type="application/ld+json">
<%= jsonOutput %>
</script>