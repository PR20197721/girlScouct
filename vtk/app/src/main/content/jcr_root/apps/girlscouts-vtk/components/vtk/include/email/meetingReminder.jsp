<div class="content clearfix">

<% //if(planView.getYearPlanComponent().getType()==YearPlanComponentType.ACTIVITY){

		%><!-- This is an activity --><% 
	//}

	//if(planView.getYearPlanComponent().getType()==YearPlanComponentType.MEETING){
		//MeetingE meeting = (MeetingE)planView.getYearPlanComponent();
		//Meeting meetingInfo = meeting.getMeetingInfo();
		//Date searchDate = planView.getSearchDate();
	Calendar c = Calendar.getInstance();
	c.setTime(planView.getSearchDate());
	c.add(Calendar.MINUTE, planView.getMeetingLength());
	Date meetingEndDate = c.getTime();
	Date searchDate = planView.getSearchDate();
	
%>

	<h4>Reminder Meeting #<%=planView.getMeetingCount()%>
	  <%= FORMAT_MEETING_REMINDER.format(searchDate) %> - <%=FORMAT_hhmm_AMPM.format(meetingEndDate)%></h4>


	<p class="sent">Sent: None</p>

	<h6>Address List</h6>
	
	<ul class="small-block-grid-3">
	  <li>
	    <input type="checkbox" id="email_to_gp" checked />
	    <label for="email_to_gp"><p>Girls / Parents</p></label>
	  </li>

	  <li>
	    <input type="checkbox" id="email_to_sf" checked />
	    <label for="email_to_sf"><p>Self</p></label>
	  </li>
	  <li>
	    <input type="checkbox" id="email_to_tv" />
	    <label for="email_to_tv"><p>Troop Volunteers</p></label>
	  </li>
	</ul>
	<section class="clearfix">
		<label for="email_to_cc">Enter your own:</label>
		<input type="email" id="email_to_cc" value="<%=troop.getSendingEmail()==null ? "" : troop.getSendingEmail().getCc()%>" placeholder="enter email addresses separated by commas"/>
	</section>
	<h6>Compose Email</h6>
	<section class="clearfix">
		<label for="email_subj">Subject:</label>
		<input type="text" id="email_subj" value="Reminder <%=troop.getTroop().getGradeLevel() %> Meeting #<%=planView.getMeetingCount()%> <%= FORMAT_MEETING_REMINDER.format(searchDate) %> - <%=FORMAT_hhmm_AMPM.format(meetingEndDate)%>" />
	</section>

	<div style="background-color:yellow;"></div>

	<textarea id="email_htm" name="textarea" class="jqte-test" rows="25" cols="25"> 
		Hello Girl Scout Families,
		<br/><br/>Here are the details of our next meeting:
		<table>
			<tr><th>Date:</th>
				<td><%= FORMAT_MEETING_REMINDER.format(searchDate)%> - <%=FORMAT_hhmm_AMPM.format(meetingEndDate)%></td>
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
		<br/><br/>
		<div id="aidLinks">
			<p class="hide">Aids Included: </p>
		</div>

		<br/><br/>
		<div id="formLinks">
			<p class="hide">Form(s) Required</p>
		</div>
	</textarea>
	

	<dl class="accordion" data-accordion>
	  <dt data-target="panel1"><h6 class="off">Include meeting aid</h6></dt>
	  <dd class="accordion-navigation">
	    <div class="content" id="panel1">
      	<ul class="small-block-grid-2">
      	<% 
      		// List<Asset> aidTags = planView.getAidTags();
      		for(int i=0;i<planView.getAidTags().size();i++) { %>
      		<li><i class="icon-pdf-file-extension"><span class="color-overlay"></span></i><span class="name"><%= planView.getAidTags().get(i).getTitle() %></span></li>
      		<li><a class="add-links" href="#nogo" title="add" onclick="addAidLink('<%=planView.getAidTags().get(i).getRefId()%>','<%=planView.getAidTags().get(i).getTitle()%>','<%=((MeetingE)planView.getYearPlanComponent()).getUid() %>')"><i class="icon-button-circle-plus"></i></a></li>
      	<%}%>
      	</ul>
	    </div>
	  </dd>
	 </dl>


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
	function addAidLink(refId,title,uid){
		$('#aidLinks').append('<li><a href="'+refId+'">'+title+'</a></li>');
		$('#aidLinks p.hide').removeClass();
		//$('.addAidToEmail').text("-");
		$('.added').dialog('open');
	    setTimeout(function() {
	    	$('.added').dialog('close');
	    }, 500);

		//addAidToEmail(refId,title,uid);

		return;
	};

</script>
 