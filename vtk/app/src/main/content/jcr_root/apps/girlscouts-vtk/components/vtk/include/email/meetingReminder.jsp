<%@ page import="java.util.Calendar,
    java.util.Date,
    java.util.ArrayList,
    org.girlscouts.vtk.utils.GSUtils" %>
<!-- /apps/girlscouts-vtk/components/vtk/include/email/meetingReminder.jsp -->
<div class="content clearfix">
    <%
        MeetingE _meeting = planView.getMeeting();
        Calendar c = Calendar.getInstance();
        c.setTime(planView.getSearchDate());
        c.add(Calendar.MINUTE, planView.getMeetingLength());
        Date meetingEndDate = c.getTime();
        Date searchDate = planView.getSearchDate();
    %>
    <h4>Reminder Meeting #<%=planView.getMeetingCount()%>
        <%= VtkUtil.formatDate(VtkUtil.FORMAT_MEETING_REMINDER, searchDate) %>
        - <%=VtkUtil.formatDate(VtkUtil.FORMAT_hhmm_AMPM, meetingEndDate)%>
    </h4>
    <p class="sent">Sent: None</p><!--TODO add the date after email is sent-->
    <h6>Address List</h6>
    <ul class="small-block-grid-3">
        <li style="width:100%">
            <input type="checkbox" id="email_to_gp" checked/>
            <label for="email_to_gp"><p>Parents / Caregivers</p></label>
        </li>
        <!-- <li>
	    <input type="checkbox" id="email_to_sf" checked />
	    <label for="email_to_sf"><p>Self</p></label>
	  </li> -->
        <li style="display:none;">
            <input type="checkbox" id="email_to_tv"/>
            <label for="email_to_tv"><p>Troop Volunteers</p></label>
        </li>
    </ul>
    <section class="clearfix">
        <label for="email_to_cc">Enter your own:</label>
        <input type="email" id="email_to_cc" placeholder="enter email addresses separated by semicolons"/>
    </section>
    <h6>Compose Email</h6>
    <section class="clearfix">
        <label for="email_subj">Subject:</label>
        <input type="text" id="email_subj"
               value="Reminder <%=selectedTroop.getGradeLevel() %> Meeting #<%=planView.getMeetingCount()%> <%= VtkUtil.formatDate(VtkUtil.FORMAT_MEETING_REMINDER, searchDate) %> - <%= VtkUtil.formatDate(VtkUtil.FORMAT_hhmm_AMPM,meetingEndDate)%>"/>
    </section>
    <div style="background-color:yellow;"></div>
    <textarea id="email_htm" name="textarea" class="jqte-test" rows="25" cols="25">
 		<%if (_meeting.getEmlTemplate() != null) {%>
		<%= _meeting.getEmlTemplate()%>  
		<%} else { %>
		<p>Hello Girl Scout Families,</p>
		<br/><p>Here are the details of our next meeting:</p>
		<table> 
			<tr><th>Date:</th>
				<td><%= VtkUtil.formatDate(VtkUtil.FORMAT_MEETING_REMINDER, searchDate)%> - <%= VtkUtil.formatDate(VtkUtil.FORMAT_hhmm_AMPM, meetingEndDate)%></td>
			</tr>
			<tr><th>Location:</th>
				<td><%
                    if (_meeting.getLocationRef() != null && selectedTroop.getYearPlan().getLocations() != null) {
                        for (int k = 0; k < selectedTroop.getYearPlan().getLocations().size(); k++) {
                            if (selectedTroop.getYearPlan().getLocations().get(k).getPath().equals(_meeting.getLocationRef())) {%>
						<%=selectedTroop.getYearPlan().getLocations().get(k).getName() %>
						<br/><%=selectedTroop.getYearPlan().getLocations().get(k).getAddress() %>
						<% }
                        }
                        }
                        %></td>
			</tr>
			<tr><th>Topic:</th>
                <td><%= _meeting.getMeetingInfo() != null ?_meeting.getMeetingInfo().getName():"" %></td>
			</tr>
		</table>
		<%
            JcrCollectionHoldString eInvite = _meeting.getMeetingInfo() != null ? _meeting.getMeetingInfo().getMeetingInfo().get("email invite"): null;
            if (eInvite != null && eInvite.getStr() != null && !eInvite.getStr().trim().isEmpty()) {
        %>
        <%=_meeting.getMeetingInfo() != null ? _meeting.getMeetingInfo().getMeetingInfo().get("email invite").getStr():null %>
		<% } else {%>
        <%=_meeting.getMeetingInfo() != null ? _meeting.getMeetingInfo().getMeetingInfo().get("overview").getStr():null %>
		<% } %>
		<br/><p>If you have any questions, or want to participate in this meeting, please contact me at 
		<%if (apiConfig.getUser().getPhone() != null)%><%=apiConfig.getUser().getPhone() %>
		<%if (apiConfig.getUser().getMobilePhone() != null)%><%=apiConfig.getUser().getMobilePhone() %>
		<%if (apiConfig.getUser().getHomePhone() != null)%><%=apiConfig.getUser().getHomePhone() %>
		<%if (apiConfig.getUser().getAssistantPhone() != null)%><%=apiConfig.getUser().getAssistantPhone() %>
		</p>
		<br/><p>Thank you for supporting your <%=selectedTroop.getGradeLevel() %>,</p>

		<br/><p><%if (apiConfig.getUser().getName() != null)%><%=apiConfig.getUser().getName() %></p>
		<p><%=selectedTroop.getTroopName() %></p>
		<br/><br/>
		<div id="aidLinks">
			<p class="hide">Aids Included: </p>
		</div>
		<br/><br/>
		<div id="formLinks">
			<p class="hide">Form(s) Required</p>
		</div>
		<% }%>
	</textarea>
    <dl class="accordion resources" data-accordion>
        <dt data-target="panel1"><h6 class="off">Include Meeting Aid</h6></dt>
        <dd class="accordion-navigation">
            <div class="content" id="panel1">
                <ul class="small-block-grid-2"><%
                    List<Asset> aidTags = planView.getAidTags() != null ? planView.getAidTags() : new ArrayList<>();
                    String section = "meeting-aids";
                    String linkParPath = "/content/vtkcontent/en/resources/volunteer-aids/vtk-" + section + "-links/_jcr_content/content/middle/par";
                    aidTags.addAll(meetingUtil.getLinkAidsForMeeting(linkParPath, _meeting.getMeetingInfo().getId(), section));
                    for (int i = 0; i < aidTags.size(); i++) {
                        Asset aidTag = aidTags.get(i);
                        if (!section.equals(aidTag.getSection())) continue;
                        String refId = aidTag.getRefId();
                        String title = aidTag.getTitle();
                        String ext = aidTag.getDocType() != null ? aidTag.getDocType() : GSUtils.getDocExtensionFromString(refId);
                        %><li>
                            <span class="name icon <%=ext%>">
                                <a href="<%=refId%>"target="_blank"><%=title%></a>
                            </span>
                        </li>
                        <li>
                            <a class="add-links" href="#nogo" title="add" onclick="addAidLink('<%=refId%>','<%=title%>','<%=_meeting.getUid()%>')">
                                <i class="icon-button-circle-plus"></i>
                            </a>
                        </li><%
                    }
                    if (aidTags.isEmpty()) {
                        %><li>No Meeting Aids found</li><%
                    }
                %></ul>
            </div>
        </dd>
    </dl>
    <dl class="accordion resources" data-accordion>
        <dt data-target="panel2"><h6 class="off">Include Additional Resource</h6></dt>
        <dd class="accordion-navigation">
            <div class="content" id="panel2">
                <ul class="small-block-grid-2"><%
                    List<Asset> resourceTags = planView.getAidTags() != null ? planView.getAidTags() : new ArrayList<>();
                    String resourceSection = "additional-resources";
                    String resourceLinkParPath = "/content/vtkcontent/en/resources/volunteer-aids/vtk-" + resourceSection + "-links/_jcr_content/content/middle/par";
                    resourceTags.addAll(meetingUtil.getLinkAidsForMeeting(resourceLinkParPath, _meeting.getMeetingInfo().getId(), resourceSection));
                    for (int i = 0; i < resourceTags.size(); i++) {
                        Asset aidTag = resourceTags.get(i);
                        if (!resourceSection.equals(aidTag.getSection())) continue;
                        String refId = aidTag.getRefId();
                        String title = aidTag.getTitle();
                        String ext = aidTag.getDocType() != null ? aidTag.getDocType() : GSUtils.getDocExtensionFromString(refId);
                        %><li>
                            <span class="name icon <%=ext%>">
                                <a href="<%=refId%>"target="_blank"><%=title%></a>
                            </span>
                        </li>
                       <li>
                           <a class="add-links" href="#nogo" title="add" onclick="addAidLink('<%=refId%>','<%=title%>','<%=_meeting.getUid()%>')">
                               <i class="icon-button-circle-plus"></i>
                           </a>
                       </li><%
                    }
                    if (resourceTags.isEmpty()) {
                        %><li>No Additional Resources found</li><%
                    }
                %></ul>
            </div>
        </dd>
    </dl>
    <dl class="accordion" data-accordion>
        <dt data-target="panel3"><h6>Include Form Link</h6></dt>
        <dd class="accordion-navigation">
            <div class="content" id="panel3"><%
                String councilId = null;
                if (apiConfig != null) {
                    if (apiConfig.getUser().getTroops().size() > 0) {
                        councilId = apiConfig.getUser().getTroops().get(0).getCouncilCode();
                    }
                }
                CouncilMapper mapper = sling.getService(CouncilMapper.class);
                String branch = mapper.getCouncilBranch(councilId);
                branch = branch.replace("/content/", "");
                //For testing on local set default council since gateway doesn't have tags
                if (branch == null || branch.isEmpty() || branch.equals("gateway")) {
                    branch = "gsnetx";
                }
                org.girlscouts.vtk.utils.DocumentUtil docUtil = new org.girlscouts.vtk.utils.DocumentUtil(resourceResolver, sling.getService(com.day.cq.tagging.JcrTagManagerFactory.class), branch);
                try {
                    int panelCount = 1;
                    DocumentCategory tempCategory = docUtil.getNextCategory();
                    while (tempCategory != null) {
                        String name = tempCategory.getName();
            %>
                <div class="row">
                    <dl class="accordion-inner clearfix" data-accordion>
                        <dt data-target="panel<%=panelCount%>b" class="clearfix">
                            <span class="name"><%=name %></span>
                        </dt>
                        <dd>
                            <div id="panel<%=panelCount%>b" class="content">
                                <ul class="small-block-grid-2"><%
                                    Document tempDoc = tempCategory.getNextDocument();
                                    while (tempDoc != null) {%>
                                    <li><a href="<%=tempDoc.getPath()%>"
                                           target="_blank"><span><%=tempDoc.getTitle()%></span></a></li>
                                    <li><a class="add-links" href="#nogo" title="add"
                                           onclick="addFormLink('<%=tempDoc.getPath()%>', '<%=tempDoc.getTitle()%>', 'panel<%=panelCount%>b')"><i
                                            class="icon-button-circle-plus"></i></a></li>
                                    <%
                                            tempDoc = tempCategory.getNextDocument();
                                        }
                                        tempCategory = docUtil.getNextCategory();%>
                                </ul>
                            </div>
                        </dd>
                    </dl>
                </div>
                <%
                        panelCount++;
                    }
                } catch (Exception e) {
                %><h1>ERROR: Tags Or Documents Not Configured Properly</h1><%
                    }%>
            </div>
        </dd>
    </dl>
    <div class="right clearfix">
        <input type="button" value="Send email" class="button btn" onclick="this.disabled=true; sendEmail();"/>
    </div>
    <div id="added">
        <p>Added to email.</p>
    </div>
    <div id="after-sent">
        <p>Email(s) sent.</p>
    </div>
