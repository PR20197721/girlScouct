<dd class="accordion-navigation clearfix">
    <div id="panel<%=i+1%>b" class="content" style="padding:20px">
        <div class="screenboard-white row">
            <div class="row">
                <div class="column small-24 medium-8">
                    Membership: <%=contact.getMembershipYear() != null ?  contact.getMembershipYear() : "N/A"%><br>
                    Role: <%= contact.getRole() %>
                </div>
                <div class="column  small-24 medium-8">
                    Membership Years: Girl: <%=contact.getMembershipYear_girl() %> Adult: <%=contact.getMembershipYear_adult() %><br>
                </div>
                <div class="column small-24 medium-8">
                    <input type="checkbox" <%=contact.isEmailOptIn() ? " CHECKED " : "" %> disabled> Email Opt In<br>
                    <input type="checkbox" <%=contact.isTxtOptIn() ? " CHECKED " : "" %> disabled> Text Opt In
                </div>
            </div>
            <div style="margin:15px 0"></div>
            <div class="row">
                <%
                    String volunteerPhone = contact.getPhone();
                    if(volunteerPhone != null){
                        volunteerPhone = volunteerPhone.replaceAll("[^\\d.]", "");
                        volunteerPhone = volunteerPhone.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3");
                    }else{
                        volunteerPhone = "";
                    }
                %>
                <div class="column small-24"> Phone: <%=volunteerPhone %>
                </div>
            </div>
            <div class="row">
                <div class="column small-24"> Email: <%=contact.getEmail() %>
                </div>
            </div>
            <div style="margin:15px 0"></div>
            <div class="row">
                <div class="column small-24"> Home
                    Address: <%=contact.getAddress() == null ? "" : contact.getAddress()%>
                    <%=contact.getAddress1() == null ? "" : contact.getAddress1()%>  <%=contact.getSuite() == null ? "" : contact.getSuite()%>
                    <%=contact.getCity() == null ? "" : contact.getCity()%> <%=contact.getState() == null ? "" : contact.getState() %>
                    <%=contact.getZip() == null ? "" : contact.getZip() %> <%=contact.getCountry() == null ? "" : contact.getCountry()%>
                </div>
            </div>
            <div class="row">
                <div style="float:right">
                    <%
                        boolean isIRM = false;
                        if(selectedTroop.getParticipationCode() != null && "IRM".equals(selectedTroop.getParticipationCode())){
                            isIRM = true;
                        }
                        if (apiConfig != null && !apiConfig.isDemoUser()) {
                            if (selectedTroop.getRole().equals("PA")) {
                                %>
                                <%if (!isIRM && isRenewMembership(contact.getMembershipYear())) {%>
                                <a href="<%=configManager.getConfig("communityUrl")%>/Membership_Renewal" class="button">RENEW NOW</a>
                                <%}%>
                                <a href="<%=configManager.getConfig("communityUrl")%>/Membership_Renewal" class="button">UPDATE CONTACT INFO</a>
                                <%
                            } else {
                                if (!isIRM && isRenewMembership(contact.getMembershipYear())) {%>
                                    <a href="<%=configManager.getConfig("communityUrl")%>/Membership_Troop_Renewal" class="button">RENEW NOW</a>
                                <%}%>
                                <a href="<%=configManager.getConfig("communityUrl")%>/Membership_Troop_Renewal" class="button">UPDATE CONTACT INFO</a>
                                <%
                            }
                        } else {
                            %>
                            <a href="javascript:void(0)" class="button" disabled=true>UPDATE CONTACT INFO*</a>
                            <%
                        }
                    %>
                </div>
            </div>
        </div>
    </div>
</dd>


