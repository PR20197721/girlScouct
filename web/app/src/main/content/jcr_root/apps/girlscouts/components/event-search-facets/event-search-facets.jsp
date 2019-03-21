<%@ page import="com.day.cq.tagging.TagManager,java.util.ArrayList,
                java.util.HashSet, java.util.Locale,java.util.Map,
                java.util.Iterator,java.util.HashMap,java.util.List,
                java.util.Set,com.day.cq.search.result.SearchResult,
                java.util.ResourceBundle,com.day.cq.search.QueryBuilder,
                javax.jcr.PropertyIterator, 
                com.day.cq.i18n.I18n,org.apache.sling.api.resource.ResourceResolver,                
                java.util.Collections"
                %>
<%@include file="/libs/foundation/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects/>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<div class="row">
    <div class="small-24 large-24 medium-24 columns">&nbsp;</div>
</div>
<div class="row">
     <div class="small-12 large-12 medium-12 columns">
          <cq:include path="search-box" resourceType="girlscouts/components/search-box" />
      </div>
      <button id="eventSearchSubmit"style="padding:10px; width: 78px; margin-left: -16px; color: white;"type="submit">Go</button>
 </div>
 <div class="row">
         <span id="advSearch">
            <a style="margin-left:6px;"href="<%=currentPage.getPath()%>.advanced.html">Advanced Search</a>
        </span>
 </div>
<script>
    $("#eventSearchSubmit").on('click', function(){
        $($(".event-search-facets").find("form")).submit();
    });
    $("#advSearch").on('click', function(){
        var ref = $($("#advSearch").find("a")).attr("href");
        if($($(".event-search-facets").find("input")).val() !== ""){
            if(!ref.includes("?search=")){
                ref = ref + "?search=" + $($(".event-search-facets").find("input")).val();
            }else{
                ref = ref.replace(ref.substring(ref.indexOf("?search=")), "?search=" + $($(".event-search-facets").find("input")).val());
            }
             $($("#advSearch").find("a")).attr("href", ref)
        }


    });
</script>