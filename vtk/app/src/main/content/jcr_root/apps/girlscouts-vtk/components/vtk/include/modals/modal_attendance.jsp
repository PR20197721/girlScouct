<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="org.girlscouts.vtk.models.Achievement,
                 org.girlscouts.vtk.models.Attendance,
                 org.girlscouts.vtk.models.Contact,
                 org.girlscouts.vtk.osgi.service.MulesoftService" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../session.jsp" %>
<%
    java.util.List<Contact> contacts = sling.getService(MulesoftService.class).getContactsForTroop(selectedTroop, user);
    String YEAR_PLAN_EVENT = "meetingEvents";
    String eventType = request.getParameter("eType");
    if (eventType != null && eventType.equals("ACTIVITY"))
        YEAR_PLAN_EVENT = "activities";
    String path = VtkUtil.getYearPlanBase(user, selectedTroop) + selectedTroop.getSfCouncil() + "/troops/" + selectedTroop.getSfTroopId() + "/yearPlan/" + YEAR_PLAN_EVENT + "/" + request.getParameter("mid");
    Attendance attendance = meetingUtil.getAttendance(user, selectedTroop, path + "/attendance");
    Achievement achievement = meetingUtil.getAchievement(user, selectedTroop, path + "/achievement");
    boolean isIRM = selectedTroop.getIsIRM();
    boolean isSUM = selectedTroop.getIsSUM();
    boolean isAttendance = true, isAchievement = true;
    if (attendance == null) {
        isAttendance = false;
    }
    if (achievement == null) {
        isAchievement = false;
    }
    String title = "";
    if (isIRM) {
        title = "Achievements";
    } else {
        title = "Attendance";
        if ("meetingEvents".equals(YEAR_PLAN_EVENT)) {
            title += " and Achievements";
        }
    }
    boolean showAchievement = request.getParameter("isAch") != null && request.getParameter("isAch").equals("true");
%>
<div class="modal-attendance">
	<div class="header clearfix">
        <%if(isIRM){%>
            <h3 class="columns large-22">Achievements</h3>
        <%} else {%>
		    <h3 class="columns large-22">Attendance <%="meetingEvents".equals(YEAR_PLAN_EVENT) ? " and Achievements" : "" %></h3>
        <%}%>
		<a class="close-reveal-modal columns large-2" href="#"><span style="font-size: 24px; color: black; font-weight: normal;">X</span></a>
	</div>
	<div class="scroll">
		<div class="content clearfix" id="modal_A_A">
			<h4><%=request.getParameter("mName")%></h4>
            <%if(isSUM){%>
                <div class="demo-info-message">
                    <p>This feature is to help you with training and support. The information below is not real and for demo purposes only.</p>
                </div>
            <%}%>
			<form action="/content/girlscouts-vtk/controllers/vtk.controller.html">
				<table>
					<thead>
						<tr>
							<th></th>
                            <%if(isIRM){%>
                                <th></th>
                            <%} else {%>
                                <th>Attendance</th>
                            <%}
                            if(showAchievement) {
							%>
                                <th>Achievement Earned</th>
							<%
                            }
							%>
						</tr>
					</thead>
					<tbody>
						<%
							for( Contact contact : contacts ){
								if(contact.getId()!=null  && (contact.getRole()!=null && contact.getRole().trim().toUpperCase().equals("GIRL") )){
						        %>
								<tr>
									<td>
										<p><%=contact.getFirstName() %></p>         
									</td>
                                    <%if(isIRM){%>
                                        <td></td>
                                    <%} else {%>
                                        <td>
                                            <input type="checkbox"  <%= ( !isAttendance || (attendance!=null && attendance.getUsers()!=null && attendance.getUsers().contains(contact.getId()) ) )  ? "checked" : "" %> name="attendance" id="a<%=contact.getId() %>" value="<%=contact.getId() %>" onclick="setDefaultAchievement(this.checked, 'c<%=contact.getId() %>')">
                                            <label for="a<%=contact.getId() %>"></label>
                                        </td>
                                    <%
                                    }
                                    if( showAchievement ){
									%>
									<td>
										<input type="checkbox"  <%= ( !isAchievement  || (achievement!=null && achievement.getUsers()!=null && achievement.getUsers().contains(contact.getId())) )  ? "checked" : "" %> name="achievement" id="c<%=contact.getId() %>" value="<%=contact.getId() %>">
										<label for="c<%=contact.getId() %>"></label>
									</td>
									<%
                                    }
									%>
								</tr>
								<%
								}
							}
						%>
					</tbody>
				</table>
				<input type="button" value="Save"  class="btn button right" onclick="updateAttendAchvm('<%=request.getParameter("mid")%>','<%=request.getParameter("eType")%>')"/>
				<input type="button" value="Select All"  class="btn button right" onclick="attendanceSelect()"/>
				<input type="button" value="Clear Selection" style="background-color: white !important; border: solid 1px #18aa51; color: #18aa51;" class="btn button right" onclick="attendanceClear()"/>
			</form>
		</div>
	</div>
</div>
<script>
    function setDefaultAchievement(checkedState, achievementId) {
        if (!checkedState) {
            if ($("#" + achievementId).length > 0) {
                $("#" + achievementId).prop('checked', false);
            }
        }
    }
</script>
