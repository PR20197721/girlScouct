<script>
//print out the date the email was sent.
 var sent_date = "";
 if(moment(new Date()) != null && moment(new Date()) !='') {
 	sent_date = $('.sent').append(moment(new Date()).format('MM/DD/YYYY'));
 } else {
 	sent_date = $('.sent').append('none');
 }
</script>
<div class="content clearfix meeting-reminder">
<% 
	String userId = apiConfig.getUserId();
	String accessToken = apiConfig.getAccessToken();
	PlanView planView = meetingUtil.planView(user, troop, request);
	MeetingE meeting = (MeetingE)planView.getYearPlanComponent();
	Meeting meetingInfo = meeting.getMeetingInfo();
	java.util.List <Activity> _activities = meetingInfo.getActivities();
	java.util.Map<String, JcrCollectionHoldString> meetingInfoItems= meetingInfo.getMeetingInfo();
	Date searchDate = planView.getSearchDate();
%> 
	<h5>Reminder Meeting <%=meetingInfo.getName() %> <%= FORMAT_MEETING_REMINDER.format(searchDate) %></h5>
	
	<p class="sent">Sent: 
		<script>sent_date;</script>
	</p>
	<h6>Address List</h6>
	<ul>
		<li>
			<input type="checkbox" id="email_to_gp" checked />
			<label for="email_to_gp">Girls / Parents</label>
		</li>

		<li>
			<input type="checkbox" id="email_to_sf" checked />
			<label for="email_to_sf">Self</label>
		</li>
		<li>
			<input type="checkbox" id="email_to_tv" />
			<label for="email_to_tv">Troop Volunteers</label>
		</li>
	
	<p>Enter your own:<input type="email" id="email_to_cc" value="<%=troop.getSendingEmail()==null ? "" : troop.getSendingEmail().getCc()%>"/></p>
	
	<div style="background-color:gray">Compose Email</div>
	
	<br/>Subject: <input type="text" id="email_subj" value="Reminder <%=troop.getTroop().getGradeLevel() %> Meeting <%=meetingInfo.getName() %> <%= FORMAT_MEETING_REMINDER.format(searchDate) %>"/>
	
	<div style="background-color:yellow;">
	</div>
	<textarea id="email_htm" name="textarea" class="jqte-test" rows="25" cols="25"> 
	
	Hello Girl Scout Families,
	<br/><br/>Here are the details of our next meeting:
	<table>
		<tr><th>Date:</th>
			<td><%= FORMAT_MEETING_REMINDER.format(searchDate) %></td>
		</tr>
		<tr><th>Location:</th>
			<td><%
			if( meeting.getLocationRef()!=null && troop.getYearPlan().getLocations()!=null ){
				for(int k=0;k<troop.getYearPlan().getLocations().size();k++){
					
					if( troop.getYearPlan().getLocations().get(k).getPath().equals( meeting.getLocationRef() ) ){
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
			<td><%= meetingInfo.getName() %></td>
		</tr>
	</table>

	<%=meetingInfoItems.get("overview").getStr() %>

	<br/><br/>If you have any questions, or want to participate in this meeting, please contact me at 
	<%if(apiConfig.getUser().getPhone()!=null)%><%=apiConfig.getUser().getPhone() %>
	<%if(apiConfig.getUser().getMobilePhone()!=null)%><%=apiConfig.getUser().getMobilePhone() %>
	<%if(apiConfig.getUser().getHomePhone()!=null)%><%=apiConfig.getUser().getHomePhone() %>
	<%if(apiConfig.getUser().getAssistantPhone()!=null)%><%=apiConfig.getUser().getAssistantPhone() %>

	<br/><br/>Thank you for supporting your <%=troop.getTroop().getGradeLevel() %>,

	<br/><br/><%if(apiConfig.getUser().getName()!=null)%><%=apiConfig.getUser().getName() %>
	<br/><%=troop.getTroop().getTroopName() %>

	<br/><br/>Aid(s) Included:xxx
	<div id=aids>
	<%EmailMeetingReminder emr = troop.getSendingEmail();
		if( emr!=null ){
			java.util.List<Asset> eAssets = emr.getAssets();
			if( eAssets!=null)
				for(int i=0;i<eAssets.size();i++){
				%><li><a href="<%=eAssets.get(i).getRefId() %>"><%=eAssets.get(i).getRefId() %></a></li><% 
				}
		}%>
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
			<% List<Asset> aidTags = planView.getAidTags();
			for(int i=0;i<aidTags.size();i++){%>
			 <tr>
				<td><%= aidTags.get(i).getRefId() %></td>
				<td><a href="javascript:void(0)" onclick="addAidToEmail('<%=aidTags.get(i).getPath()%>','<%=aidTags.get(i).getRefId()%>','<%=meeting.getUid() %>')" class="addAidToEmail"> + </a></td>
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
	<input type="button" value="Preview" onclick="previewMeetingReminderEmail('<%=meeting.getPath()%>','<%=meeting.getUid()%>')"/>
	
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
	function addLink(link){
		$('.emailhtm .formLinks').append('<a href="'+formurl+'">'+formname+'/a>');
		return;
	};
	
</script>

 