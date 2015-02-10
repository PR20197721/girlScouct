<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<% 
    String activeTab = "admin_milestones";
%>
<%@include file="include/tab_navigation.jsp"%>
<div id="panelWrapper" class="row content milestones">
<<<<<<< Updated upstream

=======
  <%@include file="include/utility_nav.jsp"%>
  <div class="columns small-20 small-centered">
    <p>Edit milestones, add dates, create new milestones, and set to show in plans.</p>
    <form>
      <table>
        <tr>
          <th></th>
          <th>Message</th>
          <th>Date</th>
          <th>Show in Plans</th>
        </tr>
        <tr>
          <td><a href="" title="remove"><i class="icon-button-circle-cross" /></a></td>
          <td><<input type="text" placeholder="Name of the milestone" /></td>
          <td><input type="text" placeholder="Start Date" id="calStartDt" name="calStartDt" value="<%=( startAlterDate!=null && !startAlterDate.trim().equals("")) ? FORMAT_MMddYYYY.format(new java.util.Date( Long.parseLong(startAlterDate))):( troop.getYearPlan().getCalStartDate()==null ? "" : FORMAT_MMddYYYY.format(new java.util.Date(troop.getYearPlan().getCalStartDate()))) %>" /></td>
          <td></td>
        </tr>
      </table>
    </form>
  </div>
>>>>>>> Stashed changes
</div>
