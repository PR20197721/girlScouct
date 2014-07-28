<%@ page import="com.day.cq.wcm.foundation.Search,
org.girlscouts.web.search.DocHit,
com.day.cq.search.eval.JcrPropertyPredicateEvaluator,com.day.cq.search.eval.FulltextPredicateEvaluator,
com.day.cq.tagging.TagManager,
java.util.Locale,com.day.cq.search.QueryBuilder,javax.jcr.Node,
java.util.ResourceBundle,com.day.cq.search.PredicateGroup,
com.day.cq.search.Predicate,com.day.cq.search.result.Hit,
com.day.cq.i18n.I18n,com.day.cq.search.Query,com.day.cq.search.result.SearchResult,
java.util.Map,java.util.HashMap,java.util.List" %>
<%@include file="/libs/foundation/global.jsp" %>

<cq:setContentBundle source="page" />


  <link rel="stylesheet" href="//code.jquery.com/ui/1.11.0/themes/smoothness/jquery-ui.css">
  <script src="//code.jquery.com/jquery-1.10.2.js"></script>
  <script src="//code.jquery.com/ui/1.11.0/jquery-ui.js"></script>

  <style>

  .ui-tooltip {
    max-width: 1550px;
    width:510px;
    height:310px;
  }
  </style>
  <script>
  $(function() {
    $( document ).tooltip({
    	
      items: "img, [data-geo], [title]",
      content: function() {
    	  
        var element = $( this );
        if ( element.is( "[data-geo]" ) ) {
        	
          var text = element.attr('href');
          text= "<iframe width='500' height='300' src='"+text+"'></iframe>";
          return text;
         
           
              
        }
        
        
        if ( element.is( "[title]" ) ) {
          return element.attr( "title" );
        }
        if ( element.is( "img" ) ) {
          return element.attr( "alt" );
        }
        
      }
  
    });
  });
  </script>

<%

  

final Locale pageLocale = currentPage.getLanguage(true);
final ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);

    QueryBuilder queryBuilder = sling.getService(QueryBuilder.class);
String q = request.getParameter("q");
//Map<String, String> query = new HashMap<String, String>();
String documentLocation = "/content/dam/girlscouts-shared/en/documents";
String searchIn = (String) properties.get("searchIn");
if (null==searchIn)
{
searchIn = currentPage.getAbsoluteParent(2).getPath();
}

final String escapedQuery = xssAPI.encodeForHTML(q != null ? q : "");
final String escapedQueryForAttr = xssAPI.encodeForHTMLAttr(q != null ? q : "");

pageContext.setAttribute("escapedQuery", escapedQuery);
pageContext.setAttribute("escapedQueryForAttr", escapedQueryForAttr);

/*
	int prev=0, next=0;
	if( request.getParameter("next")!=null ){
		try{ next= Integer.parseInt(request.getParameter("next")); }catch(Exception e){e.printStackTrace();}
	}else if( request.getParameter("prev")!=null ){
		try{prev= Integer.parseInt(request.getParameter("prev")); }catch(Exception e){e.printStackTrace();}
	}
	*/
	int pos = 0;
	if( request.getParameter("pos")!=null ){
		try{ pos= Integer.parseInt(request.getParameter("pos")); }catch(Exception e){e.printStackTrace();}
	}
	
	
	Map mapFullText = new HashMap();
    mapFullText.put("fulltext", q);
    
 
    mapFullText.put("group.1_path", currentPage.getAbsoluteParent(2).getPath());
    mapFullText.put("group.2_path", "/content/dam/girlscouts-shared/en/documents");
    mapFullText.put("group.p.or","true");

   
   
    PredicateGroup predicateFullText = PredicateGroup.create(mapFullText);
	System.err.println("***SQL: "+predicateFullText.toString());
	
	 Query query = queryBuilder.createQuery(predicateFullText, slingRequest.getResourceResolver().adaptTo(Session.class));
	 
	 query.setStart(pos);
	 query.setHitsPerPage(10);
     query.setExcerpt(true);
 SearchResult result = query.getResult();
 
List<Hit> hits = result.getHits();
System.err.println("TESt123: "+ hits.size() +" : "+result.getExecutionTimeMillis()  );


Map unq= new java.util.TreeMap();
for(Hit hit: hits)
{
	
	//System.err.println( hit.getPath() );
  try{
		if(hit.getPath().contains("textimage_")) continue;
	


        DocHit docHit = new DocHit(hit);
        String path = docHit.getURL();
      
		
		String obj[] =(String[])unq.get( docHit.getURL() );
		if( obj ==null ){
			obj = new String[3];
			obj[0]= docHit.getURL();
			obj[1]= docHit.getTitle();
			try{obj[2]= hit.getExcerpt();}catch(Exception e){e.printStackTrace();}
		}else{
			
			/*//if( obj[2] ==null || obj[2].equals(""))
			obj[2]= obj[2]+"**"+hit.getExcerpt();
			obj[1]= obj[1]+"**"+ hit.getTitle();
			*/
		}
	unq.put( docHit.getURL() , obj);
	
       
  }catch(Exception e){}
   }



int xx=pos;
java.util.Iterator itr= unq.keySet().iterator();
while( itr.hasNext() ){
	String u=(String) itr.next();
String obj[] = (String[]) unq.get(u);
xx++;
%>
<div style="background-color:#fff; margin-top:20px; ">
#<%=xx %>
<a href="<%=obj[0] %>" data-geo=""><%=obj[1]%></a>
<br/><%=obj[2]%>
</div>
<% 
}
%>


<%if( hits.size()> 9 ){ %>
<br/><br/>
<a href="/content/gateway/en/site-search.html?q=girls&pos=<%= ( (pos-10)<0 ) ? 0 : (pos-10)%>">PREV</a> ||
 <a href="/content/gateway/en/site-search.html?q=girls&pos=<%= (pos+10)%>">NEXT</a>
<br/><br/>
<%} %>



<script>
jQuery('#searchForm').bind('submit', function(event)
{
if (jQuery.trim(jQuery(this).find('input[name="q"]').val()) === '')
{
event.preventDefault();
}
});
</script>