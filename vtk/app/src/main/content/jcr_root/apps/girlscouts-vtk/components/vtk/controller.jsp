<%@page import="java.util.Comparator,org.codehaus.jackson.map.ObjectMapper,org.joda.time.LocalDate,java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*,
                org.girlscouts.vtk.modifiedcheck.ModifiedChecker, com.day.cq.commons.jcr.JcrUtil, org.apache.commons.codec.binary.Base64, com.day.cq.commons.ImageHelper, com.day.image.Layer, java.io.ByteArrayInputStream"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>

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
	
				
				
				
				StringTokenizer t = new StringTokenizer( x, ",");
				if( troop.getYearPlan().getMeetingEvents().size() != t.countTokens()){
					String tmp = x;
					if( !tmp.startsWith(",")) tmp =","+ tmp;
					if( !tmp.endsWith(",")) tmp = tmp +",";
					for( int i= troop.getYearPlan().getMeetingEvents().size();i>0;i--)
						if( tmp.indexOf( ","+i+"," )==-1 )
							x = i+","+ x;
								
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
										dateFormat4.parse(request
												.getParameter("newCustActivity_date")
												+ " "
												+ request
														.getParameter("newCustActivity_startTime")
												+ " "
												+ request
														.getParameter("newCustActivity_startTime_AP")),
										dateFormat4.parse(request
												.getParameter("newCustActivity_date")
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
											dateFormat4.parse(request
													.getParameter("calStartDt")
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
				try{
					troopUtil.selectYearPlan(user, troop,
						request.getParameter("addYearPlanUser"),
						request.getParameter("addYearPlanName"));
				}catch(VtkYearPlanChangeException e){ System.err.println(e.getMessage()); e.printStackTrace(); out.println( e.getMessage() ); }
				
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
				financeUtil.getFinances(troop, Integer.parseInt(request.getParameter("finance_qtr")), user.getCurrentYear());
				return;
			case UpdateFinances:
				financeUtil.updateFinances(troop, user.getCurrentYear(), request.getParameterMap());
				financeUtil.sendFinanceDataEmail(troop, Integer.parseInt(request.getParameter("qtr")),  user.getCurrentYear());
				return;
			case UpdateFinanceAdmin:
				financeUtil.updateFinanceConfiguration(troop, user.getCurrentYear(), request.getParameterMap());
				return;
			case RmMeeting:
				meetingUtil.createMeetingCanceled(user, troop,
                        request.getParameter("mid"), Long.parseLong(request.getParameter("rmDate")));
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
				System.err.println("tatax: "+request.getParameter("mids"));
                meetingUtil.createCustomYearPlan(user, troop, request.getParameter("mids"));
                return;
			default:
				break;
			}

		//if( true) return;

		/*
		if( request.getParameter("isMeetingCngAjax") !=null){
		//	/gscontroller/vtk/crud/meetingOrder POST {operation:UPDATE , isMeetingCngAjax: [2,4,6,31]}
		meetingUtil.changeMeetingPositions( user, troop, request.getParameter("isMeetingCngAjax") );
		}else if( request.getParameter("newCustActivity") !=null ){
		yearPlanUtil.createActivity(user, troop, new Activity( 
			request.getParameter("newCustActivity_name"), request.getParameter("newCustActivity_txt"),
			dateFormat4.parse(request.getParameter("newCustActivity_date") +" "+request.getParameter("newCustActivity_startTime") +" " +request.getParameter("newCustActivity_startTime_AP")), 
			dateFormat4.parse(request.getParameter("newCustActivity_date") +" "+request.getParameter("newCustActivity_endTime") +" "+ request.getParameter("newCustActivity_endTime_AP")), 
			request.getParameter("newCustActivityLocName"), request.getParameter("newCustActivityLocAddr"),
			VtkUtil.convertObjectToDouble(request.getParameter("newCustActivity_cost")) ));
		}else if( request.getParameter("buildSched") !=null ){
		//	/gscontroller/vtk/crud/createSchedule POST {operation: CREATE, calFreq: 'daily', calStartDt: '9/23/14', calTime: '10:46 EST'}
		try{
			session.putValue("VTK_planView_memoPos", null);
			calendarUtil.createSched(user, troop, request.getParameter("calFreq"), 
		new org.joda.time.DateTime(dateFormat4.parse(request.getParameter("calStartDt") +" "+ request.getParameter("calTime") +
		" "+ request.getParameter("calAP"))), request.getParameter("exclDt"));
		}catch(Exception e){e.printStackTrace();}
		}else if( request.getParameter("addYearPlanUser") !=null ){
		troopUtil.selectYearPlan( user,  troop, request.getParameter("addYearPlanUser"), request.getParameter("addYearPlanName"));
		}else if( request.getParameter("addLocation") !=null ){
		locationUtil.addLocation(user, troop, new Location(request.getParameter("name"),
		request.getParameter("address"), request.getParameter("city"), 
		request.getParameter("state"), request.getParameter("zip") ));
		}else if( request.getParameter("rmLocation") !=null ){
		// /gscontroller/vtk/crud/location POST {operation: DELETE, locationId: 12345}
		locationUtil.removeLocation(user, troop, request.getParameter("rmLocation"));
		}else if( request.getParameter("newCustAgendaName") !=null ){
		meetingUtil.createCustomAgenda(user, troop, 
		request.getParameter("name"), request.getParameter("newCustAgendaName"), 
		Integer.parseInt(request.getParameter("duration")), Long.parseLong( request.getParameter("startTime") ), request.getParameter("txt") );

		}else if( request.getParameter("setLocationToAllMeetings") !=null ){
		// /gscontroller/vtk/crud/updateAllMeetingLocations POST {operation: UPDATE, locationId: 12345}
		locationUtil.setLocationAllMeetings(user, troop, request.getParameter("setLocationToAllMeetings") );

		}else if( request.getParameter("updSched") !=null ){
		calendarUtil.updateSched(user, troop, request.getParameter("meetingPath"), 
			request.getParameter("time"), request.getParameter("date"), request.getParameter("ap"), 
			request.getParameter("isCancelledMeeting"), Long.parseLong( request.getParameter("currDt") ));

		}else if( request.getParameter("rmCustActivity") !=null ){	
		meetingUtil.rmCustomActivity (user, troop, request.getParameter("rmCustActivity") );	

		}else if( request.getParameter("chnLocation") !=null ){
		locationUtil.changeLocation(user, troop, request.getParameter("chnLocation"), request.getParameter("newLocPath"));

		}else if( request.getParameter("cngMeeting") !=null ){ //change Meeting
		meetingUtil.swapMeetings(user, troop, request.getParameter("fromPath"), request.getParameter("toPath"));
		}else if( request.getParameter("addMeeting") !=null ){ //add Meeting
		meetingUtil.addMeetings(user, troop,  request.getParameter("toPath"));

		}else if( request.getParameter("isActivityCngAjax") !=null ){ //activity shuffle
		try{
			meetingUtil.rearrangeActivity(user,  troop, request.getParameter("mid"), request.getParameter("isActivityCngAjax"));
		}catch(java.lang.IllegalAccessException e){e.printStackTrace();}
		}else if( request.getParameter("rmAgenda") !=null ){
		// /gscontroller/vtk/crud/removeAgenda POST {operation: DELETE, agendaId: 12345}
		meetingUtil.rmAgenda(user, troop, request.getParameter("rmAgenda") , request.getParameter("mid")  );

		}else if( request.getParameter("editAgendaDuration") !=null ){
		// /gscontroller/vtk/crud/agenda POST {operation: update, action: updateDuration, duration: 10}
		meetingUtil.editAgendaDuration(user, troop, Integer.parseInt(request.getParameter("editAgendaDuration")), 
			request.getParameter("aid"),request.getParameter("mid"));
		}else if( request.getParameter("revertAgenda") !=null ){
		// /gscontroller/vtk/crud/agenda POST {operation: UPDATE, action: revert, agendaId: 123}
		// /gscontroller/vtk/action/revertAgenda POST {agendaId: 123}
		meetingUtil.reverAgenda(user, troop,  request.getParameter("mid") );

		}else if( request.getParameter("loginAs")!=null){ //troopId
		// /gscontroller/vtk/action/changeTroop POST {operation: UPDATE, locationId: 12345}
		troopUtil.reLogin(user, troop, request.getParameter("loginAs"), session);

		}else if( request.getParameter("addAids")!=null){
		if( request.getParameter("assetType").equals("AID")){
			meetingUtil.addAids(user, troop, request.getParameter("addAids"), request.getParameter("meetingId"), java.net.URLDecoder.decode(request.getParameter("assetName") ) );
		}else{
			meetingUtil.addResource(user, troop, request.getParameter("addAids"), request.getParameter("meetingId"), java.net.URLDecoder.decode(request.getParameter("assetName") ) );
		}
		}else if( request.getParameter("rmAsset")!=null){
		meetingUtil.rmAsset(user, troop, request.getParameter("rmAsset"), request.getParameter("meetingId"));

		}else if( request.getParameter("bindAssetToYPC") !=null ){	
		vtkErr = troopUtil.bindAssetToYPC(user, troop, request.getParameter("bindAssetToYPC"), 
		request.getParameter("ypcId"), request.getParameter("assetDesc"), request.getParameter("assetTitle"));
		}else if( request.getParameter("editCustActivity") !=null ){
		vtkErr = troopUtil.editCustActivity(user, troop, request);
		}else if( request.getParameter("srch") !=null ){	
		yearPlanUtil.search( user,  troop, request);
		}else if( request.getParameter("newCustActivityBean") !=null ){	
		yearPlanUtil.createCustActivity(user, troop, (java.util.List <org.girlscouts.vtk.models.Activity>)session.getValue("vtk_search_activity"), request.getParameter("newCustActivityBean"));


		}else if(request.getParameter("isAltered") !=null){ //on yearPlan cng	
		out.println( yearPlanUtil.isYearPlanAltered(user, troop) );
		 */

		if (request.getParameter("admin_login") != null) {
			if (session.getValue("VTK_ADMIN") == null) {
				String u = request.getParameter("usr");
				String p = request.getParameter("pswd");
				if (u.equals("admin") && p.equals("icruise123"))
					session.putValue("VTK_ADMIN", u);
			}
			response.sendRedirect("/content/girlscouts-vtk/en/vtk.admin.home.html");

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
						troopDAO).getContacts(user.getApiConfig(),
						troop.getSfTroopId());
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
			}else{
				System.out.println("emr does not exit!");
			}

			org.girlscouts.vtk.ejb.Emailer emailer = sling
					.getService(org.girlscouts.vtk.ejb.Emailer.class);
			emailer.send(user,emr);
			try{
				meetingUtil.saveEmail(user, troop, emr.getMeetingId());
			}catch(Exception e){
				e.printStackTrace();
			}
			troop.setSendingEmail(null);

		} else if (request.getParameter("testAB") != null) {

			//java.util.Set<Integer> myPermissionTokens = new HashSet<Integer>();
			//troop.getTroop().setPermissionTokens(myPermissionTokens);
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
						if (!m.getRefId().contains("_")){
							yearPlanUtil.createCustomMeeting(user,troop, m, custM);
						}else {
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
			if(blurbs!=null){
				for (int i = 0; i < blurbs.length; i++) {
					String blurb = blurbs[i];
					if(blurb==null || blurb.trim().isEmpty()){break;}
					boolean show = shows[i].equals("true");
					Date date=null;
					if(!dates[i].isEmpty()){
						date = FORMAT_MMddYYYY.parse(dates[i]);
					}
					
					Milestone m = new Milestone(blurb,show,date);
					milestones.add(m);
				}
			}

			yearPlanUtil.saveCouncilMilestones(milestones,councilId);
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
System.err.println("manu reactjs___________________________________________________________ " + new java.util.Date());


			boolean isFirst = false;
			if (request.getParameter("isFirst") != null
					&& request.getParameter("isFirst").equals("1")) {
				isFirst = true;
			}

			boolean isCng = false;

			if (!isFirst) {
				System.err.println("manu reactjs 1");
			
				ModifiedChecker modifiedChecker = sling.getService(ModifiedChecker.class);

                // TODO: This is a special logic. "X". Rewrite ModifiedChecker later. There are two places in this file.
				isCng = modifiedChecker.isModified("X" + session.getId(), troop.getYearPlan().getPath());
				System.err.println("manu reactjs 2 ::" +isCng);
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
				//session.putValue("VTK_troop", troop);
				PlanView planView = meetingUtil.planView(user, troop,
						request);

				java.util.List<MeetingE> meetings = troop.getYearPlan()
						.getMeetingEvents();
				for (int i = 0; i < meetings.size(); i++) {
					MeetingE _meeting = meetings.get(i);

					if (_meeting.getMeetingInfo() != null
							&& _meeting.getMeetingInfo()
									.getActivities() != null) {

						if (request.getParameter("isActivNew") != null
								&& request.getParameter("isActivNew")
										.equals("1")) {
							_meeting.getMeetingInfo().setActivities(
									null);
							/*
							java.util.List<Activity> _activities = _meeting.getMeetingInfo().getActivities();
							Activity temp = new Activity();
							temp.setName("temp");
							temp.setDuration(0);
							temp.setActivityNumber(0);
							_activities.add( temp );
							_meeting.getMeetingInfo().setActivities( _activities);
							 */

						} else {
							java.util.List<Activity> _activities = _meeting
									.getMeetingInfo().getActivities();

//System.err.println("tata blub: "+ org.apache.commons.lang.StringEscapeUtils.escapeHtml(_meeting.getMeetingInfo().getMeetingInfo().get("meeting short description").getStr()));


_meeting.getMeetingInfo().getMeetingInfo().put("meeting short description", new JcrCollectionHoldString(org.apache.commons.lang.StringEscapeUtils.unescapeHtml(_meeting.getMeetingInfo().getMeetingInfo().get("meeting short description").getStr()))); 
		
		java.util.Comparator<Activity> comp = new org.apache.commons.beanutils.BeanComparator(
									"activityNumber");
							Collections.sort(_activities, comp);
						}

					}
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
				//out.println(mapper.writeValueAsString(troop));
			    out.println(mapper.writeValueAsString(troop).replaceAll("mailto:","").replaceAll("</a>\"</a>","</a>").replaceAll("\"</a>\"",""));
           System.err.println(mapper.writeValueAsString(troop));     
			}

		} else if (request.getParameter("yearPlanSched") != null) {
			
		
		    if (troop.getYearPlan() == null)
				return;

			boolean isFirst = false;
			if (request.getParameter("isFirst") != null
					&& request.getParameter("isFirst").equals("1")) {
				isFirst = true;
			}

			boolean isCng = false;
		    if (!isFirst) {
			    ModifiedChecker modifiedChecker = sling.getService(ModifiedChecker.class);
			    isCng = modifiedChecker.isModified("X"+ session.getId(), troop.getYearPlan().getPath());
		    }
			//isCng=true;
			if (isFirst || isCng
					|| request.getParameter("isActivNew") != null) {
				if (request.getParameter("isActivNew") != null
						&& request.getParameter("isActivNew").equals(
								"1")) {
					out.println("[{\"yearPlan\":\""
							+ troop.getYearPlan().getName()
							+ "\",\"schedule\":null}]");
				} else {

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
							.getYearPlanSched(user,
									troop.getYearPlan(), true, true);

					
					//start milestone
					try {

						if (troop.getYearPlan() != null)
							troop.getYearPlan()
									.setMilestones(
											yearPlanUtil
													.getCouncilMilestones(""
															+ troop.getSfCouncil()));
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (troop.getYearPlan().getMilestones() == null)
						troop.getYearPlan().setMilestones(
								new java.util.ArrayList());

					for (int i = 0; i < troop.getYearPlan()
							.getMilestones().size(); i++){
						if(troop.getYearPlan().getMilestones()
								.get(i).getDate()!=null)
						sched.put(troop.getYearPlan().getMilestones()
								.get(i).getDate(), troop.getYearPlan()
								.getMilestones().get(i));
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

					
					
	//****
	
	//java.util.Iterator itr= sched.keySet().iterator();
	//while( itr.hasNext() ){
	Object tmp[] = sched.values().toArray();
	for(int i=0;i<tmp.length;i++){
		try{
		   ((MeetingE)tmp[i]).getMeetingInfo().setActivities(null);
		   ((MeetingE)tmp[i]).getMeetingInfo().setMeetingInfo(null);
		   ((MeetingE)tmp[i]).getMeetingInfo().setResources(null);
		   ((MeetingE)tmp[i]).getMeetingInfo().setAgenda(null);
		}catch(Exception e){}
	}
	
	//*****
					ObjectMapper mapper = new ObjectMapper();
					out.println("{\"yearPlan\":\""
							+ troop.getYearPlan().getName()
							+ "\",\"schedule\":");
					out.println(mapper.writeValueAsString(sched).replaceAll("mailto:",""));
					out.println("}");
				}
			}
			
		} else if (request.getParameter("reactActivity") != null) {
System.err.println("manu reactActivity");					
			boolean isFirst = false;
			if (request.getParameter("isFirst") != null
					&& request.getParameter("isFirst").equals("1")) {
				isFirst = true;
			}

			boolean isCng = false;

		    if (!isFirst) {
				ModifiedChecker modifiedChecker = sling.getService(ModifiedChecker.class);
				isCng = modifiedChecker.isModified("X" + session.getId(), troop.getYearPlan().getPath());
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
				//session.putValue("VTK_troop", troop);
				java.util.List<Activity> activities = troop
						.getYearPlan().getActivities();
				for (int i = 0; i < activities.size(); i++) {
					if (activities
							.get(i)
							.getUid()
							.equals(request
									.getParameter("reactActivity")))
						currentActivity = activities.get(i);
				}

				ObjectMapper mapper = new ObjectMapper();
				out.println(mapper.writeValueAsString(currentActivity));
			
			

			}

		} else if (request.getParameter("isRmTroopImg") != null) {

			try {
				
				
				Session __session = sessionFactory.getSession();
				/*
				Session __session = (Session) resourceResolver
						.adaptTo(Session.class);
				*/
				String troopPhotoUrl = "/content/dam/girlscouts-vtk/troop-data/"+ troop.getTroop().getCouncilCode() +"/" + troop.getTroop().getTroopId() + "/imgLib/troop_pic.png";
			    
				__session.removeItem(troopPhotoUrl);
				__session.save();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (request.getParameter("isAdminRpt") != null) {
			final CouncilRpt councilRpt = sling.getService(CouncilRpt.class);  
			java.util.Map container = councilRpt.getTroopNames( request.getParameter("cid"), request.getParameter("ypPath"));
			java.util.Iterator itr= container.keySet().iterator();
			while( itr.hasNext() ){
				String troopId= (String) itr.next();
				String troopName= (String)container.get( troopId);
				%> $("#<%=troopId %>").html("<%=troopName%>"); <% 
			}
		}else if( request.getParameter("getEventImg")!=null){
			
			try {
			    String imgPath = request.getParameter("path") + "/jcr:content/data/image";

                Resource imgResource = resourceResolver.getResource(imgPath);
                if (imgResource != null) {
			        Node imgNode = imgResource.adaptTo(Node.class);  
			        if( imgNode.hasProperty("fileReference")){
			            %> <%= displayRendition(resourceResolver, imgPath, "cq5dam.web.520.520") %><% 
			        }
                }
			}catch(Exception e){e.printStackTrace();}
				
		} else if(request.getParameter("imageData") != null){
			try{
                String imgData = request.getParameter("imageData");
                //System.out.println(imgData.substring(imgData.length()-11));
				imgData = imgData.replace("data:image/png;base64,", "");
                byte[] decoded = Base64.decodeBase64(imgData);

                //creates folder path if it doesn't exist yet
                String path = "/content/dam/girlscouts-vtk/camera-test/troop-data/"+ troop.getTroop().getCouncilCode() +"/" + troop.getTroop().getTroopId() + "/imgLib";
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
		} else {
			//TODO throw ERROR CODE
		}

	} catch (java.lang.IllegalAccessException e) {
		e.printStackTrace();
	}
%>
