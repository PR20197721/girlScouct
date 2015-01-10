<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<script>
  function toggleSection(section) {
    $("#manageCalendarTab").removeClass("active");
    $("#manageLocationTab").removeClass("active");
    $("#manageActivityTab").removeClass("active");
    $("#manageCalendarSection").hide();
    $("#manageLocationSection").hide();
    $("#manageActivitySection").hide();
    if (section == "calendar") {
      $("#manageCalendarTab").addClass("active");
      $("#manageCalendarSection").show();
    } else if (section == "location")  {
        $("#manageLocationTab").addClass("active");
        $("#manageLocationSection").show();
    } else if (section == "activity") {
        $("#manageActivityTab").addClass("active");
        $("#manageActivitySection").show();
    }
  }
</script>
<div id="modal_meeting" class="reveal-modal">
    <div class="header clearfix">
      <h3 class="columns large-22">MEETING date and locations</h3>
      <a class="close-reveal-modal columns large-2" href="#"><i class="icon-button-circle-cross"></i></a>
    </div>

    <div class="tabs-wrapper">
      <dl class="tabs" data-tab>
        <dd class="active"><a href="#panel1">manage calendar</a></dd>
        <dd><a href="#panel2">manage location</a></dd>
        <dd><a href="#panel3">manage activity</a></dd>
      </dl>

      <div class="tabs-content">
        
        <div class="content clearfix active row" id="panel1">
          <form class="clearfix">
            <section class="clearfix">
              <p>Configure X meeting dates starting on or after XX/XX/XXXX:</p>
              <div class="large-4 columns">
                <input type="text" placeholder="Start Date" />
              </div>
              <div class="large-2 columns">
                <a href="#nogo" title="calendar"><i class="icon-calendar"></i></a>
              </div>
              <div class="large-4 columns">
                <input type="text" placeholder="Time" />
              </div>
              <div class="large-3 columns">
                <select>
                  <option value="0">AM</option>
                  <option value="1">PM</option>
                </select>
              </div>
              <div class="large-4 columns left">
                <select>
                  <option value="0">Frequency</option>
                  <option value="1">Once a day</option>
                </select>
              </div>
            </section>
            <section class="clearfix">
              <p>Do not schedule the meeting the week of:</p>
              <input id="checkbox1" type="checkbox" /><label for="checkbox1"><p><span class="date">12/12/14</span><span>Thanksgiving</span></p></label>
              <input id="checkbox2" type="checkbox" /><label for="checkbox2"><p><span class="date">12/12/14</span><span>Another Holiday</span></p></label>
              <input id="checkbox3" type="checkbox" /><label for="checkbox3"><p><span class="date">12/12/14</span><span>Holiday Name</span></p></label>
              <input id="checkbox4" type="checkbox" /><label for="checkbox4"><p><span class="date">12/12/14</span><span>Thanksgiving</span></p></label>
              <input id="checkbox5" type="checkbox" /><label for="checkbox5"><p><span class="date">12/12/14</span><span>Thanksgiving</span></p></label>
            </section>
            <button class="btn right">create calendar</button>
            <button class="btn right">cancel</button>
          </form>
          <!--/when user already has a meeting calendar-->
          <div class="meetings-list clearfix">
            <p>Select the calendar icon to change the date, time, or cancel an individual meeting. Or select the [symbol] to use the planning wizard to reconfigure the calendar from that date forward</p>
            <table>
              <tr>
                <td><a href="#nogo" title="calendar"><i class="icon-calendar"></i></a></td>
                <td><span>1</span></td>
                <td><span>1 Sep 15, 2014, 3:00PM</span></td>
                <td><span>This represents the Meeting Title Meeting Title Meeting Title</span></td>
                <td> <a href="#nogo" title="settings"><i class="icon-gear"></i></a></td>
              </tr>
              <tr>
                <td><a href="#nogo" title="calendar"><i class="icon-calendar"></i></a></td>
                <td><span>1</span></td>
                <td><span>1 Sep 15, 2014, 3:00PM</span></td>
                <td><span>This represents the Meeting Title Meeting Title Meeting Title</span></td>
                <td> <a href="#nogo" title="settings"><i class="icon-gear"></i></a></td>
              </tr>
              <tr>
                <td><a href="#nogo" title="calendar"><i class="icon-calendar"></i></a></td>
                <td><span>1</span></td>
                <td><span>1 Sep 15, 2014, 3:00PM</span></td>
                <td><span>This represents the Meeting Title Meeting Title Meeting Title</span></td>
                <td> <a href="#nogo" title="settings"><i class="icon-gear"></i></a></td>
              </tr>
            </table>
          </div><!--/meeting-list-->
        </div><!--/content-1-->

        <div class="content clearfix row" id="panel2">
          <p>Add, delete or edit locations to assign to your meetings.</p>
          <form>
            <section>
              <div class="column large-12">
                 <input type="text" placeholder="Location Name" />
              </div>
              <div class="column large-12">
                <input type="text" placeholder="Location Address" />
              </div>
              <p>Applies to 15 of 15 meetings</p>
            </section>
            <button class="btn right">Add</button>
          </form>
        </div><!--/content-2-->

        <div class="content clearfix row" id="panel3">
          <table>
            <tr>
              <td><strong>Jan 18, 2015, 1:00pm</strong></td>
              <td> Challange Learning Center: Living in Space Program</td>
              <td><a href="#nogo" title="remove">Remove</a></td>
            </tr>
          </table>
        </div><!--/content-3-->
    </div>
  </div><!--/tab wrapper-->
