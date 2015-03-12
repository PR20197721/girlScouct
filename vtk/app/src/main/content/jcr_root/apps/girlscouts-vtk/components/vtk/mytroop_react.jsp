<!-- PAGEID :: ./app/src/main/content/jcr_root/apps/girlscouts-vtk/components/vtk/mytroop_react.jsp -->



<%@ page import="com.google.common.collect .*"%>
<%
	java.util.List<org.girlscouts.vtk.models.Contact> contacts = null;
	if( isCachableContacts && session.getAttribute("vtk_cachable_contacts")!=null ) {
		contacts = (java.util.List<org.girlscouts.vtk.models.Contact>) session.getAttribute("vtk_cachable_contacts");
	}

	if( contacts==null ){
		contacts =	new org.girlscouts.vtk.auth.dao.SalesforceDAO(troopDAO).getContacts( user.getApiConfig(), troop.getSfTroopId() );
		if( contacts!=null )
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

		BiMap sched_bm=   HashBiMap.create(sched);//com.google.common.collect.HashBiMap().create();
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
  <div class="column large-24 large-centered mytroop">

    <dl class="accordion" data-accordion>
      <dt data-target="panel1"><h3 class="on"><%=troop.getSfTroopName() %> INFO</h3><a href='mailto:<%=emailTo%>'><i class="icon-mail"></i>email to <%= contacts.size() %> contacts</a></dt>
      <dd class="accordion-navigation">
        <div class="content active" id="panel1">
          <div class="row">
            <div class="column large-20 large-centered">
              <% for(int i=0; i<contacts.size(); i++) { 
                  org.girlscouts.vtk.models.Contact contact = contacts.get(i);
                  java.util.List<ContactExtras> infos = contactUtil.girlAttendAchievement(user, troop, contact);
                  //-Works !!! String _email= java.net.URLEncoder.encode(contact.getFirstName() +"<"+contact.getEmail() +">");
        String _email = "";
        if(contact.getFirstName() != null && contact.getEmail() != null){
            _email= contact.getFirstName().replace(" ", "&nbsp;") + java.net.URLEncoder.encode("<"+contact.getEmail() +">");
        }
                  %>
                <div class="row">
                  <dl class="accordion-inner clearfix" data-accordion>
                    <dt data-target="panel<%=i+1%>b" class="clearfix">
                      <span class="name column large-10"><%=contact.getFirstName() %></span>
                      <%
                      if(contact.getEmail() != null){
                        %><a class="column large-10 email" href="mailto:<%=_email%>">
                        <i class="icon-mail"></i><%=contact.getEmail() %><%
                      }%>
                      </a>
                      <span class="column large-4"><%=contact.getPhone() %></span>
                    </dt>
                    <dd class="accordion-navigation clearfix">
                      <div id="panel<%=i+1%>b" class="content clearfix">
                        <ul class="column large-4">
                        <%
							if(contact.getDob() != null){
							%><li>DOB: <%= FORMAT_MMddYYYY.format(fmt_yyyyMMdd.parse(contact.getDob()))  %></li><%
									}
							else{
							%><li>DOB: null</li><%
							}
							%>
                          <li>AGE: <%=contact.getAge() %></li>
                        </ul>
                        <ul class="column large-18 right">
                          <li><address><p><%=contact.getAddress() %><br/><%=contact.getCity() %>, <%=contact.getState() %><br/><%=contact.getZip() %></p></address></li>
                        </ul>
                         <ul class="column large-18">
                         
                         <%
                         if( contact.getContacts()!=null )
                          for(Contact contactSub: contact.getContacts()){ %>
                           <li class="row">                           
                              <p><strong>Secondary Info:</strong></p>
			                  <p>
                              <span class="column large-5"><%=contactSub.getFirstName() %> <%=contactSub.getLastName() %></span>
                              <a class="column large-14 email" href="mailto:<%=contactSub.getEmail()%>"><i class="icon-mail"></i><%=contactSub.getEmail() %></a>
                              <span class="column large-5"><%=contactSub.getPhone()==null ? "" : contactSub.getPhone() %></span>
			                  </p>
                            </li>
                         <%} %>
                         
                         
                            <li class="row">
                              <p><strong>Achievements:</strong></p>
<p>
<%
boolean isFirstItem = true;
for(int y=0;y<infos.size();y++){
	if(infos.get(y).isAchievement() && infos.get(y).getYearPlanComponent().getType()== YearPlanComponentType.MEETING) {
		if (!isFirstItem) {
			out.println(",");
		}
		out.println(((MeetingE) infos.get(y).getYearPlanComponent()).getMeetingInfo().getName());
		isFirstItem = false;
	}
}
%>
</p>
                             </li>
                             <li class="row">
                              <p><strong>Meetings Attended:</strong></p>
				<p>
                              <% for(int y=0;y<infos.size();y++) {
                                  if(infos.get(y).isAttended()) {
                                    out.println(fmr_ddmm.format(sched_bm_inverse.get( infos.get(y).getYearPlanComponent())));
                                    out.println((infos.size() > 1 && infos.size()-1 !=y) ? "," : "");
                                  }
                              } %>
				</p>
                            </li>                          
                         </ul>
                      </div>
                    </dd>
                  </dl>
                </div>
              <%}%>
              
            </div>
          </div>
        </div>
      </dd>
    </dl>
  </div>
