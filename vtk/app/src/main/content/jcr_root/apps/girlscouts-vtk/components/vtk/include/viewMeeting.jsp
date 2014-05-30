

		<% Meeting meeting = new MeetingDAOImpl().getMeeting(meetingE.getRefId());%>
 			<li value="<%=meetingCount%>">
 			    <span style="background-color:green; color:#FFF; ">
 					(#<%= meetingCount %>) <%= user.getYearPlan().getSchedule()==null ? "" : df.format(date) %>
 			    </span>
 			
 				<%if( meetingE.getCancelled()!=null && meetingE.getCancelled().equals("true")){%>
 					<span style="color:#FFF; background-color:red;">CANCELLED</span>
 				<% }%>
 				
 				
 				
 				Meeting: <%=meeting.getId() %> - <b><%=meeting.getName() %> </b>
 				<%=meeting.getBlurb() %>
 				
 				
 				<a href="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=date.getTime()%>">View Meeting</a>
 				
 				<span style="background-color:gray; padding:2px;"><%=meeting.getAidTags() %></span>
 			</li>
			
    	


