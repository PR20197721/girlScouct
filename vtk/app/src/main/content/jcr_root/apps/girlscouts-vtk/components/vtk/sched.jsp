<%@ page
  import="java.text.SimpleDateFormat,java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />
<%@include file="include/session.jsp"%>
<%

	String date = request.getParameter("elem");

	
	org.girlscouts.vtk.models.PlanView planView = meetingUtil.planView(user, troop, request, true);
	if( planView.getYearPlanComponent().getType() == YearPlanComponentType.MEETINGCANCELED || planView.getYearPlanComponent().getType() == YearPlanComponentType.MEETING ){
		
	}
%>
<%=planView.getSearchDate()%>
<%=planView.getMeeting().getMeetingInfo().getName()%>

<br/><input type="text" id="cngDate0" value="12/12/2017"/>
<br/><input type="text" id="cngTime0" value="04:00"/>
<br/><select id="cngAP0" id="cngAP0" class="ampm">
									<option value="pm" selected="">PM</option>
									<option value="am">AM</option>
								</select>

<br/><a href="#" onclick="updSched1(0, '', <%=planView.getSearchDate().getTime()%>)">Save</a>

			<br/><a href="#"onclick="rmMeetingWithConf( '<%=planView.getMeeting().getUid()%>',
<%=planView.getSearchDate().getTime()%>, '<%= planView.getMeeting().getMeetingInfo().getLevel()%>',
'<%=planView.getMeeting().getMeetingInfo().getName()%>')">delete meeting</a>
			<br/><a href="#" onclick="newLocCal()">See more Calendar options</a>
