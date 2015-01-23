

<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />
<%@include file="../session.jsp"%>

  <% 
    java.util.List<org.girlscouts.vtk.models.Contact>contacts = new org.girlscouts.vtk.auth.dao.SalesforceDAO(troopDAO).getContacts( user.getApiConfig(), troop.getSfTroopId() );
    String path = "/vtk/"+ troop.getSfCouncil()+"/troops/"+ troop.getSfTroopId()+"/yearPlan/meetingEvents/"+request.getParameter("mid");
    Attendance attendance = meetingUtil.getAttendance(user, troop, path + "/attendance");
    Achievement achievement = meetingUtil.getAchievement(user, troop, path + "/achievement");
  %>
<div class="modal-attendance">
  <div class="header clearfix">
    <h3 class="columns large-22">Attendance and Achievements</h3>
    <a class="close-reveal-modal columns large-2" href="#"><i class="icon-button-circle-cross"></i></a>
  </div>
  <div class="scroll">
    <div class="content" id="modal_A_A">
     <h4><%=request.getParameter("mName") %></h4>
      <form action="/content/girlscouts-vtk/controllers/vtk.controller.html">
        <input type="hidden" value="UpdAttendance" name="act"/>
        <input type="hidden" name="mid" value="<%=request.getParameter("mid")%>"/>
        <table>
          <thead>
            <tr>
              <th></th>
              <th>Attendance</th>
              <th>Achievement Earned</th>
            </tr>
          </thead>
          <tbody>
	          <%for(int i=0;i<contacts.size();i++){ %> 
	          <tr>
	            <td>
	              <p><%=contacts.get(i).getFirstName() %></p>         
	            </td>
	            <td>
	              <input type="checkbox"  <%= (attendance!=null && attendance.getUsers()!=null && attendance.getUsers().contains(contacts.get(i).getId()) )  ? " checked " : "" %> name="attendance" id="a<%=contacts.get(i).getId() %>" value="<%=contacts.get(i).getId() %>">
	              <label for="a<%=contacts.get(i).getId() %>"></label>
	            </td>
	            <td>
	              <input type="checkbox"  <%= (achievement!=null && achievement.getUsers()!=null && achievement.getUsers().contains(contacts.get(i).getId()) )  ? " checked " : "" %> name="achievement" id="c<%=contacts.get(i).getId() %>" value="<%=contacts.get(i).getId() %>">
	              <label for="c<%=contacts.get(i).getId() %>"></label>
	            </td>
	          </tr>
	          <% } %>
          </tbody>
        
        </table>        
        <input type="submit" value="Save"  class="btn button right" />
      </form>
    </div>
  </div>
</div>