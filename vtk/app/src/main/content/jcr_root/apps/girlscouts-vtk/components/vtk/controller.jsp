<%@page
        import="com.day.cq.commons.Doctype,
                com.day.cq.commons.ImageHelper,
                com.day.cq.commons.jcr.JcrUtil,
                com.day.cq.wcm.api.components.DropTarget,
                com.day.cq.wcm.foundation.Image,
                com.day.image.Layer,
                com.google.gson.Gson,
                com.google.gson.GsonBuilder,
				org.girlscouts.vtk.ocm.*,
				org.girlscouts.vtk.osgi.service.*,
				org.girlscouts.vtk.osgi.component.dao.*,
                org.apache.commons.beanutils.BeanComparator,
                org.girlscouts.vtk.auth.permission.Permission,
                org.girlscouts.vtk.osgi.component.util.CouncilRpt,
                org.girlscouts.vtk.models.EmailMeetingReminder,
                org.girlscouts.vtk.osgi.component.util.VtkYearPlanChangeException,
                org.girlscouts.vtk.mapper.vtk.CollectionModelToEntityMapper,
                org.girlscouts.vtk.modifiedcheck.ModifiedChecker,
                org.girlscouts.vtk.osgi.service.GirlScoutsSalesForceService,
                org.joda.time.LocalDate" %>
