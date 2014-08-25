<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<!-- apps/girlscouts-vtk/components/vtk/meetingLibrary.jsp  -->
<%
        boolean showVtkNav = true;
	String activeTab = "resource";
        String meetingPath = request.getParameter("mpath");
        if( meetingPath==null || meetingPath.equals("null") || meetingPath.equals("")) {
		meetingPath=null;
	}

        if(meetingPath != null){
                showVtkNav =  false;
        }

        String ageLevel=  user.getTroop().getGradeLevel();
	ageLevel= ageLevel.substring( ageLevel.indexOf("-")+1).toLowerCase().trim();
	java.util.List<Meeting> meetings =meetingDAO.getAllMeetings(ageLevel);
		
        String find="";
%>
<div class="row modalHeader">
	<div class="small-19 medium-8 large-6 columns"><h2>Meeting Library</h2></div>
	<div class="hide-for-small medium-13 large-16 columns hint">hint: meeting overviews are available under resources</div>
	<div class="small-5 medium-3 large-2 columns">
		<a class"right" href="#" onclick="closeModalPage()"><img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/close-small.png" width="20" height="20" border="0" align="right"></a>
	</div>
	<div class="show-for-small small-24 columns hint">hint: meeting overviews are available under resources</div>
</div>
<div class="row modalHeader">
<%
        boolean isWarning=false;
        String instruction = "Select a meeting to add to your Year Plan";
        if (isWarning) {
%>
	<div class="small-4 medium-2 large-2 columns">
		<div class="warning"><img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/warning-small.png" width="20" height="20" align="left"/></div>
	</div>
	<div class="small-20 medium-22 large-22 columns">
<%
        } else {
%>
	<div class="small-24 medium-24 large-24 columns">
<%
        }
%>
		<span class="instruction"><%= instruction %></span>
        </div>
<% 
        java.util.List<String> myMeetingIds= new java.util.ArrayList();
        java.util.List<MeetingE> myMeetings = user.getYearPlan().getMeetingEvents();
            
                for(int i=0;i< myMeetings.size();i++){
                        // ADD CANCELED MEETINGS if( myMeetings.get(i).getCancelled()!=null && myMeetings.get(i).getCancelled().equals("true")) continue;
                        String meetingId = myMeetings.get(i).getRefId();
                        meetingId= meetingId.substring(meetingId.lastIndexOf("/") +1).trim().toLowerCase();
                        myMeetingIds.add( meetingId );
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
	<table class="meetingSelect">
		<tbody>
<%
	for(int i=0;i<meetings.size();i++){
		Meeting meeting = meetings.get(i);
%>
		<tr>
			<td>
				<div class="yearPlanMeetings">
				<h2><%=meeting.getName()%></h2>
				<!--  <p class="tags"> <%=meeting.getAidTags() %></p> -->
				<p class="blurb"><%=meeting.getBlurb() %><p>
				<br/>
				<%  if( !myMeetingIds.contains( meeting.getId().trim().toLowerCase()) ){ %>
					<a href="#" onclick="cngMeeting('<%=meeting.getPath()%>')" class="button linkButton">Select Meeting</a>
				<% }else{%>
					&nbsp;<img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/check.png" width="20" height="20">&nbsp;<i>Included in Year Plan</i>
				<% }%>
			</td>
			<td width="10%">
<%
		try{
			String img= meeting.getId().substring( meeting.getId().lastIndexOf("/")+1).toUpperCase();
			if(img.contains("_") )img= img.substring(0, img.indexOf("_"));
%>
			<img width="100" height="100" src="/content/dam/girlscouts-vtk/local/icon/meetings/<%=img%>.png"/>
<%
		} catch(Exception e){
			e.printStackTrace();
		}
%>
			</td>
		</tr>
<% 
	}
%>
		</tbody>
	</table>
</div>
