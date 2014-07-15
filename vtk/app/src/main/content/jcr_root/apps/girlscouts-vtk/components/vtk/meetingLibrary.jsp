<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<!-- apps/girlscouts-vtk/components/vtk/meetingLibrary.jsp  -->
<%!
        boolean showVtkNav = true;
       String activeTab = "resource";
%>
<%
        String meetingPath = request.getParameter("mpath");
        if( meetingPath==null || meetingPath.equals("null") || meetingPath.equals("")) meetingPath=null;

        if(meetingPath != null){
                showVtkNav =  false;
        }

        java.util.List<Meeting> meetings =null;

        String ageLevel=  user.getTroop().getGradeLevel();
	ageLevel= ageLevel.substring( ageLevel.indexOf("-")+1).toLowerCase().trim();

        java.util.Iterator<YearPlan> yearPlans =  yearPlanDAO.getAllYearPlans(ageLevel).listIterator();

        String find= request.getParameter("ypname");
        if( find==null || find.trim().equals("")){find = user.getYearPlan().getName();}
        find= find.trim();

%>
<div class="row modalHeader">
<%
        boolean isWarning=false;
        String instruction = "Select a meeting to add to your Year Plan";
        if (isWarning) {
%>
        <div class="small-4 medium-2 large-2 columns">
                <div class="warning"><img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/warning-small.png" width="20" height="20" align="left
"/></div>
        </div>
        <div class="small-16 medium-20 large-20 columns">
<%
        } else {
%>
        <div class="small-20 medium-22 large-22 columns">
<%
        }
%>
                <span class="instruction"><%= instruction %></span>

        </div>
        <div class="small-4 medium-2 large-2 columns">
		<a class="right" href="<%= meetingPath==null ? "/content/girlscouts-vtk/en/vtk.plan.html" : "/content/girlscouts-vtk/en/vtk.planView.html"%>"><img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/close-small.png" width="20" height="20" border="0" align="right"></a>
        </div>
</div>
<div class="row modalNav">
<%
        while (yearPlans.hasNext()) {
                YearPlan yearPlan = yearPlans.next();
                if( find.equals(yearPlan.getName().trim()) ){
%>
        <div class="small-24 medium-12 large-6 columns active manageCalendarTab">
                <a href="#"><%=yearPlan.getName()%></a>
        </div>
<%
                        java.util.List<MeetingE> meetingEs = meetingDAO.getAllEventMeetings_byPath( yearPlan.getPath() +"/meetings/" );
                        meetingEs= meetingUtil.sortById(meetingEs);

                        meetings = new java.util.ArrayList();
                        for(int i=0;i<meetingEs.size();i++){
                                meetings.add(  meetingDAO.getMeeting(  meetingEs.get(i).getRefId() ) );
                        }
                }else{
                        String url ="?ypname="+java.net.URLEncoder.encode(yearPlan.getName());
                        url+= request.getParameter("xx")==null ? "" : "&xx="+java.net.URLEncoder.encode(request.getParameter("xx"))  ;
                        url+= request.getParameter("mpath")==null ? "" : "&mpath="+java.net.URLEncoder.encode(request.getParameter("mpath"));
%>
        <div class="small-24 medium-12 large-6 columns manageCalendarTab">
                <a href="#" onclick="mm('/content/girlscouts-vtk/controllers/vtk.meetingLibrary.html<%=url%>')"><%=yearPlan.getName()%></a>
        </div>
<%
                }
        }
        java.util.List<String> myMeetingIds= new java.util.ArrayList();
        java.util.List<MeetingE> myMeetings = user.getYearPlan().getMeetingEvents();

        if( find.equals(user.getYearPlan().getName().trim() ) ) {
                for(int i=0;i< myMeetings.size();i++){
                        if( myMeetings.get(i).getCancelled()!=null && myMeetings.get(i).getCancelled().equals("true")) continue;
                        String meetingId = myMeetings.get(i).getRefId();
                        meetingId= meetingId.substring(meetingId.lastIndexOf("/") +1).trim().toLowerCase();
                        myMeetingIds.add( meetingId );
                }
        }
%>
</div>
<script>
function cngMeeting(mPath){
	$( "#cngMeet" ).load( "/content/girlscouts-vtk/controllers/vtk.controller.html?<%=meetingPath ==null ? "addMeeting" : "cngMeeting"%>=true&fromPath=<%=meetingPath%>&toPath="+mPath,function( html ) {
<%
	if( request.getParameter("xx") ==null ){
%>
		document.location="/content/girlscouts-vtk/en/vtk.plan.html";
<%}else{%>
		document.location="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=request.getParameter("xx")%>";
<%}%>
	});
}
</script>
<div class="row modalBody">
<div id="cngMeet"></div>
<table cellpadding="5" cellspacing="0" border="0" width="100%">
<%
	for(int i=0;i<meetings.size();i++){
		Meeting meeting = meetings.get(i);
%>
	<tr>
		<td>
			<h2><%=meeting.getName()%></h2>
			<h6> <%=meeting.getAidTags() %></h6>
			<p><%=meeting.getBlurb() %><p>
                        <%  if( !myMeetingIds.contains( meeting.getId().trim().toLowerCase()) ){ %>
                                <a href="#" onclick="cngMeeting('<%=meeting.getPath()%>')">Select Meeting</a>
                        <% }else{%>
                                <i>Included in Year Plan</i>
                        <% }%>
		</td>
		<td width="120">
			<img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/badge.png" width="100" height="100" border="0"/>
		</td>
	</tr>
<% 
	}
%>
</table>
</div>
