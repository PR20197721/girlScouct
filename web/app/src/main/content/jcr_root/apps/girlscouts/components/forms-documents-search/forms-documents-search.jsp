<%@ page import="org.girlscouts.web.search.formsdocuments.FormsDocumentsSearch,
                 com.day.cq.search.QueryBuilder,java.util.Map,java.util.List,org.girlscouts.web.events.search.SearchResultsInfo,
                 org.girlscouts.web.events.search.FacetsInfo,com.day.cq.search.result.Hit,
                 org.girlscouts.web.search.DocHit,java.util.HashSet"%>


<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>


<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects/>

<%

// This is the path where the documents will reside


String path = properties.get("./srchLocation", "");

if(path.isEmpty()){  
  path = "/content/dam/girlscouts-shared/en/documents";
}
FormsDocumentsSearch formsDocuImpl = sling.getService(FormsDocumentsSearch.class);

QueryBuilder queryBuilder = sling.getService(QueryBuilder.class);
String q = request.getParameter("q");
String param ="";
final String escapedQueryForAttr = xssAPI.encodeForHTMLAttr(q != null ? q : "");
if(q!=null && !q.isEmpty()){
	//Add Parameter to option
	param="q="+escapedQueryForAttr;
	
}
String[] tags = new String[]{};

HashSet<String> set = new HashSet<String>();
if (request.getParameterValues("tags") != null) {
	tags = request.getParameterValues("tags");
	String tagParams="";
	 set = new HashSet<String>();
     for (String words : tags){
         set.add(words);
         tagParams+="&tags="+words;
     }
     param+=param!=null?tagParams:tagParams.replaceFirst("&", " ").trim();
} 


formsDocuImpl.executeSearch(slingRequest, queryBuilder, q, path, tags);
Map<String,List<FacetsInfo>> facetsAndTags = formsDocuImpl.getFacets();
List<Hit> hits = formsDocuImpl.getSearchResultsInfo().getResultsHits();
String suffix = slingRequest.getRequestPathInfo().getSuffix();
String formAction = currentPage.getPath()+".html";
if(suffix!=null){
	formAction+=suffix;
	
}
String placeHolder = "Keyword Search";
String advanceLink = currentPage.getPath()+".html"+"/advance";
String link = currentPage.getPath()+".html";

if(param!=null && !param.isEmpty()){
	advanceLink+="?"+param;
	link+="?"+param;
}


%>
<form action="<%=formAction%>" method="get">
<div class="baseDiv programLevel small-24 large-24 medium-24 columns" style="border-top: 2px solid rgb(191, 194, 194)">
   <div class="row">
     <div class="small-20 large-20 medium-20 columns push-4">
       <div id="searchBox" style="width:600px">
      		<div class="small-12 large-12 medium-12 columns">
      		   <%if(!escapedQueryForAttr.isEmpty()){ %>
				<input type="text" name="q" class="searchField" value="<%=escapedQueryForAttr%>" style="height:40px;" />
				<%}else{%>
					<input type="text" name="q" class="searchField" placeholder="<%=placeHolder%>" style="height:40px;" />
					
				<%} %>
    		</div>
        </div>
     </div>
   </div>
   <div class="row">
      <div class="small-24 large-24 medium-24 columns" style="padding-left:3px;padding-right:3px">
         <div style="border-top: 2px solid rgb(191, 194, 194)"></div>
   </div>
   </div>
    <div class="row">
      <div class="small-16 large-16 medium-16 columns push-8"><a href="<%=formAction%>">
         <%if(suffix!=null){%>
        	 <a href="<%=link%>"><div class="option-top"></div></a>
    	<%}else{%>
    		 <a href="<%=advanceLink%>"><div class="option-down"></div></a><%}%>
    	
    </div>
   </div>
 <%if(suffix!=null)
 {%>
   <div class="options ">
	   <div class="row">
		    <div class="small-24 large-24 medium-24 columns">
		    	<div id="title"> Categories  </div>
		    </div>
	    </div>
	    <div class=row>
	      <div class="small-24 large-24 medium-24 columns" style="padding-left:0;padding-right:0;">
	        <ul class="checkbox-grid">
	        <%
	         List fdocs = facetsAndTags.get("forms_documents");
	        for(int pi=0; pi<fdocs.size(); pi++){
	            FacetsInfo fdocLevelList = (FacetsInfo)fdocs.get(pi);
	            %>  
	            <li>
	            		<input type="checkbox" id="<%=fdocLevelList.getFacetsTagId()%>" value="<%=fdocLevelList.getFacetsTagId()%>" name="tags" <%if(set.contains(fdocLevelList.getFacetsTagId())){ %>checked <%} %>/>
	             		<label for="<%=fdocLevelList.getFacetsTitle() %>"><%=fdocLevelList.getFacetsTitle()%> (<%=fdocLevelList.getCounts()%>)</label>
	             </li> 
	            
	          <%}
	        %>
	        </ul>
        </div>
        </div>
		<div class="row">
	      <div class="small-24 large-24 medium-24 columns" style="padding-left:3px;padding-right:3px">
	        <div style="border-top: 2px solid rgb(191, 194, 194)"></div>
	      </div>
	   </div>
   </div>
   <div class="row">
      <div class="small-24 large-24 medium-24 columns">&nbsp;</div>
   </div>
   
	<div class="row" style="padding-top:10px">
		<div class="small-24 large-24 medium-24 columns">
		    <input type="submit" value="Search" id="sub" class="form-btn pull-right">
		</div>
	</div>
<%} %>
</div>
</form>
<div class="row">
  <div class="small-24 medium-24 large-24 columns">&nbsp;</div>
</div>


<% 
if((q!=null && !q.isEmpty()) ||  (request.getParameterValues("tags") != null)){
%>

<div class="search">
<%
    for(Hit hit: hits)
{
         DocHit docHit = new DocHit(hit);
         String pth = docHit.getURL();
		 int idx = pth.lastIndexOf('.');
		 String extension = idx >= 0 ? pth.substring(idx + 1) : "";
		 	 
%>
	<br/>
	<%
		if(!extension.isEmpty() && !extension.equals("html")){
	%>
	<span class="icon type_<%=extension%>"><img src="/etc/designs/default/0.gif" alt="*"></span>
     <%} %>
	<a href="<%=pth%>"><%=docHit.getTitle() %></a>
       <div><%=docHit.getExcerpt()%></div>
     <br/>
   <%}%>	
</div>
<%}%>
    
<style>
.checkbox-grid li {
    display: block;
    float: left;
    width: 50%;
}

</style>    

