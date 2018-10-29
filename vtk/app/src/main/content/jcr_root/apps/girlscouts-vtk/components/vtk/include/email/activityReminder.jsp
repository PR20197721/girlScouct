
<%@ page import="org.girlscouts.vtk.helpers.*" %>
<!--  /apps/girlscouts-vtk/components/vtk/include/email/activityReminder.jsp -->
<div class="content clearfix">

<% 
	Date searchDate = planView.getSearchDate();
	Activity _activity =  (Activity) planView.getYearPlanComponent();
	Date endDate =_activity.getEndDate();
	String endDateString = "";
	if(searchDate.getMonth() !=  endDate.getMonth() ){
		endDateString = VtkUtil.formatDate(VtkUtil.FORMAT_MEETING_REMINDER, endDate);
    }else if(searchDate.getDate() !=  endDate.getDate() ){
    	endDateString = VtkUtil.formatDate(VtkUtil.FORMAT_MEETING_REMINDER, endDate);
    }else {	
    	endDateString = VtkUtil.formatDate(VtkUtil.FORMAT_hhmm_AMPM,endDate);
    };
	
%>
	<h4>Reminder Activity <%=_activity.getName() %>
	<%= VtkUtil.formatDate(VtkUtil.FORMAT_MEETING_REMINDER, searchDate) %> - <%=endDateString %></h4> 
	<p>Sent: None</p>

	<h6>Address List</h6>
	
	<ul class="small-block-grid-3">
	  <li style="width:100%">
	    <input type="checkbox" id="email_to_gp" checked />
	    <label for="email_to_gp"><p>Parents / Caregivers</p></label>
	  </li>
	  <li>
	    <input type="checkbox" id="email_to_tv" />
	    <label for="email_to_tv"><p>Troop Volunteers</p></label>
	  </li>
	</ul>
	<section class="clearfix">
		<label for="email_to_cc">Enter your own:</label>
		<input type="email" id="email_to_cc" value="<%=troop.getSendingEmail()==null ? "" : troop.getSendingEmail().getCc()%>" placeholder="enter email addresses separated by semicolons"/>
	</section>
	<h6>Compose Email</h6>
	<section class="clearfix">
		<label for="email_subj">Subject:</label>
		<input type="text" id="email_subj" value="Reminder <%=troop.getTroop().getGradeLevel() %> Activity <%= VtkUtil.formatDate(VtkUtil.FORMAT_MEETING_REMINDER, searchDate) %> - <%=endDateString%>" />	
	</section>

	<div style="background-color:yellow;"></div>

	<textarea id="email_htm" name="textarea" class="jqte-test" rows="25" cols="25">
		<%if (_activity.getEmlTemplate()!=null) {%>
		<%= _activity.getEmlTemplate()%> 
		<%}else{ %>
		<p>Hello Girl Scout Families,</p>
		<br/><p>Here are the details of our next activity:</p>
		<table> 
			<tr><th>Date:</th>
				<td><%= VtkUtil.formatDate(VtkUtil.FORMAT_MEETING_REMINDER, searchDate)%> - <%=endDateString%></td>
			</tr>
			<tr><th>Location:</th>
				<td>
				<%=_activity.getLocationName()!=null? _activity.getLocationName():"" %> 
				<%=_activity.getLocationAddress()!=null? _activity.getLocationAddress():"" %>
				</td>
			</tr>
			<tr><th>Cost:</th>
				<td><%= _activity.getCost()!=null? FORMAT_COST_CENTS.format(_activity.getCost()):""%></td>
			</tr>
		</table>
		<%=_activity.getContent()%>
		<br/><p>If you have any questions, or want to participate in this activity, please contact me at 
		<%if(apiConfig.getUser().getPhone()!=null)%><%=apiConfig.getUser().getPhone() %> 
		<%if(apiConfig.getUser().getMobilePhone()!=null)%><%=apiConfig.getUser().getMobilePhone() %>
		<%if(apiConfig.getUser().getHomePhone()!=null)%><%=apiConfig.getUser().getHomePhone() %>
		<%if(apiConfig.getUser().getAssistantPhone()!=null)%><%=apiConfig.getUser().getAssistantPhone() %>
		</p>
		<br/><p>Thank you for supporting your <%=troop.getTroop().getGradeLevel() %>,</p>

		<br/><p><%if(apiConfig.getUser().getName()!=null)%><%=apiConfig.getUser().getName() %></p>
		<p><%=troop.getTroop().getTroopName() %></p>
		<br/><br/>

		<% }%>
	</textarea>

	<div class="right clearfix">
		<input type="button" value="Send email" class="button btn" onclick="this.disabled=true;sendEmail();"/>
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

	function sendEmail(){
		if(validate()){
	    	previewMeetingReminderEmail('<%=_activity.getUid()%>',template);
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
 