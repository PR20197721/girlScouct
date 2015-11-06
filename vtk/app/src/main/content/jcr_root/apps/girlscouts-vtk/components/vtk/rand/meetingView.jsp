<% 
java.util.Map<String, JcrCollectionHoldString> meetingInfoItems=  meetingInfo.getMeetingInfo();
java.util.List <Activity> activities = meetingInfo.getActivities();
%>

<script>
function showIt(x){
	$( "#"+x ).show();
}
</script>
<div style="background-color:#efefef">


	<div style="float:left; background-color:green; color:#FFF; padding:20px;">
		
		<% if( i>0) { %>
				<a href="/content/girlscouts-vtk/en/vtk.meeting.html?mid=<%=meetings.get(i-1).getPath()%>"><< PREV</a> 
		<% }
		
		
		%>(#<%=(i+1) %>)<% 
		
		if( troop.getYearPlan().getSchedule() !=null){
		%>
			<%= meetingDate.getTime() %>
		<%
		}
		
	
		
		
		if( i< (meetings.size()-1) ) { %>
				<a href="/content/girlscouts-vtk/en/vtk.meeting.html?mid=<%=meetings.get(i+1).getPath()%>"> NEXT >></a> 
		<% } %>
		
		
	</div>

	<h1><%=meetingInfo.getName() %></h1>
	<p>
	<%=meetingInfo.getBlurb() %>
	</p>
	<br/>Location: <%=meeting.getLocationRef() %>
	
	<%
	
	 if( meeting.getLocationRef()!=null && troop.getYearPlan().getLocations()!=null )
			for(int k=0;k<troop.getYearPlan().getLocations().size();k++){
				
				if( troop.getYearPlan().getLocations().get(k).getPath().equals( meeting.getLocationRef() ) ){
					%>
						<br/><%=troop.getYearPlan().getLocations().get(k).getName() %>
						<br/><%=troop.getYearPlan().getLocations().get(k).getAddress() %>
						<%=troop.getYearPlan().getLocations().get(k).getCity() %>
						<%=troop.getYearPlan().getLocations().get(k).getState() %>
						<%=troop.getYearPlan().getLocations().get(k).getZip() %>
					<% 
				}
			}
	
	
	
	if( meeting.getCancelled()!=null && meeting.getCancelled().equals("true")){ %>
 					<div style="color:#FFF; background-color:red;">CANCELLED</div>
 				<%} %>
 				
	<hr/>
	
	<input type="button" value="overview" onclick="showIt('m_overview')" />
	<input type="button" value="activity plan" onclick="showIt('m_activities')"/>
	
	<div id="m_overview" style="display:none;">
		Overview:<textarea rows="5" cols="5"><%=meetingInfoItems.get("overview").getStr() %></textarea>
	</div>
	
	
	
	
	
	<div id="m_activities"  style="display:none;">
	<%
	
		java.util.Iterator itr1=  meetingInfoItems.keySet().iterator(); 
		while( itr1.hasNext() ){
			String name= (String) itr1.next();
			
		
	%>
		
			<%=name %><textarea rows="5" cols="5"><%=meetingInfoItems.get(name).getStr() %></textarea>
		
	
	<%} %>
	</div>
	
	
	
	
	
	<table style="background-color:#FFF;">
	<%
	
		int countDuration=0;
		for(int a=0;a<activities.size();a++){
			
			Activity activity = activities.get(a);
			countDuration+= activity.getDuration();
			
			%>
			<tr>
			<td> 
			
			<%if( meetingDate!=null) out.println(meetingDate.getTime()); %>
 				
			 </td>
			<td> <%=activity.getName() %> </td>
			<td> <%=activity.getDuration()  %></td>
			</tr> 
			<% 
			
			if( meetingDate!=null )
			    meetingDate.add(java.util.Calendar.MINUTE,activity.getDuration() );
			
		}
	%>
		<tr>
			<td> 
			
			<%if( meetingDate!=null) out.println(meetingDate.getTime()); %>
 				
			 </td>
			<td> <b>End</b> </td>
			<td><b> <%
				int hr = countDuration /60;
				int min= countDuration%60;
				if(hr>0) out.println( hr +" : ") ;
				out.println( min );
				%>
				</b>
			</td>
			</tr> 
	</table>
	<input type="button" name="" value="+Additional Meeting Agenda Items" onclick="addCustAgenda()"/>
	
	
	<div id="newMeetingAgenda" style="display:none;">
       <% if( troop.getYearPlan().getSchedule() !=null){ %>
       <h1>Add New Agenda Item</h1> 
	
	Enter Agenda Item Name:<br/>
	<input type="text" id="newCustAgendaName" value=""/>
	
	<br/>Time Allotment: = 
	<select id="newCustAgendaDuration">
	<option value="10">10</option>
	<option value="20">20</option>
	<option value="30">30</option>
	</select>
	
	+ (<%= meetingDate.getTime()%>)
	
	
	<br/><br/>
	<div class="linkButtonWrapper">
	<input type="button" value="save" onclick="createCustAgendaItem('<%=meetingPath%>', '<%=meetingDate.getTime().getTime()%>')" class="button linkButton"/>
	</div>
     <%}else{ out.println("VIEW MODE"); } %>
       </div>
       
       
       
       
</div>
