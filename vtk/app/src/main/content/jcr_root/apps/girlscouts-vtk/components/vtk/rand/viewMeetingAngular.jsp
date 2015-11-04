<% 
	Meeting meeting = yearPlanUtil.getMeeting(user, meetingE.getRefId());
	boolean isCanceled =false;
	boolean calendarNotSet = false;
	if (troop.getYearPlan().getSchedule() == null) {
		calendarNotSet = true;
	}
	if (meetingE.getCancelled()!=null && meetingE.getCancelled().equals("true") ) {
		 isCanceled=true;
	}
%>


	<li ng-repeat="phone in phones | filter:query | orderBy:orderProp"
	  <%if( VtkUtil.hasPermission(troop, Permission.PERMISSION_VIEW_MEETING_ID) ){ %>
		onclick='self.location="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=date.getTime()%>"'
	  <%}%>	
		  class="meeting <%=( troop.getYearPlan().getSchedule()==null || new java.util.Date().before(date)) ? "ui-state-default" : "ui-state-default ui-state-disabled"%>" value="<%=meetingCount%>">

	<div  class="row">
        <div class="large-4 medium-5 small-24 columns">
			<div class="planSquare center ed-table" id="">
			{{phone.path}}
<%
	if (calendarNotSet) {
%>
				<div class="date">
					<div class="cal"><span class="month">Meeting<br/></span><span class="day"><%= meetingCount %><br/></span></div>
				</div>
<%
	} else {
%>
				<div class="count"><%= meetingCount %></div>
<%
		if (isCanceled) {
%>
				<div class="cancelled"><div class="cross">X</div></div>
<%
		}
%>
				<div class="date">
					<div class="cal"><span class="month"><%= VtkUtil.formatDate(VtkUtil.FORMAT_MONTH, date)%><br/></span>
						<span class="day"><%= VtkUtil.formatDate(VtkUtil.FORMAT_DAY_OF_MONTH, date)%><br/></span>
						
					</div>
				</div>
<%
	}
%>
			</div>
			<div class="centered-table" style="display:none;">
				<div class="show-for-small smallBadge">
<%
	String img= "";
	try{
		img= meetingE.getRefId().substring( meetingE.getRefId().lastIndexOf("/")+1).toUpperCase();
		if(img.contains("_") )img= img.substring(0, img.indexOf("_"));
	}catch(Exception e){
		e.printStackTrace();
	}
%>
					 
					<img width="100" height="100" src="/content/dam/girlscouts-vtk/local/icon/meetings/<%=img%>.png"/>
				</div>
			</div>
		</div>
        <div class="large-15 medium-12 small-24 columns">
			<div class="planMain">
				<h2>
<%
if( meetingE.getCancelled()!=null && meetingE.getCancelled().equals("true")){%>
				<span class="alert">(Cancelled)</span>
<% }

%>
				<%=meeting.getName() %> </h2>
				<p><small><%=meeting.getCat()%></small></p>
				
				<br/>
				<p class="blurb"><%=meeting.getBlurb() %></p>
				<%if( VtkUtil.hasPermission(troop, Permission.PERMISSION_VIEW_MEETING_ID) ){ %>
					<a href="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=date.getTime()%>">View Meeting</a>
				<%} %>
			</div>
		</div>
		<div class="large-4 medium-5 hide-for-small columns">
			<img width="100" height="100" src="/content/dam/girlscouts-vtk/local/icon/meetings/<%=img%>.png"/>
		</div>
        <div class="large-1 medium-2 small-24 columns touchscrollWrapper">
            <img class="touchscroll" src="/etc/designs/girlscouts-vtk/clientlibs/css/images/touchscroll.png" border="0" width="21" height="62"/>
        </div>
	</div>
</li>
