<%@ page import="com.day.text.Text, java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*, java.io.*, java.net.*"%>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<div id="error-message"></div>
<%
  String activeTab = "myTroop";
  boolean showVtkNav = true;
%>
 <div id="vtkTabNav"></div>
<div id="panelWrapper" class="row content">
<%
	if (true){//SHOW_BETA || sessionFeatures.contains(SHOW_BETA_FEATURE)) { %>
    <%@include file="mytroop_react.jsp"%><% 
  } else { %>
    <%@include file="myTroopOff.jsp"%>
<% } %>
<script>loadNav('myTroop')</script>
</div><!--panel-wrapper-->

