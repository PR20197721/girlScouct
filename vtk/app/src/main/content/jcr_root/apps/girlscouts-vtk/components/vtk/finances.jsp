<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<%!
        String activeTab = "finances";
        boolean showVtkNav = true;
%>
<%@include file="include/vtk-nav.jsp"%>
<p>
<h3>Coming in future releases:</h3> 
<ul>
<li>- Create and manage your troop's financial report</li>
<li>- Share with council personnel and with troop parents</li>
</ul>
</p>