<!-- PAGEID :: ./app/src/main/content/jcr_root/apps/girlscouts-vtk/components/vtk/mytroop_react.jsp -->
<%@ page import="com.google.common.collect .*"%>
<%
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
				emailTo += contacts.get(i).getFirstName().replace(" ", "&nbsp;")  +java.net.URLEncoder.encode("<" + contacts.get(i).getEmail() +">,");
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
			sched = meetingUtil.getYearPlanSched(user, troop.getYearPlan(), true, true);
		}catch(Exception e){e.printStackTrace();}

		BiMap sched_bm = HashBiMap.create(sched);//com.google.common.collect.HashBiMap().create();
		com.google.common.collect.BiMap sched_bm_inverse = sched_bm.inverse();
%>
<%@include file="include/utility_nav.jsp"%>
<%@include file='include/modals/modal_upload_img.jsp' %>

  <div class="hero-image">
    <%
            if (!resourceResolver.resolve(troopPhotoUrl).getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
		if (request.getParameter("newTroopPhoto") != null) {
			Random r  = new Random();
			troopPhotoUrl += "?pid=";
			troopPhotoUrl += r.nextInt();
		}
    %>
        <img src="<%=troopPhotoUrl %>" alt="GirlScouts Troop <%=troop.getTroop().getTroopName()%> Photo" />
       
       <%if(hasPermission(troop, Permission.PERMISSION_EDIT_TROOP_ID)){ %>
        <a data-reveal-id="modal_upload_image" title="update photo" href="#nogo" title="upload image"><i class="icon-photo-camera"></i></a>
        <%} %>
    <%
    	}
    %>
  </div>

<%if(hasPermission(troop, Permission.PERMISSION_canViewOwnChildDetail_TROOP_ID)){ %>
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

       <% for(int i=0; i<contacts.size(); i++) { 
            org.girlscouts.vtk.models.Contact contact = contacts.get(i);
            java.util.List<ContactExtras> infos = contactUtil.girlAttendAchievement(user, troop, contact);
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
        %>

  <div class="column large-24 large-centered mytroop">
    <dl class="accordion" data-accordion>
      <dt data-target="panel1"><h3 class="on"><%=troop.getSfTroopName() %> INFO</h3>
       <% if(!hasPermission(troop, Permission.PERMISSION_canViewOwnChildDetail_TROOP_ID)){%>
            <a href='mailto:<%=emailTo%>'><i class="icon-mail"></i>email to <%= contacts.size() %> contacts</a>
       <% }%>
      </dt>
      <dd class="accordion-navigation">
        <div class="content active" id="panel1">
           <%@include file='include/troop_member_detail.jsp' %>
        </div>
      </dd>
    </dl>
  </div>
    <% } %>

