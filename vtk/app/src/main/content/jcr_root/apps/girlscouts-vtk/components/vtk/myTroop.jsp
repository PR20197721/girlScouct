<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<%
        String activeTab = "myTroop";
        boolean showVtkNav = true;
%>
<%@include file="include/vtk-nav.jsp"%>
<br/>
<h3>Coming in future releases:</h3> 
<ul>
	<li>- View troop membership and contact information</li>
	<li>- View and contact volunteers and council support personnel</li>
	<li>- Renew troop membership</li>
	<li>- Request information or support from your council</li>
	<li>- Gain access to the troop leadership community</li>
</ul>
<br/>
<script>
	fixVerticalSizing = true;
</script>
