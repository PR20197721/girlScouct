<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />
<%@include file="../session.jsp"%>
<% 
	java.util.List<org.girlscouts.vtk.models.Contact>contacts = new org.girlscouts.vtk.auth.dao.SalesforceDAO(troopDAO, connectionFactory).getContacts( user.getApiConfig(), troop.getSfTroopId() );
	String path = VtkUtil.getYearPlanBase(user, troop)+ troop.getSfCouncil()+"/troops/"+ troop.getSfTroopId()+"/yearPlan/meetingEvents/"+request.getParameter("mid");
	Attendance attendance = meetingUtil.getAttendance(user, troop, path + "/attendance");
	Achievement achievement = meetingUtil.getAchievement(user, troop, path + "/achievement"); 

	boolean isAttendance= true, isAchievement=true;
	if( attendance==null  ){isAttendance=false;}
	if( achievement==null ){isAchievement=false;}

	boolean showAchievement=( request.getParameter("isAch")!=null && request.getParameter("isAch").equals("true")) ? true :  false;
%>
<div class="modal-attendance">
	<div class="header clearfix">
		<h3 class="columns large-22">Attendance and Achievements</h3>
		<a class="close-reveal-modal columns large-2" href="#"><i class="icon-button-circle-cross"></i></a>
	</div>
	<div class="scroll">
		<div class="content clearfix" id="modal_A_A">
			<h4><%=request.getParameter("mName")%></h4>
			<form action="/content/girlscouts-vtk/controllers/vtk.controller.html">
				<table>
					<thead>
						<tr>
							<th></th>
							<th>Attendance</th>
							<%
								if( showAchievement ) {
							%>
								<th>Achievement Earned</th>
							<%
								}
							%>
						</tr>
					</thead>
					<tbody>
						<%
							for(int i=0;i<contacts.size();i++){
								if(contacts.get(i).getId()!=null){
						%> 
								<tr>
									<td>
										<p><%=contacts.get(i).getFirstName() %></p>         
									</td>
									<td>
										<input type="checkbox"  <%= ( !isAttendance || (attendance!=null && attendance.getUsers()!=null && attendance.getUsers().contains(contacts.get(i).getId()) ) )  ? "checked" : "" %> name="attendance" id="a<%=contacts.get(i).getId() %>" value="<%=contacts.get(i).getId() %>" onclick="setDefaultAchievement(this.checked, 'c<%=contacts.get(i).getId() %>')">
										<label for="a<%=contacts.get(i).getId() %>"></label>
									</td>
									<%
									 if( showAchievement ){
									%>
									<td>
										<input type="checkbox"  <%= ( !isAchievement  || (achievement!=null && achievement.getUsers()!=null && achievement.getUsers().contains(contacts.get(i).getId())) )  ? "checked" : "" %> name="achievement" id="c<%=contacts.get(i).getId() %>" value="<%=contacts.get(i).getId() %>">
										<label for="c<%=contacts.get(i).getId() %>"></label>
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
				<input type="button" value="Save"  class="btn button right" onclick="updateAttendAchvm('<%=request.getParameter("mid")%>')"/>
			</form>
		</div>
	</div>
</div>
<script>
  //attendance_popup_width();
	function setDefaultAchievement(checkedState, achievementId) {
		if (!checkedState) {
			if ($("#" + achievementId).length > 0) {
				$("#" + achievementId).prop('checked', false);
			}
		}
	}
</script>
