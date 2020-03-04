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
<!--GSWP-2056: Created clientlibs and added categories -->
<cq:includeClientLib categories="cq.jquery.ui,apps.girlscouts,app.girlscouts.eventSearchFacets" />
<cq:defineObjects/>
<%@include file="/apps/girlscouts/components/global.jsp"%>

<%
    String placeHold = slingRequest.getParameter("search") != null ? slingRequest.getParameter("search") : "";

 %>
 <div id="searchedVal" searched = <%=placeHold%>></div>
<div class="row">
    <div class="small-24 large-24 medium-24 columns">&nbsp;</div>
</div>
<div class="row">
     <div class="small-12 large-12 medium-12 columns">
          <cq:include path="search-box" resourceType="girlscouts/components/search-box" />
      </div>
      <button id="eventSearchSubmit"style="padding:10px; width: 78px; margin-left: -16px; color: white;"type="submit">Go</button>
      <span id="advSearch">
         <a style="margin-left:6px; font-size:1rem;"href="<%=currentPage.getPath()%>.advanced.html">Advanced Search</a>
      </span>
 </div>
