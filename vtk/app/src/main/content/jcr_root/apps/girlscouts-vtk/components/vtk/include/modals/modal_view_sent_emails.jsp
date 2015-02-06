
<!-- apps/girlscouts-vtk/components/vtk/include/modals/modal_view_sent_emails.jsp -->

	<div id="modal_view_sent_emails" class="reveal-modal" data-reveal>
		<div class="header clearfix">
		  <h3 class="columns large-22">View Sent Emails</h3>
		  <a class="close-reveal-modal columns large-2" href="#"><i class="icon-button-circle-cross"></i></a>
		</div>
		<div class="scroll">
			<div class="content">
			  	<div class="browseSentEmails">
					<% if( (planView.getYearPlanComponent().getType() ==  YearPlanComponentType.ACTIVITY) ){%>
	   					<p>emails not set up for activities</p> 
	   				<% } else{
	   					MeetingE curM = planView.getMeeting();
	   					List<SentEmail> emails = curM.getSentEmails();
	   					if(emails!=null && !emails.isEmpty()){
		   					for(SentEmail eml : emails){%>
		   						<div><%=FORMAT_CALENDAR_DATE.format(eml.getSentDate()) %></div>
		   						<div><%=eml.getSubject() %></div>
		   						<div><%=eml.getHtmlMsg() %></div>
		   						<hr>
		   					<% } 
	   					}
	   				}%>
				</div>
					
			</div>
		</div>
	</div>