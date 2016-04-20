<%@ page import="org.girlscouts.web.search.formsdocuments.FormsDocumentsSearch, java.util.Map,java.util.List,org.girlscouts.web.events.search.SearchResultsInfo, org.girlscouts.web.events.search.FacetsInfo,com.day.cq.search.result.Hit, org.girlscouts.web.search.DocHit,java.util.HashSet,java.util.*"%> 
<%@page import="javax.jcr.query.*,
                javax.jcr.*" %>


<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects/>
<script>
    function checkLen(){
        var x = document.getElementById("frmSrch");

        if( x!=null  ){
            x= $.trim( x.value ) ;
        }


        if( x.length <4 ){

            var isTag=0;
            var coffee = document.frm.tags;
            for (i = 0; i < coffee.length; i++) {
              if (coffee[i].checked) {
                isTag = isTag +1 ; 
              }
            }
            if( isTag>0 ){return true;}
            alert("Search queries must be greater than 3 characters.");
            return false;
        }
        return true;
    }
</script>
<%
// This is the path where the documents will reside
String path = properties.get("./srchLocation", "");
if(path.isEmpty()){  
    path = "/content/dam/girlscouts-shared/en/documents";
}

String formDocumentContentPath = properties.get("./form-document-path","");
if(formDocumentContentPath.isEmpty()){
    //formDocumentContentPath = "/content/gateway/en/about-our-council/forms-documents";
    //change default content path to current page.
    formDocumentContentPath = currentPage.getPath();
}

FormsDocumentsSearch formsDocuImpl = sling.getService(FormsDocumentsSearch.class);
String q = request.getParameter("q");
q= q==null ? "" : q.trim();
String param ="";

// xss escaped query string
final String escapedQueryForAttr = xssAPI.encodeForHTMLAttr(q != null ? q : "");

// user selected tags
String[] tags = new String[]{};

Set<String> set = new HashSet<String>();
boolean thisIsAdvanced = false;
if (request.getParameterValues("tags") != null) {
    thisIsAdvanced = true;
    tags = request.getParameterValues("tags");
    String tagParams="";
    for (String words : tags){
        set.add(words);
    }
    
}





Map<String, List<FacetsInfo>> facetsAndTags = formsDocuImpl.loadFacets(slingRequest, currentPage.getAbsoluteParent(1).getName());
try{
    //formsDocuImpl.executeSearch(resourceResolver, q, path, tags, formDocumentContentPath,facetsAndTags);
}catch(Exception e){}
List<Hit> hits = new ArrayList(); //formsDocuImpl.getSearchResultsInfo().getResultsHits();

String suffix = slingRequest.getRequestPathInfo().getSuffix();
if (suffix != null) {
    thisIsAdvanced = true;
}
String formAction = currentPage.getPath()+".html";
String placeHolder = "Keyword Search";

%>
<div class="expandable">
<div class="programLevel">
    <form action="<%=formAction%>" method="get" name="frm" onsubmit="return checkLen()">
        <div id="searchBox" class="baseDiv">
            <input type="text" name="q" id="frmSrch" class="searchField formsAndDocuments" placeholder="<%=placeHolder%>" value="<%=escapedQueryForAttr%>" />
        </div>
<script>
    function toggleOption() {
        if ($("#optionIndicatorId").attr("src") == "/etc/designs/girlscouts-usa-green/images/green-down-arrow.png") {
            $(".optionIndicator").attr("src", "/etc/designs/girlscouts-usa-green/images/green-up-arrow.png");
            $(".advancedSearch").hide();
        } else {
                        $(".optionIndicator").attr("src", "/etc/designs/girlscouts-usa-green/images/green-down-arrow.png");
                        $(".advancedSearch").show();
        }
    }
    $(document).ready(function() {
<%
    if (thisIsAdvanced) {
%>
        toggleOption();
<%
    }
%>
    });
</script>
        <div class="baseDiv toggleDisplay">
            <a href="#" onclick="toggleOption()"><img id="optionIndicatorId" class="optionIndicator" src="/etc/designs/girlscouts-usa-green/images/green-up-arrow.png" width="15" height="20">&nbsp;Options&nbsp;<img class="optionIndicator" src="/etc/designs/girlscouts-usa-green/images/green-up-arrow.png" width="15" height="20"></a>
        </div>
        <div class="options formsSearchOptions baseDiv advancedSearch">
            <div id="title">Categories</div>
            <ul class="checkbox-grid small-block-grid-1 medium-block-grid-1 large-block-grid-2">
<%

List fdocs = facetsAndTags.get("forms_documents");
// Here if we don't have forms_document page shouldn't blow-up
try {
    for(int pi=0; pi<fdocs.size(); pi++){
        FacetsInfo fdocLevelList = (FacetsInfo)fdocs.get(pi);
%>  
            <li>
                <input type="checkbox" id="<%=fdocLevelList.getFacetsTagId()%>" value="<%=fdocLevelList.getFacetsTagId()%>" name="tags" <%if(set.contains(fdocLevelList.getFacetsTagId())){ %>checked <%} %>/>
                <label for="<%=fdocLevelList.getFacetsTagId() %>"><%=fdocLevelList.getFacetsTitle()%></label>
            </li> 
<%  }
}catch(Exception e){}
%>

            </ul>
        </div>
        
        <div class="searchButtonRow baseDiv advancedSearch">
            <input type="submit" value="Search" class="form-btn advancedSearchButton"/>
        </div>
    </form>
