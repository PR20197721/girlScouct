
<%@ page import="org.girlscouts.vtk.helpers.*" %>
<!-- /apps/girlscouts-vtk/components/vtk/include/email/meetingReminder.jsp -->
<div class="content clearfix">

<% 
	MeetingE _meeting = planView.getMeeting();
	Calendar c = Calendar.getInstance();
	c.setTime(planView.getSearchDate());
	c.add(Calendar.MINUTE, planView.getMeetingLength());
	Date meetingEndDate = c.getTime();
	Date searchDate = planView.getSearchDate();
%>

	<h4>Reminder Meeting #<%=planView.getMeetingCount()%>
	<%= VtkUtil.formatDate(VtkUtil.FORMAT_MEETING_REMINDER, searchDate) %> - <%=VtkUtil.formatDate(VtkUtil.FORMAT_hhmm_AMPM, meetingEndDate)%></h4>
	
	<p class="sent">Sent: None</p><!--TODO add the date after email is sent-->

	<h6>Address List</h6>
	
	<ul class="small-block-grid-3">
	  <li style="width:100%">
	    <input type="checkbox" id="email_to_gp" checked />
	    <label for="email_to_gp"><p>Parents / Caregivers</p></label>
	  </li>
        <!-- <li>
	    <input type="checkbox" id="email_to_sf" checked />
	    <label for="email_to_sf"><p>Self</p></label>
	  </li> -->
	  <li style="display:none;">
	    <input type="checkbox" id="email_to_tv" />
	    <label for="email_to_tv"><p>Troop Volunteers</p></label>
	  </li>
	</ul>
	<section class="clearfix">
		<label for="email_to_cc">Enter your own:</label>
		<input type="email" id="email_to_cc" placeholder="enter email addresses separated by semicolons"/>
	</section>
	<h6>Compose Email</h6>
	<section class="clearfix">
		<label for="email_subj">Subject:</label>
		<input type="text" id="email_subj" value="Reminder <%=troop.getTroop().getGradeLevel() %> Meeting #<%=planView.getMeetingCount()%> <%= VtkUtil.formatDate(VtkUtil.FORMAT_MEETING_REMINDER, searchDate) %> - <%= VtkUtil.formatDate(VtkUtil.FORMAT_hhmm_AMPM,meetingEndDate)%>" />	
	</section>

	<div style="background-color:yellow;"></div>

	<textarea id="email_htm" name="textarea" class="jqte-test" rows="25" cols="25">
 		<%if (_meeting.getEmlTemplate()!=null) {%>
		<%= _meeting.getEmlTemplate()%>  
		<%}else{ %>
		<p>Hello Girl Scout Families,</p>
		<br/><p>Here are the details of our next meeting:</p>
		<table> 
			<tr><th>Date:</th>
				<td><%= VtkUtil.formatDate(VtkUtil.FORMAT_MEETING_REMINDER, searchDate)%> - <%= VtkUtil.formatDate(VtkUtil.FORMAT_hhmm_AMPM, meetingEndDate)%></td>
			</tr>
			<tr><th>Location:</th>
				<td><%
				if( _meeting.getLocationRef()!=null && troop.getYearPlan().getLocations()!=null ){
					for(int k=0;k<troop.getYearPlan().getLocations().size();k++){	
						if( troop.getYearPlan().getLocations().get(k).getPath().equals( _meeting.getLocationRef() ) ){%>
						<%=troop.getYearPlan().getLocations().get(k).getName() %>
						<br/><%=troop.getYearPlan().getLocations().get(k).getAddress() %>
						<%-- 
						<%=troop.getYearPlan().getLocations().get(k).getCity() %>
						<%=troop.getYearPlan().getLocations().get(k).getState() %>
						<%=troop.getYearPlan().getLocations().get(k).getZip() %> 
						--%>
						<% }
					}
		   		}
				%></td>
			</tr>
			<tr><th>Topic:</th>
				<td><%= _meeting.getMeetingInfo().getName() %></td>
			</tr>
		</table>
		<%JcrCollectionHoldString eInvite =  _meeting.getMeetingInfo().getMeetingInfo().get("email invite");
		if(eInvite!=null && eInvite.getStr()!=null && !eInvite.getStr().trim().isEmpty()) {%>
		<%=_meeting.getMeetingInfo().getMeetingInfo().get("email invite").getStr() %>
		<% }else{%> 
		<%=_meeting.getMeetingInfo().getMeetingInfo().get("overview").getStr() %>
		<% } %>
		<br/><p>If you have any questions, or want to participate in this meeting, please contact me at 
		<%if(apiConfig.getUser().getPhone()!=null)%><%=apiConfig.getUser().getPhone() %>
		<%if(apiConfig.getUser().getMobilePhone()!=null)%><%=apiConfig.getUser().getMobilePhone() %>
		<%if(apiConfig.getUser().getHomePhone()!=null)%><%=apiConfig.getUser().getHomePhone() %>
		<%if(apiConfig.getUser().getAssistantPhone()!=null)%><%=apiConfig.getUser().getAssistantPhone() %>
		</p>
		<br/><p>Thank you for supporting your <%=troop.getTroop().getGradeLevel() %>,</p>

		<br/><p><%if(apiConfig.getUser().getName()!=null)%><%=apiConfig.getUser().getName() %></p>
		<p><%=troop.getTroop().getTroopName() %></p>
		<br/><br/>
		<div id="aidLinks">
			<p class="hide">Aids Included: </p>
		</div>
		<br/><br/>
		<div id="formLinks">
			<p class="hide">Form(s) Required</p>
		</div>
		<% }%>
	</textarea>
	
	
	<dl class="accordion" data-accordion>
	  <dt data-target="panel1"><h6 class="off">Include meeting aid</h6></dt>
	  <dd class="accordion-navigation">
	    <div class="content" id="panel1">
      	<ul class="small-block-grid-2"><%  
      	boolean isAidTag= false;
      	if(planView.getAidTags()!=null)
      	  for(int i=0;i<planView.getAidTags().size();i++) { 
						String ext = planView.getAidTags().get(i).getDocType();
						if(ext == null) {
							ext= org.girlscouts.vtk.utils.GSUtils.getDocExtensionFromString(planView.getAidTags().get(i).getRefId());
						}
						isAidTag= true;
      	%>
      		<li><span class="name icon <%=ext%>"><a href="<%=planView.getAidTags().get(i).getRefId()%>" target="_blank"><%= planView.getAidTags().get(i).getTitle() %></a></span></li>
      		<li><a class="add-links" href="#nogo" title="add" onclick="addAidLink('<%=planView.getAidTags().get(i).getRefId()%>','<%=planView.getAidTags().get(i).getTitle()%>','<%=_meeting.getUid() %>')"><i class="icon-button-circle-plus"></i></a></li><%
      	} %>
      	
      	<% if( !isAidTag){ %><li>No Aids found</li><% }%>
      	</ul>
	    </div>
	  </dd>
	</dl>
	<dl class="accordion" data-accordion>
	  <dt data-target="panel2"><h6>Include Form Link</h6></dt>
	  <dd class="accordion-navigation">
	    <div class="content" id="panel2"><% 
			String councilId = null;
			if (apiConfig != null) {
			    if (apiConfig.getTroops().size() > 0) {
			        councilId = Integer.toString(apiConfig.getTroops().get(0).getCouncilCode());
			    }
			}
			CouncilMapper mapper = sling.getService(CouncilMapper.class);
			String branch = mapper.getCouncilBranch(councilId);
			branch = branch.replace("/content/", "");
			
			//For testing on local set default council since gateway doesn't have tags
			if(branch == null || branch.isEmpty() || branch.equals("gateway")){
				branch = "gsnetx";
			}
			
			org.girlscouts.vtk.utils.DocumentUtil docUtil = new org.girlscouts.vtk.utils.DocumentUtil(resourceResolver, sling.getService(com.day.cq.tagging.JcrTagManagerFactory.class), branch);
			try {	
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
	      		<dd><div id="panel<%=panelCount%>b" class="content">
					<ul class="small-block-grid-2"><% 
					Document tempDoc = tempCategory.getNextDocument();
					while(tempDoc != null){%>
						<li><a href="<%=tempDoc.getPath()%>" target="_blank"><span><%=tempDoc.getTitle()%></span></a></li> 
						<li><a class="add-links" href="#nogo" title="add" onclick="addFormLink('<%=tempDoc.getPath()%>', '<%=tempDoc.getTitle()%>', 'panel<%=panelCount%>b')"><i class="icon-button-circle-plus"></i></a></li> <%
						tempDoc = tempCategory.getNextDocument();
					}
					tempCategory = docUtil.getNextCategory();%> 
					</ul>
					</div>
				</dd>
				</dl>
				</div><% 
				panelCount++; }
				}  catch(RepositoryException e){
					%><h1>ERROR: Tags Or Documents Not Configured Properly</h1><%
				}%>
			</div>
	  	</dd>
	</dl>
	<div class="right clearfix">
		<input type="button" value="Send email" class="button btn" onclick="this.disabled=true; <%=user.getApiConfig().isDemoUser() ? "" : "sendEmail()"%>";"/>
	</div>
	<div id="added">
		<p>Added to email.</p>
	</div>
	<div id="after-sent">
		<p>Email(s) sent.</p>
	</div>
	
