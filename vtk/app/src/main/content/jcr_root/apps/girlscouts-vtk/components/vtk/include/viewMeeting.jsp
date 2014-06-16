

		<% 
		MeetingDAO meetingDAO = sling.getService(MeetingDAO.class);

		Meeting meeting = meetingDAO.getMeeting(meetingE.getRefId());%>
		
		
			
 			<!-- 
 				<li  class="ui-state-default" value="<%=meetingCount%>" style="background-color:<%=( user.getYearPlan().getSchedule()==null || new java.util.Date().before(date)) ? "#FFF" : "gray"%>;">
 			    -->
 			 <li  class="<%=( user.getYearPlan().getSchedule()==null || new java.util.Date().before(date)) ? "ui-state-default" : "ui-state-default ui-state-disabled"%>" value="<%=meetingCount%>">
 			     
 			    <span style="background-color:green; color:#FFF; ">
 				<%=meetingE.getId() %>-(#<%= meetingCount %>) <%= user.getYearPlan().getSchedule()==null ? "" : df.format(date) %>
 			    </span>
 			
 				<%if( meetingE.getCancelled()!=null && meetingE.getCancelled().equals("true")){%>
 					<span style="color:#FFF; background-color:red;">CANCELLED</span>
 				<% }%>
 				
 				
 				
 				Meeting: <%=meeting.getId() %> - <b><%=meeting.getName() %> </b>
 				<%=meeting.getBlurb() %>
 				
 				
 				<a href="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=date.getTime()%>">View Meeting</a>
 				
 				<span style="background-color:gray; padding:2px;"><%=meeting.getAidTags() %></span>
 			</li>
			
    	


