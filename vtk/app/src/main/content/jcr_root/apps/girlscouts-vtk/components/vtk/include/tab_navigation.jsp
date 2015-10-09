<%@ page
  import="org.girlscouts.vtk.helpers.ConfigManager,
                org.girlscouts.vtk.helpers.CouncilMapper,java.text.SimpleDateFormat,java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />
<%@include file="session.jsp"%>
<div id="vtkTabNav">
<%
//out.println("***** "+ VtkUtil.getYearPlanBase(user, troop) );
//out.println(troop.getTroop().getPermissionTokens());
String activeTab=request.getParameter("activeTab");
if( activeTab==null ){
	   return;
}
PlanView planView = meetingUtil.planView(user, troop, request);

boolean isParent= false;
if( troop.getTroop().getRole() !=null &&  troop.getTroop().getRole().equals("PA") ){
	isParent=true;
}
String vtk_cache_uri = "";
if( isParent ){
	vtk_cache_uri = "/myvtk/" + troop.getSfCouncil() ;
}

/*
//Get URL for community page
ConfigManager configManager = (ConfigManager)sling.getService(ConfigManager.class);
String communityUrl = "";
if (configManager != null) {
    communityUrl = configManager.getConfig("communityUrl");
}
*/
String communityUrl = "/content/girlscouts-vtk/en/vtk.home.html";

/*
if (troops != null && troops.size() > 1) {
    Cookie cookie = new Cookie("vtk_prefTroop", troop.getTroop().getGradeLevel());
    cookie.setMaxAge(-1);
    response.addCookie(cookie);
*/
%>


		<div id="troop" class="row hide-for-print">

			  <div class="columns large-7 medium-9 right">


			 <%
			  if (troops != null && troops.size() > 1) {
			    Cookie cookie = new Cookie("vtk_prefTroop", troop.getTroop().getGradeLevel());
			    cookie.setMaxAge(-1);
			    response.addCookie(cookie);
              %>
			    <select id="reloginid" onchange="relogin()">
			      <%
			        for (int i = 0; i < troops.size(); i++) {
			      %>
			      <option value="<%=troops.get(i).getTroopId()%>"
			        <%=troop.getTroop().getTroopId()
			              .equals(troops.get(i).getTroopId()) ? "SELECTED"
			              : ""%>><%=troops.get(i).getTroopName()%>
			        :
			        <%=troops.get(i).getGradeLevel()%></option>
			      <%
			        }
			      %>
			     </select>
			   <%} %>

			  </div>
			  <div class="columns large-4 medium-4">

		      </div>
		</div>

<%
/*  }*/
%>



