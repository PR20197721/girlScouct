<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../../include/session.jsp"%>
<%
String troopId= request.getParameter("tid"); 
String councilCode = request.getParameter("cid"); 

User impersonateRoot =(User) VtkUtil.deepClone(user);
Troop _troop = troopUtil.getTroop(impersonateRoot, councilCode, troopId);
java.util.Map<java.util.Date, YearPlanComponent> sched = meetingUtil.getYearPlanSched(impersonateRoot,troop,
		_troop.getYearPlan(), true, true);
Set distinctGirl = new HashSet();
int badges_earned=0, meeting_activities_added=0, calendar_activities_added=0;

%>

<div> <!--  id="modal_report_detail" class="reveal-modal" data-reveal> -->
 <div class="header clearfix">
    <h3 class="columns large-22"><%=_troop.getSfTroopName() %> Detail View</h3>
    <a class="close-reveal-modal columns large-2" href="#"><i class="icon-button-circle-cross"></i></a>
  </div>
  <div class="scroll">
    <div class="content">
     <h4 id="troopName">
     
     <%
        
        java.util.List<Contact> leaders = new org.girlscouts.vtk.auth.dao.SalesforceDAO(troopDAO, connectionFactory).getTroopLeaderInfo(user.getApiConfig(), troopId); //troop.getTroop().getTroopId());
        if( leaders!=null ){
            for( int i=0;i<leaders.size();i++){
                   Contact leader = leaders.get(i);
                   if( leader==null ) continue;
                   %>
                      <br/><%=leader.getFirstName() %> <%=leader.getLastName() %>
                   <% 
            }
        }
        %>
        
     </h4>
     <h4 id="distinctGirl"> Girls Enrolled</h4>
     <div class="row bg">
        <div class="column large-12">
          <ul class="large-block-grid-2 small-block-grid-2">
            <li><strong>Plan:</strong></li>
            <li><%=_troop.getYearPlan().getName() %></li>
            <li><strong>Plan Customized:</strong></li>
            <li><%= (_troop.getYearPlan().getAltered()!=null && _troop.getYearPlan().getAltered().equals("true")) ? "Yes" : "No" %></li>
            <li><strong>Badges Earned:</strong></li>
            <li><div id="rpt_badges_earned"></div></li>
          </ul>
        </div>
        <div class="column large-12">
           <ul class="large-block-grid-2 small-block-grid-2">
            <li><strong>Meeting Activities Added:</strong></li>
            <li><div id="meeting_activities_added"></div></li>
            <li><strong>Calendar Activities Added:</strong></li>
            <li><div id="calendar_activities_added"></div></li>
          </ul>
        </div>
      </div>
      <table>
        <thead>
          <tr>
            <th>Date</th>
            <th>Topic</th>
            <th>Attendance</th>
            <th>Badge Earned</th>
          </tr>
        </thead>
        <tbody>
        
        <% 
        java.util.Iterator itr = sched.keySet().iterator();
        while( itr.hasNext() ){
        	java.util.Date date = (java.util.Date) itr.next();
        	YearPlanComponent ypc = sched.get(date);
            switch( ypc.getType() ) {
              case MEETING:
                %><%@include file="../report_meeting_detail.jsp"%><%
                break;
              case ACTIVITY:
                %><%@include file="../report_activity_detail.jsp"%><%
                calendar_activities_added++;
                break;
            }
         }
        %>
        </tbody>
      </table>
    </div>
  </div>
</div>


<script>
$("#rpt_badges_earned").html("<%=badges_earned%>");
$("#meeting_activities_added").html("<%=meeting_activities_added%>");
$("#calendar_activities_added").html("<%=calendar_activities_added%>");
$(document).ready(function() {
	$("#distinctGirl").text("<%=distinctGirl.size()%>" + $("#distinctGirl").text());
});
</script>
