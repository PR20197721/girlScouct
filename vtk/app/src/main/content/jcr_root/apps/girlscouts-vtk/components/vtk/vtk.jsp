<%@ page
        import="org.girlscouts.vtk.auth.permission.Permission,org.girlscouts.vtk.models.PlanView" %>
<%@include file="/libs/foundation/global.jsp" %>
<!-- PAGEID :: ./app/src/main/content/jcr_root/apps/girlscouts-vtk/components/vtk/vtk.jsp -->
<cq:defineObjects/>
<%@include file="include/session.jsp" %>
<%
    if (selectedTroop.getYearPlan() != null || (selectedTroop.getRole() != null && selectedTroop.getRole().equals("PA") && !"IRM".equals(selectedTroop.getParticipationCode()))) {
%>
<%@include file="plan.jsp" %>
<%
} else {
%>
<script>self.location = "/content/girlscouts-vtk/en/vtk.explore.html"; </script>
<%
    }
%>