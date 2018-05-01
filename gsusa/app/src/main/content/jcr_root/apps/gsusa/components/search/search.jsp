<%@ page import="com.day.cq.wcm.foundation.Search,
org.girlscouts.web.search.GSSearchResult, 
org.girlscouts.web.search.GSSearchResultManager,
org.girlscouts.web.search.GSJcrSearchProvider,
java.util.Locale,
java.util.ResourceBundle,
com.day.cq.i18n.I18n,
java.util.List, 
java.util.regex.*, 
java.text.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:setContentBundle source="page" />
<%
final Locale pageLocale = currentPage.getLanguage(true);
final ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);
String q = request.getParameter("q") != null ? request.getParameter("q") : "";
String cleanedQ = q.replaceAll("[^a-zA-Z0-9 ]+", "");
String start = request.getParameter("start") != null ? request.getParameter("start") : "0";
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


String documentLocation = "/content/dam/gsusa-shared/documents";
String searchIn = (String) properties.get("searchIn");
if (null==searchIn){
  searchIn = currentPage.getAbsoluteParent(2).getPath();
}

final String escapedQuery = xssAPI.encodeForHTML(cleanedQ != null ? cleanedQ : "");
final String escapedQueryForAttr = xssAPI.encodeForHTMLAttr(q != null ? q : "");
%>
<form action="${currentPage.path}.html" id="searchForm" class="row">
    <input type="text" name="q" value="${escapedQueryForAttr}" pattern=".{3,}" required title="3 characters minimum" class="searchField" />
    <input type="submit" value="search" class="button" />
</form>
<% 
if(escapedQueryForAttr != null && !escapedQueryForAttr.isEmpty()){
	pageContext.setAttribute("escapedQuery", escapedQuery);
	pageContext.setAttribute("escapedQueryForAttr", escapedQueryForAttr);

	String theseDamDocuments = properties.get("docusrchpath","");
	if(theseDamDocuments.equals("")){
	  	String regexStr = "/(content)/([^/]*)/(en)$";
	  	Pattern pattern = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE);
	  	Matcher matcher = pattern.matcher(currentPage.getAbsoluteParent(2).getPath());
	  	if (matcher.find()) {
	    	theseDamDocuments = "/" + matcher.group(1) + "/dam/gsusa-" +  matcher.group(2) + "/documents";
	  	}
	}
	long startTime = System.nanoTime();

	GSSearchResultManager gsResultManager = new GSSearchResultManager();
	try{
		GSJcrSearchProvider searchProvider = new GSJcrSearchProvider(slingRequest);
		gsResultManager.add(searchProvider.search(searchIn, "cq:Page", q));
		gsResultManager.add(searchProvider.search(theseDamDocuments, "dam:Asset", q));
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

	<%if(gsResultManager.size() < 1){ %>
	    <fmt:message key="noResultsText">
	      <fmt:param value="${escapedQueryForAttr}"/>
	    </fmt:message>
	<% } else { %>
	    <p><strong>
	        <%= numberOfResults%> <%= properties.get("resultPagesText","Results for")%> "${escapedQueryForAttr}"
	    </strong></p>
	    <ul class="search-row">
		<%
  
  
		List<GSSearchResult> gsresults = gsResultManager.getResults();
		int pathIndex = startIdx;
	  for(int i = startIdx; i < endIdx ; i++) {
	    try {
	    	GSSearchResult result = gsresults.get(i);
            String path = result.getUrl();
            int idx = path.lastIndexOf('.');
            String extension = idx >= 0 ? path.substring(idx + 1) : "";
            String description = result.getDescription(); 	      
	      	%>
	            <li>
	                <% if(!extension.isEmpty() && !extension.equals("html")) { %>
	                <span class="icon type_<%=extension%>"><!-- <img src="/etc/designs/default/0.gif" alt="*"> --></span>
	                <% } %>
	                <h5><a href="<%=path%>"><%=result.getTitle()%></a></h5>
	                <!-- <div><strong>score:</strong>&nbsp;&nbsp;<%=result.getScore()%></div> -->
	                <%
			        if(description!=null &&  !description.isEmpty()) {
			        %>  
			            <p><%=description%></p>
			        <%
			        }else{
			        %><p><%=result.getExcerpt()%></p>   
			        <%} %>	                
	            </li>
	    <% } catch(Exception w) {}
	  } %>
    </ul>
    <ul class="search-page">
    	<%if (currentPageNo != 0) {  %>
    		<li><a href="${currentPage.path}.html?q=<%= q%>&start=<%=(currentPageNo - 1)*10%>"><</a></li>
    	<%}  %>
    <%for (int i = 0; i < totalPage; i++ ) { 
    	if (currentPageNo == i) {%>
    		<li class="currentPageNo"><%= i+1 %></li>
    	<%} else {%>
    		<li><a href="${currentPage.path}.html?q=<%= q%>&start=<%=i*10%>"><%= i+1 %></a></li>
    <%	}
    }%>
    <%if (currentPageNo != totalPage-1) {  %>
    		<li><a href="${currentPage.path}.html?q=<%= q%>&start=<%=(currentPageNo + 1)*10%>">></a></li>
    	<%}  %>
    </ul>
<% } 
}
%>

<script type="text/javascript">
jQuery('#searchForm').bind('submit', function(event){
  if (jQuery.trim(jQuery(this).find('input[name="q"]').val()) === ''){
    event.preventDefault();
  }
});
</script>
