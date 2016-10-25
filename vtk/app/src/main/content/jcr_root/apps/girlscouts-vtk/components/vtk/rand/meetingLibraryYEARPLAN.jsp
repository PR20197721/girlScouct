<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<!-- apps/girlscouts-vtk/components/vtk/meetingLibrary.jsp  -->
<%
        boolean showVtkNav = true;
       String activeTab = "resource";

        String meetingPath = request.getParameter("mpath");
        if( meetingPath==null || meetingPath.equals("null") || meetingPath.equals("")) meetingPath=null;

        if(meetingPath != null){
                showVtkNav =  false;
        }

        java.util.List<Meeting> meetings =null;

        String ageLevel=  troop.getTroop().getGradeLevel();
	ageLevel= ageLevel.substring( ageLevel.indexOf("-")+1).toLowerCase().trim();

        java.util.Iterator<YearPlan> yearPlans =  yearPlanUtil.getAllYearPlans(user, ageLevel).listIterator();
	List<YearPlan> yearPlanList = new ArrayList<YearPlan>();
        while (yearPlans.hasNext()) {
		yearPlanList.add(yearPlans.next());
	}
	

        String find= request.getParameter("ypname");
        if( find==null || find.trim().equals("")){find = troop.getYearPlan().getName();}
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
<!--
		<a class="right" href="<%= meetingPath==null ? "/content/girlscouts-vtk/en/vtk.plan.html" : "/content/girlscouts-vtk/en/vtk.planView.html"%>"><img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/close-small.png" width="20" height="20" border="0" align="right"></a>
-->
		<a class"right" href="#" onclick="closeModalPage()"><img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/close-small.png" width="20" height="20" border="0" align="right"></a>
        </div>
</div>
<div class="row modalNav">
<%
	int MAX_TAB_COUNT = 4;
	int MAX_CHARS_PER_LINE = 22;
	int missingTabCount = MAX_TAB_COUNT  - yearPlanList.size() % MAX_TAB_COUNT;
	if (missingTabCount == MAX_TAB_COUNT) {
		missingTabCount = 0;
	}
	int liveTabsLarge = MAX_TAB_COUNT - missingTabCount;
	int liveTabsMedium = 2;
	if (liveTabsLarge  < liveTabsMedium) {
		liveTabsMedium = liveTabsLarge;
	}
%>
        <ul class="small-block-grid-1 medium-block-grid-<%= liveTabsMedium %> large-block-grid-<%= liveTabsLarge %>">
<%

	for (YearPlan yearPlan : yearPlanList) {
		String showLineBreak = "";
		String lineHeightCss = "";
		if(yearPlan.getName().length() <= MAX_CHARS_PER_LINE) {
			showLineBreak = "<br/>";
			lineHeightCss = "double";
		}
                if( find.equals(yearPlan.getName().trim()) ){
%>
	<li class="active manageCalendarTab <%= lineHeightCss %>">
                <a href="#"><%=yearPlan.getName()%></a><%= showLineBreak %>
        </li>
<%
						java.util.List<MeetingE> meetingEs = yearPlanUtil.getAllEventMeetings_byPath( yearPlan.getPath() +"/meetings/" );
                        meetingEs= meetingUtil.sortById(meetingEs);

                        meetings = new java.util.ArrayList();
                        for(int i=0;i<meetingEs.size();i++){
                                meetings.add(  yearPlanUtil.getMeeting(user,  meetingEs.get(i).getRefId() ) );
                        }
                }else{
                        String url ="?ypname="+java.net.URLEncoder.encode(yearPlan.getName());
                        url+= request.getParameter("xx")==null ? "" : "&xx="+java.net.URLEncoder.encode(request.getParameter("xx"))  ;
                        url+= request.getParameter("mpath")==null ? "" : "&mpath="+java.net.URLEncoder.encode(request.getParameter("mpath"));
%>
	<li class="manageCalendarTab <%= lineHeightCss %>">
                <a href="#" onclick="loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingLibrary.html<%=url%>', false, null, true)"><%=yearPlan.getName()%></a><%= showLineBreak %>
        </li>
<%
                }
        }

        java.util.List<String> myMeetingIds= new java.util.ArrayList();
        java.util.List<MeetingE> myMeetings = troop.getYearPlan().getMeetingEvents();

        if( find.equals(troop.getYearPlan().getName().trim() ) ) {
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
	$( "#cngMeet" ).load( "/content/girlscouts-vtk/controllers/vtk.controller.html?<%=meetingPath ==null ? "act=AddMeeting&addMeeting" : "act=SwapMeetings&cngMeeting"%>=true&fromPath=<%=meetingPath%>&toPath="+mPath,function( html ) {
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
<table cellpadding="5" cellspacing="0" border="0" width="100%" class="meetingSelect">
<%
	for(int i=0;i<meetings.size();i++){
		Meeting meeting = meetings.get(i);
%>
	<tr>
		<td>
			<div class="yearPlanMeetings">
			<h2><%=meeting.getName()%></h2>
			<p class="tags"> <%=meeting.getAidTags() %></p>
			<p class="blurb"><%=meeting.getBlurb() %><p>
			<br/>
                        <%  if( !myMeetingIds.contains( meeting.getId().trim().toLowerCase()) ){ %>
                                <a href="#" onclick="cngMeeting('<%=meeting.getPath()%>')">Select Meeting</a>
                        <% }else{%>
                                <i>Included in Year Plan</i>
                        <% }%>
		</td>
		<td width="10%">
			<img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/badge.png" width="100" height="100" border="0" class="hide-for-small"/>
		</td>
	</tr>
<% 
	}
%>
</table>
</div>
