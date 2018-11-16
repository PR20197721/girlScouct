
<%@ page
  import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<!-- PAGEID :: ./app/src/main/content/jcr_root/apps/girlscouts-vtk/components/vtk/vtk.jsp -->
<cq:defineObjects />
<%@include file="include/session.jsp"%>

<%
	if( troop.getYearPlan()!=null || ( troop.getTroop().getRole() !=null &&  troop.getTroop().getRole().equals("PA") ) ){
		%><%@include file="plan.jsp"%><%
	}else{
    //RequestDispatcher rd = request.getRequestDispatcher("/content/girlscouts-vtk/controllers/vtk.explore.html");
    //rd.forward(request, response);
		%><script>self.location="/content/girlscouts-vtk/en/vtk.explore.html"; </script><%
	}
%>