<%@ page
        import="org.girlscouts.vtk.auth.permission.Permission,
                org.girlscouts.vtk.models.Activity,
                org.girlscouts.vtk.models.Troop, java.net.URLEncoder" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="session.jsp" %>
<div id="vtkTabNav">
    <%
        String activeTab = request.getParameter("activeTab");
        if (activeTab == null) {
            return;
        }
        boolean isArchived = !user.getCurrentYear().equals(String.valueOf(VtkUtil.getCurrentGSYear()));
        org.girlscouts.vtk.models.PlanView planView = null;
        if (isArchived) {
            Troop archivedTroop = (Troop) session.getAttribute("VTK_archived_troop");
            planView = meetingUtil.planView(user, archivedTroop, request);
        } else {
            planView = meetingUtil.planView(user, selectedTroop, request);
        }
        boolean isParent = false;
        if (selectedTroop.getRole() != null && selectedTroop.getRole().equals("PA")) {
            isParent = true;
        }
        boolean isFinanceAdmin = false;
        if (selectedTroop.getRole() != null && selectedTroop.getRole().equals("FA")) {
            isFinanceAdmin = true;
        }
        boolean isTroopLeader = false;
        if (selectedTroop.getRole() != null && selectedTroop.getRole().equals("DP") || "IRM".equals(selectedTroop.getParticipationCode())) {
            isTroopLeader = true;
        }
        String vtk_cache_uri = "/content/girlscouts-vtk/en";
        if ((isParent || isFinanceAdmin) && !"IRM".equals(selectedTroop.getParticipationCode())) {
            vtk_cache_uri = "/myvtk/" + councilMapper.getCouncilName(selectedTroop.getSfCouncil());

        }
        boolean financeTabEnabled = !VtkUtil.getFinanceTabDisabledCouncils().contains(selectedTroop.getCouncilCode()) && !selectedTroop.getIsIRM();
    %>
    <div id="troop" class="row">
        <div class="columns large-7 medium-9 right">
            <%
                if (userTroops != null && userTroops.size() > 1) {
                    sessionlog.debug("vtk_prefTroop: "+selectedTroop.toString());
                    Cookie cookie = new Cookie("vtk_prefTroop", URLEncoder.encode(selectedTroop.getGradeLevel()));
                    cookie.setMaxAge(-1);
                    response.addCookie(cookie);
            %>
            <select id="reloginid" onchange="relogin()" <%=!user.getCurrentYear().equals(VtkUtil.getCurrentGSYear() + "") ? "disabled" : "" %>>
                <%
                    if (!user.getCurrentYear().equals(VtkUtil.getCurrentGSYear() + "")) {
                        String troopGradeLevel = " : " + selectedTroop.getGradeLevel();
                        if (selectedTroop.getParticipationCode() != null && "IRM".equals(selectedTroop.getParticipationCode())) {
                            troopGradeLevel = "";
                        }
                %>
                <option value="" SELECTED>Viewing ARCHIVED <%=selectedTroop.getTroopName()%><%=troopGradeLevel%>
                </option>
                <%
                } else {
                    for (Troop userTroop : userTroops) {
                        String troopGradeLevel = " : " + userTroop.getGradeLevel();
                        if (userTroop.getParticipationCode() != null && "IRM".equals(userTroop.getParticipationCode())) {
                            troopGradeLevel = "";
                        }
                %>
                <option value="<%=userTroop.getHash()%>" <%=selectedTroop.getHash().equals(userTroop.getHash()) ? "selected" : ""%>><%=userTroop.getTroopName()%><%=troopGradeLevel%>
                </option>
                <%
                        }
                    }%>
            </select>
            <%
            } else if (userTroops != null && userTroops.size() == 1) {
                if (!user.getCurrentYear().equals(VtkUtil.getCurrentGSYear() + "")) {
            %><input type="hidden" id="reloginid" name="" value=""/><%
        } else {
        %><input type="hidden" id="reloginid" name="reloginid" value="<%=userTroops.get(0).getHash()%>"/><%
                }//edn else
            }%>
        </div>
        <div class="columns large-4 medium-4"></div>
    </div>
    <div class="hide-for-print tab-wrapper <%= (user.getCurrentYear().equals( VtkUtil.getCurrentGSYear()+"") ) ? "vtk-currentYear" : "vtk-pastYear" %> row">
        <div class="columns large-22 large-centered small-24">
            <dl class="tabs show-for-large-up">
                <% if (user.getCurrentYear().equals(VtkUtil.getCurrentGSYear() + "") && VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_VIEW_TROOP_ID)) { %>
                <dd <%= "myTroop".equals(activeTab) ? "class='active'" : "" %>>
                    <a href="/content/girlscouts-vtk/en/vtk.myTroop.html">My Troop</a>
                </dd>
                <%} %>
                <%
                    if (isTroopLeader && VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_EDIT_YEARPLAN_ID) && (user.getCurrentYear().equals(VtkUtil.getCurrentGSYear() + ""))) {
                %>
                <dd <%= "explore".equals(activeTab) ? "class='active'" : "class=''" %>>
                    <a href="<%=vtk_cache_uri %>/vtk.explore.html">Explore</a>
                </dd>
                <%}%>
                <% if (VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_VIEW_YEARPLAN_ID)) { %>
                <dd <%= "plan".equals(activeTab) ? "class='active'" : "class=''" %>>
                    <%if (!isParent && !isFinanceAdmin && selectedTroop.getYearPlan() == null) { %>
                    <a href="javascript:void(0)"
                       onclick="modalAlert.alert('YEAR PLAN & MEETING PLAN','<p>You must first make a selection on the Explore tab, in order to view a Year Plan or meeting</p>')">
                        <%= (user.getCurrentYear().equals(VtkUtil.getCurrentGSYear() + "")) ? "Year Plan" : "Past Year Plans"%>
                    </a>
                    <%} else {%>
                    <a href="<%=vtk_cache_uri %>/vtk.html">
                        <%= (user.getCurrentYear().equals(VtkUtil.getCurrentGSYear() + "")) ? "Year Plan" : "Past Year Plans"%>
                    </a>
                    <%}%>
                </dd>
                <% } %>
                <% if (VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_VIEW_MEETING_ID)) { %>
                <dd <%= "planView".equals(activeTab) ? "class='active'" : "class=''" %>>
                    <%
                        if (selectedTroop.getYearPlan() != null && (selectedTroop.getYearPlan().getActivities() == null || selectedTroop.getYearPlan().getActivities().size() <= 0) && (selectedTroop.getYearPlan().getMeetingEvents() == null || selectedTroop.getYearPlan().getMeetingEvents().size() <= 0)) {
                    %>
                    <a href='#'
                       onClick='alert("Content only available for meetings. Add at least one meeting to the Year Plan to access this tab.")'>
                        <%= (user.getCurrentYear().equals(VtkUtil.getCurrentGSYear() + "")) ? "Meeting Plan" : "Past Meeting Plans"%>
                    </a>
                    <%
                    } else {
                        String emptyYearPlanPopup = "\"YEAR PLAN & MEETING PLAN\",\"You must first make a selection on the Explore tab, in order to view a Year Plan or meeting\"";
                        if (isParent || isFinanceAdmin) {
                            emptyYearPlanPopup = "\"YEAR PLAN & MEETING PLAN\",\"Your leader must first set up a year plan before you can view meetings.\"";
                        }
                    %>
                    <a
                            <%= selectedTroop.getYearPlan() != null ? "href='/content/girlscouts-vtk/en/vtk.details.html'" : "href='#' onClick='modalAlert.alert(" + emptyYearPlanPopup + ")'"  %>>
                        <%= (user.getCurrentYear().equals(VtkUtil.getCurrentGSYear() + "")) ? "Meeting Plan" : "Past Meeting Plans"%>
                    </a>
                    <%} %>
                </dd>
                <% } %>
                <%if (user.getCurrentYear().equals(VtkUtil.getCurrentGSYear() + "")) {%>
                <dd <%= "resource".equals(activeTab) ? "class='active'" : "" %>>
                    <a href="<%=vtk_cache_uri%>/vtk.resource.html">Resources</a>
                </dd>
                <%}%>
                <% if (VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_EDIT_MILESTONE_ID)) { %>
                <dd <%= "milestones".equals(activeTab) ? "class='active'" : "" %>>
                    <a href="/content/girlscouts-vtk/en/vtk.admin_milestones.html">Milestones</a>
                </dd>
                <% } %>
                <% if (VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_VIEW_REPORT_ID)) { %>
                <dd <%= "reports".equals(activeTab) ? "class='active'" : "" %>>
                    <a href="/content/girlscouts-vtk/en/vtk.admin_reports.html">Reports</a>
                </dd>
                <% } %>
                <% if (financeTabEnabled) { %>
                <dd
                        <%=  ("finances".equals(activeTab) || "financesadmin".equals(activeTab)) ? "class='active'" : "" %>>
                    <a href="/content/girlscouts-vtk/en/vtk.finances.html">Finances</a>
                </dd>
                <% } %>
            </dl>
            <div class="dropdown hide-for-print hide-for-large-up">
                <a id="vtk-main-menu-button"
                   onclick="$('#vtk-main-menu').slideToggle('slow');$(this).toggleClass('collapse')"
                   class="expand">Menu</a>
                <ul id="vtk-main-menu" class="hide-for-print" style="display: none;">
                    <% if (VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_VIEW_TROOP_ID)) { %>
                    <li class='has-dropdown<%= ("myTroop".equals(activeTab)) ? " active" : " " %>'>
                        <%
                            if (user.getCurrentYear().equals(VtkUtil.getCurrentGSYear() + "") && selectedTroop.getYearPlan() != null && (selectedTroop.getYearPlan().getMeetingEvents() != null && selectedTroop.getYearPlan().getMeetingEvents().size() > 0)) {
                        %>
                        <a href="/content/girlscouts-vtk/en/vtk.myTroop.html">My Troop</a>
                        <%} else { %>
                        <a href="#" onclick="alert('There is no Year Plan set up at this time.')">My Troop</a>
                        <%} %>
                        <ul class="dropdown">
                            <% if ("myTroop".equals(activeTab) && VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_EDIT_TROOP_IMG_ID)) { %>
                            <li><a data-reveal-id="modal_upload_image" title="update photo" href="#">add/change a photo
                                of your
                                troop</a></li>
                            <li><a title="remove photo" href="#" onclick="rmTroopInfo()">remove
                                troop photo</a></li>
                            <% } %>
                        </ul>
                    </li>
                    <%}%>
                    <%
                        if (VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_EDIT_YEARPLAN_ID) && (user.getCurrentYear().equals(VtkUtil.getCurrentGSYear() + ""))) {
                    %>
                    <li class='has-dropdown<%= ("explore".equals(activeTab)) ? " active" : " " %>'>
                        <a href="<%=vtk_cache_uri %>/vtk.explore.html">
                            Explore
                        </a>
                    </li>
                    <%}%>
                    <!-- TODO: JC - mobile view missing DB alert [case: no year plan] -->
                    <% if (VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_VIEW_YEARPLAN_ID)) { %>
                    <li
                            class='has-dropdown<%= ("plan".equals(activeTab)) ? " active" : " " %>'><a
                            href="<%=vtk_cache_uri %>/vtk.html">
                        <%= (user.getCurrentYear().equals(VtkUtil.getCurrentGSYear() + "")) ? "Year Plan" : "Past Year Plans"%>
                    </a>
                        <ul class="dropdown">
                            <% if ("plan".equals(activeTab) && VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_EDIT_YEARPLAN_ID)) { %>
                            <% if (selectedTroop != null && selectedTroop.getSfTroopAge() != null) { %>
                            <li><a title="Manage Calendar" onclick="newLocCal()">Manage Calendar</a></li>
                            <li><a
                                    onclick="doMeetingLib(<%=calendarUtil.isEventPastGSYear(user, selectedTroop)%>)">Add
                                Badge / Journey</a></li>
                            <%} %>
                            <li><a title="Manage Activity" onclick="newActivity()">Manage Activity</a></li>
                            <%
                                java.util.Map archivedPlans = troopDAO.getArchivedYearPlans(user, selectedTroop);
                                if (!isParent && !isFinanceAdmin && new java.util.Date().after(new java.util.Date(configManager.getConfig("startShowingArchiveCmd"))) && archivedPlans != null && archivedPlans.size() > 0) {
                            %>
                            <li><a title="Past Years"
                                   onclick="cngYear('<%=archivedPlans.keySet().iterator().next()%>')">PAST YEARS</a>
                            </li>
                            <%}%>
                            <li><a
                                    onclick="self.location='/content/girlscouts-vtk/en/cal.ics'">Download
                                Calendar</a></li>
                            <% } %>
                        </ul>
                    </li>
                    <% if (VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_VIEW_MEETING_ID)) { %>
                    <li
                            class='has-dropdown<%= ("planView".equals(activeTab)) ? " active" : " " %>'>
                            <%if(selectedTroop.getYearPlan()!=null &&
	                 (selectedTroop.getYearPlan().getActivities()==null || selectedTroop.getYearPlan().getActivities().size()<=0 ) &&
	                    ( selectedTroop.getYearPlan().getMeetingEvents()==null || selectedTroop.getYearPlan().getMeetingEvents().size()<=0 )){ %>
                        <a href='#'
                           onClick='alert("Content only available for meetings. Add at least one meeting to the Year Plan to access this tab.")'>
                            <%= (user.getCurrentYear().equals(VtkUtil.getCurrentGSYear() + "")) ? "Meeting Plan" : "Past Meeting Plans"%>
                        </a> <%}else{ %> <a
                            <%= selectedTroop.getYearPlan() != null ? "href='/content/girlscouts-vtk/en/vtk.details.html'" : "href='#' onClick='alert(\"Please select a year plan\")'"  %>>
                        <%= (user.getCurrentYear().equals(VtkUtil.getCurrentGSYear() + "")) ? "Meeting Plan" : "Past Meeting Plans"%>
                    </a> <%} %>
                            <%if( activeTab!=null  &&  "plan".equals(activeTab) && !user.getCurrentYear().equals( VtkUtil.getCurrentGSYear()+"") ){%>
                    <li><select class="vtk-dropdown" name="" onchange="cngYear(this.options[this.selectedIndex].value)">
                        <%for (int i = VtkUtil.getCurrentGSYear() - 1; i > (VtkUtil.getCurrentGSYear() - 6); i--) {%>
                        <option value="<%=i%>"><%=i%>
                        </option>
                        <%
                                if (i == 2014) {
                                    break;
                                }
                            }%>
                    </select></li>
                    <li><a onclick="resetYear()">Back to Current Year</a></li>
                    <%}%>
                    <%}%>
                    <ul class="dropdown">
                        <% if ("planView".equals(activeTab)) {
                            switch (planView.getYearPlanComponent().getType()) {
                                case ACTIVITY:
                                    Activity activity = (Activity) planView.getYearPlanComponent();
                        %>
                        <li id="linkToYplan"><a href="/content/girlscouts-vtk/en/vtk.html" title="View Year Plan">VIEW
                            YEAR PLAN</a></li>
                        <%
                            if (VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_EDIT_ACTIVITY_ID) && activity.getIsEditable()) {%>
                        <li><a href="#" title="Edit Actvitiy" onclick="doEditActivity('editCustActiv')">edit
                            activity</a></li>
                        <% }
                            if (!(activity.getCancelled() != null && activity.getCancelled().equals("true")) && activity.getRegisterUrl() != null && !activity.getRegisterUrl().equals("")) {%>
                        <li><a title="Register for this Event" href="<%=activity.getRegisterUrl()%>" target="_blank">Register
                            for this event</a></li>
                        <%
                            }
                            if (VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_RM_ACTIVITY_ID)) {
                        %>
                        <li><a
                                title="Delete This Activity"
                                href="javascript:rmCustActivity12(aPath)">delete this
                            activity</a></li>
                        <%
                                }
                            case MEETING:
                                try {
                                    if (planView != null && planView.getMeeting() != null && planView.getMeeting().getMeetingInfo() != null && planView.getMeeting().getMeetingInfo().getPath() != null) {
                                        Object meetingPath = planView.getMeeting().getMeetingInfo().getPath();
                        %>
                        <li id="linkToYplan"><a href="/content/girlscouts-vtk/en/vtk.html" title="View Year Plan">VIEW
                            YEAR PLAN</a></li>
                        <%
                            if (VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_EDIT_MEETING_ID) && meetingPath != null && meetingPath != "") {
                                String meetingDeleteFunctionName = meetingUtil.canDeleteMeeting(planView, planView.getMeeting(), user, selectedTroop) ? "rmMeetingWithConf" : "rmMeetingWithConfBlocked";
                        %>
                        <li id="replaceMeetingSmall"></li>
                        <li id="rmMeetingSmall">
                            <a href="#" title="Delete Meeting"
                               onclick="<%=meetingDeleteFunctionName %>( '<%=planView.getMeeting().getUid() %>', '<%=planView.getSearchDate().getTime() %>', '<%=selectedTroop.getSfTroopAge().substring( selectedTroop.getSfTroopAge().indexOf("-")+1 ) %>', '<%=planView.getMeeting().getMeetingInfo().getName()%>' )">delete
                                meeting</a>
                        </li>
                        <%
                                            }
                                        }
                                    } catch (Exception te) {
                                        log.error("Unable to determine meeting delete button", te);
                                    }
                                    break;
                            }%>
                        <% } %>
                    </ul>
                    </li>
                    <% } %>
                    <%if (user.getCurrentYear().equals(VtkUtil.getCurrentGSYear() + "")) {%>
                    <li <%= ("resource".equals(activeTab)) ? "class='active'" : "" %>><a
                            href="/myvtk/<%=councilMapper.getCouncilName(selectedTroop.getSfCouncil())%>/vtk.resource.html">Resources</a>
                    </li>
                    <%}%>
                    <% if (VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_EDIT_MILESTONE_ID)) { %>
                    <li <%= ("milestones".equals(activeTab)) ? "class='active'" : "" %>><a
                            href="/content/girlscouts-vtk/en/vtk.admin_milestones.html">Milestones</a>
                    </li>
                    <% } %>
                    <% if (user.isAdmin() && !"0".equals(user.getAdminCouncilId())) { %>
                    <li class='has-dropdown<%= ("reports".equals(activeTab)) ? " active" : "" %>'>
                        <a href="/content/girlscouts-vtk/en/vtk.admin_reports.html">Reports</a>
                        <% if ("reports".equals(activeTab)) { %>
                        <ul class="dropdown">
                            <li><a
                                    href="/content/girlscouts-vtk/controllers/vtk.admin_reports_downloadable.xls"
                                    title="download admin report">download</a></li>
                        </ul>
                        <% } %>
                    </li>
                    <% } %>
                    <% if (financeTabEnabled) { %>
                    <li <%= ("finances".equals(activeTab)) ? "class='active'" : "" %>><a
                            href="/content/girlscouts-vtk/en/vtk.finances.html?qtr=1">Finances</a>
                    </li>
                    <% } %>
                </ul>
            </div>
        </div>
        <!--/columns-->
    </div>
    <!-- /hide-for-print -->
