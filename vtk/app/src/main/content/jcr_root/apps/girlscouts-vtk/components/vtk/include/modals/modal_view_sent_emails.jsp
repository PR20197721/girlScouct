
<!-- apps/girlscouts-vtk/components/vtk/include/modals/modal_view_sent_emails.jsp -->

	<div id="modal_view_sent_emails" class="reveal-modal" data-reveal>
		<div class="header clearfix">
		  <h3 class="columns large-22">View Sent Emails</h3>
		  <a class="close-reveal-modal columns large-2" href="#"><i class="icon-button-circle-cross"></i></a>
		</div>
		<div class="scroll">
			<div class="content">
				<ul class="browseSentEmails">
					<% if( (planView.getYearPlanComponent().getType() ==  YearPlanComponentType.ACTIVITY) ){%>
	   					<li><p>emails not set up for activities</p></li> 
	   				<% } else{
	   					MeetingE curM = (MeetingE)planView.getYearPlanComponent();
	   					List<SentEmail> emails = curM.getSentEmails();
	   					for(SentEmail eml : emails){%>
	   						<li><%=eml.getUid() %>
	   						</li>
	   					<% } 
	   				}%>
				</ul>
					
			</div>
		</div>
	</div>