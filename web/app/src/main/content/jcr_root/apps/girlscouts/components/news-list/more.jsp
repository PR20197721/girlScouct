<%@ page import="java.text.DateFormat,
java.text.SimpleDateFormat,
java.util.Date,
java.util.List, 
java.util.ArrayList, 
javax.jcr.Node,
org.apache.sling.commons.json.JSONObject,
org.apache.sling.api.request.RequestPathInfo,
org.slf4j.Logger,
org.slf4j.LoggerFactory" %>
<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%!
final Logger newsMoreLog = LoggerFactory.getLogger(this.getClass());
public String getDate(Node nNode){
    DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
	String newsDateStr ="";
	try{
	 	if (nNode.hasProperty("jcr:content/date")) {
			Date dateString = nNode.getProperty("jcr:content/date").getDate().getTime();
			newsDateStr = dateFormat.format(dateString);
		}
	}catch(Exception e){return "";}	
   return newsDateStr;
}
public String getText(Node nNode){
	try{
		int MAX_MORE_LENGTH = 1500;
		String text = (nNode.hasProperty("jcr:content/middle/par/text/text")? nNode.getProperty("jcr:content/middle/par/text/text").getString():"");
		
		return text;
	}catch(Exception e){return "";} 
	 
 }
 
 public String getExternalUrl(Node nNode){
	 try{
	 	return(nNode.hasProperty("jcr:content/external-url")?nNode.getProperty("jcr:content/external-url").getString():"");
	 }catch(Exception e){return "";}
 }

 public String getOpenExternally(Node nNode){
    try{
    	 	return(nNode.hasProperty("jcr:content/open-externally")?nNode.getProperty("jcr:content/open-externally").getString():"");
    	 }catch(Exception e){return "";}
     }

%>
<%
   	long RESULTS_PER_PAGE = 20;
	long offset = 0;
	long resultCount = 0;
	String path = currentSite.get("newsPath", "");
	try {
        JSONObject json = new JSONObject();
        List<JSONObject> news = new ArrayList<JSONObject>();
		try{
			final RequestPathInfo requestPathInfo = slingRequest.getRequestPathInfo();
			String[] selectors = requestPathInfo.getSelectors();
			if(selectors.length == 3){
				offset = Long.parseLong(selectors[2]);
			}
		}catch(Exception e){}
	   	String EXPRESSION = "SELECT [jcr:score], [jcr:path], [jcr:primaryType] "
	   			+ "FROM [cq:Page] AS s "
	   			+ "WHERE ISDESCENDANTNODE([%s]) AND "
	   				+ "(s.[jcr:content/hideInNav] IS NULL OR s.[jcr:content/hideInNav] = 'false') "
	   			+ "ORDER BY s.[jcr:content/date] DESC";

	   	String query = String.format(EXPRESSION, path);
	   	try {
			GSJcrSearchProvider searchProvider = new GSJcrSearchProvider(slingRequest);
		 	boolean searchMore = true;
		 	while(searchMore){
		 		GSSearchResultManager gsResultManager = new GSSearchResultManager();
		 		long startTime = System.nanoTime();
			 	gsResultManager.add(searchProvider.searchWithOffset(query, RESULTS_PER_PAGE, offset));
			 	long endTime = System.nanoTime();
		 		double duration = (endTime - startTime)/1000000;
		 		newsMoreLog.error("Execution of : "+query+" with result size: "+RESULTS_PER_PAGE+" and offset: "+offset+" took "+duration+" milliseconds");
			 	if(gsResultManager.size() == 0){
			 		searchMore = false;
		 			break;
			 	}
			 	List<GSSearchResult> queryResults = gsResultManager.getResults();
			 	for(GSSearchResult qResult:queryResults){
			 		offset++;
			 		try{
				 		Node resultNode = qResult.getResultNode();
				 		String newsPath = resultNode.getPath();
				 		try{ newsPath = resourceResolver.map(newsPath);}catch(Exception e){}
				 		JSONObject newsPage = new JSONObject();
				 		newsPage.put("title", qResult.getTitle());
				 		newsPage.put("date", getDate(resultNode));
				 		newsPage.put("path", resultNode.getPath());
				 		newsPage.put("url", newsPath+".html");
				 		newsPage.put("externalUrl", getExternalUrl(resultNode));
				 		newsPage.put("text", getText(resultNode));
				 		newsPage.put("openExternally", getOpenExternally(resultNode));
				 		news.add(newsPage);
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
	   	json.put("results",news);
	   	json.put("newOffset",offset);
	   	json.put("resultCount",resultCount);
	   	json.write(response.getWriter());
	} catch(Exception e){
   		e.printStackTrace();
   	}
%>
