<%@ page import="java.util.Locale,
java.util.ResourceBundle,
java.util.Calendar,
java.util.TimeZone,
java.text.DateFormat,
java.text.SimpleDateFormat,
java.util.Date,
java.util.List, 
java.util.ArrayList,
javax.jcr.Node,
javax.jcr.PropertyIterator,
javax.jcr.Property,
org.girlscouts.web.events.search.*,
org.apache.sling.commons.json.JSONObject,
org.girlscouts.web.search.GSSearchResult, 
org.girlscouts.web.search.GSSearchResultManager,
org.girlscouts.web.search.GSJcrSearchProvider" %>    
<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>      
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>

<%
   	long RESULTS_PER_PAGE = 10;
	long offset = 0;
	String[] propertyPaths = new String[]{
			"jcr:content/jcr:title",
			"jcr:content/data/locationLabel",
			"jcr:content/data/address",
			"jcr:content/data/register",
			"jcr:content/data/end",
			"jcr:content/data/color",
			"jcr:content/data/start",
			"jcr:content/data/srchdisp",
			"jcr:content/data/eid",
			"jcr:content/data/image",
			"jcr:content/data/memberOnly",
			"jcr:content/data/priceRange",
			"jcr:content/data/thumbImage",
			"jcr:content/data/timezone",
			"jcr:content/data/visibleDate"
	};
	String EVENTS_RESOURCE_TYPE = "girlscouts/components/event-page";
	Calendar cal = Calendar.getInstance();
	cal.setTimeZone(TimeZone.getDefault());
	DateFormat sql2DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
	DateFormat printDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	sql2DateFormat.setTimeZone(TimeZone.getDefault());
	try {
        JSONObject json = new JSONObject();
		try{
			String suffix = slingRequest.getRequestPathInfo().getSuffix();
			String[] suffixArr = suffix.split("/");
			offset = Long.parseLong(suffixArr[1]);
		}catch(Exception e){}
	   	String EXPRESSION = "SELECT [jcr:score], [jcr:path], [jcr:primaryType] "
	   			+ "FROM [cq:Page] AS s "
	   			+ "WHERE ISDESCENDANTNODE([%s]) AND "
	   				+ "(s.[jcr:content/data/visibleDate] IS NULL OR s.[jcr:content/data/visibleDate] <= CAST('%s' AS DATE)) AND "
	   				+ "s.[jcr:content/data/start] >= CAST('%s' AS DATE) "
	   			+ "ORDER BY s.[jcr:content/data/start] ASC";
	   	String path = currentSite.get("eventPath",String.class);
	   	String query = String.format(EXPRESSION, path, sql2DateFormat.format(cal.getTime()), sql2DateFormat.format(cal.getTime()));
	   	try {
			GSJcrSearchProvider searchProvider = new GSJcrSearchProvider(slingRequest);
		 	boolean searchMore = true;
		 	long resultCount = 0;
		 	Calendar cale =  Calendar.getInstance();
		 	while(searchMore){
		 		GSSearchResultManager gsResultManager = new GSSearchResultManager();
		 		long startTime = System.nanoTime(); 
			 	gsResultManager.add(searchProvider.searchWithOffset(query, RESULTS_PER_PAGE, offset));
			 	long endTime = System.nanoTime();
		 		double duration = (endTime - startTime)/1000000;
		 		System.err.println("Execution of : "+query+" with result size: "+RESULTS_PER_PAGE+" and offset: "+offset+" took "+duration+" milliseconds");
			 	if(gsResultManager.size() == 0){
			 		searchMore = false;
		 			break;
			 	}
			 	List<GSSearchResult> queryResults = gsResultManager.getResults();
			 	for(GSSearchResult qResult:queryResults){
			 		offset++;
			 		try{
				 		Node resultNode = qResult.getResultNode();
				 		JSONObject event = new JSONObject();
				 		event.put("path", resultNode.getPath());
				 		for(String prop:propertyPaths){
				 			if(resultNode.hasProperty(prop)){
				 				Property property = resultNode.getProperty(prop);
				 				event.put(property.getName(), property.getString());
				 			}
				 		}
				 		json.accumulate("results", event);
				 		resultCount ++;
			 		}catch(Exception e){}
			 		if(resultCount == RESULTS_PER_PAGE){
			 			searchMore = false;
			 			break;
			 		}
			 	}
		 	}
	   	} catch(Exception e){
	   		e.printStackTrace();
	   	}
	   	json.put("newOffset",offset);
	   	json.write(response.getWriter());
	} catch(Exception e){
   		e.printStackTrace();
   	}
	
   	//out.println("<div>offset:"+offset+"</div>");
%>
