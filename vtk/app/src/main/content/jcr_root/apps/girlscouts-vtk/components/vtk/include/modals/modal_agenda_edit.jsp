
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />
<%@include file="../session.jsp"%>


<div class="modal_agenda_edit">
	<div class="header clearfix">
		<h3 class="columns large-22">
		<% 
			if (request.getParameter("isOverview") != null) {
					out.println("Overview");
			} else if (request.getParameter("isActivity") != null) {
				out.println("Activity");
			} else if (request.getParameter("isMaterials") != null) {
				out.println("Materials");
			} else if (request.getParameter("isAgenda") != null) {
				out.println("Agenda");
			}
		%>
		</h3>
		<a class="close-reveal-modal columns large-2" href="#"><i
			class="icon-button-circle-cross"></i></a>
	</div>
	<div class="scroll">
		<div class="content row">
			<div class="setupCalendar columns">
						<%
							MeetingE meeting = null;
							java.util.List<MeetingE> meetings = troop.getYearPlan()
									.getMeetingEvents();
							for (int i = 0; i < meetings.size(); i++)
								if (meetings.get(i).getUid()
										.equals(request.getParameter("mid"))) {
									meeting = meetings.get(i);
									break;
								}
							Meeting meetingInfo = yearPlanUtil.getMeeting(user,
									meeting.getRefId());
							java.util.List<Activity> _activities = meetingInfo.getActivities();
							java.util.Map<String, JcrCollectionHoldString> meetingInfoItems = meetingInfo
									.getMeetingInfo();
							Activity _activity = null;
						%>

					<%
						if (request.getParameter("isOverview") != null) {
					%>
					<div class="editable-textarea column small-20 small-centered" id="editMeetingOverview">
						<h5>Meeting 1: introduction</h5>
						<%=meetingInfoItems.get("overview").getStr()%>
					</div>
					<%
						} else if (request.getParameter("isActivity") != null) {
					%>
					<div class="editable-textarea column small-20 small-centered" id="editMeetingActivity"><%=meetingInfoItems.get("detailed activity plan").getStr()%>
					</div>
					<%
						} else if (request.getParameter("isMaterials") != null) {
					%>
					<div class="editable-textarea column small-20 small-centered" id="editMeetingMaterials"><%=meetingInfoItems.get("materials").getStr()%></div>
						<%
						} else if (request.getParameter("isAgenda") != null) {
								try {
									meetingUtil.sortActivity(_activities);
								} catch (Exception e) {
									e.printStackTrace();
								}
								for (int ii = 0; ii < _activities.size(); ii++) {
									_activity = _activities.get(ii);
									if (ii == Integer
											.parseInt(request.getParameter("isAgenda"))) {
						%>
						<%
							break;
								}
							}
						}
					%>

			<% if (request.getParameter("isAgenda") != null) {%>
						<h3>Agenda Item: <%=_activity.getName()%></h3>
						<div class="columns small-4">
							<select onchange="durEditActiv(this.options[this.selectedIndex].value, '<%=_activity.getPath()%>', '<%=meeting.getPath()%>')">
								<option value="Time Allotment" selected>Time Allotment</option>
								<option value="5"
									<%=(_activity.getDuration() == 5) ? "SELECTED" : ""%>>5</option>
								<option value="10"
									<%=(_activity.getDuration() == 10) ? "SELECTED" : ""%>>10</option>
								<option value="15"
									<%=(_activity.getDuration() == 15) ? "SELECTED" : ""%>>15</option>
								<option value="20"
									<%=(_activity.getDuration() == 20) ? "SELECTED" : ""%>>20</option>
								<option value="25"
									<%=(_activity.getDuration() == 25) ? "SELECTED" : ""%>>25</option>
								<option value="30"
									<%=(_activity.getDuration() == 30) ? "SELECTED" : ""%>>30</option>
							</select>
						</div>
						<section class="columns small-20">
							<button onclick="location.reload();" class="btn button">Save and Back to meeting</button>
							<button class="btn button" onclick="return rmAgenda('<%=_activity.getPath()%>', '<%=meeting.getPath()%>')">Delete This Agenda Item</button>
						</section>
					<section class="row">
						<%
							if (_activity.getActivityDescription() != null && !_activity.getActivityDescription().isEmpty()) {
								out.println("<div class=\"clearfix columns small-20 small-centered\">" + _activity.getActivityDescription() + "</div>");
							}
						%>
					</section>
				</div>
				<%}%>
			</div>
		</div>
	</div>
	</div>