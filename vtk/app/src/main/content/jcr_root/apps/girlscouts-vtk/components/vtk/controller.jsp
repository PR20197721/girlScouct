<%@page
	import="java.util.Comparator,org.codehaus.jackson.map.ObjectMapper,org.joda.time.LocalDate,java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*,
                org.girlscouts.vtk.modifiedcheck.ModifiedChecker, com.day.image.Layer, java.awt.geom.Rectangle2D, java.awt.geom.Rectangle2D.Double, com.day.cq.commons.jcr.JcrUtil, org.apache.commons.codec.binary.Base64, com.day.cq.commons.ImageHelper, com.day.image.Layer, java.io.ByteArrayInputStream, java.io.ByteArrayOutputStream, java.awt.image.BufferedImage, javax.imageio.ImageIO,
                org.girlscouts.vtk.helpers.TroopHashGenerator"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>

<cq:defineObjects />
<%@include file="include/session.jsp"%>
<%
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
			if (request.getParameter("act") != null)
				aContr = org.girlscouts.vtk.models.ActionController
						.valueOf(request.getParameter("act"));
		} catch (java.lang.IllegalArgumentException iae) {
		}

		if (aContr != null)
			switch (aContr) {
			case ChangeMeetingPositions:

				String x = request.getParameter("isMeetingCngAjax");
				while (x.indexOf(",,") != -1) {
					x = x.replaceAll(",,", ",");
				}

				StringTokenizer t = new StringTokenizer(x, ",");
				if (troop.getYearPlan().getMeetingEvents().size() != t
						.countTokens()) {
					String tmp = x;
					if (!tmp.startsWith(","))
						tmp = "," + tmp;
					if (!tmp.endsWith(","))
						tmp = tmp + ",";
					for (int i = troop.getYearPlan().getMeetingEvents()
							.size(); i > 0; i--)
						if (tmp.indexOf("," + i + ",") == -1)
							x = i + "," + x;

				}

				meetingUtil.changeMeetingPositions(user, troop, x);
				//meetingUtil.changeMeetingPositions( user, troop, request.getParameter("isMeetingCngAjax") );
				return;
			case CreateActivity:
				yearPlanUtil
						.createActivity(
								user,
								troop,
								new Activity(
										request.getParameter("newCustActivity_name"),
										request.getParameter("newCustActivity_txt"),
										VtkUtil.parseDate(
												VtkUtil.FORMAT_FULL,
												request.getParameter("newCustActivity_date")
														+ " "
														+ request
																.getParameter("newCustActivity_startTime")
														+ " "
														+ request
																.getParameter("newCustActivity_startTime_AP")),
										VtkUtil.parseDate(
												VtkUtil.FORMAT_FULL,
												request.getParameter("newCustActivity_date")
														+ " "
														+ request
																.getParameter("newCustActivity_endTime")
														+ " "
														+ request
																.getParameter("newCustActivity_endTime_AP")),
										request.getParameter("newCustActivityLocName"),
										request.getParameter("newCustActivityLocAddr"),
										VtkUtil.convertObjectToDouble(request
												.getParameter("newCustActivity_cost"))));
				return;
			case CreateSchedule:
				try {

					session.putValue("VTK_planView_memoPos", null);
					calendarUtil
							.createSched(
									user,
									troop,
									request.getParameter("calFreq"),
									new org.joda.time.DateTime(
											VtkUtil.parseDate(
													VtkUtil.FORMAT_FULL,
													request.getParameter("calStartDt")
															+ " "
															+ request
																	.getParameter("calTime")
															+ " "
															+ request
																	.getParameter("calAP"))),
									request.getParameter("exclDt"),
									Long.parseLong((request
											.getParameter("orgDt") == null || request
											.getParameter("orgDt")
											.equals("")) ? "0"
											: request
													.getParameter("orgDt")));
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			case SelectYearPlan:
				try {
					troopUtil.selectYearPlan(user, troop,
							request.getParameter("addYearPlanUser"),
							request.getParameter("addYearPlanName"));
				} catch (VtkYearPlanChangeException e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
					out.println(e.getMessage());
				}

				return;
			case AddLocation:

				locationUtil.addLocation(
						user,
						troop,
						new Location(request.getParameter("name"),
								request.getParameter("address"),
								request.getParameter("city"), request
										.getParameter("state"), request
										.getParameter("zip")));
				return;
			case RemoveLocation:
				locationUtil.removeLocation(user, troop,
						request.getParameter("rmLocation"));
				return;
			case CreateCustomAgenda:
				meetingUtil.createCustomAgenda(user, troop, request
						.getParameter("name"), request
						.getParameter("newCustAgendaName"), Integer
						.parseInt(request.getParameter("duration")),
						Long.parseLong(request
								.getParameter("startTime")), request
								.getParameter("txt"));
				return;
			case SetLocationAllMeetings:
				locationUtil
						.setLocationAllMeetings(
								user,
								troop,
								request.getParameter("setLocationToAllMeetings"));
				return;
			case UpdateSched:
				boolean isSucc = calendarUtil.updateSched(user, troop,
						request.getParameter("meetingPath"),
						request.getParameter("time"),
						request.getParameter("date"),
						request.getParameter("ap"),
						request.getParameter("isCancelledMeeting"),
						Long.parseLong(request.getParameter("currDt")));

				if (!isSucc) {
					response.sendError(499,
							"Date already exists in schedule");
				}
				return;
			case RemoveCustomActivity:
				meetingUtil.rmCustomActivity(user, troop,
						request.getParameter("rmCustActivity"));
				return;
			case ChangeLocation:
				locationUtil.changeLocation(user, troop,
						request.getParameter("chnLocation"),
						request.getParameter("newLocPath"));
				return;
			case SwapMeetings:
				meetingUtil.swapMeetings(user, troop,
						request.getParameter("fromPath"),
						request.getParameter("toPath"));
				return;
			case AddMeeting:
				meetingUtil.addMeetings(user, troop,
						request.getParameter("toPath"));
				return;
			case RearrangeActivity:
				try {
					meetingUtil.rearrangeActivity(user, troop,
							request.getParameter("mid"),
							request.getParameter("isActivityCngAjax"));
				} catch (java.lang.IllegalAccessException e) {
					e.printStackTrace();
				}
				return;
			case RemoveAgenda:
				meetingUtil.rmAgenda(user, troop,
						request.getParameter("rmAgenda"),
						request.getParameter("mid"));
				return;
			case EditAgendaDuration:
				//request.getParameter("aid") +" : "+request.getParameter("mid"));
				meetingUtil.editAgendaDuration(user, troop, Integer
						.parseInt(request
								.getParameter("editAgendaDuration")),
						request.getParameter("aid"), request
								.getParameter("mid"));
				return;
			case RevertAgenda:
				meetingUtil.reverAgenda(user, troop,
						request.getParameter("mid"));
				return;
			case ReLogin:
				troopUtil.reLogin(user, troop,
						request.getParameter("loginAs"), session);
				// Generator the new troopDataToken so the client can fetch data from the dispatcher.
				Troop newTroop = (Troop)session.getAttribute("VTK_troop");
				String troopId = newTroop.getTroop().getTroopId();
				System.out.println("New Troop Id = " + troopId);
				TroopHashGenerator generator = sling.getService(TroopHashGenerator.class);
				String token = generator.hash(troopId);
				Cookie cookie = new Cookie("troopDataToken", token);
				cookie.setPath("/");
				response.addCookie(cookie);
				return;
			case AddAid:
				if (request.getParameter("assetType").equals("AID")) {
					meetingUtil.addAids(user, troop, request
							.getParameter("addAids"), request
							.getParameter("meetingId"),
							java.net.URLDecoder.decode(request
									.getParameter("assetName")),
							request.getParameter("assetDocType"));
				} else {
					meetingUtil.addResource(user, troop, request
							.getParameter("addAids"), request
							.getParameter("meetingId"),
							java.net.URLDecoder.decode(request
									.getParameter("assetName")),
							request.getParameter("docType"));
				}
				return;
			case RemoveAsset:
				meetingUtil.rmAsset(user, troop,
						request.getParameter("rmAsset"),
						request.getParameter("meetingId"));
				return;
			case BindAssetToYPC:
				vtkErr = troopUtil.bindAssetToYPC(user, troop,
						request.getParameter("bindAssetToYPC"),
						request.getParameter("ypcId"),
						request.getParameter("assetDesc"),
						request.getParameter("assetTitle"));
				return;
			case EditCustActivity:
				vtkErr = troopUtil.editCustActivity(user, troop,
						request);
				return;
			case Search:
				yearPlanUtil.search(user, troop, request);
				return;
			case CreateCustomActivity:
				yearPlanUtil
						.createCustActivity(
								user,
								troop,
								(java.util.List<org.girlscouts.vtk.models.Activity>) session
										.getValue("vtk_search_activity"),
								request.getParameter("newCustActivityBean"));
				return;
			case isAltered:
				out.println(yearPlanUtil.isYearPlanAltered(user, troop));
				return;
			case GetFinances:
				financeUtil.getFinances(user, troop, Integer.parseInt(request
						.getParameter("finance_qtr")), user
						.getCurrentYear());
				return;
			case UpdateFinances:
				financeUtil.updateFinances(user, troop,
						user.getCurrentYear(),
						request.getParameterMap());
				financeUtil.sendFinanceDataEmail(user, troop,
						Integer.parseInt(request.getParameter("qtr")),
						user.getCurrentYear());
				return;
			case UpdateFinanceAdmin:
				financeUtil.updateFinanceConfiguration(user, troop,
						user.getCurrentYear(),
						request.getParameterMap());
				return;
			case RmMeeting:
				meetingUtil.createMeetingCanceled(user, troop,
						request.getParameter("mid"),
						Long.parseLong(request.getParameter("rmDate")));
				meetingUtil.rmMeeting(user, troop,
						request.getParameter("mid"));
				meetingUtil.rmSchedDate(user, troop,
						Long.parseLong(request.getParameter("rmDate")));
				return;
			case UpdAttendance:
				meetingUtil.updateAttendance(user, troop, request);
				meetingUtil.updateAchievement(user, troop, request);
				return;
			case CreateCustomYearPlan:

				meetingUtil.createCustomYearPlan(user, troop,
						request.getParameter("mids"));
				return;
			default:
				break;
			}

		if (request.getParameter("admin_login") != null) {
			if (session.getValue("VTK_ADMIN") == null) {
				String u = request.getParameter("usr");
				String p = request.getParameter("pswd");
				if (u.equals("admin") && p.equals("icruise123"))
					session.putValue("VTK_ADMIN", u);
			}
			response.sendRedirect("/content/girlscouts-vtk/en/vtk.admin.home.html");
/*
		} else if (request.getParameter("sendMeetingReminderEmail_SF") != null) { //view SalesForce
			String email_to_gp = request.getParameter("email_to_gp");
			String email_to_sf = request.getParameter("email_to_sf");
			String email_to_tv = request.getParameter("email_to_tv");
			String cc = request.getParameter("email_to_cc");
			String subj = request.getParameter("email_subj");
			String html = request.getParameter("email_htm");

			EmailMeetingReminder emr = new EmailMeetingReminder(null,
					null, cc, subj, html);
			emr.setEmailToGirlParent(email_to_gp);
			emr.setEmailToSelf(email_to_sf);
			emr.setEmailToTroopVolunteer(email_to_tv);
			emailUtil.sendMeetingReminder(troop, emr);
			*/
		} else if (request.getParameter("previewMeetingReminderEmail") != null) {
			String email_to_gp = request.getParameter("email_to_gp");
			//String email_to_sf = request.getParameter("email_to_sf");
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
			//if (email_to_sf.equals("true")) {
			emr.setEmailToSelf("true");
			emr.setTo(user.getApiConfig().getUser().getEmail());
			//}
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
				//emr.setEmailToGirlParent(emails);
				emr.setEmailToGirlParent("true");

			}

			if (email_to_tv.equals("true")) {
				//emr.setEmailToTroopVolunteer(email_to_tv);
				emr.setEmailToTroopVolunteer("true");
				/*Troop Volunteers data needed */
			}

			troop.setSendingEmail(emr);

		} else if (request.getParameter("sendMeetingReminderEmail") != null) { //view smpt
			// /gscontroller/vtk/action/sendMeetingReminderEmail parameters  
			EmailMeetingReminder emr = null;
			if (troop.getSendingEmail() != null) {
				emr = troop.getSendingEmail();
			} else {
				System.out.println("emr does not exit!");
			}

			org.girlscouts.vtk.ejb.Emailer emailer = sling
					.getService(org.girlscouts.vtk.ejb.Emailer.class);
			emailer.send(user, emr);
			try {
				meetingUtil.saveEmail(user, troop, emr.getMeetingId());
			} catch (Exception e) {
				e.printStackTrace();
			}
			troop.setSendingEmail(null);

		} else if (request.getParameter("testAB") != null) {

			
			boolean isUsrUpd = false;
			try {
				troop.setRetrieveTime(new java.util.Date());
				isUsrUpd = troopUtil.updateTroop(user, troop);
			} catch (IllegalAccessException iae) {
				iae.printStackTrace();
			}
			if (!isUsrUpd)
				vtkErr += vtkErr
						.concat("Warning: You last change was not saved.");

		} else if (request.getParameter("id") != null) {

			java.util.List<MeetingE> meetings = troop.getYearPlan()
					.getMeetingEvents();
			for (MeetingE m : meetings) {
				if (m.getUid().equals(request.getParameter("mid"))) {
					Meeting custM = m.getMeetingInfo();
					if (request.getParameter("id").equals(
							"editMeetingName")) {
						custM.setName(request.getParameter("newvalue"));
					} else if (request.getParameter("id").equals(
							"editMeetingDesc")) {
						java.util.Map<String, JcrCollectionHoldString> meetingInfoItems = custM
								.getMeetingInfo();
						meetingInfoItems.put(
								"meeting short description",
								new JcrCollectionHoldString(request
										.getParameter("newvalue")));
					} else if (request.getParameter("id").equals(
							"editMeetingOverview")) {
						java.util.Map<String, JcrCollectionHoldString> meetingInfoItems = custM
								.getMeetingInfo();
						meetingInfoItems.put(
								"overview",
								new JcrCollectionHoldString(request
										.getParameter("newvalue")));
					} else if (request.getParameter("id").equals(
							"editMeetingActivity")) {
						java.util.Map<String, JcrCollectionHoldString> meetingInfoItems = custM
								.getMeetingInfo();
						meetingInfoItems.put(
								"detailed activity plan",
								new JcrCollectionHoldString(request
										.getParameter("newvalue")));
					} else if (request.getParameter("id").equals(
							"editMeetingMaterials")) {
						java.util.Map<String, JcrCollectionHoldString> meetingInfoItems = custM
								.getMeetingInfo();
						meetingInfoItems.put(
								"materials",
								new JcrCollectionHoldString(request
										.getParameter("newvalue")));
					}

					try {
						if (!m.getRefId().contains("_")) {
							yearPlanUtil.createCustomMeeting(user,
									troop, m, custM);
						} else {
							yearPlanUtil.updateCustomMeeting(user,
									troop, m, custM);
						}
						out.println(request.getParameter("newvalue"));
					} catch (Exception e) {
						e.printStackTrace();
					}

					break;
				}
			}

		} else if (request.getParameter("test") != null) {

			ObjectMapper mapper = new ObjectMapper();

			org.girlscouts.vtk.salesforce.Troop prefTroop = apiConfig
					.getTroops().get(0);
			for (int ii = 0; ii < apiConfig.getTroops().size(); ii++) {
				if (apiConfig.getTroops().get(ii).getTroopId()
						.equals(troop.getSfTroopId())) {
					prefTroop = apiConfig.getTroops().get(ii);
					break;
				}
			}

			if (userUtil.isCurrentTroopId_NoRefresh(troop,
					user.getSid())) {
				return;
			} else {
				;
			}

			troop = troopUtil.getTroop(user,
					"" + prefTroop.getCouncilCode(),
					prefTroop.getTroopId());
			session.setAttribute("VTK_troop", troop);

			//if alter = timestamp change
			out.println(mapper.writeValueAsString(troop));

		} else if (request.getParameter("test1") != null) {

		} else if (request.getParameter("editMtLogo") != null) {

			;

		} else if (request.getParameter("updateCouncilMilestones") != null) {

			String councilId = request.getParameter("cid");

			java.util.List<Milestone> milestones = yearPlanUtil
					.getCouncilMilestones(councilId);
			for (int i = 0; i < milestones.size(); i++) {

				Milestone m = milestones.get(i);
				String blurb = request.getParameter("blurb" + i);
				String date = request.getParameter("date" + i);

				m.setBlurb(blurb);
				m.setDate(new java.util.Date(date));

			}

			//yearPlanUtil.saveCouncilMilestones(milestones);
			response.sendRedirect("/content/girlscouts-vtk/en/vtk.admin.milestones.html");

		} else if (request.getParameter("saveCouncilMilestones") != null) {

			String councilId = request.getParameter("cid");
			java.util.List<Milestone> milestones = new ArrayList<Milestone>();
			String[] blurbs = request.getParameterValues("ms_blurb[]");
			String[] dates = request.getParameterValues("ms_date[]");
			//String[] shows2 = request.getParameterValues("show_ch[]");
			String[] shows = request.getParameterValues("ms_show[]");
			if (blurbs != null) {
				for (int i = 0; i < blurbs.length; i++) {
					String blurb = blurbs[i];
					if (blurb == null || blurb.trim().isEmpty()) {
						break;
					}
					boolean show = shows[i].equals("true");
					Date date = null;
					if (!dates[i].isEmpty()) {
						date = VtkUtil.parseDate(
								VtkUtil.FORMAT_MMddYYYY, dates[i]);
					}

					Milestone m = new Milestone(blurb, show, date);
					milestones.add(m);
				}
			}

			yearPlanUtil.saveCouncilMilestones(milestones, councilId);
			response.sendRedirect("/content/girlscouts-vtk/en/vtk.admin_milestones.html");

		} else if (request.getParameter("createCouncilMilestones") != null) {

			String councilId = request.getParameter("cid");
			java.util.List<Milestone> milestones = yearPlanUtil
					.getCouncilMilestones(councilId);

			Milestone m = new Milestone();
			m.setBlurb(request.getParameter("blurb"));
			m.setDate(new java.util.Date(request.getParameter("date")));
			milestones.add(m);

			response.sendRedirect("/content/girlscouts-vtk/en/vtk.admin.milestones.html");

		} else if (request.getParameter("removeCouncilMilestones") != null) {

			java.util.List<Milestone> milestones = troop.getYearPlan()
					.getMilestones();
			for (int i = 0; i < milestones.size(); i++) {

				Milestone m = milestones.get(i);
				if (m.getUid()
						.equals(request
								.getParameter("removeCouncilMilestones"))) {
					milestones.remove(m);

					boolean isUsrUpd = troopUtil.updateTroop(user,
							troop);
					if (!isUsrUpd)
						vtkErr += vtkErr
								.concat("Warning: You last change was not saved.");

					response.sendRedirect("/content/girlscouts-vtk/en/vtk.admin.milestones.html");
					return;
				}
			}

		} else if (request.getParameter("resetCal") != null) {

			calendarUtil.resetCal(user, troop);
			out.println("Cal reset");

		} else if (request.getParameter("chngPermis") != null) {

			switch (Integer
					.parseInt(request.getParameter("chngPermis"))) {
			case 2:
				troop.getTroop()
						.setPermissionTokens(
								Permission
										.getPermissionTokens(Permission.GROUP_GUEST_PERMISSIONS));
				break;
			case 11:

				troop.getTroop()
						.setPermissionTokens(
								Permission
										.getPermissionTokens(Permission.GROUP_LEADER_PERMISSIONS));
				break;
			case 12:

				troop.getTroop()
						.setPermissionTokens(
								Permission
										.getPermissionTokens(Permission.GROUP_MEMBER_2G_PERMISSIONS));
				break;
			case 13:
				troop.getTroop()
						.setPermissionTokens(
								Permission
										.getPermissionTokens(Permission.GROUP_MEMBER_1G_PERMISSIONS));
				break;

			case 14:
				troop.getTroop()
						.setPermissionTokens(
								Permission
										.getPermissionTokens(Permission.GROUP_MEMBER_NO_TROOP_PERMISSIONS));
				break;

			case 15:
				troop.getTroop()
						.setPermissionTokens(
								Permission
										.getPermissionTokens(Permission.GROUP_MEMBER_TROOP_PERMISSIONS));
				break;

			default:
				troop.getTroop()
						.setPermissionTokens(
								Permission
										.getPermissionTokens(Permission.GROUP_GUEST_PERMISSIONS));

				break;
			}

		} else if (request.getParameter("Impersonate4S") != null) {

			troopUtil.impersonate(user, troop,
					request.getParameter("councilCode"),
					request.getParameter("troopId"), session);
			Troop x = (Troop) session.getAttribute("VTK_troop");
			response.sendRedirect("/content/girlscouts-vtk/en/vtk.html");
		} else if (request.getParameter("addAsset") != null) { //not in switch?? not used?
			//org.girlscouts.vtk.models.Asset asset = new org.girlscouts.vtk.models.Asset(request.getParameter("addAsset"));
			troopUtil.addAsset(
					user,
					troop,
					request.getParameter("meetingUid"),
					new org.girlscouts.vtk.models.Asset(request
							.getParameter("addAsset")));
		} else if (request.getParameter("reactjs") != null) {

			boolean isFirst = false;
			if (request.getParameter("isFirst") != null
					&& request.getParameter("isFirst").equals("1")) {
				isFirst = true;
			}

			boolean isCng = false;

			if (!isFirst && troop.getYearPlan()!=null) {
				ModifiedChecker modifiedChecker = sling
						.getService(ModifiedChecker.class);

				// TODO: This is a special logic. "X". Rewrite ModifiedChecker later. There are two places in this file.
				isCng = modifiedChecker.isModified(
						"X" + session.getId(), troop.getYearPlan()
								.getPath());

			}

			if (isFirst || isCng) {

				org.girlscouts.vtk.salesforce.Troop prefTroop = apiConfig
						.getTroops().get(0);
				for (int ii = 0; ii < apiConfig.getTroops().size(); ii++) {
					if (apiConfig.getTroops().get(ii).getTroopId()
							.equals(troop.getSfTroopId())) {
						prefTroop = apiConfig.getTroops().get(ii);
						break;
					}
				}

				troop = troopUtil.getTroop(user,
						"" + prefTroop.getCouncilCode(),
						prefTroop.getTroopId());
				PlanView planView = meetingUtil.planView(user, troop,
						request);

				java.util.List<MeetingE> TMP_meetings = troop.getYearPlan().getMeetingEvents();
				//planView.getYearPlanComponent().getUid()
				//for (int i = 0; i < meetings.size(); i++) {
					
					MeetingE _meeting = (MeetingE)planView.getYearPlanComponent(); // meetings.get(i);
					java.util.List<MeetingE> meetings = new java.util.ArrayList();
					meetings.add(_meeting);
					troop.getYearPlan().setMeetingEvents(meetings);
	System.err.println("tata: "+  _meeting.getUid() +" : "+ planView.getYearPlanComponent().getUid())	;			
//?if( ! _meeting.getUid().equals(  request.getParameter("reactjs") )){ _meeting=null;continue;}


/*a&a*/
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
/*a&a end*/



/* 6/15/15 pop mult loc
if( _meeting.getLocationRef()!=null && troop.getYearPlan().getLocations()!=null ) {
    for(int k=0;k<troop.getYearPlan().getLocations().size();k++){
        if( troop.getYearPlan().getLocations().get(k).getPath().equals( _meeting.getLocationRef() ) ){
        	java.util.List<Location> locations= new java.util.ArrayList<Location>();
        	locations.add( troop.getYearPlan().getLocations().get(k) );
        	troop.getYearPlan().setLocations(locations);
        }
    }
}
*/



					if (_meeting.getMeetingInfo() != null
							&& _meeting.getMeetingInfo()
									.getActivities() != null) {

						if (request.getParameter("isActivNew") != null
								&& request.getParameter("isActivNew").equals("1")) {
							_meeting.getMeetingInfo().setActivities(null);

						} else {
							java.util.List<Activity> _activities = _meeting
									.getMeetingInfo().getActivities();

							_meeting.getMeetingInfo()
									.getMeetingInfo()
									.put("meeting short description",
											new JcrCollectionHoldString(
													org.apache.commons.lang.StringEscapeUtils
															.unescapeHtml(_meeting
																	.getMeetingInfo()
																	.getMeetingInfo()
																	.get("meeting short description")
																	.getStr())));

							java.util.Comparator<Activity> comp = new org.apache.commons.beanutils.BeanComparator(
									"activityNumber");
							Collections.sort(_activities, comp);
						}

					}
				//}

				
				if( troop!=null && troop.getYearPlan()!=null){
					Helper helper = troop.getYearPlan().getHelper();
					if( helper==null ) helper= new Helper();
					helper.setNextDate(planView.getNextDate());
					helper.setPrevDate(planView.getPrevDate());
					helper.setCurrentDate(planView.getSearchDate().getTime());
					java.util.ArrayList <String> permissions= new java.util.ArrayList<String>();
					if (troop != null && userUtil.hasPermission(user.getPermissions(), Permission.PERMISSION_EDIT_MEETING_ID))
						permissions.add(String.valueOf(Permission.PERMISSION_EDIT_MEETING_ID));
					if (troop != null && userUtil.hasPermission(user.getPermissions(), Permission.PERMISSION_VIEW_ACTIVITY_PLAN_ID))
                        permissions.add(String.valueOf(Permission.PERMISSION_VIEW_ACTIVITY_PLAN_ID));
					if (troop != null && userUtil.hasPermission(user.getPermissions(), Permission.PERMISSION_SEND_EMAIL_MT_ID))
                        permissions.add(String.valueOf(Permission.PERMISSION_SEND_EMAIL_MT_ID));
					if (troop != null && userUtil.hasPermission(user.getPermissions(), Permission.PERMISSION_VIEW_ATTENDANCE_ID))
                        permissions.add(String.valueOf(Permission.PERMISSION_VIEW_ATTENDANCE_ID));
					helper.setPermissions(permissions);
					
					
					helper.setAchievementCurrent(achievementCurrent);
					helper.setAttendanceCurrent(attendanceCurrent);
					helper.setAttendanceTotal(attendanceTotal);
					troop.getYearPlan().setHelper(helper);
				}
				
				troop.setTroop(prefTroop);
				troop.setSfTroopId(troop.getTroop().getTroopId());
				troop.setSfUserId(user.getApiConfig().getUserId());
				troop.setSfTroopName(troop.getTroop().getTroopName());
				troop.setSfTroopAge(troop.getTroop().getGradeLevel());
				troop.setSfCouncil(troop.getTroop().getCouncilCode()
						+ "");
				session.putValue("VTK_troop", troop);

				ObjectMapper mapper = new ObjectMapper();
				out.println(mapper.writeValueAsString(troop)
						.replaceAll("mailto:", "")
						.replaceAll("</a>\"</a>", "</a>")
						.replaceAll("\"</a>\"", ""));
				
				
				troop.getYearPlan().setMeetingEvents(TMP_meetings);
				session.putValue("VTK_troop", troop);

			}

		} else if (request.getAttribute("yearPlanSched") != null || request.getParameter("yearPlanSched") != null) {

			if (troop.getYearPlan() == null){
				ObjectMapper mapper = new ObjectMapper();
                out.println("{\"yearPlan\":\"NYP\"}");
				return;
			}

			boolean isFirst = false;
			if ((request.getAttribute("isFirst") != null
					&& ((String)request.getAttribute("isFirst")).equals("1")) || (request.getParameter("isFirst") != null && request.getParameter("isFirst").equals("1"))) {
				isFirst = true;
			}

			boolean isCng = false;
			if (!isFirst) {
				ModifiedChecker modifiedChecker = sling
						.getService(ModifiedChecker.class);
				isCng = modifiedChecker.isModified(
						"X" + session.getId(), troop.getYearPlan()
								.getPath());
			}

			if (isFirst || isCng) {
				org.girlscouts.vtk.salesforce.Troop prefTroop = apiConfig
						.getTroops().get(0);
				for (int ii = 0; ii < apiConfig.getTroops().size(); ii++) {
					if (apiConfig.getTroops().get(ii).getTroopId()
							.equals(troop.getSfTroopId())) {
						prefTroop = apiConfig.getTroops().get(ii);
						break;
					}
				}

				troop = troopUtil.getTroop(user,
						"" + prefTroop.getCouncilCode(),
						prefTroop.getTroopId());

				java.util.Map<java.util.Date, YearPlanComponent> sched = meetingUtil
						.getYearPlanSched(user, troop.getYearPlan(), true, true);
					
				//start milestone
				try {
					if (troop.getYearPlan() != null) {
						troop.getYearPlan() .setMilestones(
							yearPlanUtil.getCouncilMilestones("" + troop.getSfCouncil()));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (troop.getYearPlan().getMilestones() == null)
					troop.getYearPlan().setMilestones(
							new java.util.ArrayList());

				for (int i = 0; i < troop.getYearPlan()
							.getMilestones().size(); i++){
						if (troop.getYearPlan().getMilestones().get(i)
								.getDate() != null)
							sched.put(troop.getYearPlan()
									.getMilestones().get(i).getDate(),
									troop.getYearPlan().getMilestones()
											.get(i));
				}

				//edn milestone

				troop.setTroop(prefTroop);
				troop.setSfTroopId(troop.getTroop().getTroopId());
				troop.setSfUserId(user.getApiConfig().getUserId());
				troop.setSfTroopName(troop.getTroop()
						.getTroopName());
				troop.setSfTroopAge(troop.getTroop()
						.getGradeLevel());
				troop.setSfCouncil(troop.getTroop()
						.getCouncilCode() + "");
					
							
				session.putValue("VTK_troop", troop);

	Object tmp[] = sched.values().toArray();
	for(int i=0;i<tmp.length;i++){
		try{
							((MeetingE) tmp[i]).getMeetingInfo()
									.setActivities(null);
							((MeetingE) tmp[i]).getMeetingInfo()
									.setMeetingInfo(null);
							((MeetingE) tmp[i]).getMeetingInfo()
									.setResources(null);
							((MeetingE) tmp[i]).getMeetingInfo()
									.setAgenda(null);
						} catch (Exception e) {
	}
					}

				ObjectMapper mapper = new ObjectMapper();
				out.println("{\"yearPlan\":\""
						+ troop.getYearPlan().getName()
						+ "\",\"schedule\":");
					out.println(mapper.writeValueAsString(sched)
							.replaceAll("mailto:", ""));
				out.println("}");
			}

		} else if (request.getParameter("reactActivity") != null) {

			boolean isFirst = false;
			if (request.getParameter("isFirst") != null
					&& request.getParameter("isFirst").equals("1")) {
				isFirst = true;
			}

			boolean isCng = false;

			if (!isFirst && troop.getYearPlan()!=null) {
				ModifiedChecker modifiedChecker = sling
						.getService(ModifiedChecker.class);
				isCng = modifiedChecker.isModified(
						"X" + session.getId(), troop.getYearPlan().getPath());
			}

			if (isFirst || isCng) {

				org.girlscouts.vtk.salesforce.Troop prefTroop = apiConfig
						.getTroops().get(0);
				for (int ii = 0; ii < apiConfig.getTroops().size(); ii++) {
					if (apiConfig.getTroops().get(ii).getTroopId()
							.equals(troop.getSfTroopId())) {
						prefTroop = apiConfig.getTroops().get(ii);
						break;
					}
				}

				Activity currentActivity = null;
				troop = troopUtil.getTroop(user,
						"" + prefTroop.getCouncilCode(),
						prefTroop.getTroopId());

				PlanView planView = meetingUtil.planView(user, troop, request);
				java.util.List<Activity> activities = troop
						.getYearPlan().getActivities();
				for (int i = 0; i < activities.size(); i++) {
					if (activities
							.get(i)
							.getUid()
							.equals(planView.getYearPlanComponent().getUid()))
						currentActivity = activities.get(i);
				}

				YearPlan yearPlan = new YearPlan();
				
				
			
				if( troop!=null && troop.getYearPlan()!=null){
                    Helper helper = troop.getYearPlan().getHelper();
                    if( helper==null ) helper= new Helper();
                    helper.setNextDate(planView.getNextDate());
                    helper.setPrevDate(planView.getPrevDate());
                    helper.setCurrentDate(planView.getSearchDate().getTime());
                    helper.setSfTroopAge( troop.getSfTroopAge());
                    java.util.ArrayList <String> permissions= new java.util.ArrayList<String>();
                    
                    if (troop != null && userUtil.hasPermission(user.getPermissions(), Permission.PERMISSION_SEND_EMAIL_ACT_ID))
                        permissions.add(String.valueOf(Permission.PERMISSION_SEND_EMAIL_ACT_ID));
                   
                    helper.setPermissions(permissions);
                    
                    yearPlan.setHelper(helper);
                }
				
				
				java.util.List<Activity> _activities= new java.util.ArrayList();
				_activities.add( currentActivity );
				yearPlan.setActivities( _activities);
				ObjectMapper mapper = new ObjectMapper();
				out.println(mapper.writeValueAsString(yearPlan));
				//orgi out.println(mapper.writeValueAsString(currentActivity));

			}

		} else if (request.getParameter("isRmTroopImg") != null) {

			try {

				Session __session = sessionFactory.getSession();

				String troopPhotoUrl = "/content/dam/girlscouts-vtk/troop-data/"
						+ troop.getTroop().getCouncilCode()
						+ "/"
						+ troop.getTroop().getTroopId()
						+ "/imgLib/troop_pic.png";

				__session.removeItem(troopPhotoUrl);
				__session.save();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (request.getParameter("isAdminRpt") != null) {
			final CouncilRpt councilRpt = sling
					.getService(CouncilRpt.class);
			java.util.Map container = councilRpt.getTroopNames(
					request.getParameter("cid"),
					request.getParameter("ypPath"));
			java.util.Iterator itr = container.keySet().iterator();
			while (itr.hasNext()) {
				String troopId = (String) itr.next();
				String troopName = (String) container.get(troopId);
					%>$("#<%=troopId%>").html("<%=troopName%>");<%
	}
		} else if (request.getParameter("getEventImg") != null) {


			try {
				String imgPath = request.getParameter("path")
						+ "/jcr:content/data/image";

				Resource imgResource = resourceResolver
						.getResource(imgPath);
				if (imgResource != null) {
					Node imgNode = imgResource.adaptTo(Node.class);
					if (imgNode.hasProperty("fileReference")) {
							%>
							<%=displayRendition(resourceResolver,
																	imgPath, "cq5dam.web.520.520")%>
							<%
	}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if(request.getParameter("imageData") != null){
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
					    } catch (NumberFormatException nfe) {};
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
                String path = "/content/dam/girlscouts-vtk/troop-data/"+ troop.getTroop().getCouncilCode() +"/" + troop.getTroop().getTroopId() + "/imgLib";
                String pathWithFile = path+"/troop_pic.png/jcr:content";

                Session __session = sessionFactory.getSession();

                Node baseNode = JcrUtil.createPath(path, "nt:folder", __session);

                ByteArrayInputStream byteStream = new ByteArrayInputStream(decoded);
                ValueFactory vf = __session.getValueFactory();
                Binary bin = vf.createBinary(byteStream);

                //for some reason, the data property can't be updated, just remade
                try{
                    __session.removeItem(path+"/troop_pic.png");
                	__session.save();
                }
                catch(Exception e){

                }

                //creates file and jcr:content nodes if they don't exist yet
                Node jcrNode = JcrUtil.createPath(pathWithFile, false, "nt:file", "nt:resource", __session, false);

                jcrNode.setProperty("jcr:data",bin);
                jcrNode.setProperty("jcr:mimeType","image/png");

				__session.save();

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (request.getParameter("viewProposedSched") != null) {

			String dates = calendarUtil
					.getSchedDates(
							user,
							troop,
							request.getParameter("calFreq"),
							new org.joda.time.DateTime(
									VtkUtil.parseDate(
											VtkUtil.FORMAT_FULL,
											request.getParameter("calStartDt")
													+ " "
													+ request
															.getParameter("calTime")
													+ " "
													+ request
															.getParameter("calAP"))),
							request.getParameter("exclDt"),
							Long.parseLong((request
									.getParameter("orgDt") == null || request
									.getParameter("orgDt").equals("")) ? "0"
									: request.getParameter("orgDt")));

			java.util.List _dates = VtkUtil
					.getStrCommDelToArrayDates(dates);
			out.println(_dates.size());
		} else if (request.getParameter("printTroopReloginids") != null) {
			 %><select id="reloginid" onchange="relogin()"><% 
			for (int i = 0; i < troops.size(); i++) { 
			 
			  %><option value="<%=troops.get(i).getTroopId()%>"
	            <%=troop.getTroop().getTroopId()
	              .equals(troops.get(i).getTroopId()) ? "SELECTED"
	              : ""%>><%=troops.get(i).getTroopName()%>
	              :  <%=troops.get(i).getGradeLevel()%></option><%
	        }
		
		   %></select><% 
		   
		   
		}else if (request.getParameter("printCngYearPlans") != null) {
		
		
	
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
	           id="r_<%=yearPlan.getId()%>" class="radio1" name="group1" onclick="chgYearPlan('<%=yearPlan.getId()%>', '<%=yearPlan.getPath()%>', '<%=confMsg%>', '<%=yearPlan.getName()%>', <%=troop.getYearPlan()!=null ? true: false %> ,'<%=troop.getYearPlan()!=null ? troop.getYearPlan().getName() : "" %>' )" />
	            <label for="r_<%=yearPlan.getId()%>"></label>
	            
	        </div>
	        <div class="small-18 columns large-pull-2 medium-pull-2 small-pull-2">
	            <a href="#" onclick="chgYearPlan('<%=yearPlan.getId()%>', '<%=yearPlan.getPath()%>', '<%=confMsg%>', '<%=yearPlan.getName()%>')"><%=yearPlan.getName()%></a>
	            <p><%=yearPlan.getDesc()%></p>
	        </div>
	      </div><!--/row-->
	      
	      <% }%>  
	      
	      
	        <div class="row">
	        <div class="columns large-push-2 medium-2 medium-push-2 small-2">
	           <input type="radio" <%=( troop.getYearPlan()!=null && (troop.getYearPlan().getName().equals("Custom Year Plan"))) ? " checked " : "" %> id="r_0" class="radio1" name="group1"  onclick="chgCustYearPlan('<%=troop.getYearPlan()==null ? "" : troop.getYearPlan().getId()%>', '<%=troop.getYearPlan()==null ? "" :troop.getYearPlan().getPath()%>', '<%=confMsg%>', '<%=troop.getYearPlan()==null ? "" :troop.getYearPlan().getName()%>')" />
	            <label for="r_0"></label>
	        </div>
	        <div class="small-18 columns large-pull-2 medium-pull-2 small-pull-2">
	            <a onclick="return chgCustYearPlan('<%=troop.getYearPlan()==null ? "" : troop.getYearPlan().getId()%>', '<%=troop.getYearPlan()==null ? "" : troop.getYearPlan().getPath()%>', '<%=confMsg%>', '<%=troop.getYearPlan()==null ? "" : troop.getYearPlan().getName()%>')">Create Your Own Year Plan</a>
	            <p>Choose this option to create your own year plan using meetings from  our meeting library</p>
	        </div>
	      </div><!--/row-->
	      
	      <% 
	      
		

		} else {
			//TODO throw ERROR CODE
			
		}

	} catch (java.lang.IllegalAccessException e) {
		e.printStackTrace();
	}
%>
