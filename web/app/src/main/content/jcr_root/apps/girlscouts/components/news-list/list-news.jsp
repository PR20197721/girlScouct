<%@ page import="com.day.cq.wcm.api.WCMMode,com.day.cq.wcm.foundation.List,java.text.DateFormat,java.util.Set,
                   com.day.cq.wcm.api.components.DropTarget,com.day.cq.search.Query,com.day.cq.search.result.SearchResult,com.day.cq.search.result.Hit,
                   java.util.Map,java.util.HashMap,com.day.cq.search.QueryBuilder,com.day.cq.search.PredicateGroup,java.util.Arrays,java.util.HashSet,java.util.ArrayList,
                   java.util.Iterator,java.text.SimpleDateFormat,java.util.Date, java.text.Format,com.day.cq.dam.commons.util.DateParser, org.jsoup.*"%>
                   
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@include file="newsHelper.jsp"%>



<%
  SearchResult results = (SearchResult)request.getAttribute("results");
  java.util.List <Hit> resultsHits = results.getHits();
  Format formatter = new SimpleDateFormat("dd MMM yyyy");
  List  list = (List)request.getAttribute("list"); 
  String start = (String) request.getAttribute("start");
  String title = "";
  String description="";
  String text="";
  String imgPath="";
  DateFormat inFormatter = new SimpleDateFormat("MM/dd/yy");
  String date="", date_yyyyMMdd="";
  String str = "";
  String external_url = "";
  int strP = 0;
  Set<String> featureNews = (HashSet)request.getAttribute("featureNews");
  
 %>
<%
 int newsRendered = 0;
 if (!list.isEmpty()){
	   	%>
	   	<div class="row">
	   	   <div class="small-24 large-24 medium-24 columns">
	   	      <h5>Featured News</h5>
	   	   </div>
	   	</div>  
	   	
	   	<ul class="searchResultsList" itemscope itemtype="http://schema.org/BreadcrumbList">
		<%
	    	Iterator<Page> items = list.getPages();
	    	String listItemClass = null;
	    	while (items.hasNext()){
	            String newsLink = "";	
	        	Page item = (Page)items.next();
	    		Node node = item.getContentResource().adaptTo(Node.class);
	    		
	    		title = getTitle(node);
	            date = getDate(node);
	            date_yyyyMMdd = getDate(node);
				text = getText(node);			
				external_url=genLink(resourceResolver,getExternalUrl(node));
				request.setAttribute("path",item.getPath());
				request.setAttribute("title",title);
				request.setAttribute("date",date);
				request.setAttribute("date_yyyyMMdd",date_yyyyMMdd);
				request.setAttribute("text",text);
				request.setAttribute("external_url",external_url);
				
			%>
           <%}%>
           
	       <cq:include script="news-list-render.jsp"/> <% newsRendered++; %> 
	       
	    
	<%}%>
    </ul>
 
 
 
 
 
 <ul class="searchResultsList" itemscope itemtype="http://schema.org/BreadcrumbList">
 <%
  for(Hit hit:resultsHits)
  {
  	Node content = hit.getNode(); 
	Node contentNode = content.getNode("jcr:content");
	title=getTitle(contentNode);
	date=getDate(contentNode);
	date_yyyyMMdd = getDate_yyyyMMdd(contentNode);
	external_url=genLink(resourceResolver,getExternalUrl(contentNode));
	text = getText(contentNode);
	request.setAttribute("path",hit.getPath());
	request.setAttribute("title",title);
	request.setAttribute("date",date);
	request.setAttribute("date_yyyyMMdd",date_yyyyMMdd);
	request.setAttribute("text",text);
	request.setAttribute("external_url",external_url);
	if(!featureNews.contains(hit.getPath())){
	%>
	
	<cq:include script="news-list-render.jsp"/> <% newsRendered++; %> 

	
	<%}
  }
 %></ul><%

	if(newsRendered == 0){ %>

		<div class="row">
	   	   <div class="small-24 large-24 medium-24 columns">
               <h4>News Component Empty:</h4>
              	<h5>No News Available</h5>
	   	   </div>
	   	</div>
<%
	}


%>

<script>
$(".searchResultsList article").readmore({
	speed: 75,
	maxHeight: 114, // 19px line-height * 6 visible lines
	heightMargin: 16,
	moreLink: '<a href="#">Read more</a>',
	lessLink: '<a href="#">Close</a>',
	embedCSS: true,
	sectionCSS: 'display: block; width: 100%;',
	expandedClass: 'readmore-js-expanded',
	collapsedClass: 'readmore-js-collapsed'

});
</script>



