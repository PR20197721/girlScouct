<%@ page import="java.text.DateFormat,com.day.cq.wcm.api.WCMMode,com.day.cq.wcm.foundation.List, com.day.cq.wcm.api.components.DropTarget,com.day.cq.search.Query,com.day.cq.search.result.SearchResult,com.day.cq.search.result.Hit, java.util.Map,java.util.HashMap,com.day.cq.search.QueryBuilder,com.day.cq.search.PredicateGroup,java.util.Arrays,java.util.HashSet,java.util.ArrayList, java.util.Iterator,java.text.SimpleDateFormat,java.util.Date, java.text.Format,com.day.cq.dam.commons.util.DateParser,java.util.Set,java.text.DateFormat"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@include file="newsHelper.jsp" %>
<%
	String designPath = currentDesign.getPath();
	String featureIcon = properties.get("fileReference", "");
	String featureTitle = properties.get("featuretitle", "");
	String featureLink = properties.get("urltolink", "");
	if (!featureLink.isEmpty()) {
		featureLink = genLink(resourceResolver, featureLink);

	}

    SearchResult results = (SearchResult)request.getAttribute("results");
    java.util.List <Hit> resultsHits = results.getHits();
	
    int newscounts = 0;
	Set<String> featureNews = (HashSet)request.getAttribute("featureNews"); 
	if(properties.containsKey("count")){
		 newscounts =  Integer.parseInt(properties.get("count",String.class));
		if(newscounts > resultsHits.size()){
			newscounts = resultsHits.size();
		 }
	}
%>

<div class="large-1 columns small-2 medium-1">
  <img src="<%= featureIcon %>" width="32" height="32" alt="feature icon"/>
</div>
<div class="column large-23 small-22 medium-23">
  <div class="row collapse">
    <h2 class="columns large-24 medium-24 clearfix"><a href="<%= featureLink %>"><%= featureTitle %></a></h2>
    <%
    // Feature news when select but author on the home page
    	List list = (List)request.getAttribute("list"); 
        
    	if (!list.isEmpty()){
       	%>
    	<%
        	Iterator<Page> items = list.getPages();
    	String listItemClass = null;
    	while (items.hasNext()){
            String newsLink = "";	
        	Page item = (Page)items.next();
    		Node node = item.getContentResource().adaptTo(Node.class);
    		newsLink = genLink(resourceResolver,item.getPath());
    		//Node contentNode = node.getNode("jcr:content");
    		String newsTitle = getTitle(node);
            
			String newsDateStr = getDate(node);
			String external_url = getExternalUrl(node);
			String newsDesc = getDesc(node);			
			String imgPath = getImgPath(node);
			request.setAttribute("newsTitle", newsTitle);
			request.setAttribute("newsDateStr", newsDateStr);
			request.setAttribute("imgPath", imgPath);
			request.setAttribute("newsDesc", newsDesc);
			request.setAttribute("newsLink", newsLink);
			request.setAttribute("external_url",external_url);		
      %><cq:include script="feature-render.jsp"/><%
    }
  }%>
  <%
    //int i=0;
    int ncount = 0;
    if(newscounts > 0){
			for(int i=0;i<resultsHits.size(); i++)
		    {
        	try{
        	String newsLink = null;
       		Node resultNode = resultsHits.get(i).getNode();
				newsLink = getPath(resultNode);
				
				Node contentNode = resultNode.getNode("jcr:content");
				String newsTitle = getTitle(contentNode);
	             
				String newsDateStr = getDate(contentNode);
				
				String newsDesc = getDesc(contentNode);			
				String imgPath = getImgPath(contentNode);
				String external_url = getExternalUrl(contentNode);
				
				
				request.setAttribute("newsTitle", newsTitle);
				request.setAttribute("newsDateStr", newsDateStr);
				request.setAttribute("imgPath", imgPath);
				request.setAttribute("newsDesc", newsDesc);
				request.setAttribute("newsLink", newsLink);
				request.setAttribute("external_url",external_url);
				if(!featureNews.contains(resultNode.getPath())){
				ncount++;
       %>
 			<cq:include script="feature-render.jsp"/>
			<%} 
			 if(ncount==newscounts)
		      {
		        	 break;
		      }
	    }catch(Exception e){}
	 } 
    }
%>
  </div>
</div>
