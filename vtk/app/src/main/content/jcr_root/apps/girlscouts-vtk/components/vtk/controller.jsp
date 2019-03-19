<%@page
	import="java.util.Comparator, org.codehaus.jackson.map.ObjectMapper,org.joda.time.LocalDate,java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*,
                    org.girlscouts.vtk.modifiedcheck.ModifiedChecker, com.day.cq.wcm.foundation.Image, com.day.cq.commons.Doctype,com.day.cq.wcm.api.components.DropTarget,com.day.image.Layer, java.awt.geom.Rectangle2D, java.awt.geom.Rectangle2D.Double, com.day.cq.commons.jcr.JcrUtil, org.apache.commons.codec.binary.Base64, com.day.cq.commons.ImageHelper, com.day.image.Layer, java.io.ByteArrayInputStream, java.io.ByteArrayOutputStream, java.awt.image.BufferedImage, javax.imageio.ImageIO,
                    org.girlscouts.vtk.helpers.TroopHashGenerator, org.girlscouts.vtk.models.JcrCollectionHoldString, org.girlscouts.vtk.ejb.CouncilRpt,org.slf4j.Logger,org.slf4j.LoggerFactory"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<cq:defineObjects />
<%@include file="include/session.jsp"%>
<%
    Logger vtklog = LoggerFactory.getLogger(this.getClass().getName());
	String vtkErr = "";
	int serverPortInt = request.getServerPort();
	String serverPort = "";
	if (serverPortInt != 80 && serverPortInt != 443) {
		serverPort = ":" + String.valueOf(serverPortInt);
	}
	String serverName = request.getServerName();
	try {
		org.girlscouts.vtk.models.ActionController aContr = null;
		try {
			if (request.getParameter("act") != null){
				aContr = org.girlscouts.vtk.models.ActionController.valueOf(request.getParameter("act"));
			}
		} catch (java.lang.IllegalArgumentException iae) {
			vtklog.error("Exception occured:",iae);
		}
        vtklog.debug("controller.jsp: aContr="+aContr);
		if (aContr != null){
			switch (aContr) {
				case ChangeMeetingPositions:
					String x = request.getParameter("isMeetingCngAjax");
					while (x.indexOf(",,") != -1) {
						x = x.replaceAll(",,", ",");
					}
					StringTokenizer t = new StringTokenizer(x, ",");
					if (troop.getYearPlan().getMeetingEvents().size() != t.countTokens()) {
						String tmp = x;
						if (!tmp.startsWith(",")){
							tmp = "," + tmp;
						}
						if (!tmp.endsWith(",")){
							tmp = tmp + ",";
						}
						for (int i = troop.getYearPlan().getMeetingEvents().size(); i > 0; i--){
							if (tmp.indexOf("," + i + ",") == -1){
								x = i + "," + x;
							}
						}
					}
					meetingUtil.changeMeetingPositions(user, troop, x);
					return;
				case CreateActivity:
					yearPlanUtil.createActivity(user, troop, new Activity(
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
						session.putValue("VTK_planView_memoPos", null);
						calendarUtil.createSched(user, troop, request.getParameter("calFreq"), new org.joda.time.DateTime(
												VtkUtil.parseDate(VtkUtil.FORMAT_FULL, request.getParameter("calStartDt")
																+ " " + request.getParameter("calTime")
																+ " " + request.getParameter("calAP"))),
												request.getParameter("exclDt"),
												Long.parseLong((request.getParameter("orgDt") == null || request.getParameter("orgDt")
												.equals("")) ? "0": request.getParameter("orgDt")));
					} catch (Exception e) {
						vtklog.error("Exception occured:",e);
					}
					return;
				case SelectYearPlan:
					try {
						troopUtil.selectYearPlan(user, troop, request.getParameter("addYearPlanUser"), request.getParameter("addYearPlanName"));
					} catch (VtkYearPlanChangeException e) {					
						vtklog.error("Exception occured:", e);
						out.println(e.getMessage());
					}
					return;
				case AddLocation:
					locationUtil.addLocation(user, troop, new Location(request.getParameter("name"),
								request.getParameter("address"), request.getParameter("city"), 
								request.getParameter("state"), request.getParameter("zip")));
					return;
				case RemoveLocation:
					locationUtil.removeLocation(user, troop, request.getParameter("rmLocation"));
					return;
				case CreateCustomAgenda:
					meetingUtil.createCustomAgenda(user, troop, request.getParameter("name"), request
						.getParameter("newCustAgendaName"), Integer.parseInt(request.getParameter("duration")),
						Long.parseLong(request.getParameter("startTime")), request.getParameter("txt"));
					return;
				case SetLocationAllMeetings:
					locationUtil.setLocationAllMeetings(user, troop, request.getParameter("setLocationToAllMeetings"));
					return;
				case UpdateSched:
					boolean isSucc = calendarUtil.updateSched(user, troop, request.getParameter("meetingPath"),
						request.getParameter("time"), request.getParameter("date"),
						request.getParameter("ap"), request.getParameter("isCancelledMeeting"),
						Long.parseLong(request.getParameter("currDt")));
					if (!isSucc) {
						response.sendError(499,"Date already exists in schedule");
					}
					return;
				case RemoveCustomActivity:
					meetingUtil.rmCustomActivity(user, troop, request.getParameter("rmCustActivity"));
					return;
				case ChangeLocation:
					locationUtil.changeLocation(user, troop, request.getParameter("chnLocation"), request.getParameter("newLocPath"));
					return;
				case SwapMeetings:
					meetingUtil.swapMeetings(user, troop, request.getParameter("fromPath"), request.getParameter("toPath"));
					return;
				case AddMeeting:
					meetingUtil.addMeetings(user, troop, request.getParameter("toPath"));
					return;
				case RearrangeActivity:
					try {
						meetingUtil.rearrangeActivity(user, troop, request.getParameter("mid"), request.getParameter("isActivityCngAjax"));
					} catch (java.lang.IllegalAccessException e) {
						vtklog.error("Exception occured:",e);
					}
					return;
				case RemoveAgenda:
					meetingUtil.rmAgenda(user, troop, request.getParameter("rmAgenda"), request.getParameter("mid"));
					return;
				case EditAgendaDuration:
					meetingUtil.editAgendaDuration(user, troop, Integer.parseInt(request.getParameter("editAgendaDuration")),
							request.getParameter("aid"), request.getParameter("mid"));
					return;
				case RevertAgenda:
					meetingUtil.reverAgenda(user, troop, request.getParameter("mid"));
					return;
				case ReLogin:
	  			    VtkUtil.cngYear(request,  user,  troop);
					troopUtil.reLogin(user, troop, request.getParameter("loginAs"), session);
					// Generator the new troopDataToken so the client can fetch data from the dispatcher.
					Troop newTroop = (Troop)session.getAttribute("VTK_troop");
					String troopId = newTroop.getTroop().getTroopId();		
    				TroopHashGenerator generator = sling.getService(TroopHashGenerator.class);
    				String token = generator.hash(troopId);
    				Cookie cookie = new Cookie("troopDataToken", token);
    				cookie.setPath("/");
    				response.addCookie(cookie);
					return;
				case AddAid:
					if (request.getParameter("assetType").equals("AID")) {
						meetingUtil.addAids(user, troop, request.getParameter("addAids"), 
								request.getParameter("meetingId"), 
								java.net.URLDecoder.decode(request.getParameter("assetName")),
								request.getParameter("assetDocType"));
					} else {
						meetingUtil.addResource(user, troop, request.getParameter("addAids"), 
								request.getParameter("meetingId"),
								java.net.URLDecoder.decode(request.getParameter("assetName")),
								request.getParameter("docType"));
					}
					return;
				case RemoveAsset:
					meetingUtil.rmAsset(user, troop, request.getParameter("rmAsset"), request.getParameter("meetingId"));
					return;
				case BindAssetToYPC:
					vtkErr = troopUtil.bindAssetToYPC(user, troop,
							request.getParameter("bindAssetToYPC"),
							request.getParameter("ypcId"),
							request.getParameter("assetDesc"),
							request.getParameter("assetTitle"));
					return;
				case EditCustActivity:
					vtkErr = troopUtil.editCustActivity(user, troop, request);
					return;
				case Search:
					yearPlanUtil.search(user, troop, request);
					return;
				case CreateCustomActivity:
					yearPlanUtil.createCustActivity(user, troop,
									(java.util.List<org.girlscouts.vtk.models.Activity>) session
											.getValue("vtk_search_activity"), request.getParameter("newCustActivityBean"));
					return;
				case isAltered:
					out.println(yearPlanUtil.isYearPlanAltered(user, troop));
					return;
				case GetFinances:
					financeUtil.getFinances(user, troop, Integer.parseInt(request.getParameter("finance_qtr")), user.getCurrentYear());
					return;
				case UpdateFinances:
					financeUtil.updateFinances(user, troop, user.getCurrentYear(), request.getParameterMap());
					financeUtil.sendFinanceDataEmail(user, troop, Integer.parseInt(request.getParameter("qtr")), user.getCurrentYear());
					return;
				case UpdateFinanceAdmin:
					financeUtil.updateFinanceConfiguration(user, troop, user.getCurrentYear(), request.getParameterMap());
					return;
				case RmMeeting:			
					meetingUtil.rmMeeting( user, troop, request.getParameter("mid") );
					//meetingUtil.rmSchedDate(user, troop,Long.parseLong(request.getParameter("rmDate")));
				return;
				case UpdAttendance:
					meetingUtil.updateAttendance(user, troop, request);
					if( "MEETING".equals( request.getParameter("eType") ) ){
						meetingUtil.updateAchievement(user, troop, request);
					}
				return;
				case CreateCustomYearPlan:
					meetingUtil.createCustomYearPlan(user, troop, request.getParameter("mids"));
					return;
				case RemoveVtkErrorMsg:
					String vtkErrMsgId= request.getParameter("vtkErrMsgId");
				    if( vtkErrMsgId!=null && !vtkErrMsgId.equals("")){
				             VtkUtil.rmVtkError(request,vtkErrMsgId );
				    }
					return;
				default:
					break;
			}
		}
		if (request.getParameter("admin_login") != null) {
			if (session.getValue("VTK_ADMIN") == null) {
				String u = request.getParameter("usr");
				String p = request.getParameter("pswd");
				if (u.equals("admin") && p.equals("icruise123")){
					session.putValue("VTK_ADMIN", u);
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
			if (troop.getSendingEmail() != null) {
				emr = troop.getSendingEmail();
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
				java.util.List<Contact> contacts = new org.girlscouts.vtk.auth.dao.SalesforceDAO(
						troopDAO, connectionFactory).getContacts(
						user.getApiConfig(), troop.getSfTroopId());
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
			troop.setSendingEmail(emr);
		} else if (request.getParameter("sendMeetingReminderEmail") != null) {
			vtklog.debug("sendMeetingReminderEmail");
			EmailMeetingReminder emr = null;
			if (troop.getSendingEmail() != null) {
				emr = troop.getSendingEmail();
			} 
			org.girlscouts.vtk.ejb.Emailer emailer = sling.getService(org.girlscouts.vtk.ejb.Emailer.class);
			emailer.send(user, troop, emr);
			try {
				meetingUtil.saveEmail(user, troop, emr.getMeetingId());
			} catch (Exception e) {
				vtklog.error("Exception occured:",e);
			}
			troop.setSendingEmail(null);
		} else if (request.getParameter("id") != null) {
			vtklog.debug("id");
			java.util.List<MeetingE> meetings = troop.getYearPlan().getMeetingEvents();
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
							yearPlanUtil.createCustomMeeting(user, troop, m, custM);
						} else {
							yearPlanUtil.updateCustomMeeting(user, troop, m, custM);
						}
						out.println(request.getParameter("newvalue"));
					} catch (Exception e) {
						vtklog.error("Exception occured:",e);
					}
					break;
				}
			}
		} else if (request.getParameter("updateCouncilMilestones") != null) {
           	vtklog.debug("updateCouncilMilestones");
			yearPlanUtil.updateMilestones( user,  troop,  request );
			response.sendRedirect("/content/girlscouts-vtk/en/vtk.admin.milestones.html");
		} else if (request.getParameter("saveCouncilMilestones") != null) {
            vtklog.debug("saveCouncilMilestones");
			yearPlanUtil.saveMilestones(user, troop, request);
			response.sendRedirect("/content/girlscouts-vtk/en/vtk.admin_milestones.html");
		} else if (request.getParameter("createCouncilMilestones") != null) {
            vtklog.debug("createCouncilMilestones");
			yearPlanUtil.createMilestones(user, troop, request);
			response.sendRedirect("/content/girlscouts-vtk/en/vtk.admin.milestones.html");
		} else if (request.getParameter("removeCouncilMilestones") != null) {
            vtklog.debug("removeCouncilMilestones");
			boolean isRm = troopUtil.removeMilestones(user, troop, request);
		    if(isRm )
		    	response.sendRedirect("/content/girlscouts-vtk/en/vtk.admin.milestones.html");
		    else
		    	vtkErr += vtkErr.concat("Warning: You last change was not saved.");
		} else if (request.getParameter("resetCal") != null) {
            vtklog.debug("resetCal");
			calendarUtil.resetCal(user, troop);
			out.println("Cal reset");
		} else if (request.getParameter("chngPermis") != null) {
            vtklog.debug("chngPermis");
			VtkUtil.changePermission(user, troop, Integer.parseInt(request.getParameter("chngPermis")));
		} else if (request.getParameter("Impersonate4S") != null) {
            vtklog.debug("Impersonate4S");
			troopUtil.impersonate(user, troop, request.getParameter("councilCode"), request.getParameter("troopId"), session);
			Troop x = (Troop) session.getAttribute("VTK_troop");
			response.sendRedirect("/content/girlscouts-vtk/en/vtk.html");
		} else if (request.getParameter("addAsset") != null) {
			vtklog.debug("addAsset");
			troopUtil.addAsset(user, troop, request.getParameter("meetingUid"),
					new org.girlscouts.vtk.models.Asset(request.getParameter("addAsset")));
		} else if (request.getParameter("reactjs") != null || request.getAttribute("reactjs") != null) {
           	vtklog.debug("reactjs");
       		try{
				boolean isFirst = false;
				if ((request.getParameter("isFirst") != null && request.getParameter("isFirst").equals("1")) ||
				    (request.getAttribute("isFirst") != null && request.getAttribute("isFirst").equals("1"))) {
					isFirst = true;
				}
				boolean isCng = false;
				if (!isFirst && troop.getYearPlan()!=null) {
					ModifiedChecker modifiedChecker = sling.getService(ModifiedChecker.class);	
					// TODO: This is a special logic. "X". Rewrite ModifiedChecker later. There are two places in this file.
					isCng = modifiedChecker.isModified("X" + session.getId(), troop.getYearPlan().getPath());		
				}
				if (isFirst || isCng) {
                	org.girlscouts.vtk.salesforce.Troop prefTroop = null;
                    if (apiConfig.getTroops() != null && apiConfig.getTroops().size() > 0) {
                            prefTroop = apiConfig.getTroops().get(0);
                    }
					for (int ii = 0; ii < apiConfig.getTroops().size(); ii++) {
						if (apiConfig.getTroops().get(ii).getTroopId().equals(troop.getSfTroopId())) {
							prefTroop = apiConfig.getTroops().get(ii);
							break;
						}
					}
					troop = troopUtil.getTroop(user, "" + prefTroop.getCouncilCode(), prefTroop.getTroopId());
					java.util.List <MeetingE> tt= troop.getYearPlan().getMeetingEvents();
					//archive
               		VtkUtil.cngYear(request,  user,  troop);
	                if( !user.getCurrentYear().equals( VtkUtil.getCurrentGSYear()+"") ){
	                     java.util.Set permis= org.girlscouts.vtk.auth.permission.Permission.getPermissionTokens(org.girlscouts.vtk.auth.permission.Permission.GROUP_MEMBER_1G_PERMISSIONS);
	                     org.girlscouts.vtk.salesforce.Troop newTroopCloned = ((org.girlscouts.vtk.salesforce.Troop)VtkUtil.deepClone(prefTroop));
	                     newTroopCloned.setPermissionTokens( permis );
	                     troop.setTroop(newTroopCloned);
	                }else{
	                    troop.setTroop(prefTroop);
	                }
               		//end archive
					//troop.setTroop(prefTroop);
	                troop.setSfTroopId(troop.getTroop().getTroopId());
	                troop.setSfUserId(user.getApiConfig().getUserId());
	                troop.setSfTroopName(troop.getTroop().getTroopName());
	                troop.setSfTroopAge(troop.getTroop().getGradeLevel());
	                troop.setSfCouncil(troop.getTroop().getCouncilCode() + "");
					PlanView planView = meetingUtil.planView(user, troop, request);
					java.util.List<MeetingE> TMP_meetings = (java.util.List<MeetingE> )VtkUtil.deepClone(troop.getYearPlan().getMeetingEvents());
					MeetingE _meeting = (MeetingE) planView.getYearPlanComponent();
					if(_meeting != null && _meeting.getMeetingInfo() != null){
						_meeting.setAllMultiActivitiesSelected(VtkUtil.isAllMultiActivitiesSelected(_meeting.getMeetingInfo().getActivities()));
					}
					java.util.List<MeetingE> meetings = new java.util.ArrayList();
					boolean isAnyOutdoorActivitiesInMeeting = VtkUtil.isAnyOutdoorActivitiesInMeeting( _meeting.getMeetingInfo() );
					_meeting.setAnyOutdoorActivityInMeeting(isAnyOutdoorActivitiesInMeeting);
					boolean isAnyOutdoorActivitiesInMeetingAvailable = VtkUtil.isAnyOutdoorActivitiesInMeetingAvailable( _meeting.getMeetingInfo() );
					_meeting.setAnyOutdoorActivityInMeetingAvailable(isAnyOutdoorActivitiesInMeetingAvailable);
					
					boolean isAnyGlobalActivitiesInMeeting = VtkUtil.isAnyGlobalActivitiesInMeeting( _meeting.getMeetingInfo() );
					_meeting.setAnyGlobalActivityInMeeting(isAnyGlobalActivitiesInMeeting);
					boolean isAnyGlobalActivitiesInMeetingAvailable = VtkUtil.isAnyGlobalActivitiesInMeetingAvailable( _meeting.getMeetingInfo() );
					_meeting.setAnyGlobalActivityInMeetingAvailable(isAnyGlobalActivitiesInMeetingAvailable);
					
					if(_meeting.getNotes() == null){
                        _meeting.setNotes(new LinkedList<Note>());
                    }
					meetings.add(_meeting);
					troop.getYearPlan().setMeetingEvents(meetings);
					Attendance attendance = meetingUtil.getAttendance( user,  troop,  _meeting.getPath()+"/attendance");
					Achievement achievement = meetingUtil.getAchievement( user,  troop,  _meeting.getPath()+"/achievement");
					int achievementCurrent=0, attendanceCurrent=0, attendanceTotal=0;
					if( attendance !=null && attendance.getUsers()!=null ){
					    attendanceCurrent = new StringTokenizer( attendance.getUsers(), ",").countTokens();
					    attendanceTotal= attendance.getTotal();
					}
					if( achievement !=null && achievement.getUsers()!=null ){
					    achievementCurrent = new StringTokenizer( achievement.getUsers(), ",").countTokens();
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
							if( sendEmails!=null && sendEmails.size()>0 ){
								for(int se=0;se< sendEmails.size();se++){
									SentEmail sEmail = sendEmails.get(se);
									sEmail.setHtmlDiff("tessss123");
								}
							}						
							java.util.Comparator<Activity> comp = new org.apache.commons.beanutils.BeanComparator( "activityNumber");
							Collections.sort(_activities, comp);
						}
					}
					if(troop!=null && troop.getYearPlan()!=null){
						Helper helper = troop.getYearPlan().getHelper();
						if( helper==null ) {
							helper= new Helper();
						}
						helper.setNextDate(planView.getNextDate());
						helper.setPrevDate(planView.getPrevDate());
						helper.setCurrentDate(planView.getSearchDate().getTime());
						java.util.ArrayList <String> permissions= new java.util.ArrayList<String>();
						if (troop != null && troop.getTroop() != null) {
							if(VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID)) {
								permissions.add(String.valueOf(Permission.PERMISSION_EDIT_MEETING_ID));
							}
                               if(VtkUtil.hasPermission(troop, Permission.PERMISSION_VIEW_ACTIVITY_PLAN_ID)) {
                                       permissions.add(String.valueOf(Permission.PERMISSION_VIEW_ACTIVITY_PLAN_ID));
                               }
                               if(VtkUtil.hasPermission(troop, Permission.PERMISSION_SEND_EMAIL_MT_ID)) {
                                       permissions.add(String.valueOf(Permission.PERMISSION_SEND_EMAIL_MT_ID));
                               }
                               if(VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_ATTENDANCE_ID)) {
                                       permissions.add(String.valueOf(Permission.PERMISSION_EDIT_ATTENDANCE_ID));
                               }
						}
						helper.setPermissions(permissions);
						helper.setAchievementCurrent(achievementCurrent);
						helper.setAttendanceCurrent(attendanceCurrent);
						helper.setAttendanceTotal(attendanceTotal);
						troop.getYearPlan().setHelper(helper);		
		                session.putValue("VTK_troop", troop);
                   		ObjectMapper mapper = new ObjectMapper();
                   		try {
                   			response.setContentType("application/json");
		                    out.println(mapper.writeValueAsString(troop).replaceAll("mailto:", "")
		                            .replaceAll("</a>\"</a>", "</a>").replaceAll("\"</a>\"", ""));
	                    } catch (Exception ee) {
	                    	vtklog.error("Exception occured:",ee);
	                    }
	                    troop.getYearPlan().setMeetingEvents(TMP_meetings);
	                    session.putValue("VTK_troop", troop);
					} else {
						if(troop == null){
							vtklog.error("troop:"+troop);
						}else{
							vtklog.error("troop:"+troop+", yearplan:"+troop.getYearPlan());
						}
					}
				}
			}catch(Exception e){
				vtklog.error("Exception occured:",e);
			}
		} else if (request.getAttribute("yearPlanSched") != null || request.getParameter("yearPlanSched") != null) {
			vtklog.debug("yearPlanSched");
			try{
				if (troop.getYearPlan() == null){
					ObjectMapper mapper = new ObjectMapper();
	                out.println("{\"yearPlan\":\"NYP\"}");
					return;
				}
				boolean isFirst = false;
				  if((request.getAttribute("isFirst") != null && ((String)request.getAttribute("isFirst")).equals("1")) || (request.getParameter("isFirst") != null && request.getParameter("isFirst").equals("1"))) {
					isFirst = true;
				}
				boolean isCng = false;
				if (!isFirst) {
					ModifiedChecker modifiedChecker = sling.getService(ModifiedChecker.class);
					isCng = modifiedChecker.isModified("X" + session.getId(), troop.getYearPlan().getPath());
				}			
				if (isFirst || isCng) {
					org.girlscouts.vtk.salesforce.Troop prefTroop = null;
					if (apiConfig.getTroops() != null && apiConfig.getTroops().size() > 0) {
						prefTroop = apiConfig.getTroops().get(0);
					}
					for (int ii = 0; ii < apiConfig.getTroops().size(); ii++) {
						if (apiConfig.getTroops().get(ii).getTroopId()
								.equals(troop.getSfTroopId())) {
							prefTroop = apiConfig.getTroops().get(ii);
							break;
						}
					}
       				troop = troopUtil.getTroop(user, "" + prefTroop.getCouncilCode(), prefTroop.getTroopId());
       				//archive
       				VtkUtil.cngYear(request,  user,  troop);
			        if( !user.getCurrentYear().equals( VtkUtil.getCurrentGSYear()+"") ){
			             java.util.Set permis= org.girlscouts.vtk.auth.permission.Permission.getPermissionTokens(org.girlscouts.vtk.auth.permission.Permission.GROUP_MEMBER_1G_PERMISSIONS);
			             org.girlscouts.vtk.salesforce.Troop newTroopCloned = ((org.girlscouts.vtk.salesforce.Troop)VtkUtil.deepClone(prefTroop));
			             newTroopCloned.setPermissionTokens( permis );
			             troop.setTroop(newTroopCloned);
			        }else{
			            troop.setTroop(prefTroop);
			        }
       				//end archive
					//troop.setTroop(prefTroop);
	                troop.setSfTroopId(troop.getTroop().getTroopId());
	                troop.setSfUserId(user.getApiConfig().getUserId());
	                troop.setSfTroopName(troop.getTroop().getTroopName());
	                troop.setSfTroopAge(troop.getTroop().getGradeLevel());
	                troop.setSfCouncil(troop.getTroop().getCouncilCode() + "");		
					java.util.Map<java.util.Date, YearPlanComponent> sched = meetingUtil.getYearPlanSched(user, troop, troop.getYearPlan(), true, true);
					//start milestone
					try {
						if (troop.getYearPlan() != null) {
							troop.getYearPlan() .setMilestones(yearPlanUtil.getCouncilMilestones(user,"" + troop.getSfCouncil()));
						}
					} catch (Exception e) {
						vtklog.error("Exception occured:",e);
					}
					if (troop.getYearPlan().getMilestones() == null){
						troop.getYearPlan().setMilestones(new java.util.ArrayList());
					}
					for (int i = 0; i < troop.getYearPlan().getMilestones().size(); i++){
							if (troop.getYearPlan().getMilestones().get(i).getDate() != null &&
									troop.getYearPlan().getMilestones().get(i).getShow())
										sched.put(troop.getYearPlan().getMilestones().get(i).getDate(),
												troop.getYearPlan().getMilestones().get(i));
					}
					session.putValue("VTK_troop", troop);
					Object tmp[] = sched.values().toArray();
					for(int i=0;i<tmp.length;i++){
						try{ 		
		   					if(!(tmp[i] instanceof   MeetingE)){
		   						continue;
		   					}			   
		   					boolean isAnyOutdoorActivitiesInMeeting = VtkUtil.isAnyOutdoorActivitiesInMeeting(((MeetingE) tmp[i]).getMeetingInfo()); 
		   					((MeetingE) tmp[i]).setAnyOutdoorActivityInMeeting(isAnyOutdoorActivitiesInMeeting);
		   					boolean isAnyOutdoorActivitiesInMeetingAvailable = VtkUtil.isAnyOutdoorActivitiesInMeetingAvailable( ((MeetingE) tmp[i]).getMeetingInfo() );
		   					((MeetingE) tmp[i]).setAnyOutdoorActivityInMeetingAvailable(isAnyOutdoorActivitiesInMeetingAvailable);
		   					boolean isAnyGlobalActivitiesInMeeting = VtkUtil.isAnyGlobalActivitiesInMeeting(((MeetingE) tmp[i]).getMeetingInfo()); 
		   					((MeetingE) tmp[i]).setAnyGlobalActivityInMeeting(isAnyGlobalActivitiesInMeeting);
		   					boolean isAnyGlobalActivitiesInMeetingAvailable = VtkUtil.isAnyGlobalActivitiesInMeetingAvailable( ((MeetingE) tmp[i]).getMeetingInfo() );
		   					((MeetingE) tmp[i]).setAnyGlobalActivityInMeetingAvailable(isAnyGlobalActivitiesInMeetingAvailable);
		   					((MeetingE) tmp[i]).getMeetingInfo().setActivities(null);
							((MeetingE) tmp[i]).getMeetingInfo().setMeetingInfo(null);
							((MeetingE) tmp[i]).getMeetingInfo().setResources(null);
							((MeetingE) tmp[i]).getMeetingInfo().setAgenda(null);							
							((MeetingE) tmp[i]).setSentEmails(null); //GSVTK-1324
						
						} catch (Exception e) {
							vtklog.error("Exception occured:",e);
						}
					}
					response.setContentType("application/json");
					ObjectMapper mapper = new ObjectMapper();
					out.println("{\"yearPlan\":\"" + troop.getYearPlan().getName() + "\",\"schedule\":");
					out.println(mapper.writeValueAsString(sched).replaceAll("mailto:", ""));
					out.println("}");
				}
			}catch(Exception e){
				vtklog.error("Exception occured:",e);
			}
		} else if (request.getParameter("reactActivity") != null) {
			vtklog.debug("reactActivity");
           	boolean isFirst = false;
            if (request.getParameter("isFirst") != null && request.getParameter("isFirst").equals("1")) {
                isFirst = true;
            }
           	boolean isCng = false;
           	if (!isFirst && troop.getYearPlan()!=null) {
               	ModifiedChecker modifiedChecker = sling.getService(ModifiedChecker.class);
               	isCng = modifiedChecker.isModified("X" + session.getId(), troop.getYearPlan().getPath());
           	}
           	if (isFirst || isCng) {
               	org.girlscouts.vtk.salesforce.Troop prefTroop = null;
                   if (apiConfig.getTroops() != null && apiConfig.getTroops().size() > 0) {
                           prefTroop = apiConfig.getTroops().get(0);
                   }
                for (int ii = 0; ii < apiConfig.getTroops().size(); ii++) {
                    if (apiConfig.getTroops().get(ii).getTroopId()
                            .equals(troop.getSfTroopId())) {
                        prefTroop = apiConfig.getTroops().get(ii);
                        break;
                    }
                }
                Activity currentActivity = null;
                troop = troopUtil.getTroop(user, "" + prefTroop.getCouncilCode(), prefTroop.getTroopId());
                troop.setTroop(prefTroop);
                PlanView planView = meetingUtil.planView(user, troop, request);
                java.util.List<Activity> activities = troop.getYearPlan().getActivities();
                for (int i = 0; i < activities.size(); i++) {
                    if (activities.get(i).getUid().equals(planView.getYearPlanComponent().getUid())){
                        currentActivity = activities.get(i);
                    }
                }
               	YearPlan yearPlan = new YearPlan();
                Attendance attendance = meetingUtil.getAttendance( user,  troop,  currentActivity.getPath()+"/attendance");
				Achievement achievement = meetingUtil.getAchievement( user,  troop,  currentActivity.getPath()+"/achievement");
				int achievementCurrent=0, attendanceCurrent=0, attendanceTotal=0;	
				if( attendance !=null && attendance.getUsers()!=null ){
				    attendanceCurrent = new StringTokenizer( attendance.getUsers(), ",").countTokens();
				    attendanceTotal= attendance.getTotal();
				}	
				if( achievement !=null && achievement.getUsers()!=null ){
				    achievementCurrent = new StringTokenizer( achievement.getUsers(), ",").countTokens();
				}                
                if( troop!=null && troop.getYearPlan()!=null){
                    Helper helper = troop.getYearPlan().getHelper();
                    if( helper==null ) helper= new Helper();
                    helper.setNextDate(planView.getNextDate());
                    helper.setPrevDate(planView.getPrevDate());
                    helper.setCurrentDate(planView.getSearchDate().getTime());
                    helper.setSfTroopAge( troop.getSfTroopAge());
                    java.util.ArrayList <String> permissions= new java.util.ArrayList<String>();	
                    if (troop != null && VtkUtil.hasPermission(troop, Permission.PERMISSION_SEND_EMAIL_ACT_ID)){
                        permissions.add(String.valueOf(Permission.PERMISSION_SEND_EMAIL_ACT_ID));
                    }
                    helper.setPermissions(permissions);
                    helper.setAchievementCurrent(achievementCurrent);
					helper.setAttendanceCurrent(attendanceCurrent);
					helper.setAttendanceTotal(attendanceTotal);						
                    yearPlan.setHelper(helper);
                }
                java.util.List<Activity> _activities= new java.util.ArrayList();
                _activities.add( currentActivity );
                yearPlan.setActivities( _activities);
                ObjectMapper mapper = new ObjectMapper();
                out.println(mapper.writeValueAsString(yearPlan));
           	}
		} else if (request.getParameter("isRmTroopImg") != null) {
			vtklog.debug("isRmTroopImg");
			Session __session =null;
			ResourceResolver rr= null;
			try {
				rr = sessionFactory.getResourceResolver();
			 	__session = rr.adaptTo(Session.class);
				String troopPhotoUrl = "/content/dam/girlscouts-vtk/troop-data"+VtkUtil.getCurrentGSYear()+"/"
						+ troop.getTroop().getCouncilCode() + "/"
						+ troop.getTroop().getTroopId() + "/imgLib/troop_pic.png";
				__session.removeItem(troopPhotoUrl);
				__session.save();
			} catch (Exception e) {
				vtklog.error("Exception occured:",e);
			} finally {
				try {
					if( rr!=null )
						sessionFactory.closeResourceResolver( rr );
					if (__session != null)
						__session.logout();
				} catch (Exception ex) {
					vtklog.error("Exception occured:",ex);
				}
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
            if(imgData.hasProperty("imagePath") && !"".equals(imgData.getProperty("imagePath").getString())){
                %> <img src="<%= imgData.getProperty("imagePath").getString() %>" /> <%
            }
            else{
                Resource imgResource = resourceResolver.getResource(imgPath);
                if (imgResource != null) {
                    Node imgNode = imgResource.adaptTo(Node.class);
                    if (imgNode.hasProperty("fileReference")) {
                        %> <img src="<%= imgNode.getProperty("fileReference").getString() %>" /> <%
                    }else{
                         Image image = new Image(imgResource);
                         image.setSrc(gsImagePathProvider.getImagePathByLocation(image));
                         String width;
                         String height;
                         if(imgNode.hasProperty("./width")){
                             width = imgNode.getProperty("./width").getString();
                         } else{
                             width = "0";
                         }
                         if(imgNode.hasProperty("./height")){
                             height = imgNode.getProperty("./height").getString();
                         } else{
                             height = "0";
                             }
                         try{

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

                             if(!newWindow) {
                                  image.draw(out);
                             } else { %>
                                 <%= image.getString().replace("<a ", "<a target=\"_blank\"") %>
                                 <%
                                 }
                         }catch (Exception e){

                         }
                     }
                }
            }
        } catch (Exception e) {
            vtklog.error("Exception occured:",e);
        }
		} else if(request.getParameter("imageData") != null){
			vtklog.debug("imageData");
			ResourceResolver rr= null;
			Session __session = null;
			try{
				int x1 = -1, x2 = -1, y1 = -1, y2 = -1, width = -1, height = -1;
                double maxW = 960;
				int[] coords = new int[0];
				if(request.getParameter("coords") != null){
					String coordString = request.getParameter("coords").toString();
					String[] nums = coordString.replaceAll("\\[", "").replaceAll("\\]", "").split(",");
					coords = new int[nums.length];
					for (int i = 0; i < nums.length; i++) {
				    	try {
				      		coords[i] = Integer.parseInt(nums[i]);
				    	} catch (NumberFormatException nfe) {
							vtklog.error("Exception occured:",nfe);
                       	};
					}
				}
				if(coords.length == 7){
					x1 = coords[0];
					y1 = coords[1];
					x2 = coords[2];
					y2 = coords[3];
					width = coords[4];
					height = coords[5];
                    maxW = coords[6];
				}
                String imgData = request.getParameter("imageData");
				imgData = imgData.replace("data:image/png;base64,", "");
                byte[] decoded = Base64.decodeBase64(imgData);
                if(x1 >= 0 && x2 >= 0 && y1 >= 0 && y2 >= 0 && width >= 0 && height >= 0){
                	ByteArrayInputStream bais = new ByteArrayInputStream(decoded);
                	BufferedImage inputImage = ImageIO.read(bais);
					String formatName = "PNG";
                	Layer layer = new Layer(inputImage);
                	int smallerX = Math.min(x1, x2);
                	int smallerY = Math.min(y1, y2);
					double ratio = 1;
                    if(layer.getWidth() > maxW){
                        ratio = layer.getWidth() / maxW;
                    }
                	Rectangle2D rect = new Rectangle2D.Double(smallerX * ratio, smallerY * ratio, width * ratio, height * ratio);
                	layer.crop(rect);
                	inputImage = layer.getImage();
                	ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                	ImageIO.write(inputImage, formatName, outStream);
                	decoded = outStream.toByteArray();
				}
                //creates folder path if it doesn't exist yet
                String path = "/content/dam/girlscouts-vtk/troop-data"+VtkUtil.getCurrentGSYear()+"/"+ troop.getTroop().getCouncilCode() +"/" + troop.getTroop().getTroopId() + "/imgLib";
                String pathWithFile = path+"/troop_pic.png/jcr:content";
                rr = sessionFactory.getResourceResolver();
                __session = rr.adaptTo(Session.class);                 
                Node baseNode = JcrUtil.createPath(path, "nt:folder", __session);
                ByteArrayInputStream byteStream = new ByteArrayInputStream(decoded);
                ValueFactory vf = __session.getValueFactory();
                Binary bin = vf.createBinary(byteStream);
                //for some reason, the data property can't be updated, just remade
                try{
                    __session.removeItem(path+"/troop_pic.png");
                	__session.save();
                } catch(Exception e){
                	vtklog.error("Exception occured:",e);
                }
                //creates file and jcr:content nodes if they don't exist yet
                Node jcrNode = JcrUtil.createPath(pathWithFile, false, "nt:file", "nt:resource", __session, false);
                jcrNode.setProperty("jcr:data",bin);
                jcrNode.setProperty("jcr:mimeType","image/png");
				__session.save();
			} catch (Exception e) {
				vtklog.error("Exception occured:",e);
			} finally {
				try {
					if( rr!=null )
						sessionFactory.closeResourceResolver( rr );
					if (__session != null)
						__session.logout();
				} catch (Exception ex) {
					vtklog.error("Exception occured:",ex);
				}
			}
		} else if (request.getParameter("viewProposedSched") != null) {
			vtklog.debug("viewProposedSched");
			String dates = calendarUtil.getSchedDates(user, troop,
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
			 for (int i = 0; i < troops.size(); i++) {
			  %><option value="<%=troops.get(i).getTroopId()%>"
	            <%=troop.getTroop().getTroopId().equals(troops.get(i).getTroopId()) ? "SELECTED"
	              : ""%>><%=troops.get(i).getTroopName()%>
	              :  <%=troops.get(i).getGradeLevel()%></option><%
	        }
	   		%></select><%
		}else if (request.getParameter("printCngYearPlans") != null) {
			vtklog.debug("printCngYearPlans");
			String ageLevel=  troop.getTroop().getGradeLevel();
			ageLevel= ageLevel.substring( ageLevel.indexOf("-")+1);
			ageLevel=ageLevel.toLowerCase().trim();
			String confMsg="";
			if( troop.getYearPlan()!=null ){
			  	if( troop.getYearPlan().getAltered()!=null && troop.getYearPlan().getAltered().equals("true") ){
			      	confMsg ="Are You Sure? You will lose customizations that you have made";
			  	}
			}
      		java.util.Iterator<YearPlan> yearPlans = yearPlanUtil.getAllYearPlans(user, ageLevel).listIterator();
        	while (yearPlans.hasNext()) {
          		YearPlan yearPlan = yearPlans.next();
			      %>
			      <div class="row">
			        <div class="columns large-push-2 medium-2 medium-push-2 small-2">
			       <input type="radio" <%=( troop.getYearPlan()!=null && (yearPlan.getName().equals(troop.getYearPlan().getName()))) ? " checked " : "" %>
			           id="r_<%=yearPlan.getId()%>" class="radio1" name="group1" onclick="chgYearPlan('<%=yearPlan.getId()%>', '<%=yearPlan.getPath()%>', '<%=confMsg%>', '<%=yearPlan.getName()%>', <%=troop.getYearPlan()!=null ? true: false %> ,'<%=troop.getYearPlan()!=null ? troop.getYearPlan().getName() : "" %>', false)" />
			            <label for="r_<%=yearPlan.getId()%>"></label>
		
			        </div>
			        <div class="small-18 columns large-pull-2 medium-pull-2 small-pull-2">
			            <a href="#" onclick="chgYearPlan('<%=yearPlan.getId()%>', '<%=yearPlan.getPath()%>', '<%=confMsg%>', '<%=yearPlan.getName()%>', <%=troop.getYearPlan()!=null ? true: false %> ,'<%=troop.getYearPlan()!=null ? troop.getYearPlan().getName() : "" %>', false)"><%=yearPlan.getName()%></a>
			            <p><%=yearPlan.getDesc()%></p>
			        </div>
			      </div><!--/row-->			
			<%}%>
			<div class="row">
	        	<!-- <div class="small-20 small-centered columns"> -->
	        	<!-- <div class="row"> -->
	        	<%Boolean condition = troop!=null  && troop.getSfTroopAge()!=null && !troop.getSfTroopAge().toLowerCase().contains("multilevel");  
	     		boolean isMeetingLib= true;
	      		if( troop!=null  && troop.getSfTroopAge()!=null && (troop.getSfTroopAge().toLowerCase().contains("senior") || troop.getSfTroopAge().toLowerCase().contains("cadette") || troop.getSfTroopAge().toLowerCase().contains("ambassador"))){
	      			isMeetingLib=false;
	      		}
	     		if(condition){ %>   
                    <div class="columns large-push-2 medium-2 medium-push-2 small-2">
		            <input type="radio" <%=( troop.getYearPlan()!=null && (troop.getYearPlan().getName().equals("Custom Year Plan"))) ? " checked " : "" %> id="r_0" class="radio1" name="group1"  onclick="chgYearPlan('', '', '<%=confMsg%>', 'Custom Year Plan', <%=troop.getYearPlan()!=null ? true: false %> ,'<%=troop.getYearPlan()!=null ? troop.getYearPlan().getName() : "" %>', <%= isMeetingLib %> )" />
		            <label for="r_0"></label> </div>
	            <%} %>
	        	<div class="small-18 columns large-pull-2 medium-pull-2 small-pull-2" style="<%= condition ? "padding-left:16px" : ""  %>"  >
	        		<div style="margin-left:-10px;margin-right: -10px;">
			            <a onclick="return chgYearPlan('', '', '<%=confMsg%>', 'Custom Year Plan', <%=troop.getYearPlan()!=null ? true: false %> ,'<%=troop.getYearPlan()!=null ? troop.getYearPlan().getName() : "" %>', <%= isMeetingLib %> )">	
			            <% if(troop!=null  && troop.getSfTroopAge()!=null &&
		                           (troop.getSfTroopAge().toLowerCase().contains("senior") || troop.getSfTroopAge().toLowerCase().contains("cadette") || troop.getSfTroopAge().toLowerCase().contains("ambassador") )){%>
		                        Customize Your Troop Year
		                 <%}else if(troop!=null  && troop.getSfTroopAge()!=null && troop.getSfTroopAge().toLowerCase().contains("multi-level")){ %>
		                      <h4 style="color:#18aa51;margin-bottom:15px !important;"> Create Your Multi-Level Troop Year Plan </h4>
		                 <%}else{ %>
		                       Create Your Own Year Plan
		                 <%} %>
			            </a>
	            		<p>
		            		<% if( troop!=null  && troop.getSfTroopAge()!=null && (troop.getSfTroopAge().toLowerCase().contains("senior") || troop.getSfTroopAge().toLowerCase().contains("cadette") || troop.getSfTroopAge().toLowerCase().contains("ambassador") )){%>
		                		Select this option to create activities or add council activities to your calendar.		            
		            		<%}else  if( troop!=null  && troop.getSfTroopAge()!=null && troop.getSfTroopAge().toLowerCase().contains("multi-level")){ %>
                            	<p style="margin-bottom:15px !important;">All Girls Scouts plan have been organized so you can easily filter through the set to select the right ones for your multi-level troop.. Once your meeting selections are made you'll be able to arrange and finalize the dates in the Year Plan view.</p>
	                            <p style="margin-bottom:15px !important;">You will begin by selecting the Girl Scout Levels and types of meetings you want to see.</p>
	                            <br/><input type="button" class="button" value="Create Your Year Plan" onclick="return chgYearPlan('', '', '<%=confMsg%>', 'Custom Year Plan', <%=isMeetingLib%>"/>
				            <%}else{ %>
			    	            Choose this option to create your own year plan using meetings from  our meeting library
				           	<%} %>
	            		</p>
	            	</div>
	        	</div>
        	<!-- </div> -->
        	<!-- </div> -->
      		</div><!--/row-->
      	<%
		}else if( request.getParameter("cngYear") != null ){
			vtklog.debug("cngYear");
	      	VtkUtil.cngYear(request, user, troop);
        }else if( request.getParameter("cngYearToCurrent") != null ){
        	vtklog.debug("cngYearToCurrent");
            user.setCurrentYear( VtkUtil.getCurrentGSYear()+"" );
            java.util.Set permis= org.girlscouts.vtk.auth.permission.Permission.getPermissionTokens(org.girlscouts.vtk.auth.permission.Permission.GROUP_LEADER_PERMISSIONS);
            org.girlscouts.vtk.salesforce.Troop newTroopCloned = ((org.girlscouts.vtk.salesforce.Troop)VtkUtil.deepClone(troop.getTroop()));
            newTroopCloned.setPermissionTokens( permis );
            troop.setTroop(newTroopCloned);
            if( !troopDAO.isArchivedYearPlan(user, troop,  ""+VtkUtil.getCurrentGSYear()) ){troop.setYearPlan(null);}
           
            //Cloned Troop object from archived year plan references archived year plan path ex: "/vtk2014/999/". It is necessary to change Troop path to current year ex: ""/vtk2015/999/"".
            troop.setPath( "/vtk"+VtkUtil.getCurrentGSYear()+"/"+troop.getSfCouncil() +"/troops/"+ troop.getSfTroopId() );
            session.putValue("VTK_troop", troop);
        }else if( request.getParameter("addNote") != null ){
            response.setContentType("application/json");
        	vtklog.debug("addNote");
            Note note = null;
            try{ 
            	List <Note> notes = meetingUtil.addNote(user, troop, request.getParameter("mid"), request.getParameter("message"));
            	out.println( new ObjectMapper().writeValueAsString(notes));
           	}catch(Exception e){
           		vtklog.error("Exception occured:",e);
           	}
        }else if(request.getParameter("rmNote") != null ){
            response.setContentType("application/json");
        	vtklog.debug("rmNote");
       	 	boolean isRm= false;
        	try{
				isRm = meetingUtil.rmNote(user, troop, request.getParameter("nid"));
        	}catch(Exception e){
       		 	vtklog.error("Exception occured:",e);
       		}      	 
       	 	if( !isRm ){
       		 	response.sendError(404, "Note not removed.");
       	 	}else{
       		    java.util.List <org.girlscouts.vtk.models.Note> notes = meetingUtil.getNotesByMid(  user,  troop, request.getParameter("mid") );
                out.println( new ObjectMapper().writeValueAsString(notes));
       	 	}
	        }else if( request.getParameter("editNote") != null ){  
	            out.println("{vtkresp:"+ meetingUtil.editNote(user, troop,request.getParameter("nid"), request.getParameter("msg") )+"}");
	        }else if( request.getParameter("getNotes") != null ){
	                response.setContentType("application/json");
	                java.util.List <org.girlscouts.vtk.models.Note> notes = meetingUtil.getNotesByMid(  user,  troop, request.getParameter("mid") ); 
	                out.println( new ObjectMapper().writeValueAsString(notes));
	        }else if(request.getParameter("addMeetings") != null){
	            meetingUtil.addMeetings(user, troop, request.getParameterValues("addMeetingMulti"));
	            %><script>self.location='/content/girlscouts-vtk/en/vtk.html';</script><% 
        }else if(request.getParameter("cngOutdoor") != null){
        	vtklog.debug("cngOutdoor");
        	String mid= request.getParameter("mid");
        	String aid= request.getParameter("aid");
        	boolean isOutdoor = "true".equals( request.getParameter("isOutdoor") ) ? true : false;      
            MeetingE meeting = VtkUtil.findMeetingById( troop.getYearPlan().getMeetingEvents(), mid );
        	Activity activity = VtkUtil.findActivityByPath( meeting.getMeetingInfo().getActivities(), aid );
        	//TODO meetingUtil.updateActivityOutdoorStatus(user, troop, meeting, activity, isOutdoor);
        }else if(request.getParameter("act") != null && "combineCal".equals(request.getParameter("act")) ){
        	vtklog.debug("combineCal");
        	calendarUtil.combineMeeting(user, troop, request.getParameter("mids"), request.getParameter("dt"));	   
        }else if(request.getParameter("act") != null && "hideVtkBanner".equals(request.getParameter("act")) ){    
        	vtklog.debug("hideVtkBanner");
            session.setAttribute("isHideVtkBanner", "true");
        }else if(request.getParameter("act") != null && "hideVtkMaintenance".equals(request.getParameter("act")) ){
            vtklog.debug("hideVtkMaintenance");
            session.setAttribute("isHideVtkMaintenance", "true");
        }else if( request.getParameter("alex658Xf409Re49v") !=null){
        	vtklog.debug("alex658Xf409Re49v");
        	try{ yearPlanUtil.GSMonthlyDetailedRpt( request.getParameter("year") ); }catch(Exception e){vtklog.error("Exception occured:",e);}
        }else if( request.getParameter("alex344") !=null){
        	vtklog.debug("alex344");
        	try{ yearPlanUtil.GSRptCouncilPublishFinance(); }catch(Exception e){vtklog.error("Exception occured:",e);}
        }else if( "switchFinanceYear".equals(request.getParameter("act") ) ){
        	vtklog.debug("switchFinanceYear");
        	int financeYear = 0;
        	try{ 
       			financeYear = Integer.parseInt(request.getParameter("financeYear") );
       			user.setCurrentFinanceYear( financeYear );
        	}catch(Exception e){
        		vtklog.error("Exception occured:",e);
        	}	
        }else if(request.getParameter("act") != null && "selectSubActivity".equals(request.getParameter("act"))){
        	vtklog.debug("selectSubActivity");
            String mPath = request.getParameter("mPath");
            String activityPath = request.getParameter("aPath");
            String subActivityPath = request.getParameter("subAPath");
            meetingUtil.setSelectedSubActivity(user, troop, mPath, activityPath, subActivityPath);
     
        }else if( request.getParameter("whatIsMyLevel") != null ){
        	vtklog.debug("whatIsMyLevel");
        	String myLevel= "";
        	if( troop!=null && troop.getTroop() !=null &&  troop.getTroop().getGradeLevel()!=null ){

        		myLevel= troop.getTroop().getGradeLevel();
        	}
        	response.setContentType("application/json");
        	out.println("{\"level\":\""+ myLevel +"\"}");
        } else {
        	vtklog.debug("no request parameters");
		}
	} catch (Exception e) {
		vtklog.error("Exception occured:",e);
	}
%>
