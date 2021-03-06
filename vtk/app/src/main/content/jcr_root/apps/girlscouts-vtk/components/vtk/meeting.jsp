<%@ page
        import="org.girlscouts.vtk.models.Meeting, org.girlscouts.vtk.models.MeetingE, java.util.Calendar" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp" %>
<div id="modal_popup" class="reveal-modal" data-reveal></div>
<a href="/content/girlscouts-vtk/en/vtk.html">Back to Plan</a>
<div id="errInfo"></div>
<%
    String meetingPath = request.getParameter("mid");
    java.util.List<MeetingE> meetings = selectedTroop.getYearPlan().getMeetingEvents();
    java.util.StringTokenizer calT = null;
    if (selectedTroop.getYearPlan().getSchedule() != null) {
        String schedStr = selectedTroop.getYearPlan().getSchedule().getDates();
        calT = new java.util.StringTokenizer(schedStr, ",");
    }
    java.util.Calendar meetingDate = null;
    for (int i = 0; i < meetings.size(); i++) {
        MeetingE meeting = meetings.get(i);
        if (meeting.getPath().equals(meetingPath)) {
            Meeting meetingInfo = yearPlanUtil.getMeeting(user, meeting.getRefId());
            if (calT != null) { //sched
                meetingDate = Calendar.getInstance();
                meetingDate.setTimeInMillis(Long.parseLong(calT.nextToken()));
            }
%>
<%@include file="include/meetingView.jsp" %>
<%
            break;
        }
        if (calT != null) {
            calT.nextToken();
        }
    }
%>
