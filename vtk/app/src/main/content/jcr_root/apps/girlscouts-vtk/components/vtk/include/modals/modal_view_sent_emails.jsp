
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
	   						if(emails.size()==1){
	   							SentEmail eml = emails.get(0);%>
	   							<div><%=eml.getSubject() %></div>
	   							<div>Sent: <%=VtkUtil.formatDate(VtkUtil.FORMAT_CALENDAR_DATE, eml.getSentDate()) %></div>
		   						<div><%=curA.getEmlTemplate()==null? "":eml.getHtmlMsg(curA.getEmlTemplate())%> </div><% 
	   						}else{%>
		   						<div class="scroll_1">
	                <ul id="email-list"><% 
	                for(int k=emails.size(); k>0; k--){
	                  SentEmail eml = emails.get(k-1);%>
	                  <li class="<%= k==emails.size()? "active" : ""%>"><a href="#nogo" data-target="panel<%=k%>s"><%=eml.getSubject()%></a><span class="right"><%=VtkUtil.formatDate(VtkUtil.FORMAT_MONTH_DAY, eml.getSentDate()) %></span></li>
	                <% } %>
	                </ul> 
	                </div>
	                <div class="scroll_2"><% 
	                for(int k=emails.size(); k>0; k--){
	                  SentEmail eml = emails.get(k-1); %>
	                  <div id="panel<%=k%>s" class="<%= k==emails.size()? "show" : "hide"%>">
	                      <div><%=eml.getSubject() %></div>
	                      <div>Sent: <%=VtkUtil.formatDate(VtkUtil.FORMAT_MMMM_dd_hhmm_AMPM, eml.getSentDate())%></div>
	                    <div><%=curA.getEmlTemplate()==null? "":eml.getHtmlMsg(curA.getEmlTemplate())%> </div>
	                  </div>
                <% } %>
                </div><% 
	   						}

	   					}else{%>
	   						<div>No email has been sent.</div><% 
	   					}
	   				} else{
	   					//MeetingE curM = planView.getMeeting();
	   					
	   					MeetingE curM =  meetingUtil.getMeetingE( user,  troop, 
	 							((MeetingE) planView.getYearPlanComponent()).getPath() );
	   					if(curM != null){
		   					List<SentEmail> emails = curM.getSentEmails();
		   					if(emails!=null && !emails.isEmpty()){
		   						if(emails.size()==1){
	   							SentEmail eml = emails.get(0);%>
	   							<div><%=eml.getSubject() %></div>
	   							<div>Sent: <%=VtkUtil.formatDate(VtkUtil.FORMAT_CALENDAR_DATE, eml.getSentDate()) %></div>
		   						<div><%=curM.getEmlTemplate()==null? "":eml.getHtmlMsg(curM.getEmlTemplate())%> </div><% 
	   						}else{%>
	   							<div class="scroll_1">
	   							<ul id="email-list"><% 
			   					for(int k=emails.size(); k>0; k--){
			   						SentEmail eml = emails.get(k-1);%>
			   						<li class="<%= k==emails.size()? "active" : ""%>"><a href="#nogo" data-target="panel<%=k%>s"><%=eml.getSubject()%></a><span class="right"><%=VtkUtil.formatDate(VtkUtil.FORMAT_MONTH_DAY, eml.getSentDate())  %></span></li>
			   					<% } %>
			   					</ul> 
			   					</div>
			   					<div class="scroll_2"><% 
			   					for(int k=emails.size(); k>0; k--){
			   						SentEmail eml = emails.get(k-1); %>
		    						<div id="panel<%=k%>s" class="<%= k==emails.size()? "show" : "hide"%>">
	      								<div><%=eml.getSubject() %></div>
	      								<div>Sent: <%=VtkUtil.formatDate(VtkUtil.FORMAT_MMMM_dd_hhmm_AMPM, eml.getSentDate())%></div>
	 		   							<div><%=curM.getEmlTemplate()==null? "":eml.getHtmlMsg(curM.getEmlTemplate())%> </div>
	 		   						</div>
			   					<% } %>
			   					</div><% 
		   						}
		   					}else{%>
	   							<div>No email has been sent.</div><% 
	   						}
	   					}else{%>
							<div>No email has been sent.</div><% 
						}
	   				}%>
				</div>
					
			</div>
		</div>
	</div>

<script>
	$('.browseSentEmails li a').on('click', function() {
		$('.browseSentEmails li').removeClass('active');
		var link = $(this).data('target');
		$(this).parent().addClass('active');
		$('div.show').removeClass('show').addClass('hide');
		$('div#'+ link ).removeClass('hide').addClass('show');
	})
</script>
	