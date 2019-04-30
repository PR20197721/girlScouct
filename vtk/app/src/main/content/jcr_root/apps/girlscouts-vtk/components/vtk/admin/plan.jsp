<%@ page
        import="org.girlscouts.vtk.models.MeetingE, org.girlscouts.vtk.models.YearPlan" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../include/session.jsp" %>
<!-- include for edit framework to work %@include file="../admin/toolbar.jsp"% -->
<%


    if (selectedTroop.getYearPlan() == null) {
        YearPlan plan = new YearPlan();
        plan.setName("My custom plan");
        java.util.List<MeetingE> meetings = new java.util.ArrayList<MeetingE>();
        plan.setMeetingEvents(meetings);
        selectedTroop.setYearPlan(plan);

    }

%>


<jsp:forward page="/content/girlscouts-vtk/controllers/vtk.plan.html"/>