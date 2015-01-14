<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>

<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>

<%@include file="/libs/foundation/global.jsp" %>

<cq:defineObjects/>


<%
	
	String startAlterDate = request.getParameter("alterYPStartDate") ==null ? "" : request.getParameter("alterYPStartDate");

	
%>

<form class="clearfix">
  <section class="clearfix">

    <div class="large-4 columns">
      <input type="text" placeholder="Start Date" id="calStartDt" name="calStartDt" value="<%=( startAlterDate!=null && !startAlterDate.trim().equals("")) ? FORMAT_MMddYYYY.format(new java.util.Date( Long.parseLong(startAlterDate))):( troop.getYearPlan().getCalStartDate()==null ? "" : FORMAT_MMddYYYY.format(new java.util.Date(troop.getYearPlan().getCalStartDate()))) %>" />
    </div>
    <div class="large-2 columns">
      <label for="calStartDt"><i class="icon-calendar"></i></label>
    </div>
    <div class="large-4 columns">
      <input type="text" placeholder="Time" id="calTime" value="<%=troop.getYearPlan().getCalStartDate()==null ? (org.girlscouts.vtk.models.VTKConfig.CALENDAR_START_TIME_HOUR+":"+org.girlscouts.vtk.models.VTKConfig.CALENDAR_START_TIME_MIN) : FORMAT_hhmm.format(new java.util.Date(troop.getYearPlan().getCalStartDate())) %>" />
    </div>
    <div class="large-3 columns">
      <select id="calAP">
        <% String AM = "PM";
          if( troop.getYearPlan().getCalStartDate() !=null ){
            AM = FORMAT_AMPM.format(new java.util.Date(troop.getYearPlan().getCalStartDate()));
          } 
        %>
        <option value="pm" <%=AM.equals("PM") ? " SELECTED" : "" %>>PM</option>
        <option value="am" <%=AM.equals("AM") ? " SELECTED" : "" %>>AM</option>
      </select>
    </div>
    <div class="large-4 columns left">
      <select id="calFreq">
        <option value="weekly" <%= troop.getYearPlan().getCalFreq().equals("weekly") ? " SELECTED" : "" %>>weekly</option>
        <option value="biweekly" <%= troop.getYearPlan().getCalFreq().equals("biweekly") ? " SELECTED" : "" %>>biweekly</option>
        <option value="monthly" <%= troop.getYearPlan().getCalFreq().equals("monthly") ? " SELECTED" : "" %>>monthly</option>
      </select>
    </div>
  </section>
  <section class="clearfix holidays">
    <p>Do not schedule the meeting the week of:</p>
      <%
      String exlDates = troop.getYearPlan().getCalExclWeeksOf();
      exlDates= exlDates==null ? "" : exlDates;
      UserGlobConfig ubConf =troopUtil.getUserGlobConfig();
      %>
    <input type="checkbox" id="chk_1" name="exclDt" value="09/07/2015" <%=("".equals(exlDates) || exlDates.contains("09/07/2015")) ? "CHECKED" : ""  %>/><label for="chk_1"><p><span class="date">09/07/2015</span><span>Labor Day</span></p></label>

    <input type="checkbox" id="chk_2" name="exclDt" value="10/12/2015" <%=("".equals(exlDates) || exlDates.contains("10/12/2015")) ? "CHECKED" : ""  %>/><label for="chk_2"><p><span class="date">10/12/2015</span><span>Columbus Day</span></p></label>

    <input type="checkbox" id="chk_3" name="exclDt" value="11/11/2015" <%=("".equals(exlDates) || exlDates.contains("11/11/2015")) ? "CHECKED" : ""  %>/><label for="chk_3"><p><span class="date">11/11/2015</span><span>Veteran's Day Day</span></p></label>

    <input type="checkbox" id="chk_4" name="exclDt" value="11/26/2015" <%=("".equals(exlDates) || exlDates.contains("11/26/2015")) ? "CHECKED" : ""  %>/><label for="chk_4"><p><span class="date">11/26/2015</span><span>Thanksgiving</span></p></label>

    <input type="checkbox" id="chk_5" name="exclDt" value="12/25/2015" <%=("".equals(exlDates) || exlDates.contains("12/25/2015")) ? "CHECKED" : ""  %>/><label for="chk_5"><p><span class="date">12/25/2015</span><span>Christmas</span></p></label>

    <input type="checkbox" id="chk_6" name="exclDt" value="01/01/2016" <%=("".equals(exlDates) || exlDates.contains("01/01/2016")) ? "CHECKED" : ""  %>/><label for="chk_6"><p><span class="date">01/01/2016</span><span>New Years</span></p></label>

    <input type="checkbox" id="chk_7" name="exclDt" value="01/18/2016" <%=("".equals(exlDates) || exlDates.contains("01/18/2016")) ? "CHECKED" : ""  %>/><label for="chk_7"><p><span class="date">01/18/2016</span><span>Martin Luther King, Jr.</span></p></label>

    <input type="checkbox" id="chk_8" name="exclDt" value="02/15/2016" <%=("".equals(exlDates) || exlDates.contains("02/15/2016")) ? "CHECKED" : ""  %>/><label for="chk_8"><p><span class="date">02/15/2016</span><span>Washington's Birthday</span></p></label>

    <input type="checkbox" id="chk_9" name="exclDt" value="05/30/2016" <%=("".equals(exlDates) || exlDates.contains("02/30/2016")) ? "CHECKED" : ""  %>/><label for="chk_9"><p><span class="date">05/30/2016</span><span>Memorial Day</span></p></label>

    <input type="checkbox" id="chk_10" name="exclDt" value="07/04/2016" <%=("".equals(exlDates) || exlDates.contains("07/04/2016")) ? "CHECKED" : ""  %>/><label for="chk_10"><p><span class="date">07/04/2016</span><span>Independence Day</span></p></label>
  </section>
  <button class="btn right" onclick="buildSched()">Update Calendar</button>

</form>

<div id="calView"></div>
<script>
  //inicialize the calendar.
  $( "#calStartDt" ).datepicker();
</script>
