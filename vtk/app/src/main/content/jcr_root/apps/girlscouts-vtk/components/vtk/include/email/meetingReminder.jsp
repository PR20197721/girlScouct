
<!-- apps/girlscouts-vtk/components/vtk/include/email/meetingReminder.jsp -->
<div class="content clearfix">

	Reminder Meeting <%=((MeetingE)planView.getYearPlanComponent()).getMeetingInfo().getName() %>
	<%= FORMAT_MEETING_REMINDER.format(planView.getSearchDate()) %>

	<div id="sent">
	Sent: 
	</div>
	<script>
		$('#sent').append(moment(new Date()).format('MM/DD/YYYY'));
	</script>

	<div style="background-color:gray">Address List</div>
	
	<input type="checkbox" id="email_to_gp" checked/>Girls/Parents
	<input type="checkbox" id="email_to_sf"/>Self
	<input type="checkbox" id="email_to_tv"/>Troop Volunteers
	
	</br>Enter your own:<input type="email" id="email_to_cc" value="<%=troop.getSendingEmail()==null ? "" : troop.getSendingEmail().getCc()%>"/>
	
	<div style="background-color:gray">Compose Email</div>
	
	<br/>Subject: <input type="text" id="email_subj" value="Reminder <%=troop.getTroop().getGradeLevel() %> Meeting <%=((MeetingE)planView.getYearPlanComponent()).getMeetingInfo().getName() %> <%= FORMAT_MEETING_REMINDER.format(planView.getSearchDate()) %>"/>
	
	<div style="background-color:yellow;">
	</div>
	<textarea id="email_htm" name="textarea" class="jqte-test" rows="25" cols="25"> 
	
	Hello Girl Scout Families,
	<br/><br/>Here are the details of our next meeting:
	<table>
		<tr><th>Date:</th>
			<td><%= FORMAT_MEETING_REMINDER.format(planView.getSearchDate()) %></td>
		</tr>
		<tr><th>Location:</th>
			<td><%
			if( ((MeetingE)planView.getYearPlanComponent()).getLocationRef()!=null && troop.getYearPlan().getLocations()!=null ){
				for(int k=0;k<troop.getYearPlan().getLocations().size();k++){
					
					if( troop.getYearPlan().getLocations().get(k).getPath().equals( ((MeetingE)planView.getYearPlanComponent()).getLocationRef() ) ){
						%>
							<br/><%=troop.getYearPlan().getLocations().get(k).getPath()%><%=troop.getYearPlan().getLocations().get(k).getName() %>
							<br/><%=troop.getYearPlan().getLocations().get(k).getAddress() %>
							<%=troop.getYearPlan().getLocations().get(k).getCity() %>
							<%=troop.getYearPlan().getLocations().get(k).getState() %>
							<%=troop.getYearPlan().getLocations().get(k).getZip() %>
						<% 
					}
				}
	   		}
			%></td>
		</tr>
		<tr><th>Topic:</th>
			<td><%= ((MeetingE)planView.getYearPlanComponent()).getMeetingInfo().getName() %></td>
		</tr>
	</table>

	<%=((MeetingE)planView.getYearPlanComponent()).getMeetingInfo().getMeetingInfo().get("overview").getStr() %>

	<br/><br/>If you have any questions, or want to participate in this meeting, please contact me at 
	<%if(apiConfig.getUser().getPhone()!=null)%><%=apiConfig.getUser().getPhone() %>
	<%if(apiConfig.getUser().getMobilePhone()!=null)%><%=apiConfig.getUser().getMobilePhone() %>
	<%if(apiConfig.getUser().getHomePhone()!=null)%><%=apiConfig.getUser().getHomePhone() %>
	<%if(apiConfig.getUser().getAssistantPhone()!=null)%><%=apiConfig.getUser().getAssistantPhone() %>

	<br/><br/>Thank you for supporting your <%=troop.getTroop().getGradeLevel() %>,

	<br/><br/><%if(apiConfig.getUser().getName()!=null)%><%=apiConfig.getUser().getName() %>
	<br/><%=troop.getTroop().getTroopName() %>

	<br/><br/>Aid(s) Included:xxx
	<div id=aidLinks>
	<%--  
	<%if( troop.getSendingEmail()!=null ){
			java.util.List<Asset> eAssets = troop.getSendingEmail().getAssets();
			if( eAssets!=null)
				for(int i=0;i<eAssets.size();i++){
				%><li><a href="<%=eAssets.get(i).getRefId() %>"><%=eAssets.get(i).getTitle() %></a></li><% 
				}
	}%>
	--%>
	</div>
	
	<br/></br/>Form(s) Required:xxx
	<div id="formLinks"></div>
	
	</textarea>
	

	<div id="ima">
		<div style="background-color:gray" id="imaHdr">Include Meeting Aid:</div>
		<div id="imaBd">
		<table>
			<tr>
				<th>&nbsp;</th>
				<th>Add to Email</th>
			</tr>
			<%
			//List<Asset> aidTags = planView.getAidTags();
			for(int i=0;i<planView.getAidTags().size();i++){%>
			 <tr>
				<td><%= planView.getAidTags().get(i).getTitle() %></td>
			 	<td><a href="javascript:void(0)" onclick="addAidLink('<%=planView.getAidTags().get(i).getRefId()%>','<%=planView.getAidTags().get(i).getTitle()%>','<%=((MeetingE)planView.getYearPlanComponent()).getUid() %>')" class="addAidToEmail"> + </a></td>
			 	
			 </tr>
			 <%}%>
		</table>
		</div>
	</div>
	<div id="ifl">
	<div style="background-color:gray" id="iflHdr">Include Form Link:</div>
	<div id="iflBd">
	<%/*form needed
		for(int i=0;i<_forms.size();i++){
		String formName;
		String formurl;%>
	
		<input type="checkbox" id="<%=formname%>" onclick="addLinkToEmail(forms(i))"/><%=formname %>

	<%}*/%>
	</div>
	</div>
	<input type="button" value="Preview" onclick="previewMeetingReminderEmail('<%=((MeetingE)planView.getYearPlanComponent()).getPath()%>','<%=((MeetingE)planView.getYearPlanComponent()).getUid()%>')"/>
	
</div>
<% //}%>

 
<div id="email_dialog" style="display:none">
  <p>added to email.</p>
</div>

<!-- end of /content -->
<script>
	$(".jqte-test").jqte({
		"source": false,
		"rule": false,
		"sub": false,
		"strike": false,
		"fsizes": ['10','12','14','16','18','20','22','24','28','32']
	});
	function addFormLink(link){
		$('textarea #emailhtm .formLinks').append('<a href="'+formurl+'">'+formname+'/a>');
		return;
	};
	function addAidLink(refId,title,uid){
		$('#aidLinks').append('<li><i class="icon-pdf-file-extension"></i><a href="'+refId+'">'+title+'</a></li>');
		$( "#email_dialog" ).dialog();
		//$('.addAidToEmail').text("-");
		//addAidToEmail(refId,title,uid);
		return;
	};
	
</script>

 