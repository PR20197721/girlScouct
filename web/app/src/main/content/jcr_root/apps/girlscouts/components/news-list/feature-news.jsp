<%@ page import="java.text.DateFormat,com.day.cq.wcm.api.WCMMode,com.day.cq.wcm.foundation.List, com.day.cq.wcm.api.components.DropTarget,com.day.cq.search.Query,com.day.cq.search.result.SearchResult,com.day.cq.search.result.Hit, java.util.Map,java.util.HashMap,com.day.cq.search.QueryBuilder,com.day.cq.search.PredicateGroup,java.util.Arrays,java.util.HashSet,java.util.ArrayList, java.util.Iterator,java.text.SimpleDateFormat,java.util.Date, java.text.Format,com.day.cq.dam.commons.util.DateParser,java.util.Set,java.text.DateFormat"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@include file="newsHelper.jsp" %>
<%
	String designPath = currentDesign.getPath();
	String featureIcon = properties.get("fileReference", "");
	String featureTitle = properties.get("title", "");
	String featureLink = properties.get("urltolink", "");
	if (!featureLink.isEmpty()) {
	    featureLink = "href=\"" + genLink(resourceResolver, featureLink) + "\" ";
	}

    SearchResult results = (SearchResult)request.getAttribute("results");
    java.util.List <Hit> resultsHits = results.getHits();
	
	Integer count =  Integer.parseInt(properties.get("count",String.class));
	Set<String> featureNews = (HashSet)request.getAttribute("featureNews"); 
	if(count > resultsHits.size()){
		count = resultsHits.size();
    }
	
%>
<div class="small-24 medium-24 large-24 columns news-section">
	<div class="row">
		<div class="hide-for-small hide-for-medium large-24 columns featureNewsHeader">
			<div class="feature-icon">
				<img src="<%= featureIcon %>" width="50" height="50">
			</div>	
			<div class="feature-title">
				<h2>
					<a href="<%= featureLink %>"><%= featureTitle %></a>
				</h2>
			</div>
		</div>
		<div class="medium-8 show-for-medium columns">&nbsp;</div>
        <div class="small-24 medium-24 hide-for-large hide-for-xlarge hide-for-xxlarge columns">
            <div class="feature-icon">
                <img src="<%= featureIcon %>" width="50" height="50">
            </div>
            <div class="feature-title">
                <h2><a href="<%= featureLink %>"><%= featureTitle %></a></h2>
            </div>
        </div>
        <div class="medium-4 show-for-medium columns">&nbsp;</div>
	</div>

	<ul class="news-block">

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
    
}
%>
<%
        //int i=0;

        int newsCount = 0;
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
				newsCount++;
%>
 			<cq:include script="feature-render.jsp"/>
			<%} 
			 if(newsCount==count)
		      {
		        	 break;
		      }
	    }catch(Exception e){}
	 } 	
%>
	</ul>
</div>