</div>
<!-- MaintenanceBanner -->
<% Resource maintenanceNode = resourceResolver.getResource("/content/vtkcontent/en/vtk-maintenanceBanner/jcr:content/content/middle/par/breaking-news");
    if (maintenanceNode != null && (request.getSession().getAttribute("isHideVtkMaintenance") == null)) { %>
<div id="vtkBreakingNews" class="row">
    <cq:include path="/content/vtkcontent/en/vtk-maintenanceBanner/jcr:content/content/middle/par/breaking-news"
                resourceType="girlscouts-vtk/components/breaking-news"/>
</div>
<%}%>
<!-- start utility nav -->
<div id="vtkNav" class="row">
    <div id="vtk_banner2234" data-cached="<%=session.getAttribute("isHideVtkBanner")!=null ? "yes" : "no" %>"
         class="column medium-20 small-24 small-centered" style="display:none;"></div>
    <div class="crumbs clearfix show-for-large-up">
        <div class="column small-24 large-centered large-20">
            <div class="row">
                <div class="columns small-18 medium-19">
                    <ul id="sub-nav" class="inline-list hide-for-print">
                        <%
                            if ("reports".equals(activeTab) && user.isAdmin() && !"0".equals(user.getAdminCouncilId())) { %>
                        <li><a
                                href="/content/girlscouts-vtk/controllers/vtk.admin_reports_downloadable.xls"
                                title="download admin report">download</a></li>
                        <% }
                        %>
                        <!-- if on YP page this menu shows -->
                        <%
                            if ("plan".equals(activeTab) && selectedTroop.getYearPlan() != null && VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_EDIT_YEARPLAN_ID)) { %>
                        <% if (selectedTroop != null && selectedTroop.getSfTroopAge() != null) { %>
                        <li class="second-type"><a href="#" onclick="newLocCal()"
                                                   title="Manage Calendar"><i class="icon-calendar"></i> Manage Calendar</a>
                        </li>
                        <li class="second-type"><a href="#"
                                                   onclick="doMeetingLib(<%=calendarUtil.isEventPastGSYear(user, selectedTroop)%>)"
                                                   title="Add Badge / Journey"><i
                                class="icon-search-magnifying-glass"></i> Add Badge / Journey</a></li>
                        <% } %>
                        <li class="second-type"><a href="#" onclick="newActivity()" title="Manage Activity"> <i
                                class="icon-flag"></i>Manage Activity</a></li>
                        <% }
                        %>
                        <!-- if on Meeting Detail Page -->
                        <% if ("planView".equals(activeTab)) { %>
                        <!--if activity detail page-->
                        <% switch (planView.getYearPlanComponent().getType()) {
                            case ACTIVITY:
                                pageContext.setAttribute("YearPlanComponent", "ACTIVITY");
                                Activity activity = (Activity) planView.getYearPlanComponent();
                        %>
                        <li id="linkToYplan"><a href="/content/girlscouts-vtk/en/vtk.html" title="View Year Plan"><i
                                class="icon-layout-list"></i>VIEW YEAR PLAN</a></li>
                        <%
                            if (VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_EDIT_ACTIVITY_ID) && activity.getIsEditable()) {%>
                        <li><a data-reveal-id="modal_popup_activity"
                               title="Edit Activity"
                               data-reveal-ajax="true"
                               href="/content/girlscouts-vtk/controllers/vtk.include.activity_edit_react.html?elem=<%=planView.getSearchDate().getTime()%>">Edit
                            Activity</a></li>
                        <% }
                            if (!(activity.getCancelled() != null && activity.getCancelled().equals("true")) && activity.getRegisterUrl() != null && !activity.getRegisterUrl().equals("")) {%>
                        <li><a title="Register for this Event" href="<%=activity.getRegisterUrl()%>" target="_blank">Register
                            for this event</a></li>
                        <%
                            }
                            if (VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_RM_ACTIVITY_ID)) {
                        %>
                        <li><a
                                title="Delete This Activity"
                                href="javascript:rmCustActivity12(aPath)">delete this
                            activity</a></li>
                        <%
                                }
                                break;
                            case MEETING:
                                pageContext.setAttribute("YearPlanComponent", "MEETING");
                                try {
                                    if (planView != null && planView.getMeeting() != null && planView.getMeeting().getMeetingInfo() != null && planView.getMeeting().getMeetingInfo().getPath() != null) {
                        %>
                        <li id="linkToYplan"><a href="/content/girlscouts-vtk/en/vtk.html" title="View Year Plan"><i
                                class="icon-layout-list"></i>VIEW YEAR PLAN</a></li>
                        <%
                            Object meetingPath = planView.getMeeting().getMeetingInfo().getPath();
                            if (VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_EDIT_MEETING_ID) && meetingPath != null && meetingPath != "") {
                                String meetingDeleteFunctionName = meetingUtil.canDeleteMeeting(planView, planView.getMeeting(), user, selectedTroop) ? "rmMeetingWithConf" : "rmMeetingWithConfBlocked";
                        %>
                        <li id="replaceMeeting"></li>
                        <li id="rmMeeting">
                            <a href="#" title="Delete Meeting"
                               onclick="<%=meetingDeleteFunctionName %>( '<%=planView.getMeeting().getUid() %>', '<%=planView.getSearchDate().getTime() %>', '<%=selectedTroop.getSfTroopAge().substring( selectedTroop.getSfTroopAge().indexOf("-")+1 ) %>', '<%=planView.getMeeting().getMeetingInfo().getName()%>' )">delete
                                meeting</a>
                        </li>
                        <%
                                                }
                                            }
                                        } catch (Exception te) {
                                            log.error("Unable to determine meeting delete button", te);
                                        }
                                        break;
                                }
                            }%>
                        <!-- if on a My Troop page-->
                        <% if ("myTroop".equals(activeTab) && VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_EDIT_TROOP_IMG_ID)) { %>
                        <li><a data-reveal-id="modal_upload_image"
                               title="update photo" href="#">add/change a photo of your
                            troop</a></li>
                        <li><a title="remove photo" href="#" onclick="rmTroopInfo()">remove
                            troop photo</a></li>
                        <% } %>
                    </ul>
                    <%
                        if (user.getCurrentYear().equals(VtkUtil.getCurrentGSYear() + "") && activeTab != null && "plan".equals(activeTab)) {
                            java.util.Map archivedPlans = troopDAO.getArchivedYearPlans(user, selectedTroop);
                            if (!isParent && !isFinanceAdmin && new java.util.Date().after(new java.util.Date(configManager.getConfig("startShowingArchiveCmd"))) && archivedPlans != null && archivedPlans.size() > 0) {
                    %>
                    <div class="past_years">
                        <a title="Past Years" href="javascript:void(0)"
                           onclick="cngYear('<%=archivedPlans.keySet().iterator().next()%>')"><i
                                class="icon-glasses_2"></i> Past Years </a>
                    </div>
                    <%}%>
                    <%
                    } else if (activeTab != null && "plan".equals(activeTab) && !user.getCurrentYear().equals(VtkUtil.getCurrentGSYear() + "")) {
                        java.util.Map archivedPlans = troopDAO.getArchivedYearPlans(user, selectedTroop);
                    %>
                    <select class="vtk-dropdown" name="" onchange="cngYear(this.options[this.selectedIndex].value)">
                        <option value="">Select Year</option>
                        <%
                            java.util.Iterator itr = archivedPlans.keySet().iterator();
                            while (itr.hasNext()) {
                                String yr = ((Integer) itr.next()).toString();
                        %>
                        <option value="<%=yr%>" <%=((user.getCurrentYear().equals(yr)) ? "SELECTED" : "")%>><%=yr%>
                        </option>
                        <%
                                //if( yr.equals("2014") ) break;
                            }%>
                    </select>
                    <input class="vtk-button" type="button" value="Back to Current Year" onclick="resetYear()"/>
                    <%}%>
                </div>
                <div class="columns small-6 medium-5">
                    <ul class="inline-list" id="util-links">
                        <% if ("myTroop".equals(activeTab) && (VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_EDIT_TROOP_IMG_ID))) { %>
                        <li><a
                                title="print" href="/content/girlscouts-vtk/controllers/vtk.include.troopRoster.pdf"
                                target="_blank"><i class="icon-printer"></i></a></li>
                        <li style="margin-right:15px"><a
                                title="download"
                                href="/content/girlscouts-vtk/controllers/vtk.include.troopRosterCsvRpt.html"
                                target="_blank"><i class="icon-download"></i></a></li>
                        <%}
                        	if(user.isParent() && !selectedTroop.getIsIRM() && "myTroop".equals(activeTab)){ %>
                        		 <li style="margin-right:15px"><a
                                	title="Girl Scout Achievement Report"
                                	href="/content/girlscouts-vtk/controllers/vtk.include.gsachievementRptCsv.html"
                               		target="_blank"><i class="icon-download"></i></a></li>
                        	<%}%>
                        <%if (activeTab != null && ("plan".equals(activeTab) || (pageContext.getAttribute("YearPlanComponent") != null && ((String) pageContext.getAttribute("YearPlanComponent")).equals("MEETING") && "planView".equals(activeTab)))) { %>
                        <li><a data-reveal-id="modal_help" title="help"><i
                                class="icon-questions-answers"></i></a></li>
                        <%if ("planView".equals(activeTab)) {%>
                        <li style="margin-right:15px"><a
                                onclick="callPrintModal()"
                                title="print"><i class="icon-printer"></i></a></li>
                        <%}%>
                        <%} %>
                        <% if ("plan".equals(activeTab)) {%>
                        <%if (user.getCurrentYear().equals(VtkUtil.getCurrentGSYear() + "")) {%>
                        <li>
                            <a <%
                             if (selectedTroop.getYearPlan() != null && planView != null && planView.getSearchDate() != null && planView.getSearchDate().after(new java.util.Date("1/1/1977"))) {%>
                                onclick="vtkTrackerPushAction('DownloadCalendar');self.location = '/content/girlscouts-vtk/en/cal.ics'"
                             <% } else { %>
                                onclick="alert('You have not yet scheduled your meeting calendar.\nPlease select a year plan and schedule your meetings by clicking on the MEETING DATES AND LOCATION link.')"
                             <% }
                                %> title="download the calendar">
                                <i class="icon-download"></i>
                            </a>
                        </li>
                        <%}//edn if archive%>
                        <li>
                            <a onclick="window.print();vtkTrackerPushAction('Print');" title="print">
                                <i class="icon-printer"></i>
                            </a>
                        </li>
                        <% } %>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>