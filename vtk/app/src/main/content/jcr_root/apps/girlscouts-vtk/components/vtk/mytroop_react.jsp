<!-- PAGEID :: ./app/src/main/content/jcr_root/apps/girlscouts-vtk/components/vtk/mytroop_react.jsp -->
<%@ page import="com.google.common.collect .*"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
    java.util.Map<Contact, java.util.List<ContactExtras>> contactsExtras=null;
	java.util.List<org.girlscouts.vtk.models.Contact> contacts = null;
	if( isCachableContacts && session.getAttribute("vtk_cachable_contacts")!=null ) {
		contacts = (java.util.List<org.girlscouts.vtk.models.Contact>) session.getAttribute("vtk_cachable_contacts");
	}

	if( contacts==null ){
		contacts =	new org.girlscouts.vtk.auth.dao.SalesforceDAO(troopDAO, connectionFactory, sling.getService(org.girlscouts.vtk.ejb.SessionFactory.class)).getContacts( user.getApiConfig(), troop.getSfTroopId());
		if( contacts!=null ) {
			session.setAttribute("vtk_cachable_contacts" , contacts);
		}
		
		
		String emailTo=",";
		try{
			for(int i=0;i<contacts.size();i++)
			if( contacts.get(i).getEmail()!=null && !contacts.get(i).getEmail().trim().equals("") && !emailTo.contains( contacts.get(i).getEmail().trim()+"," )) {
				emailTo += (contacts.get(i).getFirstName()!=null ? contacts.get(i).getFirstName().replace(" ","%20") : "") + java.net.URLEncoder.encode("<" + contacts.get(i).getEmail() +">")+",";
			}
			emailTo = emailTo.trim();
			if( emailTo.endsWith(",") )  {
				emailTo= emailTo.substring(0, emailTo.length()-1);
			}
			if( emailTo.startsWith(",") ) {
				emailTo= emailTo.substring(1, emailTo.length());
			}
		}catch(Exception e){e.printStackTrace();}
		
		java.util.Map<java.util.Date, YearPlanComponent> sched = null;
		try{
			 //GOOD-sched = meetingUtil.getYearPlanSched(user, troop.getYearPlan(), true, true);
			sched = meetingUtil.getYearPlanSched(user, troop, troop.getYearPlan(), true, false);
		}catch(Exception e){e.printStackTrace();}

		BiMap sched_bm = HashBiMap.create(sched);//com.google.common.collect.HashBiMap().create();
		com.google.common.collect.BiMap sched_bm_inverse = sched_bm.inverse();

		 contactsExtras = contactUtil.getContactsExtras( user,  troop, contacts);
	
		 
    
	%> 
	<div class="email-content">
          <div class="modal-header">
       		 <div class="vtk-email-news-button">
                    <i class="icon-button-circle-cross"></i>
              </div>
             <div class="emailHeader">Email Content: </br></div>
      	  </div>
        <div class="modal-body">
          <textarea name="message" id="message" rows="10" cols="30"></textarea>
    	</div>
    	<div class="modal-footer">
        	<strong>-The GSUSA VTK Team</strong>
      	</div>

      </div>
	<%@include file='myTroopImg.jsp' %>
	
	

			<% if( !VtkUtil.hasPermission(troop, Permission.PERMISSION_CAN_VIEW_MEMBER_DETAIL_TROOP_ID) && VtkUtil.hasPermission(troop, Permission.PERMISSION_CAN_VIEW_OWN_CHILD_DETAIL_TROOP_ID)){
					  
			
			       for(int i=0; i<contacts.size(); i++) {
			            org.girlscouts.vtk.models.Contact contact = contacts.get(i);
			           // java.util.List<ContactExtras> infos = contactUtil.girlAttendAchievement(user, troop, contact);
			           java.util.List<ContactExtras> infos = contactsExtras.get( contact );
			           if(!user.getApiConfig().getUser().getContactId().equals(contact.getContactId() ) )
			        		continue;
			            %>
						  <div class="column large-24 large-centered mytroop">
						    <dl class="accordion" data-accordion>
			                <dt data-target="panel_myChild_<%=i%>"><h3 class="on">Achievements for <%=contact.getFirstName() %></h3></dt>
						      <dd class="accordion-navigation">
						        <div class="content <%=i==0 ? "active" : "" %>" id="panel_myChild_<%=i%>">
						             <%@include file='include/troop_child_achievmts.jsp' %>
							        </div>
							      </dd>
							    </dl>
							  </div>
			        <%}//edn for
			       
			      
			 }//edn if %>
		
		<%
		
		String role="Girl";
		if( role.equals("Girl") ){ %>
		  <div class="column large-24 large-centered mytroop">
		    <dl class="accordion" data-accordion>
		      <dt data-target="panel1">
		        <h3 class="on"><%=troop.getSfTroopName() %> INFO</h3>
		        <% if(VtkUtil.hasPermission(troop, Permission.PERMISSION_SEND_EMAIL_ALL_TROOP_PARENTS_ID)){ %>
		           <a id = "#mailTroop" href="mailto:<%=emailTo%>"><i class="icon-mail"></i>email to <%= contacts.size() %> contacts</a>
		           <label><input type="checkbox" name="delimiter">Please check this box if you use Outlook</label>
		         <%} %>
		         
		      </dt>
		      <dd class="accordion-navigation">
		        <div class="content active" id="panel1">
		           <%@include file='include/troop_member_detail.jsp' %>
		        </div>
		      </dd>
		    </dl>
		  </div>
		  
	   <%}
		role="Adult";
		if( role.equals("Adult") ){ %>
	      <div class="column large-24 large-centered mytroop">
            <dl class="accordion" data-accordion>
              <dt data-target="panel2">
                <h3 class="on"><%=troop.getSfTroopName() %> VOLUNTEERS</h3>
                <% if(VtkUtil.hasPermission(troop, Permission.PERMISSION_SEND_EMAIL_ALL_TROOP_PARENTS_ID)){ %>
                  <a style="float:right;margin-right: 20px" href="<%= sling.getService(org.girlscouts.vtk.helpers.ConfigManager.class).getConfig("communityUrl")%>/Membership_Troop_Renewal">Add a New Volunteer <img width="30px" src="/etc/designs/girlscouts-vtk/clientlibs/css/images/arrow2-right_yellow.png" valign="middle"> </a>
           
                 <%} %>
                 
              </dt>
              <dd class="accordion-navigation">
                <div class="content active" id="panel2">                
                   <%@include file='include/troop_volunteer_detail.jsp' %>
                </div>
              </dd>
            </dl>
          </div>
       <%}//edn else %>

<% }//edn if contact %>

<script>
	$("#test").click(function(){
        	var el = $("#test");
            if(el.attr("show") !== "true" ){
                el.attr("show", "true");
                $(".email-content").show();
            }else{
    			el.attr("show", "false");
            }

        });
        $(window).load(function(){
            var interval = setInterval(function() {
                if ($('#test').attr("show") === "true") {
                    $(".email-content").show();
                    clearInterval(interval);
                }
            }, 100)
                var notice = $(".email-content");
            $(".vtk-email-news-button").click(function(){
                notice.css('display', 'none');
                $("#test").attr("show","false");
                var interval = setInterval(function() {
                    if ($('#test').attr("show") === "true") {
                        $(".email-content").show();
                        clearInterval(interval);
                    }
                }, 100)
            });
            $(".email-content").click(function(event) {
                event.stopPropagation();
            });

        });
</script>

