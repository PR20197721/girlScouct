<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>


<!-- apps/girlscouts-vtk/components/vtk/reminderEmail.jsp  -->
<% 
	PlanView planView = meetingUtil.planView(user, troop, request);
	MeetingE meeting = (MeetingE)planView.getYearPlanComponent();

	Meeting meetingInfo = meeting.getMeetingInfo();
	java.util.List <Activity> _activities = meetingInfo.getActivities();
	java.util.Map<String, JcrCollectionHoldString> meetingInfoItems= meetingInfo.getMeetingInfo();
%> 
<!--/header-->
	<div class="header clearfix">
		<h3 class="columns large-22">Reminder Email</h3>
	  	<a class="close-reveal-modal columns large-2" onclick="closeModalPage()"><i class="icon-button-circle-cross"></i></a>
	</div>
<!-- content -->
	<div class="content clearfix">
		<div id=title>
		<% if(meeting==null) {%>
			<span>meeting not scheduled</span>
		<%}else { %>
			<p>Reminder Meeting #<%=planView.getMeetingCount()%> 
			<% Date date = planView.getSearchDate();
			if(date!=null && date.after( new java.util.Date("1/1/1977") )){ %>
			<%= FORMAT_MEETING_REMINDER.format(date)%>
			<% } %>
			</p>
		<% }%>
		</div>
		<div id=sent>Sent:
			<script>
			$('#sent').append(moment(new Date()).format('MM/DD/YYYY'));
			</script>
		</div>
		<div id=email>
			<form>
				<span>Address list</span>
				<div>
					<input type="checkbox" name="addr" id="girlsParents" value="girlsParents">
					<label for="girlsParents">Girl/Parents</label>
					<input type="checkbox" name="addr" id="self" value="self">
					<label for="self">Self</label>					
					<input type="checkbox" name="addr" id="volunteers" value="volunteers">
					<label for="volunteers">Troop Volunteers</label>	
				</div>
				<div><label for="ownAddr">Enter your own:</label>>
					<input name="addr" type="email" id="ownAddr">
				</div>
				<span>Compose email</span>
				<div><label for="subject">Subject:</label>>
					<input name="subject" type="text" id="subject">
				</div>
				<!-- content -->
			    <div id="content-container">
			      <div class="advanced-wrapper">
			        <div class="toolbar-container"><span class="ql-format-group">
			            <select title="Font" class="ql-font">
			              <option value="sans-serif" selected>Sans Serif</option>
			              <option value="Georgia, serif">Serif</option>
			              <option value="Monaco, 'Courier New', monospace">Monospace</option>
			            </select>
			            <select title="Size" class="ql-size">
			              <option value="10px">Small</option>
			              <option value="13px" selected>Normal</option>
			              <option value="18px">Large</option>
			              <option value="32px">Huge</option>
			            </select></span><span class="ql-format-group"><span title="Bold" class="ql-format-button ql-bold"></span><span class="ql-format-separator"></span><span title="Italic" class="ql-format-button ql-italic"></span><span class="ql-format-separator"></span><span title="Underline" class="ql-format-button ql-underline"></span></span><span class="ql-format-group">
			            <select title="Text Color" class="ql-color">
			              <option value="rgb(0, 0, 0)" selected></option>
			              <option value="rgb(230, 0, 0)"></option>
			              <option value="rgb(255, 153, 0)"></option>
			              <option value="rgb(255, 255, 0)"></option>
			              <option value="rgb(0, 138, 0)"></option>
			              <option value="rgb(0, 102, 204)"></option>
			              <option value="rgb(153, 51, 255)"></option>
			              <option value="rgb(255, 255, 255)"></option>
			              <option value="rgb(250, 204, 204)"></option>
			              <option value="rgb(255, 235, 204)"></option>
			              <option value="rgb(255, 255, 204)"></option>
			              <option value="rgb(204, 232, 204)"></option>
			              <option value="rgb(204, 224, 245)"></option>
			              <option value="rgb(235, 214, 255)"></option>
			              <option value="rgb(187, 187, 187)"></option>
			              <option value="rgb(240, 102, 102)"></option>
			              <option value="rgb(255, 194, 102)"></option>
			              <option value="rgb(255, 255, 102)"></option>
			              <option value="rgb(102, 185, 102)"></option>
			              <option value="rgb(102, 163, 224)"></option>
			              <option value="rgb(194, 133, 255)"></option>
			              <option value="rgb(136, 136, 136)"></option>
			              <option value="rgb(161, 0, 0)"></option>
			              <option value="rgb(178, 107, 0)"></option>
			              <option value="rgb(178, 178, 0)"></option>
			              <option value="rgb(0, 97, 0)"></option>
			              <option value="rgb(0, 71, 178)"></option>
			              <option value="rgb(107, 36, 178)"></option>
			              <option value="rgb(68, 68, 68)"></option>
			              <option value="rgb(92, 0, 0)"></option>
			              <option value="rgb(102, 61, 0)"></option>
			              <option value="rgb(102, 102, 0)"></option>
			              <option value="rgb(0, 55, 0)"></option>
			              <option value="rgb(0, 41, 102)"></option>
			              <option value="rgb(61, 20, 102)"></option>
			            </select><span class="ql-format-separator"></span>
			            <select title="Text Alignment" class="ql-align">
			              <option value="left" selected></option>
			              <option value="center"></option>
			              <option value="right"></option>
			              <option value="justify"></option>
			            </select><span class="ql-format-group">
			            <span title="List" class="ql-format-button ql-list"></span>
			            <span class="ql-format-separator"></span>
			            <span title="Bullet" class="ql-format-button ql-bullet"></span></div>
			        <div class="editor-container"></div>
			      </div>
			    </div>
			    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.0/jquery.js"></script>
				<script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/lodash.js/2.4.1/lodash.js"></script>
				<script type="text/javascript" src="../quill.js"></script>
				<script type="text/javascript" src="scripts/advanced.js"></script>

			</form>
		</div>
	
	</div>
