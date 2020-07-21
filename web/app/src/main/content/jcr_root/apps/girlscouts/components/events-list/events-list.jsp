<%@ page
	import="com.day.cq.tagging.TagManager,java.util.ArrayList,
            java.util.HashSet,java.text.DateFormat,
            java.text.SimpleDateFormat,java.util.Date,
            java.util.Locale,java.util.Arrays,java.util.List,
            java.util.Iterator,java.util.Map,java.util.HashMap,
            java.util.Set,com.day.cq.search.result.SearchResult,
            java.util.ResourceBundle,com.day.cq.search.QueryBuilder,
            javax.jcr.PropertyIterator,
            com.day.cq.i18n.I18n,org.apache.sling.api.resource.ResourceResolver,
            java.util.Calendar,java.util.TimeZone,com.day.cq.dam.api.Asset,
            java.util.ArrayDeque"%>

<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<!-- apps/girlscouts/components/events-list/events-list.jsp -->
<cq:includeClientLib categories="apps.girlscouts.components.events-list" />
<cq:defineObjects />

<cq:include script="feature-include.jsp" />
<%
	String iconImg = properties.get("fileReference", "#");
	String eventsLink = properties.get("urltolink", "");
	eventsLink = eventsLink.isEmpty() ? "" : eventsLink+ ".html";
	String featureTitle = properties.get("featuretitle", "UPCOMING EVENTS");
	int eventCount = Integer.parseInt(properties.get("eventcount", "0"));
	int daysofevents = Integer.parseInt(properties.get("daysofevents", "0"));
	String filterProp = properties.get("filter", "end"); // filtered by start or end date of the events. by cwu

	// Upcoming Events
	SearchResultsInfo srchInfo = (SearchResultsInfo) request.getAttribute("eventresults");
	if (srchInfo == null) {
		%><cq:include script="/apps/girlscouts/components/event-search/event-search.jsp" /><%
		srchInfo = (SearchResultsInfo) request.getAttribute("eventresults");
	}
	List<String> srchInfoList = srchInfo != null ? srchInfo.getResults() : new ArrayList<String>();

	// Tagged Events
	ArrayDeque<String> taggedEvents = (ArrayDeque) request.getAttribute("taggedEvents");
	List<String> taggedEventsList = new ArrayList<>(taggedEvents);

	// Select Events based upon config
	List<String> results = !taggedEventsList.isEmpty() ? taggedEventsList : srchInfoList;

	Date today = new Date();
	DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");
	String evtStartDt = formatter.format(today);
	try {
		today = formatter.parse(evtStartDt);
	} catch (Exception e) {}
	int eventsRendered = 0;
	List<Map<String, Object>> renderMaps = new ArrayList<>();
%>

	<div class="large-1 columns small-2 medium-1">
		<img src="<%=iconImg%>" onerror="this.style.display='none'" width="32" height="32" alt="feature icon" />
	</div>
	<div class="column large-23 small-22 medium-23" >
		<div class="row collapse">
			<h2 class="columns large-24 medium-24">
				<a href="<%=eventsLink%>" style="color: #414141"><%=featureTitle%></a>
			</h2>
			<ul class="small-block-grid-1 medium-block-grid-2 large-block-grid-2">
				<%
					// Feature Events
					//com.day.cq.wcm.foundation.List elist= (com.day.cq.wcm.foundation.List)request.getAttribute("elist");
					ArrayDeque<String> featureEvents = (ArrayDeque) request.getAttribute("featureEvents");
					Calendar cale = Calendar.getInstance();
					if (eventCount > 0 && !featureEvents.isEmpty()) {
						Iterator<String> itemUrl = featureEvents.descendingIterator();
						Date currentDate = new Date();
						while (itemUrl.hasNext()) {
							Node node = resourceResolver.getResource(itemUrl.next()).adaptTo(Node.class);
							try {
								if (node.hasNode("jcr:content/data")) {
									Node propNode = node.getNode("jcr:content/data");

									// Check for featured events excluded by date
									if (propNode.hasProperty(filterProp)) {
										cale.setTime(fromFormat.parse(propNode.getProperty(filterProp).getString()));
									} else {
										cale.setTime(fromFormat.parse(propNode.getProperty("start").getString()));
									}
									Date eventStartDate = cale.getTime();

									if (eventStartDate.after(currentDate)) {
										Map<String, Object> renderMap = new HashMap<>();
										renderMap.put("date", eventStartDate);
										renderMap.put("propNode", propNode);
										renderMap.put("node", node);
										renderMap.put("href", node.getPath() + ".html");
										renderMap.put("title", propNode.getProperty("../jcr:title").getString());
										renderMaps.add(renderMap);
									}
								}
							} catch (Exception e) {}
						}
					}

					// Sort
					renderMaps.sort((m1, m2) -> {
						Date d1 = (Date)m1.get("date");
						Date d2 = (Date)m2.get("date");
						return d1.before(d2) ? -1 : 1;
					});

					// Render
					for (Map<String, Object> renderMap : renderMaps) {
						renderMap.forEach((k, v) -> {
							if (!k.equals("date")) request.setAttribute(k, v);
						});
						%><cq:include script="event-render.jsp"/><%
					}

					// Clear maps
					renderMaps.clear();

					// Upcoming Events or Tagged Events
					// need to look for the event starting/ending date is great then TODAYS date, if end date is not there, else start >= todays date.
					GSDateTime gsToday = new GSDateTime();
					GSDateTimeFormatter dtfIn = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
					int count = 0;
					if (eventCount > results.size()) {
						eventCount = results.size();
					}
					if (eventCount > 0) {
						if (daysofevents > 0) {
							Calendar cal1 = Calendar.getInstance();
							cal1.add(Calendar.DATE, daysofevents);
							//changing today variable from the current date to the future date
							// based on the users selection.
							today = formatter.parse(formatter.format(cal1.getTime()));
						}
						for (String result : results) {
							if (count == eventCount) {
								break;
							}
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

									try {
										String eventDt = formatter.format(fromdate);
										fromdate = formatter.parse(eventDt);
									} catch (Exception e) {}

									if ((fromdate.after(today) || fromdate.equals(today)) && !featureEvents.contains(result)) {
										Map<String, Object> renderMap = new HashMap<>();
										renderMap.put("date", fromdate);
										renderMap.put("propNode", propNode);
										renderMap.put("node", node);
										renderMap.put("href", result + ".html");
										renderMap.put("title", propNode.getProperty("../jcr:title").getString());
										renderMaps.add(renderMap);
										count++;
									}
								}
							} catch (Exception e) {}
						}
					}

					// Sort
					renderMaps.sort((m1, m2) -> {
						Date d1 = (Date)m1.get("date");
						Date d2 = (Date)m2.get("date");
						return d1.before(d2) ? -1 : 1;
					});

					// Render
					for (Map<String, Object> renderMap : renderMaps) {
						if (eventsRendered < eventCount) {
							renderMap.forEach((k, v) -> {
								if (!k.equals("date")) request.setAttribute(k, v);
							});
							%><cq:include script="event-render.jsp"/><%
							eventsRendered++;
						}
					}
				%>
			</ul>
		</div><!--/inner row collapse-->

	</div><!--/columns--><%
		if (eventsRendered == 0) {
			%><div style="height:75px"></div><%
		}
	%>



