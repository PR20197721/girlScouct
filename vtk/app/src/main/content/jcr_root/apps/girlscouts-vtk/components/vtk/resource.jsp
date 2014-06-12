<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*, org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>





<dl class="tabs" data-tab >
  <dd ><a href="#panel2-1">My Troup</a></dd>
  <dd ><a href="/content/girlscouts-vtk/en/vtk.html?ageLevel=brownie">Year Plan</a></dd>
  <dd ><a href="/content/girlscouts-vtk/en/vtk.planView.html">Meeting Plan</a></dd>
  <dd class="active"><a href="#panel2-4">Resources</a></dd>
  <dd><a href="#panel2-5">Community</a></dd>
</dl>
<div class="tabs-content">
    <div class="content" id="panel2-1"></div>
    <div class="content" id="panel2-2"></div>
    <div class="content" id="panel2-3"></div>
    <div class="content" id="panel2-4"></div>
    <div class="content" id="panel2-5"></div>
</div>


<a href="javascript:void(0)" id="rsc_hlp_href">help</a>
<div id="rsc_hlp" style="display:none;"><h1>Resources Help:</h1><ul><li>asdf</li><li>asdf</li><li>asdf</li></ul></div>

<br/><b>Search for Resources</b>
<input type="text" id="search_resource"/>


<br/><b>Browse Resources by Category</b>



<script>


$('#rsc_hlp_href').click(function() 
    $('#rsc_hlp').dialog();
    return false;
});
</script>