<%

MeetingDAO meetingDAO = sling.getService(MeetingDAO.class);

MeetingE meeting = (MeetingE) _comp;
Meeting meetingInfo = meetingDAO.getMeeting(  meeting.getRefId() );
java.util.List <Activity> _activities = meetingInfo.getActivities();
java.util.Map<String, JcrCollectionHoldString> meetingInfoItems=  meetingInfo.getMeetingInfo();
%>

<div style=" background-color:#FFF;">

<div style="float:left; width:100px; height:100px;background-color:blue; color:#fff;">
	
	
	<%if( prevDate!=0 ){ %>
		<a href="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=prevDate%>"> << PREV </a>
	<%} %>
	
	<br/>
	<%if( user.getYearPlan().getSchedule()!=null ) {%>
		<%=fmt.format(searchDate) %>
	<%}else{ out.println( fmtX.format(searchDate) ); } %>
	
	
	<%if( nextDate!=0 ){ %>
		<br/><a href="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=nextDate%>">NEXT>></a>
	<%} %>
	
	

</div>

<!--  <a href="/content/girlscouts-vtk/en/vtk.meetingLibrary.html">change this meeting</a> -->
<a href="javascript:void(0)" onclick="viewMeetingLibrary('<%=meeting.getPath()%>')">replace this meeting</a>

<h1>Meeting: <%= meetingInfo.getName() %></h1>

<!--  
<br/>Date: <%= fmtDate.format(searchDate) %>
<br/>Time: <%= fmtHr.format(searchDate) %>
<br/>Age Range:
-->

<p><%=meetingInfo.getBlurb() %></p>

<br/>Location: <%=meeting.getLocationRef() %>
<%


   if( meeting.getLocationRef()!=null && user.getYearPlan().getLocations()!=null )
	for(int k=0;k<user.getYearPlan().getLocations().size();k++){
		
		if( user.getYearPlan().getLocations().get(k).getPath().equals( meeting.getLocationRef() ) ){
			%>
				<br/><%=user.getYearPlan().getLocations().get(k).getPath()%><%=user.getYearPlan().getLocations().get(k).getName() %>
				<br/><%=user.getYearPlan().getLocations().get(k).getAddress() %>
				<%=user.getYearPlan().getLocations().get(k).getCity() %>
				<%=user.getYearPlan().getLocations().get(k).getState() %>
				<%=user.getYearPlan().getLocations().get(k).getZip() %>
			<% 
		}
	}
%>



<%
if( meeting.getCancelled()!=null && meeting.getCancelled().equals("true")){ %>
 					<div style="color:#FFF; background-color:red;">CANCELLED</div>
 				<%} %>
 				

  <div style="background-color:#efefef; padding:5px;">
  	<%=meetingInfo.getBlurb() %>
 </div>
 
 
 
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
	
	
	
	
	
	
 
 <div style="background-color:gray; color:#fff;">Meeting Aids</div>
 Name of the video
 <br/>Represents PDF
 
 
 <div style="background-color:gray; color:#fff;">Meeting Agenda</div>
 <p>
	Select an item to view details, edit duration, or delete. Drag and drop items to reorder them.
 </p>
 
 <ul id="sortable" >
 	<%
 		java.util.Calendar activSched= java.util.Calendar.getInstance();
 		activSched.setTime( searchDate);
 		int duration =0;
 		for(int ii=0;ii< _activities.size();ii++){
 			Activity _activity = _activities.get(ii);
 			%>
 				 
 				<li value="<%=(ii+1)%>">
 					<table>
  					<td><%if( user.getYearPlan().getSchedule()!=null ){ out.println(fmtHr.format(activSched.getTime())); }%></td>
 				    <td><a href="javascript:void(0)" onclick="editAgenda('<%=_activity.getActivityNumber()%>')"><%=_activity.getName() %></a></td>
 				    <td><%=_activity.getDuration() %></td>
 				    </table>
 				</li>
 				
 				
 			<% 
 			if( user.getYearPlan().getSchedule()!=null )
		 		activSched.add(java.util.Calendar.MINUTE, _activity.getDuration() );
			
 			duration+= _activity.getDuration();
 		}
 	%>
 	</ul>
 		<table>
 		<td></td>
 		<td><b>End</b></td>
 		<td>
 		<b> 
 				<%int min= duration%60;%>
				<%=duration /60 >0 ? duration /60 +"hr" : ""%>
				<%= min<10 ? "0"+min : min%>min 	
				</b>
 		</td>
 		</table>
 	
 	
 	
 	
 	<% for(int ii=0;ii< _activities.size();ii++){ 
 			Activity _activity = _activities.get(ii);
 			
 			%><%@include file="editActivity.jsp" %> 
 	<%} %>
 
 
 
 
 <input type="button" name="" value="Add Agenda Items" onclick="addCustAgenda()"/>
 <div id="newMeetingAgenda" style="display:none;">
       <% if( user.getYearPlan().getSchedule() !=null){ %>
       <h1>Add New Agenda Item</h1> 
	
	Enter Agenda Item Name:<br/>
	<input type="text" id="newCustAgendaName" value=""/>
	
	<br/>Time Allotment: = 
	<select id="newCustAgendaDuration">
	<option value="10">10</option>
	<option value="20">20</option>
	<option value="30">30</option>
	</select>
	
	 + (<%= activSched.getTime()%>)
	
	<br/>Description:<textarea id="newCustAgendaTxt"></textarea>
	<br/><br/>
	
	
	<input type="button" value="save" onclick="createCustAgendaItem1('<%=searchDate.getTime()%>', '<%=activSched.getTime().getTime()%>', '<%=meeting.getPath()%>')"/>
     <%}else{ out.println("VIEW MODE"); } %>
       </div>
       
       
       
       
</div>

<br/><br/>
 
 
 
 
 <div id="meetingLibraryView"></div>
</div>


<script>
  $("#sortable").sortable(
        				   
        				   {
        				       
        				        update:  function (event, ui) {
        				       
        				       repositionActivity('<%=meeting.getRefId()%>');
        				        }
        				}
        		   );
        		   </script>
        		   
        		   
        		   