</div>
<!--end of content-->


<script>
	var template;
	$(document).ready(function(){
		 $('#added').dialog({ autoOpen: false, zIndex: 200 });
		 $('#after-sent').dialog({ autoOpen: false, zIndex: 200 });
		$(".jqte-test").jqte({
			"source": false,
			"rule": false,
			"sub": false,
			"strike": false,
			"fsizes": ['10','12','14','16','18','20','22','24','28','32']
		});
		getTemplate();
	});
	
	function getTemplate(){
		$('#email_htm').val(removeIndentions($('.jqte_editor').html()));
		template = $('#email_htm').val();
		
	};
	function addFormLink(link, formname, categoryId){
		var url = window.location.href;
		var arr = url.split("/");
		var host = arr[0] + "//" + arr[2];
		$('.jqte_editor #formLinks').append('<li><a href="'+host+link+'" target="_blank">'+formname+'</a></li>');
		$('.jqte_editor #formLinks p.hide').removeClass();
		$("dt[data-target='" + categoryId + "'] span").removeClass('on');
		$('.accordion #' + categoryId).slideToggle('slow');
		$('.ui-dialog-titlebar').css('display', 'none');
		$('#added').dialog('open');
		$('.ui-dialog').css('z-index', 300);
	    setTimeout(function() {
	    	$('#added').dialog('close');
	    }, 1000);
		return;
	};
	function addAidLink(refId,title,uid){
		var url = window.location.href;
		var arr = url.split("/");
		var host = arr[0] + "//" + arr[2];
		$('.jqte_editor #aidLinks').append('<li><a href="'+host+refId+'" target="_blank">'+title+'</a></li>');
		$('.jqte_editor #aidLinks p.hide').removeClass();
		$('#added').dialog('open');
		$('.ui-dialog-titlebar').css('display', 'none');
		$('.ui-dialog').css('z-index', 300);
	    setTimeout(function() {
	    	$('#added').dialog('close');
	    }, 1000);
		//addAidToEmail(refId,title,uid);
		return;
	};
	function sendEmail(){
		if(validate()){
	    	previewMeetingReminderEmail('<%=_meeting.getUid()%>',template);
		}
	};
	function validate(){
	    //allow leading and trailing spaces for every email addr
	    var emailReg = /^((\ *[\w-\.]+@([\w-]+\.)+[\w-]{2,4}\ *)\;?)+$/;
	    var emailAddr = $('#email_to_cc').val();
	    var subject = $('#email_subj').val();
	    var body = $('#email_htm').val();

	    if(emailAddr.length){
		    if(!emailReg.test(emailAddr)){
		    	//$('#email_to_cc') label turn red or input background turn red
		    	$('.scroll').scrollTop($('#email_to_cc').position().top);
	            alert("Please enter valid email address(es).");
	    	    return false;
	        }
		} else if (!$("input:checkbox:checked").length){
	    	$('.scroll').scrollTop($('#email_to_cc').position().top);
    		alert("Address list can not be empty.");
	    	return false;
		}
		if(!subject.length){
	    	$('.scroll').scrollTop($('#email_subj').position().top);
    		alert("Subject can not be empty.");
    		return false;
		}
		if(!body.length){
	    	$('.scroll').scrollTop($('#email_htm').position().top);
    		alert("Email body can not be empty.");
    		return false;
		}
		return true;
	    
	};
	function removeIndentions(x) {
		return x.replace(/^\s+|\s+$/gim, '');

	};
	$("#modal-meeting-reminder").on('change', 'input', function(event){
    	$('input[type="button"]').attr('disabled',false);
	});


	
</script>
 
