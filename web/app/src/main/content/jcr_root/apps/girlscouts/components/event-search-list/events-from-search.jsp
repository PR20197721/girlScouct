<%@ page import="com.day.cq.tagging.TagManager,com.day.cq.dam.api.Asset,java.util.ArrayList,java.util.HashSet, java.util.Locale,java.util.Map,java.util.Iterator,java.util.HashMap,java.util.List,java.util.Set,com.day.cq.search.result.SearchResult, java.util.ResourceBundle,com.day.cq.search.QueryBuilder,javax.jcr.PropertyIterator,
 com.day.cq.i18n.I18n,org.apache.sling.api.resource.ResourceResolver,
	javax.servlet.http.HttpSession,
	com.day.text.Text,
	org.girlscouts.common.events.search.*,
	java.util.Collections" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects/>

<%!
	public class DateComparator implements java.util.Comparator<String> {
		ResourceResolver rr;
	
		public DateComparator(ResourceResolver rr){
			this.rr = rr;
		}
	
		@Override
		public int compare(String s1, String s2){
			try{
				Node n1 =  rr.getResource(s1).adaptTo(Node.class);
				Node n2 =  rr.getResource(s2).adaptTo(Node.class);
				Node prop1 = n1.getNode("jcr:content/data");
				Node prop2 = n2.getNode("jcr:content/data");
				String start1 = prop1.getProperty("start").getString();
				String start2 = prop2.getProperty("start").getString();
				return start1.compareTo(start2);
			} catch(Exception e){
				e.printStackTrace();
				return 0;
			}
		}
	}
    public class AddedDateComparator implements java.util.Comparator<String> {
        ResourceResolver rr;

        public AddedDateComparator(ResourceResolver rr){
            this.rr = rr;
        }

        @Override
        public int compare(String s1, String s2){
            try{
                Node n1 =  rr.getResource(s1).adaptTo(Node.class);
                Node n2 =  rr.getResource(s2).adaptTo(Node.class);
                Node prop1 = n1.getNode("jcr:content");
                Node prop2 = n2.getNode("jcr:content");
                String created1 = prop1.getProperty("jcr:created").getString();
                String created2 = prop2.getProperty("jcr:created").getString();
                return created1.compareTo(created2);
            } catch(Exception e){
                e.printStackTrace();
                return 0;
            }
        }
    }
%>

<%

Boolean includeCart = false;
if(homepage.getContentResource().adaptTo(Node.class).hasProperty("event-cart")){
	if(homepage.getContentResource().adaptTo(Node.class).getProperty("event-cart").getString().equals("true")){
		includeCart = true;
	}
}

GSDateTime today = new GSDateTime();
GSDateTimeFormatter dtfIn = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
GSDateTimeFormatter dtfOutDate = GSDateTimeFormat.forPattern("EEE MMM dd");
GSDateTimeFormatter dtfOutTime = GSDateTimeFormat.forPattern("h:mm aa");
GSDateTimeFormatter dtfOutMY = GSDateTimeFormat.forPattern("MMMM yyyy");
GSDateTimeFormatter dtUTF = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm");
String register = "";
String membersOnly = "false";
String eventID = "-1";