</div>
<% if((q!=null && !q.isEmpty()) ||  (request.getParameterValues("tags") != null)){ %>
<div class="search">
<%
try{

 boolean isTag=false;
 if( set!=null && set.size()>0){isTag= true;} 

Session session = resourceResolver.adaptTo(Session.class);
QueryManager qm = session.getWorkspace().getQueryManager();
if( q.length()>3  ||  (set!=null && set.size()>0))
 for(int i=0;i<3;i++){  

String query = "";
    if( i==1 )
        query ="SELECT "+ (isTag ? "[/jcr:content/metadata/cq:tags]," : "") +" [excerpt(/renditions/original/jcr:content/jcr:data)],[jcr:content/renditions/original/jcr:content/jcr:data], [jcr:path],[jcr:content/metadata/dc:title],[jcr:content/jcr:title],[jcr:content/metadata/dc:description] from [dam:Asset] WHERE ISDESCENDANTNODE(["+path +"]) "+ ((q.length() > 0 ) ? " AND contains(*, '"+q+"')" : "");

    else if( i==0 )
        query ="SELECT "+ (isTag ? "[/jcr:content/cq:tags]," : "") +" [excerpt(/renditions/original/jcr:content/jcr:data)],[jcr:content/renditions/original/jcr:content/jcr:data], [jcr:path],[jcr:content/metadata/dc:title],[jcr:content/jcr:title],[jcr:content/metadata/dc:description] from [cq:Page] WHERE ISDESCENDANTNODE(["+formDocumentContentPath+"])  "+ ((q.length() > 0 ) ? " AND contains(*, '"+q+"')" : "");

    else if( i==2 )
        query ="SELECT  [excerpt(/renditions/original/jcr:content/jcr:data)],[jcr:content/renditions/original/jcr:content/jcr:data], [jcr:path],[jcr:content/metadata/dc:title],[jcr:content/jcr:title],[jcr:content/metadata/dc:description] from [dam:Asset] WHERE ISDESCENDANTNODE([/content/dam/girlscouts-shared/documents])  "+ ((q.length() > 0 ) ? " AND contains(*, '"+q+"')" : "");

        out.println("<!-- "+ query +" -->" ); 



    Query q1 = qm.createQuery(query, Query.JCR_SQL2);
    // q1.setLimit(100); 
QueryResult result = q1.execute();
    alex:for (RowIterator it = result.getRows(); it.hasNext(); ) {
        try{       
   Row r = it.nextRow();




  if( i!=2 && isTag ){

   boolean isFound= false; 
   String _tags=  "";
   if( i!=0 ) _tags = getValue(r,"/jcr:content/metadata/cq:tags");
    else
        _tags = getValue(r,"/jcr:content/cq:tags");

       _tags= _tags +",";
       StringTokenizer t= new StringTokenizer( _tags, ",");
       while( t.hasMoreElements()){
            String _tag= t.nextToken();
            if( set.contains( _tag ) ){ isFound= true; }
        }

    if( !isFound ){continue alex;}
  }//edn if





    String pth = getValue(r,"jcr:path");

    String title = "";
    title = getValue(r, "jcr:content/metadata/dc:title");

    if (title.isEmpty()) {
        title = getValue(r, "jcr:content/jcr:title");
    }
    if (title.isEmpty()) {
        title = path.substring(path.lastIndexOf("/") + 1);
    }


String description = getValue(r, "jcr:content/metadata/dc:description");



int idx = pth.lastIndexOf('.');
        String extension = idx >= 0 ? pth.substring(idx + 1) : "";
        String newWindow = "";
%>
        <br/>
<%
        if(!extension.isEmpty() && !extension.equals("html")){
            newWindow = "target=_blank";
%>
        <span class="icon type_<%=extension%>"><img src="/etc/designs/default/0.gif" alt="*"></span>
<%
        }
%>
    <a href="<%=(i==0) ? (pth+".html") : pth%>" <%=newWindow %>><%=title %></a>
<%
        if ( (set!=null && set.size()>0 ) || (q != null && !q.isEmpty()) ) {
            String excerpt = description;
            if (description ==null || "".equals(description)) {
                excerpt = title;
            }
%>
    <div><%= excerpt %></div>
<%
        }
%>
    <br/>

<!--end-->


    <%
        }catch(Exception ex){ex.printStackTrace();}
}//end for
}//end i

}catch(Exception e){ e.printStackTrace();System.err.println(e.getMessage()); }
%>
</div>
<%}%>
</div>



<%!
public String getValue(Row r, String column) throws ItemNotFoundException, RepositoryException {
    String toRet="";

    try{
        if( r==null) return "";
        Value val = r.getValue(column);
        if (val == null) {
            return "";
        }
        String strVal = val.getString();
        if (strVal == null) {
            return "";
        }
        return  strVal.trim();

    }catch(Exception exx){System.err.println("tata: "+column); exx.printStackTrace();}
    return "";
}
%>