</div><!--/modal-->


<!-- apps/girlscouts-vtk/components/vtk/locations.jsp  -->
<div class="row modalHeader">
    <%
      boolean isWarning=false;
      String instruction = "Specify Dates and Locations";
      if (isWarning) {
    %>
  <div class="small-4 medium-2 large-2 columns">                
    <div class="warning">
      <img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/warning-small.png" width="20" height="20" align="left"/>
    </div>
  </div>
  <div class="small-16 medium-20 large-20 columns">
    <% } else { %>
  <div class="small-20 medium-22 large-22 columns">
    <% } %>
        <span class="instruction"><%= instruction %></span>
  </div>
  <div class="small-4 medium-2 large-2 columns">
      <a class="right" href="/content/girlscouts-vtk/en/vtk.html?rand=<%= new java.util.Date().getTime()%>">
        <img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/close-small.png" width="20" height="20" border="0" align="right">
      </a>
  </div>
</div><!--/header-->

<div class="row modalNav">
  <ul class="small-block-grid-1 medium-block-grid-3 large-block-grid-3 specifyDates">
    <li id="manageCalendarTab" class="active manageCalendarTab"><a href="#" onclick="toggleSection('calendar')">Calendar</a></li>
    <li id="manageLocationTab" class="manageCalendarTab"><a href="#" onclick="toggleSection('location')">Location</a></li>
    <li id="manageActivityTab" class="manageCalendarTab"><a href="#" onclick="toggleSection('activity')">Activities</a></li>
  </ul>
</div>
<div class="row modalBody">
  <div class="small-24 medium-24 large-24 columns">
  <%
    if (troop.getYearPlan() != null) {
  %>
    <div id="manageCalendarSection">
      <div class="sectionBar">Manage Calendar</div>
       <div id="calMng">
        <%
          if( troop.getYearPlan().getSchedule() == null ) {
          %>
        <%@include file="include/calSched.jsp" %>
        <%
          }else{
        %>
        <%@include file="include/calList.jsp" %>
        <% } %>
       </div>
      </div>
      <div id="manageLocationSection">
        <%
          if( false) {//troop.getYearPlan().getSchedule() == null ){
        %>
         Please first select your calendar start date to set meeting locations.
        <%
        		} else {
        %>
        <%@include file="include/location.jsp" %>
        <%
        		}
        %>
      </div>
      <div id="manageActivitySection">
        <%
          if( false) {// troop.getYearPlan().getSchedule() == null ){
        %>
          Please first select your calendar start date to set meeting activities.
        <%
          } else {
        %>
        <%@include file="include/manageActivities.jsp" %>
        <% } %>
      </div>
      <%
        } else {
      %>
      <span class="error">This year plan has no meetings.<br/> Please select a different year plan.</span>
      <% } %>
    </div>
  </div>
