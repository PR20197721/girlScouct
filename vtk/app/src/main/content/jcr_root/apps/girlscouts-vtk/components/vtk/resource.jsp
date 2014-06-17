<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" import="org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*, org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<%!
        String activeTab = "resource";
        boolean showVtkNav = true;

%>
<%@include file="include/vtk-nav.jsp"%>



<link rel="stylesheet" type="text/css" href="css/jquery.autocomplete.css" />
	<script src="http://www.google.com/jsapi"></script>  
	<script>  
		google.load("jquery", "1");
	</script>
	<script src="js/jquery.autocomplete.js"></script>  
	<style>
		input {
			font-size: 120%;
		}
	</style>
	
	
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



$("#search_resource").autocomplete("/content/girlscouts-vtk/controllers/vtk.getdata.html");

</script>
