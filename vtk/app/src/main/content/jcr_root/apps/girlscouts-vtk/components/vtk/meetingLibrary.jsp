<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*, org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>




<%
	HttpSession session = request.getSession();

	MeetingDAO meetingDAO = sling.getService(MeetingDAO.class);

	String meetingPath = request.getParameter("mpath");
	if( meetingPath==null || meetingPath.equals("null") || meetingPath.equals("")) meetingPath=null;

	java.util.List< Meeting> meetings=  meetingDAO.search();



java.util.List<String> myMeetingIds= new java.util.ArrayList();
	User user= (User)session.getValue("VTK_user");
	java.util.List<MeetingE> myMeetings = user.getYearPlan().getMeetingEvents();
	for(int i=0;i< myMeetings.size();i++){
		
		String meetingId = myMeetings.get(i).getRefId();
		meetingId= meetingId.substring(meetingId.lastIndexOf("/") +1).trim().toLowerCase();
		myMeetingIds.add( meetingId );
		
		
		
	}

%>


<%if(meetingPath ==null){ %>
<dl class="tabs" data-tab >
  <dd ><a href="#panel2-1">My Troup</a></dd>
  <dd ><a href="/content/girlscouts-vtk/en/vtk.html?ageLevel=brownie">Year Plan</a></dd>
  <dd ><a href="/content/girlscouts-vtk/en/vtk.planView.html">Meeting Plan</a></dd>
  <dd class="active"><a href="#panel2-4">Resources</a></dd>
  <dd><a href="#panel2-5">Community</a></dd>
</dl>
<div class="tabs-content">
    <div class="content" id="panel2-1"></div>
    <div class="content" id="panel2-2"></div>
    <div class="content" id="panel2-3"></div>
    <div class="content" id="panel2-4"></div>
    <div class="content" id="panel2-5"></div>
</div>
<%} %>


<script>
function cngMeeting(mPath){
	
	//console.log(mPath);
	//console.log("/VTK/include/controller.jsp?<%=meetingPath ==null ? "addMeeting" : "cngMeeting"%>=true&fromPath=<%=meetingPath%>&toPath="+mPath);
	$( "#cngMeet" ).load( "/content/girlscouts-vtk/controllers/vtk.controller.html?<%=meetingPath ==null ? "addMeeting" : "cngMeeting"%>=true&fromPath=<%=meetingPath%>&toPath="+mPath,function( html ) {
		 <%if(meetingPath==null){%>
		    document.location="/content/girlscouts-vtk/en/vtk.plan.html";
		 <%}else{%>
		 	document.location="/content/girlscouts-vtk/en/vtk.planView.html";
		 <%}%>
		 
	  });
}
</script>
<div id="cngMeet"></div>

 <a href="<%= meetingPath==null ? "/content/girlscouts-vtk/en/vtk.plan.html" : "/content/girlscouts-vtk/en/vtk.planView.html"%>">exit meeting library</a>
<div style="background-color:gray;">Meeting Library</div>
<p>Browse meetings, and select them to review the details</p>
<div>
<%
	for(int i=0;i<meetings.size();i++){
		Meeting meeting = meetings.get(i);
		%> <div style="border:1px solid #000;">
			<div>#<%=i+1 %></div>
			<div><%=meeting.getName()%></div>
			<%=meeting.getBlurb() %>


			<%  if( !myMeetingIds.contains( meeting.getId().trim().toLowerCase()) ){ %>
				<!-- depric 
  <a href="/content/girlscouts-vtk/en/vtk.viewMeetingLibraryMeeting.html?rpath=<%=meetingPath %>&mpath=<%=meeting.getPath()%>">View Meeting</a>

<a href="viewMeetingLibraryMeeting.jsp?rpath=<%=meetingPath %>&mpath=<%=meeting.getPath()%>">View Meeting</a> -->
				<input type="button" value="select this plan" onclick="cngMeeting('<%=meeting.getPath()%>')"/>
			
			<% }else{%>
			
				<span style="background-color:red;">EXISTING MEETING</span>
			<% }%>



    <%=meeting.getAidTags() %>
			</div>
	    <% 
	}
%>
</div>


