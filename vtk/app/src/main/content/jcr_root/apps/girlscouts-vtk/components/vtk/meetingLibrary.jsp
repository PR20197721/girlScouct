<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*, org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>

<%
	HttpSession session = request.getSession();

	MeetingDAO meetingDAO = sling.getService(MeetingDAO.class);

	String meetingPath = request.getParameter("mpath");
	if( meetingPath==null || meetingPath.equals("null") || meetingPath.equals("")) meetingPath=null;

	java.util.List< Meeting> meetings=  meetingDAO.search();
%>
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
			<a href="/content/girlscouts-vtk/en/vtk.viewMeetingLibraryMeeting.html?rpath=<%=meetingPath %>&mpath=<%=meeting.getPath()%>">View Meeting</a>
			<%=meeting.getAidTags() %>
			</div>
	    <% 
	}
%>
</div>


