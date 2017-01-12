<div class="row">
  <div class="column large-20 large-centered">
    <div class="row">
      <dl class="accordion-inner clearfix" data-accordion>
       
       
       <% for(int i=0; i<contacts.size(); i++) { 
    	    org.girlscouts.vtk.models.Contact contact = contacts.get(i);
    	    //java.util.List<ContactExtras> infos = contactUtil.girlAttendAchievement(user, troop, contact);
    	    java.util.List<ContactExtras> infos = contactsExtras.get(contact);
    	    %><p>Meetings attended by <%=contact.getFirstName() %> <%= contacts.size()%> </p><% 
    	    for(int y=0;y<infos.size();y++) {
                if(infos.get(y).isAttended()) {
                   %>
                   <ul class="inline-list">
                    <li><%=VtkUtil.formatDate(VtkUtil.FORMAT_Md,(java.util.Date)sched_bm_inverse.get( infos.get(y).getYearPlanComponent()))%></li>
                   </ul>
                  <% 
                }
            } 
       } %>
    
      </dl>
    </div>
  </div>
</div>