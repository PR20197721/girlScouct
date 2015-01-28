
<!-- apps/girlscouts-vtk/components/vtk/include/email/meetingReminder.jsp -->
<div class="content clearfix">
<% 
	String userId = apiConfig.getUserId() ;
	String accessToken = apiConfig.getAccessToken() ;
	PlanView planView = meetingUtil.planView(user, troop, request);
	MeetingE meeting = (MeetingE)planView.getYearPlanComponent();
	Meeting meetingInfo = meeting.getMeetingInfo();
	java.util.List <Activity> _activities = meetingInfo.getActivities();
	java.util.Map<String, JcrCollectionHoldString> meetingInfoItems= meetingInfo.getMeetingInfo();
	Date searchDate = planView.getSearchDate();
	  
%> 
	Reminder Meeting # <%=planView.getMeetingCount()%> <%= FORMAT_MEETING_REMINDER.format(searchDate) %>
	
	Sent: 
	<script>
		$('#sent').append(moment(new Date()).format('MM/DD/YYYY'));
	</script>

	<div style="background-color:gray">Address List</div>
	
	<input type="checkbox" id="email_to_gp" checked/>Girls/Parents
	<input type="checkbox" id="email_to_sf"/>Self
	<input type="checkbox" id="email_to_tv"/>Troop Volunteers
	
	</br>Enter your own:<input type="email" id="email_to_cc" value="<%=troop.getSendingEmail()==null ? "" : troop.getSendingEmail().getCc()%>"/>
	
	<div style="background-color:gray">Compose Email</div>
	
	<br/>Subject: <input type="text" id="email_subj" value="Reminder <%=troop.getTroop().getGradeLevel() %> Meeting #<%=planView.getMeetingCount()%> <%= FORMAT_MEETING_REMINDER.format(searchDate) %>"/>
	
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
	<div id=formLinks"></div>
	
	</textarea>
	

	<div id="ima">
		<div style="background-color:gray" id="imaHdr">Include Meeting Aid:</div>
		<div id="imaBd">
		<table>
			<tr>
				<th>&nbsp;</th>
				<th>Add to Email</th>
			</tr>
			<%/*
			for(int i=0;i<_aidTags.size();i++){%>
			 <tr>
				<td><%= _aidTags.get(i).getDesc() %></td>
				<td><a href="javascript:void(0)" onclick="addAidToEmail('<%=_aidTags.get(i).getPath()%>','<%=meeting.getUid() %>')" class="addAidToEmail"> + </a></td>
			 </tr>
			 <%}*/%>
		</table>
		</div>
	</div>
	<dl class="accordion" data-accordion>
	  <dt data-target="panel2"><h6>Include Form Link</h6></dt>
	  <dd class="accordion-navigation">
	    <div class="content" id="panel2">
	<%

        org.girlscouts.vtk.utils.DocumentUtil docUtil = new org.girlscouts.vtk.utils.DocumentUtil(resourceResolver, sling.getService(com.day.cq.tagging.JcrTagManagerFactory.class), "gsctx");
		try{
			
			int panelCount = 1;
			DocumentCategory tempCategory = docUtil.getNextCategory();
			while(tempCategory != null){
				String name = tempCategory.getName();
			%>
			<div class="row">
          		<dl class="accordion-inner clearfix" data-accordion>
            		<dt data-target="panel<%=panelCount%>b" class="clearfix">
            			<span class="name"><%=name %></span>
            		</dt>
            	<dd>
              		<div id="panel<%=panelCount%>b" class="content">
                		<ul class="small-block-grid-2">
			
			<%
			            Document tempDoc = tempCategory.getNextDocument();
			            while(tempDoc != null){
			            	 %><li><span><%=tempDoc.getName()%></span></li> 
			            	 	<li><a href="" title="add form"><i class="icon-button-circle-plus"></i></a></li> <%
			            	tempDoc = tempCategory.getNextDocument();
			            }
						tempCategory = docUtil.getNextCategory();
						%> 
						</ul>
             		 </div>
            		</dd>
          		</dl>
	      		</div>
						
						<%
						panelCount++;
			}
		}  catch(RepositoryException e){
			%><h1>ERROR: Tags Or Documents Not Configured Properly</h1><%
		}
%>
	 </div>
	  </dd>
	 </dl>
	
	<input type="button" value="Preview" onclick="previewMeetingReminderEmail('<%=meeting.getPath()%>','<%=meeting.getUid()%>')"/>
	<input type="button" value="Send" onclick="sendMeetingReminderEmail()"/>
	
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
		$('.emailhtm .formLinks').append('<a href="'+formurl+'">'+formname+'/a>');
		return;
	};
	
</script>

 