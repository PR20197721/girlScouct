<div class="row">
  <div class="column large-20 large-centered">
    <% for(int i=0; i<contacts.size(); i++) { 
      org.girlscouts.vtk.models.Contact contact = contacts.get(i);
      java.util.List<ContactExtras> infos = contactUtil.girlAttendAchievement(user, troop, contact);
       String _email= contact.getFirstName().replace(" ", "&nbsp;") + java.net.URLEncoder.encode("<"+contact.getEmail() +">");     
    %>
    <div class="row">
      <dl class="accordion-inner clearfix" data-accordion>
        <dt data-target="panel<%=i+1%>b" class="clearfix">
          <span class="name column large-6"><%=contact.getFirstName() %></span>
          <span class="name column large-4"><%=VtkUtil.getSubContact( contact, 1) %></span>
          <a class="column large-10 email" href="mailto:<%=_email%>">
          <i class="icon-mail"></i><%=contact.getEmail() %>
          </a>
          <span class="column large-4"><%=contact.getPhone() %></span>
        </dt>       
        <%if(hasPermission(troop, Permission.PERMISSION_canViewMemberdDetail_TROOP_ID)){ %>
          <%@include file='troop_child_detail.jsp' %>
        <%} %>
      </dl>
    </div>
  <%}%>
  </div>
</div>