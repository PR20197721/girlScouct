<%@ page import="com.day.cq.wcm.api.WCMMode,com.day.cq.wcm.foundation.List,
                   com.day.cq.wcm.api.components.DropTarget,com.day.cq.search.Query,com.day.cq.search.result.SearchResult,com.day.cq.search.result.Hit,
                   java.util.Map,java.util.HashMap,com.day.cq.search.QueryBuilder,com.day.cq.search.PredicateGroup,java.util.Arrays,java.util.HashSet,java.util.ArrayList,
                   java.util.Iterator,java.text.SimpleDateFormat,java.util.Date, java.text.Format,com.day.cq.dam.commons.util.DateParser"%>

<%@include file="/libs/foundation/global.jsp"%>



<%
  HashSet<String>set = new HashSet<String>(); 
  String path = currentPage.getAbsoluteParent(2).getPath();
  Map <String, String> queryMap = new HashMap<String, String>();
  queryMap.put("type", "cq:Page");
 // queryMap.put("path", path+"/news");
  
 queryMap.put("path", currentPage.getPath());
 
 
 queryMap.put("1_boolproperty","jcr:content/hideInNav");
  queryMap.put("1_boolproperty.value","false");
  queryMap.put("2_boolproperty","jcr:content/isFeature");
  queryMap.put("2_boolproperty.value","false");
  
  queryMap.put("orderby","@jcr:content/date");
  queryMap.put("orderby.sort","desc");
  queryMap.put("p.limit", "-1");
  
  QueryBuilder queryBuilder = sling.getService(QueryBuilder.class);
  Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), slingRequest.getResourceResolver().adaptTo(Session.class));
  
   
  SearchResult results = query.getResult();
  java.util.List <Hit> resultsHits = results.getHits();
  request.setAttribute("results", results);
  
  
  
  %>
  
  