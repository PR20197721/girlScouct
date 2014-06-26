
<%@page import="org.girlscouts.vtk.auth.models.ApiConfig" %>

<%=apiConfig.getUserId() %>--
<%=apiConfig.getAccessToken() %>

<br/>Reminder Meeting#  <%=(currInd+1) %> <%= dateFormat55.format(searchDate) %>
	

<br/>Sent: XXX


<div style="background-color:gray">Address List</div>

<input type="checkbox" id="email_to_gp" checked/>Girls /Parents
<input type="checkbox" id="email_to_sf" checked/>Self
<input type="checkbox" id="email_to_tv"/>Troop Volunteers


<br/>Enter your own:<input type="text" id="email_to_cc" value="<%=apiConfig.getUser().getEmail()%>"/>

<div style="background-color:gray">Compose Email</div>

<br/>Subject: <input type="text" id="email_subj" value="Reminder XXX  Meeting# <%=(currInd+1) %> <%= dateFormat55.format(searchDate) %>"/>

<div>XXX widget fmt</div>

  <textarea id="email_htm" rows="25" cols="25"> 

Hello Girl Scout Families,
<br/><br/>Here are the details of our next meeting:

<table>
	<tr>
		<th>Date:</th><td><%= dateFormat55.format(searchDate) %></td>
	</tr>
	<tr>
		<th>Location:</th><td>
		
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
		
		
		</td>
		</tr>
		<tr>
		<th>Topic:</th><td><%= meetingInfo.getName() %></td>
		</tr>
	</tr>
</table>



<%=meetingInfoItems.get("overview").getStr() %>

<br/><br/>If you have any questions, or want to participate in this meeting, please contact me at 
<%=apiConfig.getUser().getPhone() %>
<%=apiConfig.getUser().getMobilePhone() %>
<%=apiConfig.getUser().getHomePhone() %>
<%=apiConfig.getUser().getAssistantPhone() %>

<br/><br/>Thank you for supporting your XXX,

<br/><br/><%=apiConfig.getUser().getName() %>
<br/>Troop XXX


<br/><br/>Aid(s) Included:

<%

/*
t= new java.util.StringTokenizer( aidTags, ";");
while( t.hasMoreElements()){
	
	com.day.cq.tagging.TagManager.FindResults x = tagManager.findByTitle(t.nextToken());
	java.util.Iterator r = x.resources ;
	while( r.hasNext() ){
		Resource res = (Resource) r.next();
	    %><li> <a href="<%=res.getPath().replace("/jcr:content/metadata", "")%>"> <%=res.getName()%></a></li><%

	  }
}
*/

for(int i=0;i<_aidTags.size();i++){
	%><li><a href="<%=_aidTags.get(i).getPath()%>">aid tag</a> </li><% 
}
%>


<br/><br/>Form(s) Required:
XXX
 </textarea> 


<br/><br/><input type="button" value="Send" onclick="sendMeetingReminderEmail('<%=meeting.getPath()%>')"/>




<% 
/*
	ApiConfig apiConfig= (ApiConfig)session.getAttribute(ApiConfig.class.getName());
	out.println( ">>>>>>>>>>>>>>>>>>>    " + (apiConfig==null) );
	out.println(apiConfig.getUserId());
	*/
%>







