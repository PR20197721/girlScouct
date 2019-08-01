<%@ page
        import="org.girlscouts.vtk.auth.permission.Permission,org.girlscouts.vtk.osgi.component.dao.YearPlanComponentType,org.girlscouts.vtk.models.MeetingE" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp" %>
<%
    String activeTab = "planView";
    boolean showVtkNav = true;
    org.girlscouts.vtk.models.PlanView planView = meetingUtil.planView(user, selectedTroop, request, true);
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
%>
<script>
    //need to call it again here.
    $(document).ready(function () {
        resizeWindow();
    })
</script>



