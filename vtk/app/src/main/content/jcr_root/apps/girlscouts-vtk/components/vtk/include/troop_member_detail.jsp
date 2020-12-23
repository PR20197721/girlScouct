<div class="row">
    <div class="column large-20 large-centered">
        <%
            if (contacts != null) {
                for (int i = 0; i < contacts.size(); i++) {
                    Contact contact = contacts.get(i);
                    if (!role.equals(contact.getRole())) {
                        %><!-- skipped <%=contact.getContactId()%> because role <%=contact.getRole()%> != <%=role%>--><%
                        continue;
                    }
                    List<ContactExtras> infos = contactsExtras.get(contact);
                    String _email = "";
                    if (contact.getFirstName() != null && contact.getEmail() != null) {
                        _email = (contact.getFirstName() != null ? contact.getFirstName().replace(" ", "%20") : "") + java.net.URLEncoder.encode("<" + contact.getEmail() + ">");
                    }

                    if (!(VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_CAN_VIEW_MEMBER_DETAIL_TROOP_ID) || VtkUtil.isUserCaregiverForContact(user, contact))) {
                        %><!-- skipped <%=contact.getContactId()%> because caregiver= <%=VtkUtil.isUserCaregiverForContact(user, contact)%> --><%
                        continue;
                    }
                    PrimaryGuardian caregiver = contact.getPrimaryGuardian();
                    if(caregiver != null){
                        %>
                        <div class="row">
                            <dl class="accordion-inner clearfix" data-accordion>
                                <dt data-target="panel<%=i+1%>b" class="clearfix">
                                    <span class="name column large-6"><%=contact.getFirstName() %> <%=contact.getLastName() %></span>
                                    <span class="name column large-4"
                                          style="word-wrap:break-word;"><%= caregiver == null ? "" : ((caregiver.getFirstName() == null ? "" : caregiver.getFirstName()) + " " + (caregiver.getLastName() == null ? "" : caregiver.getLastName()))%></span>
                                    <%if (contact.getEmail() != null && VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_SEND_EMAIL_ALL_TROOP_PARENTS_ID)) { %>
                                    <a class="column large-10 email" href="mailto:<%=_email%>">
                                        <i class="icon-mail" style="word-wrap:break-word;"></i><%=contact.getEmail() %>
                                    </a>
                                    <% } %>
                                    <%
                                        String phone =contact.getPhone();
                                        if(phone != null){
                                            phone = phone.replaceAll("[^\\d.]", "");
                                            phone = phone.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3");
                                        }else{
                                            phone = "";
                                        }
                                    %>
                                    <span class="column large-4"><%=phone%></span>
                                </dt>
                                <%@include file='troop_child_detail.jsp' %>
                            </dl>
                        </div>
                        <%
                    }
                }
            }
        %>
    </div>
</div>
