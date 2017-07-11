<%@ page import="java.util.Locale,
java.util.ResourceBundle,
java.util.List, 
java.util.ArrayList, 
java.util.regex.*, 
java.util.Arrays,
java.text.*, 
org.girlscouts.web.search.GSSearchResult, 
org.girlscouts.web.search.GSSearchResultManager,
org.girlscouts.web.search.GSJcrSearchProvider" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:setContentBundle source="page" />
<%
final Locale pageLocale = currentPage.getLanguage(true);
final ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);
String q = "";
if(slingRequest.getParameter("q") != null){
	q = slingRequest.getParameter("q").trim();
}
//String documentLocation = "/content/dam/girlscouts-shared/documents";
String searchIn = (String) properties.get("searchIn");
if (null==searchIn){
    searchIn = currentPage.getAbsoluteParent(2).getPath();
}

if(q == null) q = "[[empty search criteria]]";
if(q.length() <= 2) q = "[[too short search criteria]]";
// pagination init
String start = request.getParameter("start");
if (start == null) start = "0";
if (start.length() == 0) start = "1";
if (Integer.parseInt(start) < 0) start = "1";
int pageSize = 10;
int endIdx = pageSize; //this may change for the last page
double totalPage = 0;
int startIdx = 0;
try {
	startIdx = Integer.parseInt(start); 
} catch (NumberFormatException e) {
	startIdx = 0;
}

int currentPageNo = startIdx/pageSize;


final String query = java.net.URLEncoder.encode(q != null ? q.replaceAll("[^a-zA-Z0-9'.,]"," ").replaceAll("\\s+", " ") : "","UTF-8");
final String escapedQuery = xssAPI.encodeForHTML(q != null ? q.replaceAll("%","%25").replaceAll("\\s+", " ") : "");
final String escapedQueryForAttr = xssAPI.encodeForHTMLAttr(q != null ? q.replaceAll("%","%25").replaceAll("\\s+", " ") : "");

pageContext.setAttribute("escapedQuery", java.net.URLDecoder.decode(escapedQuery, "UTF-8"));
pageContext.setAttribute("escapedQueryForAttr", java.net.URLDecoder.decode(escapedQueryForAttr, "UTF-8"));

String theseDamDocuments = properties.get("docusrchpath","");
if(theseDamDocuments.equals("")){
    String regexStr = "/(content)/([^/]*)/(en)$";
    Pattern pattern = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(currentPage.getAbsoluteParent(2).getPath());
	String[] councils = new String[]{"gssjc", "gateway","gssem" };
	
    if (matcher.find()) {
        theseDamDocuments = "/" + matcher.group(1) + "/dam/girlscouts-" +  matcher.group(2) + "/documents";
		if (Arrays.asList(councils).contains(matcher.group(2))){
            theseDamDocuments = "/" + matcher.group(1) + "/dam/" +  matcher.group(2) + "/documents";
        }

    }
}
GSSearchResultManager gsResultManager = new GSSearchResultManager();
try{
	GSJcrSearchProvider searchProvider = new GSJcrSearchProvider(slingRequest);
	gsResultManager.add(searchProvider.search(searchIn, "cq:Page", query));
	gsResultManager.add(searchProvider.search(theseDamDocuments, "dam:Asset", query));
	gsResultManager.filter();	
}catch(Exception e){
	e.printStackTrace();
}
String numberOfResults = String.valueOf(gsResultManager.size());
if (startIdx + pageSize > gsResultManager.size()) {
	endIdx = gsResultManager.size(); //last page
} else {
	endIdx = startIdx + pageSize; //all other page
}
totalPage = Math.ceil((double)gsResultManager.size()/pageSize);
%>
<center>
     <form action="${currentPage.path}.html" id="searchForm">
        <input type="text" name="q" value="${escapedQueryForAttr}" class="searchField" />
     </form>
</center>
<br/>
<%if(gsResultManager.size() < 1){ %>
    <fmt:message key="noResultsText">
      <fmt:param value="${escapedQuery}"/>
    </fmt:message>
 <%} else{ %>
    <%=properties.get("resultPagesText","Results for")%> "${escapedQuery}"
  <br/>
<%
	List<GSSearchResult> gsresults = gsResultManager.getResultsSortedByScore();
    int pathIndex = startIdx;
    
    for(int i = startIdx; i < endIdx ; i++) {
        try{
        	GSSearchResult result = gsresults.get(i);
            String path = result.getUrl();
            int idx = path.lastIndexOf('.');
            String extension = idx >= 0 ? path.substring(idx + 1) : "";
            String description = result.getDescription(); 
            %>
            <br/>
        <%
        if(!extension.isEmpty() && !extension.equals("html")){
        %>
            <span class="icon type_<%=extension%>"><img src="/etc/designs/default/0.gif" alt="*"></span>
        <%}%>
            <a href="<%=path%>"><%=result.getTitle()%></a>
            <!--   <div><strong>score:</strong>&nbsp;&nbsp;<%=result.getScore()%></div>-->
        <%
        if(description!=null &&  !description.isEmpty()) {
        %>  
            <div><%=description%></div>
        <%
        }else{
        %><div><%=result.getExcerpt()%></div>   
        <%} %>  
        <br/>
         <%}catch(Exception w){
             w.printStackTrace();
         }
    }   
}
%>
	
<ul class="search-page">
    	<%if (currentPageNo != 0) {  %>
    		<li><a href="${currentPage.path}.html?q=<%= q%>&start=<%=(currentPageNo - 1)*10%>"><</a></li>
    	<%}  %>

    	<%
        int first =0;
		int last =10;
    	if (currentPageNo>10){
        	first = currentPageNo -10;
        	last = currentPageNo;
		}
		
		if (totalPage<last) {
    		last = (int)totalPage -1;
		}

		if(totalPage > 1){
	    	for (int i = first; i <= last; i++ ) { 
	    		if (currentPageNo == i) {
	            	%><li class="currentPageNo"><%= i+1 %></li><%
	        	} else {
	            	if (((last-i)==1 && last >10 ) || (currentPageNo==10 &&i==9) ){
	                	%><li class="currentPageNo"><a href="${currentPage.path}.html?q=<%= q%>&start=<%=i*10%>"><%= i+1 %></a></li><%
	            	} else {
	                	%><li><a href="${currentPage.path}.html?q=${escapedQueryForAttr}&start=<%=i*10%>"><%= i+1 %></a></li><%
	            	}
	       	 	}
	    	}%>
	
	    	<%if (currentPageNo != totalPage-1 ) {  %>
	    		<li><a href="${currentPage.path}.html?q=${escapedQueryForAttr}&start=<%=(currentPageNo + 1)*10%>">></a></li>
	    	<%}
    	}  %>
</ul>
	
<script>
jQuery('#searchForm').bind('submit', function(event){
    if (jQuery.trim(jQuery(this).find('input[name="q"]').val()) === ''){
        event.preventDefault();
    }
});
</script>
