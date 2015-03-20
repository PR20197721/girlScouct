<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<!-- apps/girlscouts-vtk/components/vtk/viewMeetingLibraryMeeting.jsp -->
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<%
String orgMeetingPath = request.getParameter("rpath");
if( orgMeetingPath==null || orgMeetingPath.equals("null") || orgMeetingPath.equals("")) orgMeetingPath=null;
	String meetingPath = request.getParameter("mpath");
	Meeting meeting = yearPlanUtil.getMeeting(user, meetingPath);
	if( meeting==null ){ out.println("Meeting not found");return;}
	java.util.List <Activity> activities = meeting.getActivities();
	
	java.util.Map<String, JcrCollectionHoldString> meetingInfoItems=  meeting.getMeetingInfo();
	
%>
<script>
	function cngMeeting(){
		$( "#cngMeet" ).load( "/content/girlscouts-vtk/controllers/vtk.controller.html?<%=orgMeetingPath==null ? "act=AddMeeting&addMeeting" : "act=SwapMeetings&cngMeeting"%>=true&fromPath=<%=orgMeetingPath%>&toPath=<%=meetingPath%>",function( html ) {
			 <%if(orgMeetingPath==null){%>
			    document.location="/content/girlscouts-vtk/en/vtk.plan.html";
			 <%}else{%>
			 	document.location="/content/girlscouts-vtk/en/vtk.planView.html";
			 <%}%>
			 
		  });
	}
	
	function exitLib(){
		
	 <%if(orgMeetingPath==null){%>
	    document.location="/content/girlscouts-vtk/en/vtk.plan.html";
	 <%}else{%>
	 	document.location="/content/girlscouts-vtk/en/vtk.planView.html";
	 <%}%>
		
	}
	
	function showIt(x){
   		$( "#"+x ).show();
   	}
</script>
<input type="button" value="exit library" onclick="exitLib()"/>
<div style="background-color:gray">Meeting Library </div>
<div id="cngMeet"></div>

<input type="button" value="select this plan" onclick="cngMeeting()"/>
<div style="background-color:#fff;">
<%=meeting.getName() %>
<p><%=meeting.getBlurb() %></p>





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
Viddeo HERE
PDF HERE

<div style="background-color:gray; color:#fff;">Meeting Agendas</div>
<table>
<%
	int countDuration = 0;
	for(int i=0;i<activities.size();i++){
		Activity activity = activities.get(i);
		countDuration += activity.getDuration();
				
		%>
			<tr>
				<td><%=(i+1) %></td>
				<td><%=activity.getName() %></td>
				<td><%=activity.getDuration()%></td>
			</tr>
		<% 
	}
%>
<tr>
	<td></td>
	<td><b>End</b></td>
	<td>
		<%
				int hr = countDuration /60;
				int min= countDuration%60;
				if(hr>0) out.println( hr +" : ") ;
				out.println( min );
				%>
	</td>
</tr>
</table>
</div>

<%@include file="include/manageCommunications.jsp" %> 
