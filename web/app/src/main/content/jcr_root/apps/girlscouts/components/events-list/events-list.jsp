<%@ page
	import="com.day.cq.tagging.TagManager,java.util.ArrayList,
            java.util.HashSet,java.text.DateFormat,
            java.text.SimpleDateFormat,java.util.Date,
            java.util.Locale,java.util.Arrays,
            java.util.Iterator,
            java.util.Set,com.day.cq.search.result.SearchResult,
            java.util.ResourceBundle,com.day.cq.search.QueryBuilder,
            javax.jcr.PropertyIterator,org.girlscouts.web.events.search.SearchResultsInfo,
            com.day.cq.i18n.I18n,org.apache.sling.api.resource.ResourceResolver,
            org.girlscouts.web.events.search.*,java.util.Calendar,java.util.TimeZone,com.day.cq.dam.api.Asset,
            java.util.ArrayDeque, java.util.Iterator"%>

<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<!-- apps/girlscouts/components/events-list/events-list.jsp -->
<cq:includeClientLib categories="apps.girlscouts.components.events-list" />
<cq:defineObjects />

<cq:include script="feature-include.jsp" />
<%
	SearchResultsInfo srchInfo = (SearchResultsInfo) request.getAttribute("eventresults");
	if (null == srchInfo) {
%>
<cq:include
	script="/apps/girlscouts/components/event-search/event-search.jsp" />
<%
	srchInfo = (SearchResultsInfo) request.getAttribute("eventresults");
	
	}
%>

<%
	Date today = new Date();
	DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");
	String evtStartDt = formatter.format(today);
	try {
		today = formatter.parse(evtStartDt);
	}catch (Exception e) {}

	java.util.List<String> results = srchInfo.getResults();
	int eventcounts = 0;
	int eventsRendered = 0;
	String key = "";
	String value = "";
	
	if (properties.containsKey("eventcount")) {
			eventcounts = Integer.parseInt(properties.get("eventcount",String.class));
			if (eventcounts > results.size()) {
				eventcounts = results.size();
			}
	}
	
	String designPath = currentDesign.getPath();
	String iconImg = properties.get("fileReference", String.class);
	String eventsLink = properties.get("urltolink", "") + ".html";
	String featureTitle = properties.get("featuretitle","UPCOMING EVENTS");
	int daysofevents = Integer.parseInt(properties.get("daysofevents","0"));
	//filtered by start or end date of the events. by cwu
	String filterProp = properties.get("filter", "end");
	Date startDate = null;
	String href = "";
	String title = "";
%>

			<div class="large-1 columns small-2 medium-1">
				<img src="<%=iconImg%>" width="32" height="32" alt="feature icon" />
			</div>
			<div class="column large-23 small-22 medium-23">
				<div class="row collapse">
					<h2 class="columns large-24 medium-24"><a href="<%=eventsLink%>"><%=featureTitle%></a></h2>
					<ul class="small-block-grid-1 medium-block-grid-2 large-block-grid-2">
						<%
							//com.day.cq.wcm.foundation.List elist= (com.day.cq.wcm.foundation.List)request.getAttribute("elist");
							ArrayDeque<String> featureEvents = (ArrayDeque) request.getAttribute("featureEvents");
							Calendar cale =  Calendar.getInstance();
							if (!featureEvents.isEmpty()) {
								Iterator<String> itemUrl = featureEvents.descendingIterator();
								Date currentDate = new Date();
								while (itemUrl.hasNext()) {
									Node node = resourceResolver.getResource(itemUrl.next()).adaptTo(Node.class);
									href = node.getPath() + ".html";
									try {
										if (node.hasNode("jcr:content/data")) {
											Node propNode = node.getNode("jcr:content/data");

                      //Check for featured events excluded by date 
                     if (propNode.hasProperty(filterProp)){
							cale.setTime(fromFormat.parse(propNode.getProperty(filterProp).getString()));
                     }else {
                    	 cale.setTime(fromFormat.parse(propNode.getProperty("start").getString()));
                     }
										Date eventStartDate = cale.getTime();

                      if(eventStartDate.after(currentDate)){

                          title = propNode.getProperty("../jcr:title").getString();

                          request.setAttribute("propNode", propNode);
                          request.setAttribute("node", node);
                          request.setAttribute("href", href);
                          request.setAttribute("title", title);
                          %>
                              <cq:include script="event-render.jsp" />
                          <%
                              eventsRendered++;
                      }
										}
									} catch (Exception e) {}
								}

							}

						%>
						<%
                            // need to look for the event starting/ending date is great then TODAYS date, if end date is not there, else start >= todays date.
							GSDateTime gsToday = new GSDateTime();
							GSDateTimeFormatter dtfIn = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                            int count = 0;
							if (eventcounts > 0) {
                                if (daysofevents > 0) {
										Calendar cal1 = Calendar.getInstance();
										cal1.add(Calendar.DATE, daysofevents);
										//changing today variable from the current date to the future date
										// based on the users selection.
										today = formatter.parse(formatter.format(cal1.getTime()));
                                }
								for (String result : results) {
									Node node = resourceResolver.getResource(result).adaptTo(Node.class);
									Date fromdate = null;
									try {
										if (node.hasNode("jcr:content/data")) {
											Node propNode = node.getNode("jcr:content/data");
											if(propNode.hasProperty("visibleDate")){
												String visibleDate = propNode.getProperty("visibleDate").getString();

												GSDateTime vis = GSDateTime.parse(visibleDate,dtfIn);
												if(vis.isAfter(gsToday)){
													continue;
												}
											}
											if (propNode.hasProperty(filterProp)) {
												cale.setTime(fromFormat.parse(propNode.getProperty(filterProp).getString()));
												fromdate = cale.getTime();
											} else if (propNode.hasProperty("start")) {
												cale.setTime(fromFormat.parse(propNode.getProperty("start").getString()));
												fromdate = cale.getTime();
											}

											title = propNode.getProperty("../jcr:title").getString();
											try {
												String eventDt = formatter.format(fromdate);
												fromdate = formatter.parse(eventDt);
											} catch (Exception e) {}
											href = result + ".html";
											if (fromdate.after(today) || fromdate.equals(today)) {
												request.setAttribute("propNode", propNode);
												request.setAttribute("node", node);
												request.setAttribute("href", href);
												request.setAttribute("title", title);
												if (!featureEvents.contains(result)) {
															
												%>
													<cq:include script="event-render.jsp" />

												<%		
                                                    eventsRendered++;
													count++;
												}
											}
											if (eventcounts == count) {
												break;
											}
										}
									} catch (Exception e) {}
								}
							}
						%>
					</ul>
				</div><!--/inner row collapse-->

			</div><!--/columns-->
			 <% if(eventsRendered == 0){
                            %>  <div style="height:75px"></div> <%

                        }
				%>



