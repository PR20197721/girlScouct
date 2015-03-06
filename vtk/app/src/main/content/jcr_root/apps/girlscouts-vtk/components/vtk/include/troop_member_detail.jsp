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
                    
                    <%@include file='troop_child_detail.jsp' %>
                    
                  </dl>
                </div>
              <%}%>
              
            </div>
          </div>