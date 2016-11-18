
<%@page import="
                javax.jcr.Session,
				java.io.*,
                org.apache.sling.api.resource.ResourceResolver,
                org.apache.sling.api.resource.Resource,
				org.apache.sling.api.adapter.Adaptable,
                com.day.cq.wcm.api.PageManager,
                com.day.cq.wcm.api.Page,
                com.day.cq.dam.api.Asset,
                com.day.cq.search.PredicateGroup,
                com.day.cq.search.Query,
                com.day.cq.search.QueryBuilder,
                org.girlscouts.vtk.ejb.YearPlanUtil,
                com.day.cq.search.result.SearchResult,
                org.girlscouts.vtk.helpers.CouncilMapper"%>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp"%>

<%@include file="../session.jsp"%>

<%
String resourceName = "";
if (request.getParameter("resource") != null) {
	resourceName = request.getParameter("resource");
}

// Page resourceContent;
%>

<script>
  var resourceName = <%= resourceName %>;
  var stringNice = 'https://www.youtube.com/embed/'+resourceName.split('/').reverse()[0].split('=').reverse()[0]+'?rel=0&amp;showinfo=0'
  $(function(){
    $('#youtube').attr('src',stringNice);
  })
</script>


<div class="modal_resource">
	<div class="header clearfix">
		<h3 class="columns large-22">
			Video
		</h3>
		<a class="close-reveal-modal columns large-2" href="#"><i
			class="icon-button-circle-cross"></i></a>
	</div>
	<div class="scroll content" style="max-height: 471px;">
      <div class="section video">
        <div  class="video-container">
          <iframe id="youtube" width="560" height="315" src="" frameborder="0" allowfullscreen=""></iframe>
        </div>
      </div>
	</div>
</div>
