<dd class="accordion-navigation clearfix">
    <div id="panel<%=i+1%>b" class="content clearfix" style="display:none">
        <ul class="column large-4">
            <li>DOB: <%=contact.getDob() != null ? contact.getDob() : "N/A"%></li>
            <li>AGE: <%=contact.getAge() != null ? contact.getAge() : "N/A" %></li>
        </ul>
        <%if (contact.getAddress() != null) {%>
        <ul class="column large-18 right">
            <li>
                <address><p>
                    <%=contact.getAddress() == null ? "" : contact.getAddress() %>
                    <br/><%=contact.getCity() == null ? "" : contact.getCity() %>
                    <%=contact.getState() == null ? "" : (", " + contact.getState()) %>
                    <br/><%=contact.getZip() == null ? "" : contact.getZip() %>
                </p></address>
            </li>
        </ul>
        <%}%>
        <ul class="column large-18">
            <%
                if (contact.getContacts() != null) {
                    for (Contact contactSub : contact.getContacts()) {
                        if (!VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_CAN_VIEW_OWN_CHILD_DETAIL_TROOP_ID)) {%>
            <li class="row">
                <p><strong> Secondary Info:</strong></p>
                <div class="row">
                    <span class="column large-5" style="word-wrap:break-word;"><%=contactSub.getFirstName() != null? contactSub.getFirstName():""%> <%=contactSub.getLastName() != null ? contactSub.getLastName():""%></span>
                        <%if (VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_SEND_EMAIL_ALL_TROOP_PARENTS_ID)) { %>
                            <a class="column large-14 email" href="mailto:<%=contactSub.getEmail() != null ? contactSub.getEmail():""%>"><i class="icon-mail"></i><%=contactSub.getEmail()!= null ? contactSub.getEmail():""%></a>
                        <%} %>
                    <span class="column large-5"><%=contactSub.getPhone() == null ? "" : contactSub.getPhone() %></span>
                </div >
            </li >
                        <%} %>
            <%}
        } %>
            <li class="row">
                <p><strong>Achievements:</strong></p>
                <p>
                    <%
                        boolean isFirstItem = true;
                        for (int y = 0; y < infos.size(); y++) {
                            if (infos.get(y).isAchievement() && infos.get(y).getYearPlanComponent().getType() == YearPlanComponentType.MEETING) {
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
            <%if (selectedTroop.getParticipationCode() != null && !"IRM".equals(selectedTroop.getParticipationCode())) {%>
                <li class="row">
                    <p><strong> Attendance:</strong></p>
                    <p>
                        <% for (int y = 0; y < infos.size(); y++) {
                            if (infos.get(y).isAttended()) {
                                out.println(VtkUtil.formatDate(VtkUtil.FORMAT_Md, (java.util.Date) sched_bm_inverse.get(infos.get(y).getYearPlanComponent())));
                                out.println((infos.size() > 1 && infos.size() - 1 != y) ? "," : "");
                            }
                        } %>
                    </p>
                </li>
            <%}%>
            <li class="row">
                <div style="float:right">
                    <%
                        boolean isIRM = false;
                        if(selectedTroop.getParticipationCode() != null && "IRM".equals(selectedTroop.getParticipationCode())){
                            isIRM = true;
                        }
                        if (selectedTroop.getRole().equals("PA")) {
                            %>
                            <%if (!isIRM && VtkUtil.isRenewMembership(contact.getMembershipYear())) {%>
                                <a href="<%=configManager.getConfig("renewUrl")%>" class="button">RENEW NOW</a>
                            <%}%>
                                <a href="<%=configManager.getConfig("renewUrl")%>" class="button">UPDATE CONTACT INFO</a>
                            <%
                        } else {
                            %>
                            <%if (!isIRM && VtkUtil.isRenewMembership(contact.getMembershipYear())) {%>
                                <a href="<%=configManager.getConfig("renewUrl")%>" class="button">RENEW NOW</a>
                            <%}%>
                                <a href="<%=configManager.getConfig("renewUrl")%>" class="button">UPDATE CONTACT INFO</a>
                            <%
                        }
                    %>
                </div>
            </li>
        </ul>
    </div>
</dd>