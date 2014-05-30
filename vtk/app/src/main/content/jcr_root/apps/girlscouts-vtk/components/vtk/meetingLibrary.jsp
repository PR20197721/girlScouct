<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="org.girlsscout.vtk.models.*,org.girlsscout.vtk.dao.*, org.girlsscout.vtk.ejb.*" %>

<%
	String meetingPath = request.getParameter("mpath");
	if( meetingPath==null || meetingPath.equals("null") || meetingPath.equals("")) meetingPath=null;

	java.util.List< Meeting> meetings=  new MeetingDAOImpl().search();
%>
 <a href="<%= meetingPath==null ? "plan.jsp" : "planView.jsp"%>">exit meeting library</a>
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
			<a href="viewMeetingLibraryMeeting.jsp?rpath=<%=meetingPath %>&mpath=<%=meeting.getPath()%>">View Meeting</a>
			<%=meeting.getAidTags() %>
			</div>
	    <% 
	}
%>
</div>


