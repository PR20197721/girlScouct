<!-- PAGEID :: ./app/src/main/content/jcr_root/apps/girlscouts-vtk/components/vtk/mytroop_react.jsp -->



<%@ page import="com.google.common.collect .*"%>
<%
    java.util.List<org.girlscouts.vtk.models.Contact> contacts = null;
    if( isCachableContacts && session.getAttribute("vtk_cachable_contacts")!=null ) {
        contacts = (java.util.List<org.girlscouts.vtk.models.Contact>) session.getAttribute("vtk_cachable_contacts");
    }

    if( contacts==null ){
        contacts =  new org.girlscouts.vtk.auth.dao.SalesforceDAO(troopDAO).getContacts( user.getApiConfig(), troop.getSfTroopId() );
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
            sched = meetingUtil.getYearPlanSched(user, troop, troop.getYearPlan(), true, true);
        }catch(Exception e){e.printStackTrace();}

        BiMap sched_bm=   com.google.common.collect.HashBiMap().create();
        if( sched!=null)
        	HashBiMap.create(sched);
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
        <%
        }
    %>
  </div>
  <div class="column large-24 large-centered mytroop">

    <dl class="accordion" data-accordion>
      <dt data-target="panel1"><h3 class="on"><%=troop.getSfTroopName() %> INFO</h3>
      <%if(VtkUtil.hasPermission(troop, Permission.PERMISSION_SEND_EMAIL_ALL_TROOP_PARENTS_ID) ){ %>
         <a href='mailto:<%=emailTo%>'><i class="icon icon-mail"></i>email to <%= contacts.size() %> contacts</a>
      <%} %>
      </dt>
      <dd class="accordion-navigation">
        <div class="content active" id="panel1">
          <div class="row">
            <div class="column large-20 large-centered">
              <% for(int i=0; i<contacts.size(); i++) { 
                  org.girlscouts.vtk.models.Contact contact = contacts.get(i);
                  java.util.List<ContactExtras> infos = contactUtil.girlAttendAchievement(user, troop, contact);
                  //-Works !!! String _email= java.net.URLEncoder.encode(contact.getFirstName() +"<"+contact.getEmail() +">");
                String _email= contact.getFirstName().replace(" ", "&nbsp;") + java.net.URLEncoder.encode("<"+contact.getEmail() +">");
                
                  %>
                <div class="row">
                  <dl class="accordion-inner clearfix" data-accordion>
                    <dt data-target="panel<%=i+1%>b" class="clearfix">
                      <span class="name column large-10"><%=contact.getFirstName() %></span>
                        <a class="column large-10 email" href="mailto:<%=_email%>">
                        <i class="icon icon-mail"></i><%=contact.getEmail() %>
                      </a>
                      <span class="column large-4"><%=contact.getPhone() %></span>
                    </dt>
                    <dd class="accordion-navigation clearfix">
                      <div id="panel<%=i+1%>b" class="content clearfix">

                         <ul class="column large-18">
                         
                        
                         
                         
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
        out.println( ((MeetingE) infos.get(y).getYearPlanComponent()).getMeetingInfo().getName());
        
       %> <img src="/content/dam/girlscouts-vtk/local/icon/meetings/<%= ((MeetingE) infos.get(y).getYearPlanComponent()).getMeetingInfo().getId()%>.png"/> <% 

        
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
                                    out.println(FORMAT_Md.format(sched_bm_inverse.get( infos.get(y).getYearPlanComponent())));
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
