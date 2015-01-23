

<div class="content clearfix meeting-reminder">

<% //if(planView.getYearPlanComponent().getType()==YearPlanComponentType.ACTIVITY){
	//}

	//if(planView.getYearPlanComponent().getType()==YearPlanComponentType.MEETING){
		//MeetingE meeting = (MeetingE)planView.getYearPlanComponent();
		//Meeting meetingInfo = meeting.getMeetingInfo();
		//Date searchDate = planView.getSearchDate();
%>

	<h5>Reminder Meeting <%=((MeetingE)planView.getYearPlanComponent()).getMeetingInfo().getName() %>
	  <%= FORMAT_MEETING_REMINDER.format(planView.getSearchDate()) %></h5>
	
	<p class="sent">Sent: None</p>

	<h6>Address List</h6>
	
	<ul class="inline-list">
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
	</ul>
	<p>Enter your own:<input type="email" id="email_to_cc" value="<%=troop.getSendingEmail()==null ? "" : troop.getSendingEmail().getCc()%>"/></p>
	
	<h6>Compose Email</h6>
	
	<p>Subject: <input type="text" id="email_subj" value="Reminder <%=troop.getTroop().getGradeLevel() %> Meeting #<%=planView.getMeetingCount()%> <%= FORMAT_MEETING_REMINDER.format(planView.getSearchDate()) %>"/></p>
	
	<div style="background-color:yellow;"></div>
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

	<div id="aidLinks">
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

	<dl class="accordion" data-accordion>
	  <dt data-target="panel1"><h6 class="off">Include meeting aid</h6></dt>
	  <dd class="accordion-navigation">
	    <div class="content" id="panel1">
      	<ul class="small-block-grid-2">
      	<% for(int i=0;i<planView.getAidTags().size();i++) { %>
      		<li><i class="icon-<%=planView.getAidTags().get(i).getDocType()==null?"":planView.getAidTags().get(i).getDocType()+"-"%>file-extension">
      		<span class="color-overlay"></span></i><span class="name"><%= planView.getAidTags().get(i).getTitle() %>
      		</span>
      		</li>
      		<li><a href="#nogo" title="add" onclick="addAidLink('<%=planView.getAidTags().get(i).getRefId()%>','<%=planView.getAidTags().get(i).getTitle()%>','<%= planView.getAidTags().get(i).getDocType() %>','<%=((MeetingE)planView.getYearPlanComponent()).getUid() %>')"><i class="icon-button-circle-plus"></i></a></li>
      	<%}%>
      	</ul>
	    </div>
	  </dd>
	 </dl>

	<dl class="accordion" data-accordion>
	  <dt data-target="panel2"><h6>Include Form Link</h6></dt>
	  <dd class="accordion-navigation">
	    <div class="content" id="panel2">
        <div class="row">
          <dl class="accordion-inner clearfix" data-accordion>
            <dt data-target="panel1b" class="clearfix">
            	<span class="name">Activity Planning and Approval</span>
            </dt>
            <dd>
              <div id="panel1b" class="content">
                <ul class="small-block-grid-3">
                  <li>Name of Form</li>
                  <li>Purpose of Form</li>
                  <li><a href="" title="add form"><i class="icon-button-circle-plus"></i></a></li>
                </ul>
              </div>
            </dd>
          </dl>
	      </div>
	    </div>
	  </dd>
	 </dl>
	 <div class="right clearfix">
		<input type="button" value="Save" class="button btn" onclick="previewMeetingReminderEmail('<%=((MeetingE)planView.getYearPlanComponent()).getUid()%>')"/>
		<input class="button btn" value="Send email" type="button" onclick="sendMeetingReminderEmail()"/>
	</div>
</div>
<!--//content-->
<% //}%>
<div class="added" style="display:none">
<p>Added to email.</p>
</div>


<script>
	$(document).ready(function(){
		//print out the date the email was sent.TBD
		 /* if(moment(new Date()) != null && moment(new Date()) !='') {
		  $('.sent').append(moment(new Date()).format('MM/DD/YYYY'));
		 } else {
		  $('.sent').append('none');
		 } */
		 $(".added").dialog({ autoOpen: false });

	});
	

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
	function addAidLink(refId,title,docType,uid){
		$('#aidLinks').append('<li><i class="icon-'+docType+'-file-extension"></i><a href="'+refId+'">'+title+'</a></li>');
		//$('.addAidToEmail').text("-");
		$('.added').dialog('open');
	    setTimeout(function() {
	    	$('.added').dialog('close');
	    }, 500);

		//addAidToEmail(refId,title,uid);
		return;
	};

</script>
 