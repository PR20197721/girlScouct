<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>

<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>

<%@include file="/libs/foundation/global.jsp" %>

<cq:defineObjects/>

<%@include file="session.jsp"%>


<form className="clearfix">
  <section className="clearfix">
<!--     <p>Configure X meeting dates starting on or after XX/XX/XXXX:</p> -->
    <div className="large-4 columns">
      <input type="text" placeholder="Start Date" id="calStartDt" name="calStartDt" value="<%=troop.getYearPlan().getCalStartDate()==null ? "" : FORMAT_MMddYYYY.format(new java.util.Date(troop.getYearPlan().getCalStartDate())) %>" />
    </div>
    <div className="large-2 columns">
      <a href="#nogo" title="calendar"><i className="icon-calendar"></i></a>
    </div>
    <div className="large-4 columns">
      <input type="text" placeholder="Time" id="calTime" value="<%=troop.getYearPlan().getCalStartDate()==null ? (org.girlscouts.vtk.models.VTKConfig.CALENDAR_START_TIME_HOUR+":"+org.girlscouts.vtk.models.VTKConfig.CALENDAR_START_TIME_MIN) : FORMAT_hhmm.format(new java.util.Date(troop.getYearPlan().getCalStartDate())) %>" />
    </div>
    <div className="large-3 columns">
      <select>
        <option value="0">AM</option>
        <option value="1">PM</option>
      </select>
    </div>
    <div className="large-4 columns left">
      <select>
        <option value="0">Frequency</option>
        <option value="1">Once a day</option>
      </select>
    </div>
  </section>
  <section className="clearfix">
    <p>Do not schedule the meeting the week of:</p>
    <input id="checkbox1" type="checkbox" /><label for="checkbox1"><p><span className="date">12/12/14</span><span>Thanksgiving</span></p></label>
    <input id="checkbox2" type="checkbox" /><label for="checkbox2"><p><span className="date">12/12/14</span><span>Another Holiday</span></p></label>
    <input id="checkbox3" type="checkbox" /><label for="checkbox3"><p><span className="date">12/12/14</span><span>Holiday Name</span></p></label>
    <input id="checkbox4" type="checkbox" /><label for="checkbox4"><p><span className="date">12/12/14</span><span>Thanksgiving</span></p></label>
    <input id="checkbox5" type="checkbox" /><label for="checkbox5"><p><span className="date">12/12/14</span><span>Thanksgiving</span></p></label>
  </section>
  <button className="btn right">create calendar</button>
  <button className="btn right">cancel</button>
</form>







///////////
<div class="setupCalendar">

<form>

<div class="row">

<div class="small-24 medium-4 large-4 columns"><label for="calStartDt" ACCESSKEY=d>Start Date:</label></div>

<div class="small-24 medium-8 large-8 columns date">

<input type="text" id="calStartDt" name="calStartDt" value="<%=troop.getYearPlan().getCalStartDate()==null ? "" : FORMAT_MMddYYYY.format(new java.util.Date(troop.getYearPlan().getCalStartDate())) %>" />

</div>

                <div class="small-24 medium-4 large-4 columns"><label for="calTime" ACCESSKEY=t>Time:</label></div>

<div class="small-24 medium-8 large-8 columns">

<input type="text" id="calTime" value="<%=troop.getYearPlan().getCalStartDate()==null ? (org.girlscouts.vtk.models.VTKConfig.CALENDAR_START_TIME_HOUR+":"+org.girlscouts.vtk.models.VTKConfig.CALENDAR_START_TIME_MIN) : FORMAT_hhmm.format(new java.util.Date(troop.getYearPlan().getCalStartDate())) %>"/>

<select id="calAP">

<%

String AM = "PM";

if( troop.getYearPlan().getCalStartDate() !=null ){

AM = FORMAT_AMPM.format(new java.util.Date(troop.getYearPlan().getCalStartDate()));

} 

%>

<option value="pm" <%=AM.equals("PM") ? " SELECTED" : "" %>>pm</option>

<option value="am" <%=AM.equals("AM") ? " SELECTED" : "" %>>am</option>

</select>

</div>

