<%
  if (troops != null && troops.size() > 1) {
    Cookie cookie = new Cookie("vtk_prefTroop", troop.getTroop().getGradeLevel());
    cookie.setMaxAge(-1);
    response.addCookie(cookie);
%>

<div id="troop" class="row hide-for-print">
  <div class="columns large-7 medium-7 right">
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
  </div>
</div>
<%
  }
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
        <% if(hasPermission(troop, Permission.PERMISSION_VIEW_TROOP_ID)) { %>
          <dd <%= "myTroop".equals(activeTab) ? "class='active'" : "" %>>
            <a href="/content/girlscouts-vtk/en/vtk.myTroop.html">My Troop</a>
          </dd>
        <%} %>
        <% if(hasPermission(troop, Permission.PERMISSION_VIEW_YEARPLAN_ID)) { %>
          <dd <%= "plan".equals(activeTab) ? "class='active'" : "" %>>
           <!--  <a href="/content/girlscouts-vtk/en/vtk.plan.html">Year Plan</a> -->
           <a href="/content/girlscouts-vtk/en/vtk.html">Year Plan</a>
          </dd>
        <% } %>
        <% if(hasPermission(troop, Permission.PERMISSION_VIEW_MEETING_ID)) { %>
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
          <a href="/content/girlscouts-vtk/en/vtk.resource.html">Resources</a>
        </dd>
        <% if(hasPermission(troop, Permission.PERMISSION_VIEW_FINANCE_ID) ){ %>
      	<% if ((SHOW_BETA || sessionFeatures.contains(SHOW_BETA_FEATURE)) && sessionFeatures.contains(SHOW_ADMIN_FEATURE)) { %>
          <dd <%= "reports".equals(activeTab) ? "class='active'" : "" %>>
            <a href="/content/girlscouts-vtk/en/vtk.admin_reports.html">Reports</a>
          </dd>
        <% }  %>
          <dd <%= "finances".equals(activeTab) ? "class='active'" : "" %>>
            <a href="/content/girlscouts-vtk/en/vtk.finances.html">Finances</a>
          </dd>
        <% }  %>
        <dd <%= "profile".equals(activeTab) ? "class='active'" : "" %>>
          <a href="/content/girlscouts-vtk/en/vtk.profile.html">Profile</a>
        </dd>
      </dl>
      <div class="dropdown hide-for-print hide-for-large-up">
        <a id="vtk-main-menu-button" onclick="$('#vtk-main-menu').slideToggle('slow')" class="expand">Menu</a>
        <ul id="vtk-main-menu" class="hide-for-print" style="display: none;">
          <% if(hasPermission(troop, Permission.PERMISSION_VIEW_TROOP_ID)) { %>
          <li class='has-dropdown<%= ("myTroop".equals(activeTab)) ? " active" : " " %>'><a href="/content/girlscouts-vtk/en/vtk.myTroop.html">My Troop</a>
          	<ul class="dropdown">
          	<% if("myTroop".equals(activeTab)) { %>
          		<li><a data-reveal-id="modal_upload_image" title="update photo" href="#">add/change a photo of your troop</a></li>
          		<li><a title="remove photo" href="#" onclick="rmTroopInfo()">remove troop photo</a></li>
          	<% } %>
            </ul>
          </li>
          <%}%> 
          <% if(hasPermission(troop, Permission.PERMISSION_VIEW_YEARPLAN_ID)) { %>
          <li class='has-dropdown<%= ("plan".equals(activeTab)) ? " active" : " " %>'><a href="/content/girlscouts-vtk/en/vtk.html">Year Plan</a>
            <ul class="dropdown">
            <% if("plan".equals(activeTab)) { %>
              <li><a onclick="newLocCal()">Specify Meeting Dates and Locations</a></li>
              <li><a onclick="doMeetingLib()">Add Meeting</a></li>
              <li><a onclick="newActivity()">Add Activity</a></li>
              <li><a onclick="self.location='/content/girlscouts-vtk/en/cal.ics'">Download Calendar</a></li>
            <% } %>
            </ul>
          </li>
          <%}%>
          <% if(hasPermission(troop, Permission.PERMISSION_VIEW_MEETING_ID)) { %>
          <li class='has-dropdown<%= ("planView".equals(activeTab)) ? " active" : " " %>'> <a <%= troop.getYearPlan() != null ? "href='/content/girlscouts-vtk/en/vtk.details.html'" :  "href='#' onClick='alert(\"Please select a year plan\")'"  %>>Meeting Plan</a>
            <ul class="dropdown">
            <% if("planView".equals(activeTab)) { 
               switch(meetingUtil.planView(user, troop, request).getYearPlanComponent().getType() ) {
                case ACTIVITY:
                  Activity activity = (Activity)meetingUtil.planView(user, troop, request).getYearPlanComponent();
                  if( activity.getIsEditable() ){%>
                  <li><a href="#" onclick="doEditActivity('editCustActiv')">edit activity</a></li>
                <% }
                  if ( !(activity.getCancelled()!=null && activity.getCancelled().equals("true") ) && 
                  activity.getRegisterUrl()  !=null && !activity.getRegisterUrl().equals("")){%>
                  <li><a href="<%=activity.getRegisterUrl()%>" target="_blank">Register for this event</a></li><%
                  } %>
                    <li><a href="javascript:rmCustActivity12(aPath)">delete this activity</a></li><% 
                    
              	case MEETING:
                	try { Object meetingPath = pageContext.getAttribute("MEETING_PATH");
                        if (meetingPath != null && meetingPath != "") {
                          Long planViewTime = (Long) pageContext.getAttribute("PLANVIEW_TIME");%>
                        <li>
                        <a href="#" onclick="loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingLibrary.html?mpath=<%=(String) meetingPath %>&xx=<%= planViewTime.longValue() %>', false, null, true)">replace this meeting</a>
                        </li><% 
                        }
                    } catch (Exception te) {
                      te.printStackTrace();
                    }
                  break;
                }%>
              <!-- <li><a href="#" onclick="doEditActivity('editCustActiv')">edit activity</a></li>
              <li><a href="#" target="_blank">Register for this event</a></li>
              <li><a href="javascript:rmCustActivity12(aPath)">delete this activity</a></li> -->
            <% } %>  
            </ul>
          </li>
          <%  } %>
          <li <%= ("resource".equals(activeTab)) ? "class='active'" : "" %>><a href="/content/girlscouts-vtk/en/vtk.resource.html">Resources</a></li>
        <% if(hasPermission(troop, Permission.PERMISSION_VIEW_FINANCE_ID) ) { %>
        <% if ((SHOW_BETA || sessionFeatures.contains(SHOW_BETA_FEATURE)) && sessionFeatures.contains(SHOW_ADMIN_FEATURE)) { %>
          <li <%= ("reports".equals(activeTab)) ? "class='active'" : "" %>><a href="/content/girlscouts-vtk/en/vtk.admin_reports.html">Reports</a></li>
	       <% } %>
          <li <%= ("finances".equals(activeTab)) ? "class='active'" : "" %>><a href="/content/girlscouts-vtk/en/vtk.finances.html?qtr=1">Finances</a></li>
         <% } %>
          <li <%= ("profile".equals(activeTab)) ? "class='active'" : "" %>><a href="/content/girlscouts-vtk/en/vtk.profile.html">Profile</a></li>
        </ul>
      </div>
      <%
      //  if (troop.getYearPlan() != null) {
      %>
    </div><!--/columns-->
  <%
    //}
  %>
</div><!--/hide-for-print-->