</div>
<!--end of content-->
<script>
    var template;
    $(document).ready(function () {
        $('#added').dialog({autoOpen: false, zIndex: 200});
        $('#after-sent').dialog({autoOpen: false, zIndex: 200});
        $(".jqte-test").jqte({
            "source": false,
            "rule": false,
            "sub": false,
            "strike": false,
            "fsizes": ['10', '12', '14', '16', '18', '20', '22', '24', '28', '32']
        });
        getTemplate();
    });

    function getTemplate() {
        $('#email_htm').val(removeIndentions($('.jqte_editor').html()));
        template = $('#email_htm').val();

    }

    function addFormLink(link, formname, categoryId) {
        var url = window.location.href;
        var arr = url.split("/");
        var host = link.startsWith("/content") ? arr[0] + "//" + arr[2] : '';
        $('.jqte_editor #formLinks').append('<li><a href="' + host + link + '" target="_blank">' + formname + '</a></li>');
        $('.jqte_editor #formLinks p.hide').removeClass();
        $("dt[data-target='" + categoryId + "'] span").removeClass('on');
        $('.accordion #' + categoryId).slideToggle('slow');
        $('.ui-dialog-titlebar').css('display', 'none');
        $('#added').dialog('open');
        $('.ui-dialog').css('z-index', 300);
        setTimeout(function () {
            $('#added').dialog('close');
        }, 1000);

    }

    function addAidLink(refId, title, uid) {
        var url = window.location.href;
        var arr = url.split("/");
        var host = refId.startsWith("/content") ? arr[0] + "//" + arr[2] : '';
        $('.jqte_editor #aidLinks').append('<li><a href="' + host + refId + '" target="_blank">' + title + '</a></li>');
        $('.jqte_editor #aidLinks p.hide').removeClass();
        $('#added').dialog('open');
        $('.ui-dialog-titlebar').css('display', 'none');
        $('.ui-dialog').css('z-index', 300);
        setTimeout(function () {
            $('#added').dialog('close');
        }, 1000);
        //addAidToEmail(refId,title,uid);

    }

    function sendEmail() {
        if (validate()) {
            previewMeetingReminderEmail('<%=_meeting.getUid()%>', template);
        }
    }

    function validate() {
        //allow leading and trailing spaces for every email addr
        var emailReg = /^((\ *[\w-\.]+@([\w-]+\.)+[\w-]{2,4}\ *)\;?)+$/;
        var emailAddr = $('#email_to_cc').val();
        var subject = $('#email_subj').val();
        var body = $('#email_htm').val();

        if (emailAddr.length) {
            if (!emailReg.test(emailAddr)) {
                //$('#email_to_cc') label turn red or input background turn red
                $('.scroll').scrollTop($('#email_to_cc').position().top);
                alert("Please enter valid email address(es).");
                return false;
            }
        } else if (!$("input:checkbox:checked").length) {
            $('.scroll').scrollTop($('#email_to_cc').position().top);
            alert("Address list can not be empty.");
            return false;
        }
        if (!subject.length) {
            $('.scroll').scrollTop($('#email_subj').position().top);
            alert("Subject can not be empty.");
            return false;
        }
        if (!body.length) {
            $('.scroll').scrollTop($('#email_htm').position().top);
            alert("Email body can not be empty.");
            return false;
        }
        return true;

    }

    function removeIndentions(x) {
        return x.replace(/^\s+|\s+$/gim, '');

    }

    $("#modal-meeting-reminder").on('change', 'input', function (event) {
        $('input[type="button"]').attr('disabled', false);
    });
</script>