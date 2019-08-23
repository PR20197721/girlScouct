<%@ page
        import="org.girlscouts.vtk.auth.permission.Permission,org.girlscouts.vtk.osgi.component.dao.YearPlanComponentType,org.girlscouts.vtk.models.MeetingE" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp" %>
<%
    String activeTab = "planView";
    boolean showVtkNav = true;
    boolean isArchived = !user.getCurrentYear().equals(String.valueOf(VtkUtil.getCurrentGSYear()));
    org.girlscouts.vtk.models.PlanView planView = null;
    if (isArchived) {
        Troop archivedTroop = (Troop)session.getAttribute("VTK_archived_troop");
        planView = meetingUtil.planView(user, archivedTroop, request);
    }else{
        planView = meetingUtil.planView(user, selectedTroop, request, true);
    }
    if(planView != null){
        try{
            if (planView.getYearPlanComponent().getType() == YearPlanComponentType.MEETINGCANCELED || planView.getYearPlanComponent().getType() == YearPlanComponentType.MEETING) {
%>
<%@include file="meeting_react2.jsp" %>
<%@include file="include/loader.jsp"%>
<%
} else if (planView.getYearPlanComponent().getType() == YearPlanComponentType.ACTIVITY) {
%>
<%@include file="activity_react2.jsp" %>
<%
            }
        }catch(Exception e){
        }
    }
%>
<script>
    //need to call it again here.
    $(document).ready(function () {
        resizeWindow();
    })
</script>



