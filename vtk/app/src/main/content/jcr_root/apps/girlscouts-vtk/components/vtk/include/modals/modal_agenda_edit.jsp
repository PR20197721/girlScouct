
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />
<%@include file="../session.jsp"%>


<div class="modal_agenda_edit">
	<div class="header clearfix">	
		<h3 class="columns large-22">
		<% 
		  String act="";
			if (request.getParameter("isOverview") != null) {
					out.println("Overview");
					act="isOverview";
			} else if (request.getParameter("isActivity") != null) {
					out.println("Activity Plan");
					act="isActivity";
			} else if (request.getParameter("isMaterials") != null) {
					out.println("Materials List");
					act="isMaterials";
			} else if (request.getParameter("isAgenda") != null) {
				out.println("Agenda");
			}
		%>
		</h3>
		<a class="close-reveal-modal columns large-2" href="#"><i class="icon-button-circle-cross"></i></a>
	</div>
	
	<div class="scroll content">
	<% if(!act.isEmpty()) { %>
		<a href="/content/girlscouts-vtk/controllers/vtk.pdfPrint.html?act=<%=act%>&mid=<%=request.getParameter("mid") %>" target="_blank" class="icon-download right" download="<%=act%>"></a>
	<% } %>
	<%  if (request.getParameter("isAgenda") == null &&  VtkUtil.hasPermission(troop, Permission.PERMISSION_SEND_EMAIL_ALL_TROOP_PARENTS_ID) ) {%>
		<a id="print-link" class="icon-printer right" title="print"></a>
	<% } %>
		<div class="setupCalendar row">
		<%
			MeetingE meeting = null;
			java.util.List<MeetingE> meetings = troop.getYearPlan().getMeetingEvents();
			for (int i = 0; i < meetings.size(); i++)
				if (meetings.get(i).getUid()
						.equals(request.getParameter("mid"))) {
					meeting = meetings.get(i);
					break;
				}
			
			if( meeting==null ){
			    java.util.List<MeetingCanceled> cmeetings = troop.getYearPlan().getMeetingCanceled();
	            for (int i = 0; i < cmeetings.size(); i++)
	                if (cmeetings.get(i).getUid()
	                        .equals(request.getParameter("mid"))) {
	                    meeting = cmeetings.get(i);
	                    break;
	                }
	            
			}
			
			
			Meeting meetingInfo = yearPlanUtil.getMeeting(user,troop,
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
			<h5><%=meetingInfo.getName()%>: introduction</h5>
			<%=meetingInfoItems.get("overview").getStr()%>
		</div>
		<%
			} else if (request.getParameter("isActivity") != null) {
				List<Activity> activities = meetingInfo.getActivities();
				Collections.sort(activities, new Comparator<Activity>() {
					public int compare(Activity activity1, Activity activity2) {
						return activity1.getActivityNumber() - activity2.getActivityNumber();
					}
				});

				StringBuilder builder = new StringBuilder();
				for (Activity activity : activities) {
					builder.append("<p><b>Activity " + Integer.toString(activity.getActivityNumber()));
					builder.append(": " + activity.getName() + "</b></p>");

					String description = activity.getActivityDescription();
					if (!description.contains("Time Allotment")) {
						builder.append("<p style=\"font-family: tahoma, arial, helvetica, sans-serif; font-size: 12px;\"><b>Time Allotment</b></p>");
						builder.append("<p>" + Integer.toString(activity.getDuration()) + " minutes");
					}
					builder.append(description);
				}
		%>
		<div class="editable-textarea column small-20 small-centered" id="editMeetingActivity"><%=builder.toString()%></div>
		<%
			} else if (request.getParameter("isMaterials") != null) {
		%>
		<div class="editable-textarea column small-20 small-centered" id="editMeetingMaterials">
		<%=meetingInfoItems.get("materials").getStr()%></div>
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
	<div class="row">
   	<form onsubmit="return false;">
			<h3>Agenda Item: <%=_activity.getName()%></h3>
			<div class="columns small-4">
				<select onchange="durEditActiv(this.options[this.selectedIndex].value, '<%=_activity.getPath()%>', '<%=meeting.getPath()%>')">
					
					<% if(VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID )) {%>
        
					<option value="0" selected>Time Allotment</option>
					<option value="5"
						<%=(_activity.getDuration() == 5)  ? "SELECTED" : ""%>>5</option>
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
						<%}else{ %>
						<option value=""><%=_activity.getDuration() %></option>
						<%} %>
				</select>
				
			</div>
		<% if(VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID )) {%>
			<div class="columns small-20">
				<button onclick="location.reload();" class="btn button">Save and Back to meeting</button>
				<button class="btn button" onclick="return rmAgenda('<%=_activity.getPath()%>', '<%=meeting.getPath()%>')">Delete This Agenda Item</button>
			
			</div>
		 <%} %>
		</form>
		</div>
				
		</div>
		<section class="row">
			<%
				if (_activity.getActivityDescription() != null && !_activity.getActivityDescription().isEmpty()) {
					out.println("<div class=\"clearfix columns small-20 small-centered\">" + _activity.getActivityDescription() + "</div>");
				}
			%>
		</section>
		<%}%>
		</div>
	</div>
	<script type="text/javascript">
	 $(document).ready(function() {
		$('#print-link').on('click',function() {
			$('.modal_agenda_edit .scroll.content').print();
		});
	});
	</script>
