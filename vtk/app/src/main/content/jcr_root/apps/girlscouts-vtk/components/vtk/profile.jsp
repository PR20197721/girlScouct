<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<div id="errInfo"></div>
<%
        String activeTab = "profile";
        boolean showVtkNav = true;
%>
<%@include file="include/tab_navigation.jsp"%>
 <div id="panelWrapper" class="row content meeting-detail">
  <div class="columns large-20 large-centered">
  	<h3>Coming in future releases:</h3> 
		<ul>
			<li>- Manage your personal profile</li>
		</ul>
  </div>
</div>
