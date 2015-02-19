<%@ page
  import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<!-- PAGEID :: ./app/src/main/content/jcr_root/apps/girlscouts-vtk/components/vtk/plan.jsp -->
<cq:defineObjects />
<%@include file="include/session.jsp"%>

<% 
final CouncilRpt councilRpt = sling.getService(CouncilRpt.class);
java.util.List<CouncilRptBean> container = councilRpt.getRpt( request.getParameter("cid"));
councilRpt.fmtRpt( container );
%>

<%=container.size()%>