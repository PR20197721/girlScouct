<div class="hide-for-print crumbs clearfix hide-for-small">
  <div class="column small-24 medium-20 large-centered medium-centered large-20">
    <div class="row">
      <div class="columns small-18 medium-19">
        <ul id="sub-nav" class="inline-list hide-for-print">
          <!--if on YP page this menu shows-->
            <% 
           		if ("plan".equals(activeTab) && troop.getYearPlan() != null  && hasPermission(troop, Permission.PERMISSION_EDIT_YEARPLAN_ID) ) { %>
            		<li><a href="#" onclick="newLocCal()" title="Metting Dates and Location">Specify Dates and Locations</a></li>
            		<li><a href="#" onclick="doMeetingLib()" title="Add Meeting">Add Meeting</a></li>
            		<li><a href="#" onclick="newActivity()" title="Add Activity">Add Activity</a></li>
          		<% }
             %>

          <!-- if on Meeting Detail Page-->
            <% if("planView".equals(activeTab)) { %>
            <!--if activity detail page-->
            <% switch(meetingUtil.planView(user, troop, request).getYearPlanComponent().getType() ) {
              case ACTIVITY:
                Activity activity = (Activity)meetingUtil.planView(user, troop, request).getYearPlanComponent();
                if( activity.getIsEditable() ){%>
                <li>
                    <!--  <a href="#" data-reveal-id="editModal" onclick="doEditActivity('editCustActiv')">edit activity</a> -->
                    <a href="#" data-reveal-id="editCustActiv">edit activity</a>
                  </li>
              <% }
                if ( !(activity.getCancelled()!=null && activity.getCancelled().equals("true") ) && 
                activity.getRegisterUrl()  !=null && !activity.getRegisterUrl().equals("")){%>
                <li><a href="<%=activity.getRegisterUrl()%>"  target="_blank">Register for this event</a></li><%
                } %>
                  <li><a href="javascript:rmCustActivity12(aPath)">delete this activity</a></li><% 
                  
            case MEETING:
              try {	Object meetingPath = pageContext.getAttribute("MEETING_PATH");
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
              }
          }%> 
        
          <!-- if on a My Troop page-->
          <% if( "myTroop".equals(activeTab) && hasPermission(troop, Permission.PERMISSION_EDIT_TROOP_ID) ) { %>
          <li><a data-reveal-id="modal_upload_image" title="update photo" href="#">add/change a photo of your troop</a></li>
          <li><a title="remove photo" href="#" onclick="rmTroopInfo()">remove troop photo</a></li>
          <% } %>
            	  <!-- if finance page -->
            <% if("finances".equals(activeTab)) {
            		if ((SHOW_BETA || sessionFeatures.contains(SHOW_BETA_FEATURE)) && sessionFeatures.contains(SHOW_ADMIN_FEATURE)) { %>
            	<li>
            <% if("editFinances".equals((String)pageContext.getAttribute("activeSubTab"))) { %>
            		<p>edit finance fields</p>
            <% } else { %>
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
        <li><a data-reveal-id="modal_help" title="help"><i class="icon-questions-answers"></i></a></li>
        <% if("plan".equals(activeTab)) {%>
          <li><a
          	<% if(troop.getYearPlan() != null && meetingUtil.planView(user, troop, request)!=null && meetingUtil.planView(user, troop, request).getSearchDate() != null 
          		&& meetingUtil.planView(user, troop, request).getSearchDate().after( new java.util.Date("1/1/1977")) ){
  	         %> onclick="self.location = '/content/girlscouts-vtk/en/cal.ics'"
  	        <%} else{
  	          	%> onclick="alert('You have not yet scheduled your meeting calendar.\nPlease select a year plan and schedule your meetings by clicking on the MEETING DATES AND LOCATION link.')"
  	        <% } %> title="download the calendar"><i class="icon-download"></i></a></li>
            <li><a onclick="javascript:window.print()" title="print"><i class="icon-printer"></i></a></li>
          <% } %>
        </ul>
      </div>
    </div>
  </div>
</div>
<%@include file="modals/modal_help.jsp"%>

