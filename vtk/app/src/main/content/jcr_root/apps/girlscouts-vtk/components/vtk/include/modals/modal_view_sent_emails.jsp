
<!-- apps/girlscouts-vtk/components/vtk/include/modals/modal_view_sent_emails.jsp -->

	<div id="modal_view_sent_emails" class="reveal-modal" data-reveal>
		<div class="header clearfix">
		  <h3 class="columns large-22">View Sent Emails</h3>
		  <a class="close-reveal-modal columns large-2" href="#"><i class="icon-button-circle-cross"></i></a>
		</div>
		<div class="scroll">
			<div class="content">
			  	<div class="browseSentEmails"><% 
					if( (planView.getYearPlanComponent().getType() ==  YearPlanComponentType.ACTIVITY) ){
						Activity curA = (Activity)planView.getYearPlanComponent();
	   					List<SentEmail> emails = curA.getSentEmails();
	   					if(emails!=null && !emails.isEmpty()){
		   					for(int k=emails.size(); k>0; k--){
		   						SentEmail eml = emails.get(k-1);%>
		   						<%-- <div>Sent: <%=FORMAT_CALENDAR_DATE.format(eml.getSentDate()) %></div>
		   						<div><%=eml.getSubject() %></div>
 		   						<div><%=curA.getEmlTemplate()==null? "":eml.getHtmlMsg(curA.getEmlTemplate())%> </div><hr> --%>
		   						<dl class="accordion" data-accordion>
	  								<dt data-target="panel1"><h6 class="off">Sent: <%=FORMAT_CALENDAR_DATE.format(eml.getSentDate()) %></h6></dt>
	  								<dd class="accordion-navigation">
	    							<div class="content" id="panel1">
      								<ul class="small-block-grid-2"> 
      								<li>
      								<div><%=eml.getSubject() %></div>
 		   							<div><%=curA.getEmlTemplate()==null? "":eml.getHtmlMsg(curA.getEmlTemplate())%> </div>
 		   							</li>
      								</ul>
	    							</div>
	  								</dd>
								</dl>
		   					
		   					<% } 

	   					}else{%>
	   						<div>No email has been sent.</div><% 
	   					}
	   				} else{
	   					MeetingE curM = planView.getMeeting();
	   					List<SentEmail> emails = curM.getSentEmails();
	   					if(emails!=null && !emails.isEmpty()){
		   					for(int k=emails.size(); k>0; k--){
		   						SentEmail eml = emails.get(k-1);%>
		   						<div>Sent: <%=FORMAT_CALENDAR_DATE.format(eml.getSentDate()) %></div>
		   						<div><%=eml.getSubject() %></div>
 		   						<div><%=curM.getEmlTemplate()==null? "":eml.getHtmlMsg(curM.getEmlTemplate())%> </div>		   						<hr>
		   					<% } 
	   					}else{%>
   						<div>No email has been sent.</div><% 
   						}
	   				}%>
				</div>
					
			</div>
		</div>
	</div>
	