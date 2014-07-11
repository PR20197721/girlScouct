
<%@page import="org.girlscouts.vtk.auth.models.ApiConfig" %>

<%=apiConfig.getUserId() %>--
<%=apiConfig.getAccessToken() %>

<br/>Reminder Meeting#  <%=(currInd +1)%> <%= FORMAT_MEETING_REMINDER.format(searchDate) %>
	

<br/>Sent: XXX


<div style="background-color:gray">Address List</div>

<input type="checkbox" id="email_to_gp" checked/>Girls /Parents
<input type="checkbox" id="email_to_sf" checked/>Self
<input type="checkbox" id="email_to_tv"/>Troop Volunteers


<br/>Enter your own:<input type="text" id="email_to_cc" value="<%=user.getSendingEmail()==null ? "" : user.getSendingEmail().getCc()%>"/>

<div style="background-color:gray">Compose Email</div>

<br/>Subject: <input type="text" id="email_subj" value="Reminder <%=user.getTroop().getGradeLevel()  %>  Meeting# <%=(currInd +1)%> <%= FORMAT_MEETING_REMINDER.format(searchDate) %>"/>

<div style="background-color:yellow;">






</div>

  <textarea id="email_htm" name="textarea" class="jqte-test" rows="25" cols="25"> 

Hello Girl Scout Families,
<br/><br/>Here are the details of our next meeting:

<table>
	<tr>
		<th>Date:</th><td><%= FORMAT_MEETING_REMINDER.format(searchDate) %></td>
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

<br/><br/>Thank you for supporting your <%=user.getTroop().getGradeLevel() %>,

<br/><br/><%=apiConfig.getUser().getName() %>
<br/>Troop <%=user.getTroop().getTroopName() %>





<br/><br/>Form(s) Required:
XXX
 </textarea> 

<div>
	Aid(s) Included:
	<%
	EmailMeetingReminder emr = user.getSendingEmail();
	if( emr!=null ){
		java.util.List<Asset> eAssets = emr.getAssets();
		if( eAssets!=null)
			for(int i=0;i<eAssets.size();i++){
				%><li><%=eAssets.get(i).getRefId() %></li><% 
			}
	}
	%>

</div>


<br/><br/><input type="button" value="Preview" onclick="previewMeetingReminderEmail('<%=meeting.getPath()%>','<%=meeting.getUid()%>')"/>



<div id="imal">
	
	
	<div id="imalHdr">Include Meeting Aid Link:</div>
	<div id="imalBd">
	
		<table>
		
			<tr>
				<th>&nbsp;</th>
				<th>Add to Email</th>
			</tr>
			
			<%
			/*
			for(int i=0;i<_aidTags.size();i++){%>
			 <tr>
				<td><%= _aidTags.get(i).getDesc() %></td>
				<td><a href="javascript:void(0)" onclick="addAidToEmail('<%=_aidTags.get(i).getPath()%>','<%=meeting.getUid() %>')" class="addAidToEmail"> + </a></td>
			 </tr>
			 <%}
			
			*/%>
		</table>
	</div>


	
</div>



<script>
	$('.jqte-test').jqte();
	
	// settings of status
	var jqteStatus = true;
	$(".status").click(function()
	{
		jqteStatus = jqteStatus ? false : true;
		$('.jqte-test').jqte({"status" : jqteStatus})
	});
</script>



