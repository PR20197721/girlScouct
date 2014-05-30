<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="org.girlsscout.vtk.models.*,org.girlsscout.vtk.dao.*, org.girlsscout.vtk.ejb.*" %>

<%

	String orgMeetingPath = request.getParameter("rpath");
    if( orgMeetingPath==null || orgMeetingPath.equals("null") || orgMeetingPath.equals("")) orgMeetingPath=null;
	String meetingPath = request.getParameter("mpath");
	Meeting meeting = new MeetingDAOImpl().getMeeting(meetingPath);
	if( meeting==null ){ out.println("Meeting not found");return;}
	java.util.List <Activity> activities = meeting.getActivities();
	
	java.util.Map<String, JcrCollectionHoldString> meetingInfoItems=  meeting.getMeetingInfo();
	
%>
<%@include file="include/headerDev.jsi" %>  
<script>
	function cngMeeting(){
		
		
		$( "#cngMeet" ).load( "/VTK/include/controller.jsp?<%=orgMeetingPath==null ? "addMeeting" : "cngMeeting"%>=true&fromPath=<%=orgMeetingPath%>&toPath=<%=meetingPath%>",function( html ) {
			 <%if(orgMeetingPath==null){%>
			    document.location="/VTK/plan.jsp";
			 <%}else{%>
			 	document.location="/VTK/planView.jsp";
			 <%}%>
			 
		  });
	}
	
	function exitLib(){
		
	 <%if(orgMeetingPath==null){%>
	    document.location="/VTK/plan.jsp";
	 <%}else{%>
	 	document.location="/VTK/planView.jsp";
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

<%@include file="include/manageCommunications.jsi" %> 
<%@include file="include/footer.jsi" %> 
