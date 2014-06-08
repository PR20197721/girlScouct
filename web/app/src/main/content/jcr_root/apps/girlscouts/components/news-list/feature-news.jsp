<%@ page import="java.text.DateFormat,com.day.cq.wcm.api.WCMMode,com.day.cq.wcm.foundation.List, com.day.cq.wcm.api.components.DropTarget,com.day.cq.search.Query,com.day.cq.search.result.SearchResult,com.day.cq.search.result.Hit, java.util.Map,java.util.HashMap,com.day.cq.search.QueryBuilder,com.day.cq.search.PredicateGroup,java.util.Arrays,java.util.HashSet,java.util.ArrayList, java.util.Iterator,java.text.SimpleDateFormat,java.util.Date, java.text.Format,com.day.cq.dam.commons.util.DateParser"%>
<%@include file="/libs/foundation/global.jsp"%>
<%
	String designPath = currentDesign.getPath();
	String featureIcon = properties.get("fileReference", "");
	String featureTitle = properties.get("title", "");
	String featureLink = properties.get("urltolink", "") + ".html";

    SearchResult results = (SearchResult)request.getAttribute("results");
    java.util.List <Hit> resultsHits = results.getHits();
	DateFormat inFormatter = new SimpleDateFormat("MM/dd/yy");
	Format formatter = new SimpleDateFormat("dd MMM yyyy");
	 
	Integer count =  Integer.parseInt(properties.get("count",String.class));
	if(count > resultsHits.size()){
	    count = resultsHits.size();
    }
%>
<div class="small-24 medium-24 large-24 columns">
	<div class="row">
		<div class="hide-for-small hide-for-medium large-24 columns">
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
        <div class="small-24 medium-12 hide-for-large  hide-for-xlarge hide-for-xxlarge columns">
            <div class="feature-icon">
                <img src="<%= designPath %>/images/arrow-down.png" width="30" height="30"/>
            </div>
            <div class="feature-title">
                <h2><a href="<%= featureLink %>"><%= featureTitle %></a></h2>
            </div>
        </div>
        <div class="medium-4 show-for-medium columns">&nbsp;</div>
	</div>
	
	<ul class="small-block-grid-1 content">
<%
	    for(int i=0;i<count;i++) {
                String newsLink = null;
                try {
			Node resultNode = resultsHits.get(i).getNode();
			newsLink = resultNode.getPath() + ".html";
			
			Node contentNode = resultNode.getNode("jcr:content");
			String newsTitle = contentNode.hasProperty("jcr:title") ? contentNode.getProperty("jcr:title").getString() : "";

			String newsDateStr = "";
			if (contentNode.hasProperty("date")) {
				String dateString = contentNode.getProperty("date").getString();
				Date newsDate = inFormatter.parse(dateString);
				newsDateStr = formatter.format(newsDate);
			}
			
			String newsDesc = contentNode.hasProperty("description") ? contentNode.getProperty("description").getString() : "";
			
			String imgPath = contentNode.hasProperty("middle/par/text/image/fileReference") ? contentNode.getProperty("middle/par/text/image/fileReference").getString() : "";
%>
    	<li>
    		<div class="row">
    			<div class="small-24 medium-8 large-6 columns">
    				<img src="<%= imgPath %>" width="483" height="305" />
    			</div>
    			<div class="small-24 medium-16 large-18 columns">
    				<h3>
    					<a href="<%= newsLink %>"><%= newsTitle %></a>
    				</h3>
    				<p><%= newsDateStr %></p>
    				<p><%= newsDesc %></p>
    			</div>
    		</div>
    	</li>
<%
                } catch (java.text.ParseException pe) {
			log.error(">>>>>>> News Item (" + newsLink + ") has an unparseable date.");
		}
           }
%>
	</ul>
</div>
