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
  if( meetingPath==null || meetingPath.equals("null") || meetingPath.equals("")) {
    meetingPath=null;
	}

  if(meetingPath != null){
    showVtkNav =  false;
  }

  String ageLevel=  troop.getTroop().getGradeLevel();
	ageLevel= ageLevel.substring( ageLevel.indexOf("-")+1).toLowerCase().trim();
	java.util.List<Meeting> meetings =yearPlanUtil.getAllMeetings(user, ageLevel);
	String find="";
%>
<div class="header clearfix">
  <h3 class="columns large-10">Meeting Library</h3>
  <span class="column large-12">HINT: meeting overviews are available under resources</span>
  <a class="close-reveal-modal columns large-2" onclick="closeModalPage()"><i class="icon-button-circle-cross"></i></a>
</div>

  <%
    boolean isWarning=false;
    String instruction = "Select a meeting to add to your Year Plan";
    if (isWarning) {
  %>
  	<div class="small-4 medium-2 large-2 columns">
  		<div class="warning"><img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/warning-small.png" width="20" height="20" align="left"/></div>
  	</div>
  	<div class="small-20 medium-22 large-22 columns">
      <% } %>
      <% 
        java.util.List<String> myMeetingIds= new java.util.ArrayList();
        java.util.List<MeetingE> myMeetings = troop.getYearPlan().getMeetingEvents();

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
  	$( "#cngMeet" ).load( "/content/girlscouts-vtk/controllers/vtk.controller.html?<%=meetingPath ==null ? "act=AddMeeting&addMeeting" : "act=SwapMeetings&cngMeeting"%>=true&fromPath=<%=meetingPath%>&toPath="+mPath,function( html ) {
        <%
        	if( request.getParameter("xx") ==null ){
        %>
      		document.location="/content/girlscouts-vtk/en/vtk.plan.html";
      <%} else {%>
      		//document.location="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=request.getParameter("xx")%>";
      <%}%>
  	});
  }
  </script>
  <div class="content meeting-library">
    <p class="instruction"><%= instruction %></p>
    <div id="cngMeet"></div>
    <table class="meetingSelect">
    	<tbody>
        <%
        for(int i=0;i<meetings.size();i++){
        	Meeting meeting = meetings.get(i);
        %>
        	<tr>
        		<td>
          			<p class="title"><%=meeting.getName()%></p>
          			<!--  <p class="tags"> <%=meeting.getAidTags() %></p> -->
          			<p class="blurb"><%=meeting.getBlurb() %></p>
        		</td>
            <td>
              <%  if( !myMeetingIds.contains( meeting.getId().trim().toLowerCase()) ) { %>
                <a href="#" onclick="cngMeeting('<%=meeting.getPath()%>')">Select Meeting</a>
              <% } else {%>
                <img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/check.png" width="10" height="15"> <i class="included">Included in Year Plan</i>
              <% }%>
            </td>
        		<td>
            <%
            	try {
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
        <% } %>
    	</tbody>
    </table>
</div>