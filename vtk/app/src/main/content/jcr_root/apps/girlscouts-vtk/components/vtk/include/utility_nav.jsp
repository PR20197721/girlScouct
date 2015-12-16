<%@ page
  import="java.text.SimpleDateFormat,java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />
<%@include file="session.jsp"%>
<%
String activeTab=request.getParameter("activeTab");
PlanView planView= meetingUtil.planView(user, troop, request);
%>

       
     
<div class="hide-for-print crumbs clearfix hide-for-small">
  <div class="column small-24 medium-20 large-centered medium-centered large-20">
    <div class="row">
      <div class="columns small-18 medium-19">
        <ul id="sub-nav" class="inline-list hide-for-print">
          
          
          
           <%
                if ("reports".equals(activeTab) && 
                		 user.getApiConfig().getUser().isAdmin() && user.getApiConfig().getUser().getAdminCouncilId()>0) { %>                   
                    <li><a href="/content/girlscouts-vtk/controllers/vtk.admin_reports_downloadable.csv" title="download admin report">download</a></li>
                <% }
             %>
             
             
             
          <!-- if on YP page this menu shows -->
            <%
           		if ("plan".equals(activeTab) && troop.getYearPlan() != null  && hasPermission(troop, Permission.PERMISSION_EDIT_YEARPLAN_ID) ) { %>
            		<% if(troop!=null && troop.getSfTroopAge()!=null && !troop.getSfTroopAge().toLowerCase().trim().contains("cadette") &&
            				!troop.getSfTroopAge().toLowerCase().trim().contains("ambassador") && !troop.getSfTroopAge().toLowerCase().trim().contains("senior")){ %>
            	       	<li><a href="#" onclick="newLocCal()" title="Meeting Dates and Location">Specify Dates and Locations</a></li>            	   
            		    <li><a href="#" onclick="doMeetingLib(<%=calendarUtil.isEventPastGSYear(user, troop)%>)" title="Add Meeting">Add Meeting</a></li>
            		<% } %>
            		<li><a href="#" onclick="newActivity()" title="Add Activity">Add Activity</a></li>
          		<% }
             %>

          <!-- if on Meeting Detail Page -->
            <% if("planView".equals(activeTab)) { %>
            <!--if activity detail page-->
            <% switch(planView.getYearPlanComponent().getType() ) {
              case ACTIVITY:
            	  pageContext.setAttribute("YearPlanComponent", "ACTIVITY");
                Activity activity = (Activity)planView.getYearPlanComponent();
                if( hasPermission(troop, Permission.PERMISSION_EDIT_ACTIVITY_ID)  && activity.getIsEditable() ){%>
                <li>
                     <a data-reveal-id="modal_popup_activity" data-reveal-ajax="true" href="/content/girlscouts-vtk/controllers/vtk.include.activity_edit_react.html?elem=<%=planView.getSearchDate().getTime()%>">Edit Activity</a>
                  </li>
              <% }
                if (  !(activity.getCancelled()!=null && activity.getCancelled().equals("true") ) && 
                activity.getRegisterUrl()  !=null && !activity.getRegisterUrl().equals("")){%>
                <li><a href="<%=activity.getRegisterUrl()%>"  target="_blank">Register for this event</a></li><%
                } 
                
                if(hasPermission(troop, Permission.PERMISSION_RM_ACTIVITY_ID) ){
                    %><li><a href="javascript:rmCustActivity12(aPath)">delete this activity</a></li><% 
                } 
                  break;
            case MEETING:
            	pageContext.setAttribute("YearPlanComponent", "MEETING");
              try {	Object meetingPath = planView.getMeeting().getMeetingInfo().getPath();//.getAttribute("MEETING_PATH");
                      if (meetingPath != null && meetingPath != "") {
                        //Long planViewTime = (Long) pageContext.getAttribute("PLANVIEW_TIME");%>
                      <li id="replaceMeeting"></li>
                      <% 
                      }
                  } catch (Exception te) {
                    te.printStackTrace();
                  }
                break;
              }
          }%> 
        
          <!-- if on a My Troop page-->
          <% if( "myTroop".equals(activeTab) && hasPermission(troop, Permission.PERMISSION_EDIT_TROOP_IMG_ID)  ) { %>
	          <li><a data-reveal-id="modal_upload_image" title="update photo" href="#">add/change a photo of your troop</a></li>
	          <li><a title="remove photo" href="#" onclick="rmTroopInfo()">remove troop photo</a></li>
          <% } %>
            	  <!-- if finance page -->
            <% if("finances".equals(activeTab) || "financesadmin".equals(activeTab)) {
            	
            		 if(hasPermission(troop, Permission.PERMISSION_EDIT_FINANCE_ID)) { %>
			            	<li>
			            <% if("editFinances".equals((String)pageContext.getAttribute("activeSubTab"))) { %>
			            		<p>edit finance fields</p>
			           
			            <% } else if("financesadmin".equals(activeTab)){ %>
                             <a title="Finance" href="/content/girlscouts-vtk/en/vtk.finances.html">enter finance</a>
                        
			            <% } else if(hasPermission(troop, Permission.PERMISSION_EDIT_FINANCE_FORM_ID)) { %>
			                 <a title="Edit Finance Fields" href="/content/girlscouts-vtk/en/vtk.admin_finances.html">edit finance fields</a>
			            <% } %>
			            	</li>
            <%
            		}
            	}
            %>
        </ul>
      </div>
      <div class="columns small-6 medium-5">
       <ul class="inline-list" id="util-links">
       <%if(activeTab!=null  && ( "plan".equals(activeTab) || (  pageContext.getAttribute("YearPlanComponent")!=null && ((String)pageContext.getAttribute("YearPlanComponent")).equals("MEETING")  &&  "planView".equals(activeTab) )) ){ %>
        <li><a data-reveal-id="modal_help" title="help"><i class="icon-questions-answers"></i></a></li>
       <%} %>
       
        <% if("plan".equals(activeTab)) {%>
          <li><a
          	<% if(troop.getYearPlan() != null && planView !=null && planView.getSearchDate() != null 
          		&& planView.getSearchDate().after( new java.util.Date("1/1/1977")) ){
  	         %> onclick="vtkTrackerPushAction('DownloadCalendar');self.location = '/content/girlscouts-vtk/en/cal.ics'"
  	        <%} else{
  	          	%> onclick="alert('You have not yet scheduled your meeting calendar.\nPlease select a year plan and schedule your meetings by clicking on the MEETING DATES AND LOCATION link.')"
  	        <% } %> title="download the calendar"><i class="icon-download"></i></a></li>
            <li><a onclick="javascript:window.print();vtkTrackerPushAction('Print');" title="print"><i class="icon-printer"></i></a></li>
          <% } %>
        </ul>
      </div>
    </div>
  </div>
</div>

<% if (true){//(SHOW_BETA || sessionFeatures.contains(SHOW_VALID_SF_USER_FEATURE)) && sessionFeatures.contains(SHOW_VALID_SF_USER_FEATURE)) { 

%>
    <script>resetIsLoggedIn();</script>
    <iframe style="display:none;" id="myframe" src="<%=sling.getService(org.girlscouts.vtk.helpers.ConfigManager.class).getConfig("idpSsoTargetUrl") %>&RelayState=<%=sling.getService(org.girlscouts.vtk.helpers.ConfigManager.class).getConfig("baseUrl") %>/content/girlscouts-vtk/controllers/vtk.include.sfUserLanding.html"/>
<%} %> 