//IF there is no configuration, we set it ourselves using event-search
//We will actually search the events too 
SearchResultsInfo srchInfo = (SearchResultsInfo)request.getAttribute("eventresults");
String q = request.getParameter("q");
String sortBy = request.getParameter("dateAdded");
if(null==srchInfo) {
	%>
	<cq:include path="content/middle/par/event-search" resourceType="girlscouts/components/event-search" />
	<%
}
//The results are stored in srchInfo
srchInfo =  (SearchResultsInfo)request.getAttribute("eventresults");
if(null!=srchInfo) {
    boolean displayHeader = true;
	List<String> results = srchInfo.getResults();
	long hitCounts = srchInfo.getHitCounts();
	SearchResult searchResults = (SearchResult)request.getAttribute("searchResults");
	if(properties.containsKey("isfeatureevents") && properties.get("isfeatureevents").equals("on") ){
	%>
		<cq:include script="feature-events.jsp"/>
	<%
	} else{
		%>
		<div id="eventListWrapper">
		<%
		int tempMonth =-1;
		if (results == null || results.size() == 0) {
		%>
			<p>No event search results for &quot;<i class="error"><%= Text.escapeXml(q) %></i>&quot;.</p>
		<%
		} else {
			long startTime = System.nanoTime();
			if("ascending".equals(sortBy)){
			    Collections.sort(results, new AddedDateComparator(resourceResolver));
			}else if("descending".equals(sortBy)){
			    Collections.sort(results, new AddedDateComparator(resourceResolver));
			    Collections.reverse(results);
			}else{
			    Collections.sort(results, new DateComparator(resourceResolver));
			}

			long estimatedTime = System.nanoTime() - startTime;
			System.out.println("Estimated Sort Time: " + (estimatedTime / 1000000) + "ms");
			for(String result: results) {
				register = "";
				GSDateTime evntComparison = null;
				Node node =  resourceResolver.getResource(result).adaptTo(Node.class);
				GSDateTime startDate = null;
				GSLocalDateTime localStartDate = null;
				try {
					Node propNode = node.getNode("jcr:content/data");
					if(propNode.hasProperty("visibleDate")){
						String visibleDate = propNode.getProperty("visibleDate").getString();
						GSDateTime vis = GSDateTime.parse(visibleDate,dtfIn);
						if(vis.isAfter(today)){
							continue;
						}
					}
					String stringStartDate = propNode.getProperty("start").getString();
					startDate = GSDateTime.parse(stringStartDate,dtfIn);
					eventID = "-1";
				
	                //Add time zone label to date string if event has one
	                String timeZoneLabel = propNode.hasProperty("timezone") ? propNode.getProperty("timezone").getString() : "";
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
						startTimeStr = dtfOutTime.print(localStartDate);
						startDateStr = dtfOutDate.print(localStartDate);
					}
					
					evntComparison = startDate;
					
					if(propNode.hasProperty("eid")){
						eventID = propNode.getProperty("eid").getString();
					}
					if(propNode.hasProperty("register") && !propNode.getProperty("register").getString().equals("")){
						register = propNode.getProperty("register").getString();
					}
					if(propNode.hasProperty("memberOnly")){
						membersOnly = propNode.getProperty("memberOnly").getString();
					}
					String title = propNode.getProperty("../jcr:title").getString();
					String href = result+".html";
					String time = "";
					String locationLabel = "";
					String region = "";
					String formatedStartDateStr = startDateStr + ", " +startTimeStr;
					if(propNode.hasProperty("locationLabel")){
						locationLabel=propNode.getProperty("locationLabel").getString();
					}
					if(propNode.hasProperty("region")){
						region=propNode.getProperty("region").getString();
					}
					String formatedEndDateStr="";
					GSDateTime endDate =null;
					String endDateStr = "";
					String endTimeStr = "";
					GSLocalDateTime localEndDate = null;
					if(propNode.hasProperty("end")){
						endDate = GSDateTime.parse(propNode.getProperty("end").getString(),dtfIn);
						if(dtz != null){
							endDate = endDate.withZone(dtz);
							endDateStr = dtfOutDate.print(endDate);
							endTimeStr = dtfOutTime.print(endDate);
						} else{
							localEndDate = GSLocalDateTime.parse(propNode.getProperty("end").getString(),dtfIn);
							endDateStr = dtfOutDate.print(localEndDate);
							endTimeStr = dtfOutTime.print(localEndDate);
						}
						evntComparison = endDate;
						boolean sameDay = startDate.year() == endDate.year() && startDate.dayOfYear() == endDate.dayOfYear();
						if (!sameDay) {
							//dateStr += " - " + endDateStr +", " + endTimeStr;
							formatedEndDateStr= " - " + endDateStr +", " + endTimeStr;
						}else {
							//dateStr += " - " + endTimeStr;
							formatedEndDateStr= " - " + endTimeStr;
						}
					}
					String details = "";
					if(propNode.hasProperty("details")){
						details = propNode.getProperty("details").getString();
					}
					int month = startDate.monthOfYear();
					
					formatedEndDateStr = formatedEndDateStr + " " + timeZoneShortLabel;

					if(evntComparison.year() > today.year() || (evntComparison.year() == today.year() && (evntComparison.dayOfYear() >= today.dayOfYear()))) {
						if(tempMonth!=month) {
							String monthYr = dtfOutMY.print(startDate);
							tempMonth = month;
							if("none".equals(sortBy)){
							%>
							<div class="eventsList monthSection">
								<div class="leftCol"><b><%=monthYr.toUpperCase() %></b></div>
								<div class="rightCol horizontalRule">&nbsp;</div>
							</div>
							<br/>
							<br/>
							<%
							}else if(displayHeader){
							displayHeader = false;
							%>
                            <div class="eventsList monthSection">
                                <div class="leftCol"><b>Events List</b></div>
                                <div class="rightCol horizontalRule">&nbsp;</div>
                            </div>
                            <br/>
                            <br/>
                            <%
                            }
						}
						%>
					<div class="eventsList eventSection" itemtype="http://schema.org/ItemList">
						<div class="leftCol" itemprop="image">
							<%
							String imgPath;
							if(propNode.hasProperty("thumbImage")){
								imgPath = propNode.getProperty("thumbImage").getString();
								%> <img src="<%= imgPath %>" /> <%
							} else if(propNode.hasProperty("image")){
								imgPath = propNode.getProperty("image").getString();
								%> <img src="<%= imgPath %>" /> <%
							} else{
								try{
                                    Resource eventImgRes = resourceResolver.resolve(propNode.getPath() + "/image");
                                    imgPath = (String)eventImgRes.getValueMap().get("fileReference");                                   
								%> <img src="<%= gsImagePathProvider.getImagePath(imgPath,"cq5dam.web.240.240") %>" /><% 
                                }catch(Exception ex1){
                                }     
							 } %>
						</div>
						<div class="rightCol">
							<h6><a class="bold" href="<%=href%>" itemprop="name"><%=title %></a></h6>
							<% if(membersOnly.equals("true")){
				  				%> <p class="bold">MEMBERSHIP REQUIRED</p> 
				  			<% }%>
							<p class="bold">Date:
						    <%try {%>
		                        <span itemprop="startDate" itemscope itemtype="http://schema.org/Event" content="<%= dtUTF.withZone(GSDateTimeZone.UTC).print(startDate) %>"><%=formatedStartDateStr%></span>
		                        <% if(formatedEndDateStr!=null && !formatedEndDateStr.equals("")){ %>
		                            <span itemprop="stopDate" itemscope itemtype="http://schema.org/Event" content="<%=(endDate==null ? "" : dtUTF.withZone(GSDateTimeZone.UTC).print(endDate))%>"><%=formatedEndDateStr %></span>
		                        <%
		                        }
		                     }catch(Exception eDateStr){eDateStr.printStackTrace();}
		                    %>
							</p>
							<% if(!region.isEmpty()){ %>
								<p class="bold" itemprop="region" itemscope itemptype="http://schema.org/Place">Region: <span itempropr="name"><%= region %></span></p>
							<% } %>				
							<%if(!locationLabel.isEmpty()){ %>
								<p class="bold" itemprop="location" itemscope itemtype="http://schema.org/Place">Location:  <span itemprop="name"><%=locationLabel %></span></p>
							<% } %>
							<% if(propNode.hasProperty("srchdisp")){ %>
								<p itemprop="description"><%=propNode.getProperty("srchdisp").getString()%></p>
							<% } %>
							<%if(includeCart && register!=null && !register.isEmpty() && !eventID.equals("-1")){%>
						        <div class="eventDetailsRegisterLink">
						    	 	<a href="<%=genLink(resourceResolver, register)%>">Register Now</a>
						    	 	<a onclick="addToCart('<%= title.replaceAll("'","\\\\'").replaceAll("\"","&quot") %>', '<%= eventID %>', '<%= href %>', '<%=register %>'); return false;">Add to MyActivities</a>
						    	</div>
    						<%} %>
						</div>
					</div>
					<div class="eventsList bottomPadding"></div>
					<%
					}
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		}%>
	</div>
	<%
	}
}
%>