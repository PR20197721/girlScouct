
<%@ page
  import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<!-- PAGEID :: ./app/src/main/content/jcr_root/apps/girlscouts-vtk/components/vtk/vtk.jsp -->
<cq:defineObjects />
<%@include file="include/session.jsp"%>

<%
	if( selectedTroop.getYearPlan()!=null || ( selectedTroop.getRole() !=null &&  selectedTroop.getRole().equals("PA") ) ){
		%><%@include file="plan.jsp"%><%
	}else{
		%><script>self.location="/content/girlscouts-vtk/en/vtk.explore.html"; </script><%
	}
%>