<%@ page import="org.girlscouts.vtk.auth.permission.Permission,org.girlscouts.vtk.models.PlanView" %>
<%@include file="/libs/foundation/global.jsp" %>
<!-- PAGEID :: ./app/src/main/content/jcr_root/apps/girlscouts-vtk/components/vtk/vtk.jsp -->
<cq:defineObjects/>
<%
    Logger vtklog = LoggerFactory.getLogger(this.getClass().getName());
    if (request.getSession().getAttribute("fatalError") != null) {
        vtklog.debug("fatal error is set in session " + request.getSession().getAttribute("fatalError"));
        %>
        <%@include file="include/vtkError.jsp" %>
        <%
    } else {
        %>
        <%@include file="include/session.jsp" %>
        <%
        if (selectedTroop.getYearPlan() != null || (selectedTroop.getRole() != null && (selectedTroop.getRole().equals("PA") || selectedTroop.getRole().equals("FA")) && !"IRM".equals(selectedTroop.getParticipationCode()))) {
            %>
            <%@include file="plan.jsp" %>
            <%
        } else {
            if("VTK Admin View".equals(selectedTroop.getTroopName())){
                %>
                <script>self.location = "/content/girlscouts-vtk/en/vtk.finances.html"; </script>
                <%
            }else{
                %>
                <script>self.location = "/content/girlscouts-vtk/en/vtk.explore.html"; </script>
                <%
            }
        }
    }
%>