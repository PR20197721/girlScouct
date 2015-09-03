<!-- PAGEID :: ./app/src/main/content/jcr_root/apps/girlscouts-vtk/components/vtk/mytroop_react.jsp -->
<%@ page import="com.google.common.collect .*"%>
<%


java.util.Map<Contact, java.util.List<ContactExtras>> contactsExtras=null;
	java.util.List<org.girlscouts.vtk.models.Contact> contacts = null;
	if( isCachableContacts && session.getAttribute("vtk_cachable_contacts")!=null ) {
		contacts = (java.util.List<org.girlscouts.vtk.models.Contact>) session.getAttribute("vtk_cachable_contacts");
	}

	if( contacts==null ){
		contacts =	new org.girlscouts.vtk.auth.dao.SalesforceDAO(troopDAO, connectionFactory).getContacts( user.getApiConfig(), troop.getSfTroopId() );
		if( contacts!=null ) {
			session.setAttribute("vtk_cachable_contacts" , contacts);
		}

		String emailTo=",";
		try{
			for(int i=0;i<contacts.size();i++)
			if( contacts.get(i).getEmail()!=null && !contacts.get(i).getEmail().trim().equals("") && !emailTo.contains( contacts.get(i).getEmail().trim()+"," )) {
				//emailTo += (new java.net.URI((contacts.get(i).getFirstName() + "<" + contacts.get(i).getEmail() +">,")).getRawPath());
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
		//if(true)return;
		java.util.Map<java.util.Date, YearPlanComponent> sched = null;
		try{
			 //GOOD-sched = meetingUtil.getYearPlanSched(user, troop.getYearPlan(), true, true);
			sched = meetingUtil.getYearPlanSched(user, troop.getYearPlan(), true, false);
		}catch(Exception e){e.printStackTrace();}

		BiMap sched_bm = HashBiMap.create(sched);//com.google.common.collect.HashBiMap().create();
		com.google.common.collect.BiMap sched_bm_inverse = sched_bm.inverse();
//if(true)return;
		 contactsExtras = contactUtil.getContactsExtras( user,  troop, contacts);
	
		 
    
	%> 
	<%@include file='myTroopImg.jsp' %>
	
	

<% if( false) {
	//!hasPermission(troop, Permission.PERMISSION_CAN_VIEW_MEMBER_DETAIL_TROOP_ID) && hasPermission(troop, Permission.PERMISSION_CAN_VIEW_OWN_CHILD_DETAIL_TROOP_ID)){ %>
  <div class="column large-24 large-centered mytroop">

    <dl class="accordion" data-accordion>
      <dt data-target="panel2"><h3>Attendance</h3></dt>
      <dd class="accordion-navigation">
        <div class="content" id="panel2">
           <%@include file='include/troop_child_attnds.jsp' %>
        </div>
      </dd>
    </dl>
  </div>

       <%

       for(int i=0; i<contacts.size(); i++) {
            org.girlscouts.vtk.models.Contact contact = contacts.get(i);
           // java.util.List<ContactExtras> infos = contactUtil.girlAttendAchievement(user, troop, contact);
           java.util.List<ContactExtras> infos = contactsExtras.get( contact );
           if(!user.getApiConfig().getUser().getContactId().equals(contact.getContactId() ) )
        		continue;
            %>
			  <div class="column large-24 large-centered mytroop">
			    <dl class="accordion" data-accordion>
                <dt data-target="panel_myChild_<%=i%>"><h3 class="on">Achievements for <%=contact.getFirstName() %></h3></a></dt>
			      <dd class="accordion-navigation">
			        <div class="content <%=i==0 ? "active" : "" %>" id="panel_myChild_<%=i%>">
			             <%@include file='include/troop_child_achievmts.jsp' %>
				        </div>
				      </dd>
				    </dl>
				  </div>
        <%}
 }

if(hasPermission(troop, Permission.PERMISSION_CAN_VIEW_MEMBER_DETAIL_TROOP_ID)){
        %>

  <div class="column large-24 large-centered mytroop">
    <dl class="accordion" data-accordion>
      <dt data-target="panel1"><h3 class="on"><%=troop.getSfTroopName() %> INFO</h3>
      
            <a href="mailto:<%= emailTo %>"><i class="icon-mail"></i>email to <%= contacts.size() %> contacts</a>
    
      </dt>
      <dd class="accordion-navigation">
        <div class="content active" id="panel1">
           <%@include file='include/troop_member_detail.jsp' %>
        </div>
      </dd>
    </dl>
  </div>
  
    <% } } %>