</div>

<div class="row">

<div class="small-24 medium-4 large-4 columns"><label for="calFreq" ACCESSKEY=f>Frequency:</label></div>

<div class="small-24 medium-8 large-8 columns">

<select id="calFreq">

<option value="weekly" <%= troop.getYearPlan().getCalFreq().equals("weekly") ? " SELECTED" : "" %>>weekly</option>

<option value="biweekly"  <%= troop.getYearPlan().getCalFreq().equals("biweekly") ? " SELECTED" : "" %>>biweekly</option>

<option value="monthly"  <%= troop.getYearPlan().getCalFreq().equals("monthly") ? " SELECTED" : "" %>>monthly</option>

</select>

</div>

<div class="hide-for-small medium-12 large-12">&nbsp;</div>

</div>

<p><label for="exclDt" ACCESSKEY=s>Do not schedule a meeting during the week of:</label></p>

<%

String exlDates = troop.getYearPlan().getCalExclWeeksOf();

exlDates= exlDates==null ? "" : exlDates;

%>

<ul class="doubleList">






<%

UserGlobConfig ubConf =troopUtil.getUserGlobConfig();




%>




<li><label><input type="checkbox" name="exclDt" value="09/07/2015" <%=("".equals(exlDates) || exlDates.contains("09/07/2015")) ? "CHECKED" : ""  %>/>09/07/2015 (Labor Day)</label></li>

<li><label><input type="checkbox" name="exclDt" value="10/12/2015" <%=("".equals(exlDates) || exlDates.contains("10/12/2015")) ? "CHECKED" : ""  %>/>10/12/2015 (Columbus Day)</label></li>

                <li><label><input type="checkbox" name="exclDt" value="11/11/2015" <%=("".equals(exlDates) || exlDates.contains("11/11/2015")) ? "CHECKED" : ""  %>/>11/11/2015 (Veteran's Day Day)</label></li>

<li><label><input type="checkbox" name="exclDt" value="11/26/2015" <%=("".equals(exlDates) || exlDates.contains("11/26/2015")) ? "CHECKED" : ""  %>/>11/26/2015 (Thanksgiving)</label></li>

<li><label><input type="checkbox" name="exclDt" value="12/25/2015" <%=("".equals(exlDates) || exlDates.contains("12/25/2015")) ? "CHECKED" : ""  %>/>12/25/2015 (Christmas)</label></li>

                <li><label><input type="checkbox" name="exclDt" value="01/01/2016" <%=("".equals(exlDates) || exlDates.contains("01/01/2016")) ? "CHECKED" : ""  %>/>01/01/2016 (New Years)</label></li>

                <li><label><input type="checkbox" name="exclDt" value="01/18/2016" <%=("".equals(exlDates) || exlDates.contains("01/18/2016")) ? "CHECKED" : ""  %>/>01/18/2016 (Martin Luther King, Jr.)</label></li>

                <li><label><input type="checkbox" name="exclDt" value="02/15/2016" <%=("".equals(exlDates) || exlDates.contains("02/15/2016")) ? "CHECKED" : ""  %>/>02/15/2016 (Washington's Birthday)</label></li>

                <li><label><input type="checkbox" name="exclDt" value="05/30/2016" <%=("".equals(exlDates) || exlDates.contains("02/30/2016")) ? "CHECKED" : ""  %>/>05/30/2016 (Memorial Day)</label></li>

                <li><label><input type="checkbox" name="exclDt" value="07/04/2016" <%=("".equals(exlDates) || exlDates.contains("07/04/2016")) ? "CHECKED" : ""  %>/>07/04/2016 (Independence Day)</label></li>

</ul>

<br/>

<div class="linkButtonWrapper">

<input type="button" value="update calendar" onclick="buildSched()" class="button linkButton"/>

</div>

</form>

</div>

<div id="calView"></div>

<script type="text/javascript">

$( "#calStartDt" ).datepicker({

        minDate: 0,

        showOn: "button",

        buttonImage: "/etc/designs/girlscouts-vtk/clientlibs/css/images/calendar-pick.png",

        buttonImageOnly: true

});

</script>