<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<div id="errInfo"></div>
<%
        String activeTab = "finances";
        boolean showVtkNav = true;
%>
<%@include file="include/vtk-nav.jsp"%>
<script>
        fixVerticalSizing = true;
</script>
<<<<<<< HEAD
<div class="row content">
  <div class="columns push-1">
    <h3>Coming in future releases:</h3> 
  	<ul>
  		<li>- Create and manage your troop's financial report</li>
  		<li>- Share with council personnel and with troop parents</li>
  	</ul>
  </div>
=======
<div id="vtkBody">
	<br/>
	<h3>Coming in future releases:</h3> 
		<ul>
			<li>- Create and manage your troop's financial report</li>
			<li>- Share with council personnel and with troop parents</li>
		</ul>
	<br/>
>>>>>>> parent of f644004... VTK changes to tabs, overall pages rows, and columns
</div>