<%@ page import="java.util.*, org.girlscouts.vtk.mapper.vtk.ModelToRestEntityMapper, org.girlscouts.vtk.osgi.component.util.Emailer, org.girlscouts.vtk.utils.ActivityNumberComparator" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp" %>
<%
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Cache-Control", "no-store");
    response.addHeader("Cache-Control", "must-revalidate");
    response.addHeader("pragma", "no-cache");
    response.addHeader("expires", "0");
    final String jsonDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    Gson gson = new GsonBuilder()
            .setDateFormat(jsonDateFormat)
            .enableComplexMapKeySerialization()
            .create();
    Logger vtklog = LoggerFactory.getLogger(this.getClass().getName());
    String vtkErr = "";
    int serverPortInt = request.getServerPort();
    String serverPort = "";
    if (serverPortInt != 80 && serverPortInt != 443) {
        serverPort = ":" + serverPortInt;
    }
    String serverName = request.getServerName();
    try {
        ActionController aContr = null;
        try {
            if (request.getParameter("act") != null) {
                aContr = ActionController.valueOf(request.getParameter("act"));
            }
        } catch (java.lang.IllegalArgumentException iae) {
            vtklog.error("Exception occured:", iae);
        }
        vtklog.debug("controller.jsp: aContr=" + aContr);
        if (aContr != null) {
            switch (aContr) {
                case ChangeMeetingPositions:
                    String x = request.getParameter("isMeetingCngAjax");
                    while (x.indexOf(",,") != -1) {
                        x = x.replaceAll(",,", ",");
                    }
                    StringTokenizer t = new StringTokenizer(x, ",");
                    if (selectedTroop.getYearPlan().getMeetingEvents().size() != t.countTokens()) {
                        String tmp = x;
                        if (!tmp.startsWith(",")) {
                            tmp = "," + tmp;
                        }
                        if (!tmp.endsWith(",")) {
                            tmp = tmp + ",";
                        }
                        for (int i = selectedTroop.getYearPlan().getMeetingEvents().size(); i > 0; i--) {
                            if (tmp.indexOf("," + i + ",") == -1) {
                                x = i + "," + x;
                            }
                        }
                    }
                    meetingUtil.changeMeetingPositions(user, selectedTroop, x);
                    return;
                case CreateActivity:
                    yearPlanUtil.createActivity(user, selectedTroop, new Activity(
                            request.getParameter("newCustActivity_name"),
                            request.getParameter("newCustActivity_txt"),
                            VtkUtil.parseDate(VtkUtil.FORMAT_FULL, request.getParameter("newCustActivity_date")
                                    + " " + request.getParameter("newCustActivity_startTime")
                                    + " " + request.getParameter("newCustActivity_startTime_AP")),
                            VtkUtil.parseDate(VtkUtil.FORMAT_FULL, request.getParameter("newCustActivity_date")
                                    + " " + request.getParameter("newCustActivity_endTime")
                                    + " " + request.getParameter("newCustActivity_endTime_AP")),
                            request.getParameter("newCustActivityLocName"),
                            request.getParameter("newCustActivityLocAddr"),
                            VtkUtil.convertObjectToDouble(request.getParameter("newCustActivity_cost"))));
                    return;
                case CreateSchedule:
                    try {
                        session.setAttribute("VTK_planView_memoPos", null);
                        calendarUtil.createSched(user, selectedTroop, request.getParameter("calFreq"), new org.joda.time.DateTime(
                                        VtkUtil.parseDate(VtkUtil.FORMAT_FULL, request.getParameter("calStartDt")
                                                + " " + request.getParameter("calTime")
                                                + " " + request.getParameter("calAP"))),
                                request.getParameter("exclDt"),
                                Long.parseLong((request.getParameter("orgDt") == null || request.getParameter("orgDt")
                                        .equals("")) ? "0" : request.getParameter("orgDt")));
                    } catch (Exception e) {
                        vtklog.error("Exception occured:", e);
                    }
                    return;
                case SelectYearPlan:
                    try {
                        troopUtil.selectYearPlan(user, selectedTroop, request.getParameter("addYearPlanUser"), request.getParameter("addYearPlanName"));
                    } catch (VtkYearPlanChangeException e) {
                        vtklog.error("Exception occured:", e);
                        out.println(e.getMessage());
                    }
                    return;
                case AddLocation:
                    locationUtil.addLocation(user, selectedTroop, new Location(request.getParameter("name"),
                            request.getParameter("address"), request.getParameter("city"),
                            request.getParameter("state"), request.getParameter("zip")));
                    return;
                case RemoveLocation:
                    locationUtil.removeLocation(user, selectedTroop, request.getParameter("rmLocation"));
                    return;
                case CreateCustomAgenda:
                    meetingUtil.createCustomAgenda(user, selectedTroop, request.getParameter("name"), request
                                    .getParameter("newCustAgendaName"), Integer.parseInt(request.getParameter("duration")),
                            Long.parseLong(request.getParameter("startTime")), request.getParameter("txt"));
                    return;
                case SetLocationAllMeetings:
                    locationUtil.setLocationAllMeetings(user, selectedTroop, request.getParameter("setLocationToAllMeetings"));
                    return;
                case UpdateSched:
                    boolean isSucc = calendarUtil.updateSched(user, selectedTroop, request.getParameter("meetingPath"),
                            request.getParameter("time"), request.getParameter("date"),
                            request.getParameter("ap"), request.getParameter("isCancelledMeeting"),
                            Long.parseLong(request.getParameter("currDt")));
                    if (!isSucc) {
                        response.sendError(499, "Date already exists in schedule");
                    }
                    return;
                case RemoveCustomActivity:
                    meetingUtil.rmCustomActivity(user, selectedTroop, request.getParameter("rmCustActivity"));
                    return;
                case ChangeLocation:
                    locationUtil.changeLocation(user, selectedTroop, request.getParameter("chnLocation"), request.getParameter("newLocPath"));
                    return;
                case SwapMeetings:
                    meetingUtil.swapMeetings(user, selectedTroop, request.getParameter("fromPath"), request.getParameter("toPath"));
                    return;
                case AddMeeting:
                    meetingUtil.addMeetings(user, selectedTroop, request.getParameter("toPath"));
                    return;
                case RearrangeActivity:
                    try {
                        meetingUtil.rearrangeActivity(user, selectedTroop, request.getParameter("mid"), request.getParameter("isActivityCngAjax"));
                    } catch (java.lang.IllegalAccessException e) {
                        vtklog.error("Exception occured:", e);
                    }
                    return;
                case RemoveAgenda:
                    meetingUtil.rmAgenda(user, selectedTroop, request.getParameter("rmAgenda"), request.getParameter("mid"));
                    return;
                case EditAgendaDuration:
                    meetingUtil.editAgendaDuration(user, selectedTroop, Integer.parseInt(request.getParameter("editAgendaDuration")),
                            request.getParameter("aid"), request.getParameter("mid"));
                    return;
                case RevertAgenda:
                    meetingUtil.reverAgenda(user, selectedTroop, request.getParameter("mid"));
                    return;
                case ReLogin:
                    VtkUtil.cngYear(request, user);
                    troopUtil.selectTroopForView(user, request.getParameter("loginAs"), session);
                    // Generator the new troopDataToken so the client can fetch data from the dispatcher.
                    Troop newTroop = (Troop) session.getAttribute("VTK_troop");
                    String troopId = newTroop.getTroopId();
                    Cookie cookie = new Cookie("troopDataToken", newTroop.getHash());
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    return;
                case AddAid:
                    if (request.getParameter("assetType").equals("AID")) {
                        meetingUtil.addAids(user, selectedTroop, request.getParameter("addAids"),
                                request.getParameter("meetingId"),
                                java.net.URLDecoder.decode(request.getParameter("assetName")),
                                request.getParameter("assetDocType"));
                    } else {
                        meetingUtil.addResource(user, selectedTroop, request.getParameter("addAids"),
                                request.getParameter("meetingId"),
                                java.net.URLDecoder.decode(request.getParameter("assetName")),
                                request.getParameter("docType"));
                    }
                    return;
                case RemoveAsset:
                    meetingUtil.rmAsset(user, selectedTroop, request.getParameter("rmAsset"), request.getParameter("meetingId"));
                    return;
                case BindAssetToYPC:
                    vtkErr = troopUtil.bindAssetToYPC(user, selectedTroop,
                            request.getParameter("bindAssetToYPC"),
                            request.getParameter("ypcId"),
                            request.getParameter("assetDesc"),
                            request.getParameter("assetTitle"));
                    return;
                case EditCustActivity:
                    vtkErr = troopUtil.editCustActivity(user, selectedTroop, request);
                    return;
                case Search:
                    yearPlanUtil.search(user, selectedTroop, request);
                    return;
                case CreateCustomActivity:
                    yearPlanUtil.createCustActivity(user, selectedTroop,
                            (java.util.List<Activity>) session
                                    .getAttribute("vtk_search_activity"), request.getParameter("newCustActivityBean"));
                    return;
                case isAltered:
                    out.println(yearPlanUtil.isYearPlanAltered(user, selectedTroop));
                    return;
                case GetFinances:
                    financeUtil.getFinances(user, selectedTroop, Integer.parseInt(request.getParameter("finance_qtr")), user.getCurrentYear());
                    return;
                case UpdateFinances:
                    financeUtil.updateFinances(user, selectedTroop, user.getCurrentYear(), request.getParameterMap());
                    financeUtil.sendFinanceDataEmail(user, selectedTroop, Integer.parseInt(request.getParameter("qtr")), user.getCurrentYear());
                    return;
                case UpdateFinanceAdmin:
                    financeUtil.updateFinanceConfiguration(user, selectedTroop, user.getCurrentYear(), request.getParameterMap());
                    return;
                case RmMeeting:
                    meetingUtil.rmMeeting(user, selectedTroop, request.getParameter("mid"));
                    //meetingUtil.rmSchedDate(user, selectedTroop,Long.parseLong(request.getParameter("rmDate")));
                    return;
                case UpdAttendance:
                    meetingUtil.updateAttendance(user, selectedTroop, request);
                    if ("MEETING".equals(request.getParameter("eType"))) {
                        meetingUtil.updateAchievement(user, selectedTroop, request);
                    }
                    return;
                case CreateCustomYearPlan:
                    meetingUtil.createCustomYearPlan(user, selectedTroop, request.getParameter("mids"));
                    return;
                case RemoveVtkErrorMsg:
                    String vtkErrMsgId = request.getParameter("vtkErrMsgId");
                    if (vtkErrMsgId != null && !vtkErrMsgId.equals("")) {
                        VtkUtil.rmVtkError(request, vtkErrMsgId);
                    }
                    return;
                default:
                    break;
            }
        }
        if (request.getParameter("admin_login") != null) {
            if (session.getAttribute("VTK_ADMIN") == null) {
                String u = request.getParameter("usr");
                String p = request.getParameter("pswd");
                if (u.equals("admin") && p.equals("icruise123")) {
                    session.setAttribute("VTK_ADMIN", u);
                }
            }
            response.sendRedirect("/content/girlscouts-vtk/en/vtk.admin.home.html");
        } else if (request.getParameter("previewMeetingReminderEmail") != null) {
            vtklog.debug("previewMeetingReminderEmail");
            String email_to_gp = request.getParameter("email_to_gp");
            String email_to_tv = request.getParameter("email_to_tv");
            String bcc = request.getParameter("email_cc");
            String subj = request.getParameter("email_subj");
            String html = request.getParameter("email_htm");
            String meetingId = request.getParameter("mid");
            String template = request.getParameter("template");
            EmailMeetingReminder emr = null;
            if (selectedTroop.getSendingEmail() != null) {
                emr = selectedTroop.getSendingEmail();
                emr.setBcc(bcc);
                emr.setSubj(subj);
                emr.setHtml(html);
            } else {
                emr = new EmailMeetingReminder(null, null, bcc, subj,
                        html);
            }
            emr.setMeetingId(meetingId);
            emr.setTemplate(template);
            emr.setEmailToSelf("true");
            emr.setTo(user.getApiConfig().getUser().getEmail());
            if (email_to_gp.equals("true")) {
                java.util.List<Contact> contacts = sling.getService(GirlScoutsSalesForceService.class).getContactsForTroop(user.getApiConfig(), selectedTroop);
                String emails = null;
                for (int i = 0; i < contacts.size(); i++) {
                    String contactEmail = contacts.get(i).getEmail();
                    if (emails == null)
                        emails = contactEmail;
                    else
                        emails += ";" + contactEmail;
                }
                emr.addTo(emails);
                emr.setEmailToGirlParent("true");
            }
            if (email_to_tv.equals("true")) {
                emr.setEmailToTroopVolunteer("true");
            }
            selectedTroop.setSendingEmail(emr);
        } else if (request.getParameter("sendMeetingReminderEmail") != null) {
            vtklog.debug("sendMeetingReminderEmail");
            EmailMeetingReminder emr = null;
            if (selectedTroop.getSendingEmail() != null) {
                emr = selectedTroop.getSendingEmail();
            }
            Emailer emailer = sling.getService(Emailer.class);
            emailer.send(user, selectedTroop, emr);
            try {
                meetingUtil.saveEmail(user, selectedTroop, emr.getMeetingId());
            } catch (Exception e) {
                vtklog.error("Exception occured:", e);
            }
            selectedTroop.setSendingEmail(null);
        } else if (request.getParameter("id") != null) {
            vtklog.debug("id");
            java.util.List<MeetingE> meetings = selectedTroop.getYearPlan().getMeetingEvents();
            for (MeetingE m : meetings) {
                if (m.getUid().equals(request.getParameter("mid"))) {
                    Meeting custM = m.getMeetingInfo();
                    if (request.getParameter("id").equals("editMeetingName")) {
                        custM.setName(request.getParameter("newvalue"));
                    } else if (request.getParameter("id").equals("editMeetingDesc")) {
                        java.util.Map<String, JcrCollectionHoldString> meetingInfoItems = custM.getMeetingInfo();
                        meetingInfoItems.put("meeting short description", new JcrCollectionHoldString(request.getParameter("newvalue")));
                    } else if (request.getParameter("id").equals("editMeetingOverview")) {
                        java.util.Map<String, JcrCollectionHoldString> meetingInfoItems = custM.getMeetingInfo();
                        meetingInfoItems.put("overview", new JcrCollectionHoldString(request.getParameter("newvalue")));
                    } else if (request.getParameter("id").equals("editMeetingActivity")) {
                        java.util.Map<String, JcrCollectionHoldString> meetingInfoItems = custM.getMeetingInfo();
                        meetingInfoItems.put("detailed activity plan", new JcrCollectionHoldString(request.getParameter("newvalue")));
                    } else if (request.getParameter("id").equals("editMeetingMaterials")) {
                        java.util.Map<String, JcrCollectionHoldString> meetingInfoItems = custM.getMeetingInfo();
                        meetingInfoItems.put("materials", new JcrCollectionHoldString(request.getParameter("newvalue")));
                    }
                    try {
                        if (!m.getRefId().contains("_")) {
                            yearPlanUtil.createCustomMeeting(user, selectedTroop, m, custM);
                        } else {
                            yearPlanUtil.updateCustomMeeting(user, selectedTroop, m, custM);
                        }
                        out.println(request.getParameter("newvalue"));
                    } catch (Exception e) {
                        vtklog.error("Exception occured:", e);
                    }
                    break;
                }
            }
        } else if (request.getParameter("updateCouncilMilestones") != null) {
            vtklog.debug("updateCouncilMilestones");
            yearPlanUtil.updateMilestones(user, selectedTroop, request);
            response.sendRedirect("/content/girlscouts-vtk/en/vtk.admin.milestones.html");
        } else if (request.getParameter("saveCouncilMilestones") != null) {
            vtklog.debug("saveCouncilMilestones");
            yearPlanUtil.saveMilestones(user, selectedTroop, request);
            response.sendRedirect("/content/girlscouts-vtk/en/vtk.admin_milestones.html");
        } else if (request.getParameter("createCouncilMilestones") != null) {
            vtklog.debug("createCouncilMilestones");
            yearPlanUtil.createMilestones(user, selectedTroop, request);
            response.sendRedirect("/content/girlscouts-vtk/en/vtk.admin.milestones.html");
        } else if (request.getParameter("removeCouncilMilestones") != null) {
            vtklog.debug("removeCouncilMilestones");
            boolean isRm = troopUtil.removeMilestones(user, selectedTroop, request);
            if (isRm)
                response.sendRedirect("/content/girlscouts-vtk/en/vtk.admin.milestones.html");
            else
                vtkErr += vtkErr.concat("Warning: You last change was not saved.");
        } else if (request.getParameter("resetCal") != null) {
            vtklog.debug("resetCal");
            calendarUtil.resetCal(user, selectedTroop);
            out.println("Cal reset");
        } else if (request.getParameter("chngPermis") != null) {
            vtklog.debug("chngPermis");
            VtkUtil.changePermission(user, selectedTroop, Integer.parseInt(request.getParameter("chngPermis")));
        } else if (request.getParameter("Impersonate4S") != null) {
            vtklog.debug("Impersonate4S");
            troopUtil.impersonate(user, selectedTroop, request.getParameter("councilCode"), request.getParameter("troopId"), session);
            Troop x = (Troop) session.getAttribute("VTK_troop");
            response.sendRedirect("/content/girlscouts-vtk/en/vtk.html");
        } else if (request.getParameter("addAsset") != null) {
            vtklog.debug("addAsset");
            troopUtil.addAsset(user, selectedTroop, request.getParameter("meetingUid"),
                    new org.girlscouts.vtk.models.Asset(request.getParameter("addAsset")));
        } else if (request.getParameter("reactjs") != null || request.getAttribute("reactjs") != null) {
            vtklog.debug("reactjs");
            try {
                boolean isFirst = false;
                if ((request.getParameter("isFirst") != null && request.getParameter("isFirst").equals("1")) ||
                        (request.getAttribute("isFirst") != null && request.getAttribute("isFirst").equals("1"))) {
                    isFirst = true;
                }
                boolean isCng = false;
                if (!isFirst && selectedTroop.getYearPlan() != null) {
                    ModifiedChecker modifiedChecker = sling.getService(ModifiedChecker.class);
                    // TODO: This is a special logic. "X". Rewrite ModifiedChecker later. There are two places in this file.
                    isCng = modifiedChecker.isModified("X" + session.getId(), selectedTroop.getYearPlan().getPath());
                }
                if (isFirst || isCng) {
                    Troop prefTroop = null;
                    if (userTroops != null && userTroops.size() > 0) {
                        prefTroop = userTroops.get(0);
                    }
                    for (Troop userTroop : userTroops) {
                        if (userTroop.getHash().equals(selectedTroop.getHash())) {
                            prefTroop = userTroop;
                            break;
                        }
                    }
                    //archive
                    VtkUtil.cngYear(request, user);
                    if (!user.getCurrentYear().equals(VtkUtil.getCurrentGSYear() + "")) {
                        Set permis = org.girlscouts.vtk.auth.permission.Permission.getPermissionTokens(org.girlscouts.vtk.auth.permission.Permission.GROUP_MEMBER_1G_PERMISSIONS);
                        Troop newTroopCloned = ((Troop) VtkUtil.deepClone(prefTroop));
                        newTroopCloned.setPermissionTokens(permis);
                        selectedTroop = newTroopCloned;
                    } else {
                        selectedTroop = prefTroop;
                    }
                    Troop selectedTroopRepoData = troopUtil.getTroopByPath(user, selectedTroop.getPath());
                    java.util.List<MeetingE> tt = selectedTroopRepoData.getYearPlan().getMeetingEvents();
                    //end archive
                    selectedTroop.setYearPlan(selectedTroopRepoData.getYearPlan());
                    //selectedTroop.setPath(selectedTroopRepoData.getPath());
                    selectedTroop.setCurrentTroop(selectedTroopRepoData.getCurrentTroop());
                    PlanView planView = meetingUtil.planView(user, selectedTroop, request);
                    try {
                        String meetingCode = planView.getMeeting().getRefId().substring(planView.getMeeting().getRefId().lastIndexOf("/"));
                        String pathToAssets = "/content/dam/girlscouts-vtk2019/local/aid/meetings"+meetingCode;
                        List<org.girlscouts.vtk.models.Asset> assets = new ArrayList<org.girlscouts.vtk.models.Asset>();
                        Resource meetingAidFolder = resourceResolver.resolve(pathToAssets);
                        Iterator<Resource> aidResources = meetingAidFolder.listChildren();
                        while(aidResources.hasNext()){
                            try {
                                Resource aidResource = aidResources.next();

                                if("dam:Asset".equals(aidResource.getResourceType())){
                                    Resource metadata = aidResource.getChild("jcr:content/metadata");
                                    if(metadata != null){
                                        Node props = metadata.adaptTo(Node.class);
                                        org.girlscouts.vtk.models.Asset asset = new org.girlscouts.vtk.models.Asset();
                                        asset.setRefId(aidResource.getPath());
                                        if (props.hasProperty("dc:isOutdoorRelated")) {
                                            asset.setIsOutdoorRelated(props.getProperty("dc:isOutdoorRelated").getBoolean());
                                        } else {
                                            asset.setIsOutdoorRelated(false);
                                        }
                                        asset.setIsCachable(true);
                                        asset.setType("AID");
                                        asset.setDescription(props.getProperty("dc:description").getString());
                                        asset.setTitle(props.getProperty("dc:title").getString());
                                        assets.add(asset);
                                    }
                                }
                            } catch (Exception e) {

                                //log.error("Cannot get assets for meeting: " + rootPath + ". Root cause was: " + e.getMessage());
                            }
                        }

                        planView.getMeeting().setAssets(assets);

                    }catch(Exception e){
                        vtklog.error("Error occurred:",e);
                    }
                    java.util.List<MeetingE> TMP_meetings = (java.util.List<MeetingE>) VtkUtil.deepClone(selectedTroop.getYearPlan().getMeetingEvents());
                    MeetingE _meeting = (MeetingE) planView.getYearPlanComponent();
                    if (_meeting != null && _meeting.getMeetingInfo() != null) {
                        _meeting.setAllMultiActivitiesSelected(VtkUtil.isAllMultiActivitiesSelected(_meeting.getMeetingInfo().getActivities()));
                    }
                    java.util.List<MeetingE> meetings = new java.util.ArrayList();
                    boolean isAnyOutdoorActivitiesInMeeting = VtkUtil.isAnyOutdoorActivitiesInMeeting(_meeting.getMeetingInfo());
                    _meeting.setAnyOutdoorActivityInMeeting(isAnyOutdoorActivitiesInMeeting);
                    boolean isAnyOutdoorActivitiesInMeetingAvailable = VtkUtil.isAnyOutdoorActivitiesInMeetingAvailable(_meeting.getMeetingInfo());
                    _meeting.setAnyOutdoorActivityInMeetingAvailable(isAnyOutdoorActivitiesInMeetingAvailable);
                    boolean isAnyGlobalActivitiesInMeeting = VtkUtil.isAnyGlobalActivitiesInMeeting(_meeting.getMeetingInfo());
                    _meeting.setAnyGlobalActivityInMeeting(isAnyGlobalActivitiesInMeeting);
                    boolean isAnyGlobalActivitiesInMeetingAvailable = VtkUtil.isAnyGlobalActivitiesInMeetingAvailable(_meeting.getMeetingInfo());
                    _meeting.setAnyGlobalActivityInMeetingAvailable(isAnyGlobalActivitiesInMeetingAvailable);
                    if (_meeting.getNotes() == null) {
                        _meeting.setNotes(new LinkedList<Note>());
                    }
                    meetings.add(_meeting);
                    selectedTroop.getYearPlan().setMeetingEvents(meetings);
                    Attendance attendance = meetingUtil.getAttendance(user, selectedTroop, _meeting.getPath() + "/attendance");
                    Achievement achievement = meetingUtil.getAchievement(user, selectedTroop, _meeting.getPath() + "/achievement");
                    int achievementCurrent = 0, attendanceCurrent = 0, attendanceTotal = 0;
                    if (attendance != null && attendance.getUsers() != null) {
                        attendanceCurrent = new StringTokenizer(attendance.getUsers(), ",").countTokens();
                        attendanceTotal = attendance.getTotal();
                    }
                    if (achievement != null && achievement.getUsers() != null) {
                        achievementCurrent = new StringTokenizer(achievement.getUsers(), ",").countTokens();
                    }
                    if (_meeting.getMeetingInfo() != null && _meeting.getMeetingInfo().getActivities() != null) {
                        if (request.getParameter("isActivNew") != null && request.getParameter("isActivNew").equals("1")) {
                            _meeting.getMeetingInfo().setActivities(null);
                        } else {
                            java.util.List<Activity> _activities = _meeting.getMeetingInfo().getActivities();
                            _meeting.getMeetingInfo().getMeetingInfo().put("meeting short description",
                                    new JcrCollectionHoldString(org.apache.commons.lang.StringEscapeUtils.unescapeHtml(_meeting
                                            .getMeetingInfo().getMeetingInfo() // fixme - refactor
                                            .get("meeting short description").getStr())));
                            java.util.List<SentEmail> emails = _meeting.getSentEmails();
                            //_meeting.setSentEmails(null); //GSVTK-1324
                            java.util.List<SentEmail> sendEmails = _meeting.getSentEmails();
                            if (sendEmails != null && sendEmails.size() > 0) {
                                for (int se = 0; se < sendEmails.size(); se++) {
                                    SentEmail sEmail = sendEmails.get(se);
                                    sEmail.setHtmlDiff("tessss123");
                                }
                            }
                            Collections.sort(_activities, new ActivityNumberComparator());
                        }
                    }
                    if (selectedTroop != null && selectedTroop.getYearPlan() != null) {
                        Helper helper = selectedTroop.getYearPlan().getHelper();
                        if (helper == null) {
                            helper = new Helper();
                        }
                        helper.setNextDate(planView.getNextDate());
                        helper.setPrevDate(planView.getPrevDate());
                        helper.setCurrentDate(planView.getSearchDate().getTime());
                        java.util.ArrayList<String> permissions = new java.util.ArrayList<String>();
                        if (selectedTroop != null) {
                            if (VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_EDIT_MEETING_ID)) {
                                permissions.add(String.valueOf(Permission.PERMISSION_EDIT_MEETING_ID));
                            }
                            if (VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_VIEW_ACTIVITY_PLAN_ID)) {
                                permissions.add(String.valueOf(Permission.PERMISSION_VIEW_ACTIVITY_PLAN_ID));
                            }
                            if (VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_SEND_EMAIL_MT_ID)) {
                                permissions.add(String.valueOf(Permission.PERMISSION_SEND_EMAIL_MT_ID));
                            }
                            if (VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_EDIT_ATTENDANCE_ID)) {
                                permissions.add(String.valueOf(Permission.PERMISSION_EDIT_ATTENDANCE_ID));
                            }
                        }
                        helper.setPermissions(permissions);
                        helper.setAchievementCurrent(achievementCurrent);
                        helper.setAttendanceCurrent(attendanceCurrent);
                        helper.setAttendanceTotal(attendanceTotal);
                        selectedTroop.getYearPlan().setHelper(helper);
                        session.setAttribute("VTK_troop", selectedTroop);
                        try {
                            response.setContentType("application/json");
                            String json = ModelToRestEntityMapper.INSTANCE.toEntity(selectedTroop).getJson();
                            out.println(json.replaceAll("mailto:", "").replaceAll("</a>\"</a>", "</a>").replaceAll("\"</a>\"", ""));
                        } catch (Exception ee) {
                            vtklog.error("Exception occured:", ee);
                        }
                        selectedTroop.getYearPlan().setMeetingEvents(TMP_meetings);
                        session.setAttribute("VTK_troop", selectedTroop);
                    } else {
                        if (selectedTroop == null) {
                            vtklog.error("troop:" + selectedTroop);
                        } else {
                            vtklog.error("troop:" + selectedTroop + ", yearplan:" + selectedTroop.getYearPlan());
                        }
                    }
                }
            } catch (Exception e) {
                vtklog.error("Exception occured:", e);
            }
        } else if (request.getAttribute("yearPlanSched") != null || request.getParameter("yearPlanSched") != null) {
            vtklog.debug("yearPlanSched");
            try {
                if (selectedTroop.getYearPlan() == null) {
                    response.setContentType("application/json");
                    out.println("{\"yearPlan\":\"NYP\"}");
                    return;
                }
                boolean isFirst = false;
                if ((request.getAttribute("isFirst") != null && ((String) request.getAttribute("isFirst")).equals("1")) || (request.getParameter("isFirst") != null && request.getParameter("isFirst").equals("1"))) {
                    isFirst = true;
                }
                boolean isCng = false;
                if (!isFirst) {
                    ModifiedChecker modifiedChecker = sling.getService(ModifiedChecker.class);
                    isCng = modifiedChecker.isModified("X" + session.getId(), selectedTroop.getYearPlan().getPath());
                }
                if (isFirst || isCng) {
                    Troop prefTroop = null;
                    if (userTroops != null && userTroops.size() > 0) {
                        prefTroop = userTroops.get(0);
                    }
                    for (Troop userTroop : userTroops) {
                        if (userTroop.getHash().equals(selectedTroop.getHash())) {
                            prefTroop = userTroop;
                            break;
                        }
                    }
                    //archive
                    VtkUtil.cngYear(request, user);
                    if (!user.getCurrentYear().equals(VtkUtil.getCurrentGSYear() + "")) {
                        java.util.Set permis = org.girlscouts.vtk.auth.permission.Permission.getPermissionTokens(org.girlscouts.vtk.auth.permission.Permission.GROUP_MEMBER_1G_PERMISSIONS);
                        Troop newTroopCloned = ((Troop) VtkUtil.deepClone(prefTroop));
                        newTroopCloned.setPermissionTokens(permis);
                        selectedTroop = newTroopCloned;
                    } else {
                        selectedTroop = prefTroop;
                    }
                    Troop selectedTroopRepoData = troopUtil.getTroopByPath(user, selectedTroop.getPath());
                    //end archive
                    if (selectedTroopRepoData != null) {
                        selectedTroop.setYearPlan(selectedTroopRepoData.getYearPlan());
                        selectedTroop.setCurrentTroop(selectedTroopRepoData.getCurrentTroop());
                    }
                    java.util.Map<java.util.Date, YearPlanComponent> sched = meetingUtil.getYearPlanSched(user, selectedTroop, selectedTroop.getYearPlan(), true, true);
                    //start milestone
                    try {
                        if (selectedTroop.getYearPlan() != null) {
                            selectedTroop.getYearPlan().setMilestones(yearPlanUtil.getCouncilMilestones(user, selectedTroop));
                        }
                    } catch (Exception e) {
                        vtklog.error("Exception occured:", e);
                    }
                    if (selectedTroop.getYearPlan().getMilestones() == null) {
                        selectedTroop.getYearPlan().setMilestones(new java.util.ArrayList());
                    }
                    for (int i = 0; i < selectedTroop.getYearPlan().getMilestones().size(); i++) {
                        if (selectedTroop.getYearPlan().getMilestones().get(i).getDate() != null && selectedTroop.getYearPlan().getMilestones().get(i).getShow()) {
                            sched.put(selectedTroop.getYearPlan().getMilestones().get(i).getDate(), selectedTroop.getYearPlan().getMilestones().get(i));
                        }
                    }
                    session.setAttribute("VTK_troop", selectedTroop);
                    Object[] tmp = sched.values().toArray();
                    for (int i = 0; i < tmp.length; i++) {
                        try {
                            if (!(tmp[i] instanceof MeetingE)) {
                                continue;
                            }
                            boolean isAnyOutdoorActivitiesInMeeting = VtkUtil.isAnyOutdoorActivitiesInMeeting(((MeetingE) tmp[i]).getMeetingInfo());
                            ((MeetingE) tmp[i]).setAnyOutdoorActivityInMeeting(isAnyOutdoorActivitiesInMeeting);
                            boolean isAnyOutdoorActivitiesInMeetingAvailable = VtkUtil.isAnyOutdoorActivitiesInMeetingAvailable(((MeetingE) tmp[i]).getMeetingInfo());
                            ((MeetingE) tmp[i]).setAnyOutdoorActivityInMeetingAvailable(isAnyOutdoorActivitiesInMeetingAvailable);
                            boolean isAnyGlobalActivitiesInMeeting = VtkUtil.isAnyGlobalActivitiesInMeeting(((MeetingE) tmp[i]).getMeetingInfo());
                            ((MeetingE) tmp[i]).setAnyGlobalActivityInMeeting(isAnyGlobalActivitiesInMeeting);
                            boolean isAnyGlobalActivitiesInMeetingAvailable = VtkUtil.isAnyGlobalActivitiesInMeetingAvailable(((MeetingE) tmp[i]).getMeetingInfo());
                            ((MeetingE) tmp[i]).setAnyGlobalActivityInMeetingAvailable(isAnyGlobalActivitiesInMeetingAvailable);
                            ((MeetingE) tmp[i]).getMeetingInfo().setActivities(null);
                            ((MeetingE) tmp[i]).getMeetingInfo().setMeetingInfo(null);
                            ((MeetingE) tmp[i]).getMeetingInfo().setResources(null);
                            ((MeetingE) tmp[i]).getMeetingInfo().setAgenda(null);
                            ((MeetingE) tmp[i]).setSentEmails(null); //GSVTK-1324

                        } catch (Exception e) {
                            vtklog.error("Exception occured:", e);
                        }
                    }
                    response.setContentType("application/json");
                    String json = gson.toJson(CollectionModelToEntityMapper.mapYearPlanComponents(sched));
                    out.println("{\"yearPlan\":\"" + selectedTroop.getYearPlan().getName() + "\",\"schedule\":");
                    out.println(json.replaceAll("mailto:", ""));
                    out.println("}");
                }
            } catch (Exception e) {
                vtklog.error("Exception occured:", e);
            }
        } else if (request.getParameter("reactActivity") != null) {
            vtklog.debug("reactActivity");
            boolean isFirst = false;
            if (request.getParameter("isFirst") != null && request.getParameter("isFirst").equals("1")) {
                isFirst = true;
            }
            boolean isCng = false;
            if (!isFirst && selectedTroop.getYearPlan() != null) {
                ModifiedChecker modifiedChecker = sling.getService(ModifiedChecker.class);
                isCng = modifiedChecker.isModified("X" + session.getId(), selectedTroop.getYearPlan().getPath());
            }
            if (isFirst || isCng) {
                Troop prefTroop = null;
                if (userTroops != null && userTroops.size() > 0) {
                    prefTroop = userTroops.get(0);
                }
                for (Troop userTroop : userTroops) {
                    if (userTroop.getHash().equals(selectedTroop.getHash())) {
                        prefTroop = userTroop;
                        break;
                    }
                }
                Activity currentActivity = null;
                PlanView planView = meetingUtil.planView(user, selectedTroop, request);
                java.util.List<Activity> activities = selectedTroop.getYearPlan().getActivities();
                for (int i = 0; i < activities.size(); i++) {
                    if (activities.get(i).getUid().equals(planView.getYearPlanComponent().getUid())) {
                        currentActivity = activities.get(i);
                    }
                }
                YearPlan yearPlan = new YearPlan();
                Attendance attendance = meetingUtil.getAttendance(user, selectedTroop, currentActivity.getPath() + "/attendance");
                Achievement achievement = meetingUtil.getAchievement(user, selectedTroop, currentActivity.getPath() + "/achievement");
                int achievementCurrent = 0, attendanceCurrent = 0, attendanceTotal = 0;
                if (attendance != null && attendance.getUsers() != null) {
                    attendanceCurrent = new StringTokenizer(attendance.getUsers(), ",").countTokens();
                    attendanceTotal = attendance.getTotal();
                }
                if (achievement != null && achievement.getUsers() != null) {
                    achievementCurrent = new StringTokenizer(achievement.getUsers(), ",").countTokens();
                }
                if (selectedTroop != null && selectedTroop.getYearPlan() != null) {
                    Helper helper = selectedTroop.getYearPlan().getHelper();
                    if (helper == null) helper = new Helper();
                    helper.setNextDate(planView.getNextDate());
                    helper.setPrevDate(planView.getPrevDate());
                    helper.setCurrentDate(planView.getSearchDate().getTime());
                    helper.setSfTroopAge(selectedTroop.getSfTroopAge());
                    java.util.ArrayList<String> permissions = new java.util.ArrayList<String>();
                    if (selectedTroop != null && VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_SEND_EMAIL_ACT_ID)) {
                        permissions.add(String.valueOf(Permission.PERMISSION_SEND_EMAIL_ACT_ID));
                    }
                    helper.setPermissions(permissions);
                    helper.setAchievementCurrent(achievementCurrent);
                    helper.setAttendanceCurrent(attendanceCurrent);
                    helper.setAttendanceTotal(attendanceTotal);
                    yearPlan.setHelper(helper);
                }
                java.util.List<Activity> _activities = new java.util.ArrayList();
                _activities.add(currentActivity);
                yearPlan.setActivities(_activities);
                String json = ModelToRestEntityMapper.INSTANCE.toEntity(yearPlan).getJson();
                out.println(json);
            }
        } else if (request.getParameter("isAdminRpt") != null) {
            vtklog.debug("isAdminRpt");
            final CouncilRpt councilRpt = sling.getService(CouncilRpt.class);
            java.util.Map container = councilRpt.getTroopNames(request.getParameter("cid"), request.getParameter("ypPath"));
            java.util.Iterator itr = container.keySet().iterator();
            while (itr.hasNext()) {
                String troopId = (String) itr.next();
                String troopName = (String) container.get(troopId);
%>$("#<%=troopId%>").html("<%=troopName%>");<%
    }
} else if (request.getParameter("getEventImg") != null) {
    vtklog.debug("getEventImg");
    try {
        String imgDataPath = request.getParameter("path") + "/jcr:content/data";
        String imgPath = request.getParameter("path") + "/jcr:content/data/image";
        Node imgData = resourceResolver.resolve(imgDataPath).adaptTo(Node.class);
        if (imgData != null && imgData.hasProperty("imagePath") && !"".equals(imgData.getProperty("imagePath").getString())) {
%> <img src="<%= imgData.getProperty("imagePath").getString() %>"/> <%
} else {
    Resource imgResource = resourceResolver.getResource(imgPath);
    if (imgResource != null) {
        Node imgNode = imgResource.adaptTo(Node.class);
        if (imgNode.hasProperty("fileReference")) {
%> <img src="<%= imgNode.getProperty("fileReference").getString() %>"/> <%
} else {
    Image image = new Image(imgResource);
    image.setSrc(gsImagePathProvider.getImagePathByLocation(image));
    String width;
    String height;
    if (imgNode.hasProperty("./width")) {
        width = imgNode.getProperty("./width").getString();
    } else {
        width = "0";
    }
    if (imgNode.hasProperty("./height")) {
        height = imgNode.getProperty("./height").getString();
    } else {
        height = "0";
    }
    try {
        //drop target css class = dd prefix + name of the drop target in the edit config
        image.addCssClass(DropTarget.CSS_CLASS_PREFIX + "image");
        image.loadStyleData(currentStyle);
        image.setSelector(".img"); // use image script
        image.setDoctype(Doctype.fromRequest(request));
        if (!"0".equals(width)) {
            image.addAttribute("width", width + "px");
        }
        if (!"0".equals(height)) {
            image.addAttribute("height", height + "px");
        }
        Boolean newWindow = properties.get("./newWindow", false);
        // add design information if not default (i.e. for reference paras)
        if (!currentDesign.equals(resourceDesign)) {
            image.setSuffix(currentDesign.getId());
        }
        if (!newWindow) {
            image.draw(out);
        } else { %>
<%= image.getString().replace("<a ", "<a target=\"_blank\"") %>
<%
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
    } catch (Exception e) {
        vtklog.error("Exception occured:", e);
    }
} else if (request.getParameter("viewProposedSched") != null) {
    vtklog.debug("viewProposedSched");
    String dates = calendarUtil.getSchedDates(user, selectedTroop,
            request.getParameter("calFreq"), new org.joda.time.DateTime(
                    VtkUtil.parseDate(VtkUtil.FORMAT_FULL,
                            request.getParameter("calStartDt")
                                    + " " + request.getParameter("calTime")
                                    + " " + request.getParameter("calAP"))),
            request.getParameter("exclDt"),
            Long.parseLong((request.getParameter("orgDt") == null || request.getParameter("orgDt").equals("")) ? "0" : request.getParameter("orgDt")));
    java.util.List _dates = VtkUtil.getStrCommDelToArrayDates(dates);
    out.println(_dates.size());
} else if (request.getParameter("printTroopReloginids") != null) {
    vtklog.debug("printTroopReloginids");
%><select id="reloginid" onchange="relogin()"><%
    for (Troop userTroop : userTroops) {
        String troopGradeLevel = " : "+ userTroop.getGradeLevel();
        if(userTroop.getParticipationCode() != null && "IRM".equals(userTroop.getParticipationCode())){
            troopGradeLevel="";
        }
%>
    <option value="<%=userTroop.getHash()%>"
            <%=selectedTroop.getHash().equals(userTroop.getHash()) ? "selected" : ""%>><%=userTroop.getTroopName()%><%=troopGradeLevel%>
    </option>
    <%
        }
    %></select><%
} else if (request.getParameter("printCngYearPlans") != null) {
    vtklog.debug("printCngYearPlans");
    String ageLevel = selectedTroop.getGradeLevel();
    ageLevel = ageLevel.substring(ageLevel.indexOf("-") + 1);
    ageLevel = ageLevel.toLowerCase().trim();
    String confMsg = "";
    if (selectedTroop.getYearPlan() != null) {
        if (selectedTroop.getYearPlan().getAltered() != null && selectedTroop.getYearPlan().getAltered().equals("true")) {
            confMsg = "Are You Sure? You will lose customizations that you have made";
        }
    }
    java.util.Iterator<YearPlan> yearPlans = yearPlanUtil.getAllYearPlans(user, ageLevel).listIterator();
    while (yearPlans.hasNext()) {
        YearPlan yearPlan = yearPlans.next();
%>
<div class="row">
    <div class="columns large-push-2 medium-2 medium-push-2 small-2">
        <input type="radio" <%=(selectedTroop.getYearPlan() != null && (yearPlan.getName().equals(selectedTroop.getYearPlan().getName()))) ? " checked " : "" %>
               id="r_<%=yearPlan.getId()%>" class="radio1" name="group1"
               onclick="chgYearPlan('<%=yearPlan.getId()%>', '<%=yearPlan.getPath()%>', '<%=confMsg%>', '<%=yearPlan.getName()%>', <%=selectedTroop.getYearPlan() != null %> ,'<%=selectedTroop.getYearPlan()!=null ? selectedTroop.getYearPlan().getName() : "" %>', false)"/>
        <label for="r_<%=yearPlan.getId()%>"></label>
    </div>
    <div class="small-18 columns large-pull-2 medium-pull-2 small-pull-2">
        <a href="#"
           onclick="chgYearPlan('<%=yearPlan.getId()%>', '<%=yearPlan.getPath()%>', '<%=confMsg%>', '<%=yearPlan.getName()%>', <%=selectedTroop.getYearPlan() != null %> ,'<%=selectedTroop.getYearPlan()!=null ? selectedTroop.getYearPlan().getName() : "" %>', false)"><%=yearPlan.getName()%>
        </a>
        <p><%=yearPlan.getDesc()%>
        </p>
    </div>
</div>
<!--/row-->
<%}%>
<div class="row">
    <!-- <div class="small-20 small-centered columns"> -->
    <!-- <div class="row"> -->
    <%
        Boolean condition = selectedTroop != null && selectedTroop.getSfTroopAge() != null && !selectedTroop.getSfTroopAge().toLowerCase().contains("multilevel");
        boolean isMeetingLib = true;
        if (selectedTroop != null && selectedTroop.getSfTroopAge() != null && (selectedTroop.getSfTroopAge().toLowerCase().contains("senior") || selectedTroop.getSfTroopAge().toLowerCase().contains("cadette") || selectedTroop.getSfTroopAge().toLowerCase().contains("ambassador"))) {
            isMeetingLib = false;
        }
        if (condition) {
    %>
    <div class="columns large-push-2 medium-2 medium-push-2 small-2">
        <input type="radio" <%=(selectedTroop.getYearPlan() != null && (selectedTroop.getYearPlan().getName().equals("Custom Year Plan"))) ? " checked " : "" %>
               id="r_0" class="radio1" name="group1"
               onclick="chgYearPlan('', '', '<%=confMsg%>', 'Custom Year Plan', <%=selectedTroop.getYearPlan() != null %> ,'<%=selectedTroop.getYearPlan()!=null ? selectedTroop.getYearPlan().getName() : "" %>', <%= isMeetingLib %> )"/>
        <label for="r_0"></label></div>
    <%} %>
    <div class="small-18 columns large-pull-2 medium-pull-2 small-pull-2"
         style="<%= condition ? "padding-left:16px" : ""  %>">
        <div style="margin-left:-10px;margin-right: -10px;">
            <a onclick="return chgYearPlan('', '', '<%=confMsg%>', 'Custom Year Plan', <%=selectedTroop.getYearPlan() != null %> ,'<%=selectedTroop.getYearPlan()!=null ? selectedTroop.getYearPlan().getName() : "" %>', <%= isMeetingLib %> )">
                <% if (selectedTroop != null && selectedTroop.getSfTroopAge() != null &&
                        (selectedTroop.getSfTroopAge().toLowerCase().contains("senior") || selectedTroop.getSfTroopAge().toLowerCase().contains("cadette") || selectedTroop.getSfTroopAge().toLowerCase().contains("ambassador"))) {%>
                Customize Your Troop Year
                <%} else if (selectedTroop != null && selectedTroop.getSfTroopAge() != null && selectedTroop.getSfTroopAge().toLowerCase().contains("multi-level")) { %>
                <h4 style="color:#18aa51;margin-bottom:15px !important;"> Create Your Multi-Level Troop Year Plan </h4>
                <%} else { %>
                Create Your Own Year Plan
                <%} %>
            </a>
            <p>
                    <% if( selectedTroop!=null  && selectedTroop.getSfTroopAge()!=null && (selectedTroop.getSfTroopAge().toLowerCase().contains("senior") || selectedTroop.getSfTroopAge().toLowerCase().contains("cadette") || selectedTroop.getSfTroopAge().toLowerCase().contains("ambassador") )){%>
                Select this option to create activities or add council activities to your calendar.
                    <%}else  if( selectedTroop!=null  && selectedTroop.getSfTroopAge()!=null && selectedTroop.getSfTroopAge().toLowerCase().contains("multi-level")){ %>
            <p style="margin-bottom:15px !important;">All Girls Scouts plan have been organized so you can easily filter
                through the set to select the right ones for your multi-level troop.. Once your meeting selections are
                made you'll be able to arrange and finalize the dates in the Year Plan view.</p>
            <p style="margin-bottom:15px !important;">You will begin by selecting the Girl Scout Levels and types of
                meetings you want to see.</p>
            <br/><input type="button" class="button" value="Create Your Year Plan"
                        onclick="return chgYearPlan('', '', '<%=confMsg%>', 'Custom Year Plan',; <%=isMeetingLib%>"/>
            <%} else { %>
            Choose this option to create your own year plan using meetings from our meeting library
            <%} %>
            </p>
        </div>
    </div>
    <!-- </div> -->
    <!-- </div> -->
</div>
<!--/row-->
<%
} else if (request.getParameter("cngYear") != null) {
    vtklog.debug("cngYear");
    VtkUtil.cngYear(request, user);
} else if (request.getParameter("cngYearToCurrent") != null) {
    vtklog.debug("cngYearToCurrent");
    user.setCurrentYear(VtkUtil.getCurrentGSYear() + "");
    java.util.Set permis = org.girlscouts.vtk.auth.permission.Permission.getPermissionTokens(org.girlscouts.vtk.auth.permission.Permission.GROUP_LEADER_PERMISSIONS);
    Troop newTroopCloned = ((Troop) VtkUtil.deepClone(selectedTroop));
    newTroopCloned.setPermissionTokens(permis);
    selectedTroop = newTroopCloned;
    if (!troopDAO.isArchivedYearPlan(user, selectedTroop, "" + VtkUtil.getCurrentGSYear())) {
        selectedTroop.setYearPlan(null);
    }
    //Cloned Troop object from archived year plan references archived year plan path ex: "/vtk2014/999/". It is necessary to change Troop path to current year ex: ""/vtk2015/999/"".
    //selectedTroop.setPath("/vtk" + VtkUtil.getCurrentGSYear() + "/" + selectedTroop.getSfCouncil() + "/troops/" + selectedTroop.getSfTroopId());
    session.setAttribute("VTK_troop", selectedTroop);
} else if (request.getParameter("addNote") != null) {
    response.setContentType("application/json");
    vtklog.debug("addNote");
    Note note = null;
    try {
        List<Note> notes = meetingUtil.addNote(user, selectedTroop, request.getParameter("mid"), request.getParameter("message"));
        String json = gson.toJson(CollectionModelToEntityMapper.mapNotes(notes));
        out.println(json);
    } catch (Exception e) {
        vtklog.error("Exception occured:", e);
    }
} else if (request.getParameter("rmNote") != null) {
    response.setContentType("application/json");
    vtklog.debug("rmNote");
    boolean isRm = false;
    try {
        isRm = meetingUtil.rmNote(user, selectedTroop, request.getParameter("nid"));
    } catch (Exception e) {
        vtklog.error("Exception occured:", e);
    }
    if (!isRm) {
        response.sendError(404, "Note not removed.");
    } else {
        java.util.List<Note> notes = meetingUtil.getNotesByMid(user, selectedTroop, request.getParameter("mid"));
        String json = gson.toJson(CollectionModelToEntityMapper.mapNotes(notes));
        out.println(json);
    }
} else if (request.getParameter("editNote") != null) {
    response.setContentType("application/json");
    out.println("{vtkresp:" + meetingUtil.editNote(user, selectedTroop, request.getParameter("nid"), request.getParameter("msg")) + "}");
} else if (request.getParameter("getNotes") != null) {
    response.setContentType("application/json");
    java.util.List<Note> notes = meetingUtil.getNotesByMid(user, selectedTroop, request.getParameter("mid"));
    String json = gson.toJson(CollectionModelToEntityMapper.mapNotes(notes));
    out.println(json);
} else if (request.getParameter("addMeetings") != null) {
    meetingUtil.addMeetings(user, selectedTroop, request.getParameterValues("addMeetingMulti"));
%>
<script>self.location = '/content/girlscouts-vtk/en/vtk.html';</script>
<%
        } else if (request.getParameter("cngOutdoor") != null) {
            vtklog.debug("cngOutdoor");
            String mid = request.getParameter("mid");
            String aid = request.getParameter("aid");
            boolean isOutdoor = "true".equals(request.getParameter("isOutdoor"));
            MeetingE meeting = VtkUtil.findMeetingById(selectedTroop.getYearPlan().getMeetingEvents(), mid);
            Activity activity = VtkUtil.findActivityByPath(meeting.getMeetingInfo().getActivities(), aid);
            //TODO meetingUtil.updateActivityOutdoorStatus(user, selectedTroop, meeting, activity, isOutdoor);
        } else if (request.getParameter("act") != null && "combineCal".equals(request.getParameter("act"))) {
            vtklog.debug("combineCal");
            calendarUtil.combineMeeting(user, selectedTroop, request.getParameter("mids"), request.getParameter("dt"));
        } else if (request.getParameter("act") != null && "hideVtkBanner".equals(request.getParameter("act"))) {
            vtklog.debug("hideVtkBanner");
            session.setAttribute("isHideVtkBanner", "true");
        } else if (request.getParameter("act") != null && "hideVtkMaintenance".equals(request.getParameter("act"))) {
            vtklog.debug("hideVtkMaintenance");
            session.setAttribute("isHideVtkMaintenance", "true");
        } else if (request.getParameter("alex658Xf409Re49v") != null) {
            vtklog.debug("alex658Xf409Re49v");
            try {
                yearPlanUtil.GSMonthlyDetailedRpt(request.getParameter("year"));
            } catch (Exception e) {
                vtklog.error("Exception occured:", e);
            }
        } else if (request.getParameter("alex344") != null) {
            vtklog.debug("alex344");
            try {
                yearPlanUtil.GSRptCouncilPublishFinance();
            } catch (Exception e) {
                vtklog.error("Exception occured:", e);
            }
        } else if ("switchFinanceYear".equals(request.getParameter("act"))) {
            vtklog.debug("switchFinanceYear");
            int financeYear = 0;
            try {
                financeYear = Integer.parseInt(request.getParameter("financeYear"));
                user.setCurrentFinanceYear(financeYear);
            } catch (Exception e) {
                vtklog.error("Exception occured:", e);
            }
        } else if (request.getParameter("act") != null && "selectSubActivity".equals(request.getParameter("act"))) {
            vtklog.debug("selectSubActivity");
            String mPath = request.getParameter("mPath");
            String activityPath = request.getParameter("aPath");
            String subActivityPath = request.getParameter("subAPath");
            meetingUtil.setSelectedSubActivity(user, selectedTroop, mPath, activityPath, subActivityPath);

        } else if (request.getParameter("whatIsMyLevel") != null) {
            vtklog.debug("whatIsMyLevel");
            String myLevel = "";
            if (selectedTroop != null && selectedTroop.getGradeLevel() != null) {
                myLevel = selectedTroop.getGradeLevel();
            }
            response.setContentType("application/json");
            out.println("{\"level\":\"" + myLevel + "\"}");
        } else {
            vtklog.debug("no request parameters");
        }
    } catch (Exception e) {
        vtklog.error("Exception occured:", e);
    }
%>
