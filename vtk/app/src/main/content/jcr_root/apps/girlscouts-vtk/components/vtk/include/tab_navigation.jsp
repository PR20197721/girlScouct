<%@ page
  import="org.girlscouts.vtk.helpers.ConfigManager,
                org.girlscouts.vtk.helpers.CouncilMapper,java.text.SimpleDateFormat,java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />
<%@include file="session.jsp"%>
<%
out.println("***** "+ VtkUtil.getYearPlanBase(user, troop) );

String activeTab=request.getParameter("activeTab");
PlanView planView = meetingUtil.planView(user, troop, request);


/*
//Get URL for community page
ConfigManager configManager = (ConfigManager)sling.getService(ConfigManager.class);
String communityUrl = "";
if (configManager != null) {
    communityUrl = configManager.getConfig("communityUrl");
}
*/
String communityUrl = "/content/girlscouts-vtk/en/vtk.home.html";

out.println("***** "+ VtkUtil.getYearPlanBase(user, troop) );

if (troops != null && troops.size() > 1) {
    Cookie cookie = new Cookie("vtk_prefTroop", troop.getTroop().getGradeLevel());
    cookie.setMaxAge(-1);
    response.addCookie(cookie);
%>

<div id="troop" class="row hide-for-print">
	  
	  <div class="columns large-7 medium-9 right">
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
	  <div class="columns large-4 medium-4">
	  
       <a href="<%=communityUrl%>">Member Profile</a>
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
          
              <%if(troop.getYearPlan()!=null &&
                 (troop.getYearPlan().getMeetingEvents()!=null && troop.getYearPlan().getMeetingEvents().size()>0 )){ %>
                    <a href="/content/girlscouts-vtk/en/vtk.myTroop.html">My Troop</a>
           <%}else{ %>
                    <a href="#" onclick="alert('There is not Year Plan set up at this time.')">My Troop</a>
           <%} %>
          </dd>
        <%} %>
        <% if(hasPermission(troop, Permission.PERMISSION_VIEW_YEARPLAN_ID)) { %>
          <dd <%= "plan".equals(activeTab) ? "class='active'" : "" %>>
           
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
        <% if(hasPermission(troop, Permission.PERMISSION_VIEW_REPORT_ID) ){ %>
      	  <dd <%= "reports".equals(activeTab) ? "class='active'" : "" %>>
            <a href="/content/girlscouts-vtk/en/vtk.admin_reports.html">Reports</a>
          </dd>
          <% }  %>
       
       <% if(hasPermission(troop, Permission.PERMISSION_VIEW_FINANCE_ID) ){ %>
          <dd <%=  ("finances".equals(activeTab) || "financesadmin".equals(activeTab)) ? "class='active'" : "" %>>
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
          <li class='has-dropdown<%= ("myTroop".equals(activeTab)) ? " active" : " " %>'>
          
          <%if(troop.getYearPlan()!=null &&
                 (troop.getYearPlan().getMeetingEvents()!=null && troop.getYearPlan().getMeetingEvents().size()>0 )){ %>
             <a href="/content/girlscouts-vtk/en/vtk.myTroop.html">My Troop</a>
          <%}else{ %>
            <a href="#" onclick="alert('There is no Year Plan set up at this time.')">My Troop</a>
          <%} %>
          
          
          	<ul class="dropdown">
          	<% if("myTroop".equals(activeTab) &&  hasPermission(troop, Permission.PERMISSION_EDIT_TROOP_IMG_ID) ) { %>
          		<li><a data-reveal-id="modal_upload_image" title="update photo" href="#">add/change a photo of your troop</a></li>
          		<li><a title="remove photo" href="#" onclick="rmTroopInfo()">remove troop photo</a></li>
          	<% } %>
            </ul>
          </li>
          <%}%>
          <% if(hasPermission(troop, Permission.PERMISSION_VIEW_YEARPLAN_ID)) { %>
          <li class='has-dropdown<%= ("plan".equals(activeTab)) ? " active" : " " %>'><a href="/content/girlscouts-vtk/en/vtk.html">Year Plan</a>
            <ul class="dropdown">
            <% if("plan".equals(activeTab)  && hasPermission(troop, Permission.PERMISSION_EDIT_YEARPLAN_ID)) { %>
              <li><a onclick="newLocCal()">Specify Meeting Dates and Locations</a></li>
              <li><a onclick="doMeetingLib(<%=calendarUtil.isEventPastGSYear(user, troop)%>)">Add Meeting</a></li>
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
                  Activity activity = (Activity)planView.getYearPlanComponent();
                  if( hasPermission(troop, Permission.PERMISSION_EDIT_ACTIVITY_ID)  && activity.getIsEditable() ){%>
                  <li><a href="#" onclick="doEditActivity('editCustActiv')">edit activity</a></li>
                <% }
                  if ( !(activity.getCancelled()!=null && activity.getCancelled().equals("true") ) &&
                  activity.getRegisterUrl()  !=null && !activity.getRegisterUrl().equals("")){%>
                  <li><a href="<%=activity.getRegisterUrl()%>" target="_blank">Register for this event</a></li><%
                  }
                  
                  if(hasPermission(troop, Permission.PERMISSION_RM_ACTIVITY_ID) ){
                        %><li><a href="javascript:rmCustActivity12(aPath)">delete this activity</a></li><%
                  }		   
              	case MEETING:
                	try { 
                		
                		
                		
                		Object meetingPath = planView.getMeeting().getMeetingInfo().getPath(); //pageContext.getAttribute("MEETING_PATH");
                       if(hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID))
                    	  if (meetingPath != null && meetingPath != ""  ) {
                          //Long planViewTime = (Long) pageContext.getAttribute("PLANVIEW_TIME");
                          
                            
                          %>
                          
                        <li id="replaceMeetingSmall"></li>
                        <%
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
          <li <%= ("resource".equals(activeTab)) ? "class='active'" : "" %>><a href="/content/girlscouts-vtk/en/vtk.resource.html">Resources</a></li>
        <% if(hasPermission(troop, Permission.PERMISSION_VIEW_FINANCE_ID) ) { %>
	        <% if(hasPermission(troop, Permission.PERMISSION_VIEW_REPORT_ID) ){ %>
	            <li <%= ("reports".equals(activeTab)) ? "class='active'" : "" %>><a href="/content/girlscouts-vtk/en/vtk.admin_reports.html">Reports</a></li>
		     <% } %>
         
         
          <li <%= ("finances".equals(activeTab)) ? "class='active'" : "" %>><a href="/content/girlscouts-vtk/en/vtk.finances.html?qtr=1">Finances</a>
		         <ul>
		          <% if("finances".equals(activeTab)) {
		                
		                     if(hasPermission(troop, Permission.PERMISSION_EDIT_FINANCE_ID)) { %>
		                            <li>
		                       
		                             <a title="Edit Finance Fields" href="/content/girlscouts-vtk/en/vtk.admin_finances.html">edit finance fields</a>
		                       
		                            </li>
		            <%
		                    }
		                }else if("financesadmin".equals(activeTab)){
		                	
		                	 if(hasPermission(troop, Permission.PERMISSION_EDIT_FINANCE_ID)) { %>
                             <li>
                        
                              <a title="enter finance" href="/content/girlscouts-vtk/en/vtk.finances.html">enter finance</a>
                        
                             </li>
                            <%
		                	 }
                     }
		                	
		                	
		                
		            %>
		            
		            
		            
		            </ul>
          </li>
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
  
</div><!-- /hide-for-print -->