<div class="hide-for-print tab-wrapper row">
  <%
    //if (troop.getYearPlan() != null) {
  %>
    <div class="columns large-22 large-centered small-24">
      <%
      //  }
      %>
      <dl class="tabs show-for-large-up">
        <% if(VtkUtil.hasPermission(troop, Permission.PERMISSION_VIEW_TROOP_ID)) { %>
          <dd <%= "myTroop".equals(activeTab) ? "class='active'" : "" %>>

              <%if(true){//troop.getYearPlan()!=null && (troop.getYearPlan().getMeetingEvents()!=null && troop.getYearPlan().getMeetingEvents().size()>0 )){ %>
                    <a href="/content/girlscouts-vtk/en/vtk.myTroop.html">My Troop</a>
           <%}else{ %>
                    <a href="#" onclick="alert('There is no year plan set up at this time. Please wait until a troop leader creates a year plan before accessing this tab.')">My Troop</a>
           <%} %>
          </dd>
        <%} %>
        <% if(VtkUtil.hasPermission(troop, Permission.PERMISSION_VIEW_YEARPLAN_ID)) { %>
          <dd <%= "plan".equals(activeTab) ? "class='active'" : "" %>>

           <a href="/content/girlscouts-vtk/en<%=vtk_cache_uri %>/vtk.html">Year Plan</a>
          </dd>
        <% } %>
        <% if(VtkUtil.hasPermission(troop, Permission.PERMISSION_VIEW_MEETING_ID)) { %>
        <dd <%= "planView".equals(activeTab) ? "class='active'" : "" %>>
           <%if(troop.getYearPlan()!=null &&
        		 (troop.getYearPlan().getActivities()==null || troop.getYearPlan().getActivities().size()<=0 ) &&
        		    ( troop.getYearPlan().getMeetingEvents()==null || troop.getYearPlan().getMeetingEvents().size()<=0 )){ %>
        		     <a href='#' onClick='alert("Content only available for meetings. Add at least one meeting to the Year Plan to access this tab.")'>Meeting Plan</a>
           <%}else{ %>
                     <a <%= troop.getYearPlan() != null ? "href='/content/girlscouts-vtk/en/vtk.details.html'" :  "href='#' onClick='alert(\"Please select a year plan\")'"  %>>Meeting Plan</a>
           <%} %>
        </dd>
        <%  } %>
        <dd <%= "resource".equals(activeTab) ? "class='active'" : "" %>>
          <a href="/content/girlscouts-vtk/en/myvtk/<%= troop.getSfCouncil() %>/vtk.resource.html">Resources</a>
        </dd>

        <% if(VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MILESTONE_ID) ){ %>

       <dd <%= "milestones".equals(activeTab) ? "class='active'" : "" %>>
            <a href="/content/girlscouts-vtk/en/vtk.admin_milestones.html">Milestones</a>
          </dd>

        <% } %>

        <% if(VtkUtil.hasPermission(troop, Permission.PERMISSION_VIEW_REPORT_ID) ){ %>
      	  <dd <%= "reports".equals(activeTab) ? "class='active'" : "" %>>
            <a href="/content/girlscouts-vtk/en/vtk.admin_reports.html">Reports</a>
          </dd>
          <% }  %>

       <% if(VtkUtil.hasPermission(troop, Permission.PERMISSION_VIEW_FINANCE_ID) ){ %>
          <dd <%=  ("finances".equals(activeTab) || "financesadmin".equals(activeTab)) ? "class='active'" : "" %>>
            <a href="/content/girlscouts-vtk/en/vtk.finances.html">Finances</a>
          </dd>

        <% }else if(VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_FINANCE_FORM_ID)) { %>
            <dd <%= ("financesadmin".equals(activeTab)) ? "class='active'" : "" %>> <a title="Edit Finance Fields" href="/content/girlscouts-vtk/en/vtk.admin_finances.html">Finances</a>
            </dd>
         <% } %>

      <!--
        <dd <%= "profile".equals(activeTab) ? "class='active'" : "" %>>
          <a href="/content/girlscouts-vtk/en/vtk.profile.html">Profile</a>
        </dd>
        -->
      </dl>
      <div class="dropdown hide-for-print hide-for-large-up">
        <a id="vtk-main-menu-button" onclick="$('#vtk-main-menu').slideToggle('slow')" class="expand">Menu</a>
        <ul id="vtk-main-menu" class="hide-for-print" style="display: none;">
          <% if(VtkUtil.hasPermission(troop, Permission.PERMISSION_VIEW_TROOP_ID)) { %>
          <li class='has-dropdown<%= ("myTroop".equals(activeTab)) ? " active" : " " %>'>

          <%if(troop.getYearPlan()!=null &&
                 (troop.getYearPlan().getMeetingEvents()!=null && troop.getYearPlan().getMeetingEvents().size()>0 )){ %>
             <a href="/content/girlscouts-vtk/en/vtk.myTroop.html">My Troop</a>
          <%}else{ %>
            <a href="#" onclick="alert('There is no Year Plan set up at this time.')">My Troop</a>
          <%} %>


          	<ul class="dropdown">
          	<% if("myTroop".equals(activeTab) &&  VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_TROOP_IMG_ID) ) { %>
          		<li><a data-reveal-id="modal_upload_image" title="update photo" href="#">add/change a photo of your troop</a></li>
          		<li><a title="remove photo" href="#" onclick="rmTroopInfo()">remove troop photo</a></li>
          	<% } %>
            </ul>
          </li>
          <%}%>
          <% if(VtkUtil.hasPermission(troop, Permission.PERMISSION_VIEW_YEARPLAN_ID)) { %>
          <li class='has-dropdown<%= ("plan".equals(activeTab)) ? " active" : " " %>'><a href="/content/girlscouts-vtk/en<%=vtk_cache_uri %>/vtk.html">Year Plan</a>
            <ul class="dropdown">
            <% if("plan".equals(activeTab)  && VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_YEARPLAN_ID)) { %>

                <% if(troop!=null && troop.getSfTroopAge()!=null && !troop.getSfTroopAge().toLowerCase().trim().contains("cadette") &&
                            !troop.getSfTroopAge().toLowerCase().trim().contains("ambassador") && !troop.getSfTroopAge().toLowerCase().trim().contains("senior")){ %>

			              <li><a onclick="newLocCal()">Specify Meeting Dates and Locations</a></li>
			              <li><a onclick="doMeetingLib(<%=calendarUtil.isEventPastGSYear(user, troop)%>)">Add Meeting</a></li>
               <%} %>
              <li><a onclick="newActivity()">Add Activity</a></li>
              <li><a onclick="self.location='/content/girlscouts-vtk/en/cal.ics'">Download Calendar</a></li>
            <% } %>
            </ul>
          </li>
          <%}%>






          <% if(VtkUtil.hasPermission(troop, Permission.PERMISSION_VIEW_MEETING_ID)) { %>
          <li class='has-dropdown<%= ("planView".equals(activeTab)) ? " active" : " " %>'>

         <%if(troop.getYearPlan()!=null &&
                 (troop.getYearPlan().getActivities()==null || troop.getYearPlan().getActivities().size()<=0 ) &&
                    ( troop.getYearPlan().getMeetingEvents()==null || troop.getYearPlan().getMeetingEvents().size()<=0 )){ %>
                     <a href='#' onClick='alert("Content only available for meetings. Add at least one meeting to the Year Plan to access this tab.")'>Meeting Plan</a>
           <%}else{ %>
                     <a <%= troop.getYearPlan() != null ? "href='/content/girlscouts-vtk/en/vtk.details.html'" :  "href='#' onClick='alert(\"Please select a year plan\")'"  %>>Meeting Plan</a>
           <%} %>

            <ul class="dropdown">
            <% if("planView".equals(activeTab)) {
               switch(planView.getYearPlanComponent().getType() ) {
                case ACTIVITY:
                  Activity activity = (Activity)planView.getYearPlanComponent();
                  if( VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_ACTIVITY_ID)  && activity.getIsEditable() ){%>
                  <li><a href="#" onclick="doEditActivity('editCustActiv')">edit activity</a></li>
                <% }
                  if ( !(activity.getCancelled()!=null && activity.getCancelled().equals("true") ) &&
                  activity.getRegisterUrl()  !=null && !activity.getRegisterUrl().equals("")){%>
                  <li><a href="<%=activity.getRegisterUrl()%>" target="_blank">Register for this event</a></li><%
                  }

                  if(VtkUtil.hasPermission(troop, Permission.PERMISSION_RM_ACTIVITY_ID) ){
                        %><li><a href="javascript:rmCustActivity12(aPath)">delete this activity</a></li><%
                  }
              	case MEETING:
                	try {
				            if (planView != null && planView.getMeeting() != null && planView.getMeeting().getMeetingInfo() !=null && planView.getMeeting().getMeetingInfo().getPath() != null) {

                		Object meetingPath = planView.getMeeting().getMeetingInfo().getPath(); //pageContext.getAttribute("MEETING_PATH");
                       if(VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID))
                    	  if (meetingPath != null && meetingPath != ""  ) {
                          //Long planViewTime = (Long) pageContext.getAttribute("PLANVIEW_TIME");
                          %>
                        <li id="replaceMeetingSmall"></li>
                        <%
                        }
			               }
                    } catch (Exception te) {
                      te.printStackTrace();
                    }
                  break;
                }%>
            <% } %>
            </ul>
          </li>
          <%  } %>
          <li <%= ("resource".equals(activeTab)) ? "class='active'" : "" %>><a href="/content/girlscouts-vtk/en/myvtk/<%=troop.getSfCouncil() %>/vtk.resource.html">Resources</a></li>

             <% if(VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MILESTONE_ID) ){ %>
                <li <%= ("milestones".equals(activeTab)) ? "class='active'" : "" %>><a href="/content/girlscouts-vtk/en/vtk.admin_milestones.html">Milestones</a></li>
             <% } %>


	        <%  if( user.getApiConfig().getUser().isAdmin() && user.getApiConfig().getUser().getAdminCouncilId()>0){ %>
	            <li class='has-dropdown<%= ("reports".equals(activeTab)) ? " active" : "" %>'>
	               <a href="/content/girlscouts-vtk/en/vtk.admin_reports.html">Reports</a>
                  <% if("reports".equals(activeTab)) { %>
                   <ul class="dropdown">
	                    <li><a href="/content/girlscouts-vtk/controllers/vtk.admin_reports_downloadable.csv" title="download admin report">download</a></li>
                   </ul>
                   <% } %>
	             </li>
		     <% } %>


          <% if(VtkUtil.hasPermission(troop, Permission.PERMISSION_VIEW_FINANCE_ID) ) { %>
          <li <%= ("finances".equals(activeTab)) ? "class='active'" : "" %>><a href="/content/girlscouts-vtk/en/vtk.finances.html?qtr=1">Finances</a>
		         <ul>
		          <% if("finances".equals(activeTab)) {

               if(VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_FINANCE_ID)) { %>
                  <li>
                   <a title="Edit Finance Fields" href="/content/girlscouts-vtk/en/vtk.admin_finances.html">edit finance fields</a>
                  </li>
                  <%
		            }
                }else if("financesadmin".equals(activeTab)){

              	 if(VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_FINANCE_ID)) { %>
                   <li>

                    <a title="enter finance" href="/content/girlscouts-vtk/en/vtk.finances.html">enter finance</a>

                   </li>
                  <%
                	 }
                 }
		            %>
		          </ul>
          </li>
         <% }else if(VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_FINANCE_FORM_ID)) { %>
            <li <%= ("financesadmin".equals(activeTab)) ? "class='active'" : "" %>> <a title="Edit Finance Fields" href="/content/girlscouts-vtk/en/vtk.admin_finances.html">Finances</a> </li>
         <% } %>


         <!--
          <li <%= ("profile".equals(activeTab)) ? "class='active'" : "" %>><a href="/content/girlscouts-vtk/en/vtk.profile.html">Profile</a></li>
        -->
        </ul>
      </div>
      <%
      //  if (troop.getYearPlan() != null) {
      %>
    </div><!--/columns-->
  <%
    //}
  %>

</div><!-- /hide-for-print -->
</div>
<!--  start utility nav -->
<div id="vtkNav" class="row">
<div class="hide-for-print crumbs clearfix show-for-large-up">
  <div class="column small-24 large-centered large-20">
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
                if ("plan".equals(activeTab) && troop.getYearPlan() != null  && VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_YEARPLAN_ID) ) { %>
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
                if( VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_ACTIVITY_ID)  && activity.getIsEditable() ){%>
                <li>
                     <a data-reveal-id="modal_popup_activity" data-reveal-ajax="true" href="/content/girlscouts-vtk/controllers/vtk.include.activity_edit_react.html?elem=<%=planView.getSearchDate().getTime()%>">Edit Activity</a>
                  </li>
              <% }
                if (  !(activity.getCancelled()!=null && activity.getCancelled().equals("true") ) && 
                activity.getRegisterUrl()  !=null && !activity.getRegisterUrl().equals("")){%>
                <li><a href="<%=activity.getRegisterUrl()%>"  target="_blank">Register for this event</a></li><%
                } 
                
                if( VtkUtil.hasPermission(troop, Permission.PERMISSION_RM_ACTIVITY_ID) ){
                    %><li><a href="javascript:rmCustActivity12(aPath)">delete this activity</a></li><% 
                } 
                  break;
            case MEETING:
                pageContext.setAttribute("YearPlanComponent", "MEETING");
              try { Object meetingPath = planView.getMeeting().getMeetingInfo().getPath();//.getAttribute("MEETING_PATH");
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
          <% if( "myTroop".equals(activeTab) && VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_TROOP_IMG_ID)  ) { %>
              <li><a data-reveal-id="modal_upload_image" title="update photo" href="#">add/change a photo of your troop</a></li>
              <li><a title="remove photo" href="#" onclick="rmTroopInfo()">remove troop photo</a></li>
          <% } %>
                  <!-- if finance page -->
            <% if("finances".equals(activeTab) || "financesadmin".equals(activeTab)) {
                
                     if(VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_FINANCE_ID)) { %>
                            <li>
                        <% if("editFinances".equals((String)pageContext.getAttribute("activeSubTab"))) { %>
                                <p>edit finance fields</p>
                       
                        <% } else if("financesadmin".equals(activeTab)){ %>
                             <a title="Finance" href="/content/girlscouts-vtk/en/vtk.finances.html">enter finance</a>
                        
                        <% } else if( VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_FINANCE_FORM_ID)) { %>
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
</div>
