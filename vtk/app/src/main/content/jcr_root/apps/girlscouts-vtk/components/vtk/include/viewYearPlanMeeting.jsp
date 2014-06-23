<%@ page import="java.util.*, org.apache.sling.api.resource.*, org.apache.sling.jcr.api .*,java.lang.ref.*, com.day.cq.tagging.*, com.day.cq.tagging.*, org.apache.jackrabbit.commons.JcrUtils, org.apache.sling.api.resource.*"%>
<cq:defineObjects/>
<%
	MeetingDAO meetingDAO = sling.getService(MeetingDAO.class);
	MeetingE meeting = (MeetingE) _comp;
	Meeting meetingInfo = meetingDAO.getMeeting(  meeting.getRefId() );
	java.util.List <Activity> _activities = meetingInfo.getActivities();
	java.util.Map<String, JcrCollectionHoldString> meetingInfoItems=  meetingInfo.getMeetingInfo();
%>
<br/>
<div class="caca row meetingDetailHeader">
	<div class="small-2 columns previous">
<%if( prevDate!=0 ){ %>
		<a class="direction" href="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=prevDate%>"><img width="40" height="100" src="/etc/designs/girlscouts-vtk/clientlibs/css/images/previous.png"/></a>
<%} %>
	</div>
        <div class="small-4 columns">
		<div class="planSquare">
	<%if( user.getYearPlan().getSchedule()!=null ) {%>
			<%=fmt.format(searchDate) %>
	<%}else{ out.println( fmtX.format(searchDate) ); } %>
		</div>
	</div>
        <div class="small-2 columns next">
<%if( nextDate!=0 ){ %>
		<a class="direction" href="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=nextDate%>"><img width="40" height="100" src="/etc/designs/girlscouts-vtk/clientlibs/css/images/next.png"/></a>
<%} %>
	</div>
        <div class="small-12 columns">
		<h1>Meeting: <%= meetingInfo.getName() %></h1>
		<%= meetingInfo.getAidTags() %>
		<p>Location:
<%
	if( meeting.getLocationRef()!=null && user.getYearPlan().getLocations()!=null ) {
		for(int k=0;k<user.getYearPlan().getLocations().size();k++){
			if( user.getYearPlan().getLocations().get(k).getPath().equals( meeting.getLocationRef() ) ){
				%>
					<%=user.getYearPlan().getLocations().get(k).getName() %> - <%=user.getYearPlan().getLocations().get(k).getAddress() %>
				<%
			}
		}
	}

%>
		</p>
<%
	if( meeting.getCancelled()!=null && meeting.getCancelled().equals("true")){
%>
		<div style="color:#FFF; background-color:red;">CANCELLED</div>
<%
	}
%>
	</div>
        <div class="small-4 columns">
		<a id="viewMeetingButton" href="#">change this meeting</a>
		<img width="100" height="100" src="/etc/designs/girlscouts-vtk/clientlibs/css/images/badge.png"/>
	</div>
</div>
<div class="row meetingDetailDescription">
	<div class="small-1 columns">&nbsp;</div>
        <div class="small-22 columns">
                <p><%=meetingInfo.getBlurb() %></p>
	</div>
        <div class="small-1 columns">&nbsp;</div>
</div>
<div class="row meetingDetailDescription">
        <div class="small-8 columns"><a id="overviewButton" href="#">overview</a></div>
        <div class="small-8 columns"><a id="activityPlanButton" href="#">activity plan</a></div>
        <div class="small-8 columns"><!--a id="materialsListButton" href="#">materials list</a--></div>
</div>
<div class="row meetingDetailDescription">
        <div class="small-1 columns">&nbsp;</div>
        <div class="small-22 columns">
		<div id="m_overview" style="display:none;">
			<h3>Overview:</h3><%=meetingInfoItems.get("overview").getStr() %>
		</div>
		<div id="m_activities"  style="display:none;">
<%
	java.util.Iterator itr1=  meetingInfoItems.keySet().iterator(); 
	while( itr1.hasNext()){
		String name= (String) itr1.next();
		if( name.trim().toLowerCase().equals("overview")) continue;
		if( name.trim().toLowerCase().equals("meeting id")) continue;
%>
			<h3><%=name %></h3><%=meetingInfoItems.get(name).getStr() %>
<%
	}
%>
		</div>
        </div>
        <div class="small-1 columns">&nbsp;</div>
</div>
<script>
	$(function() {
		$( "#viewMeetingButton" ).button().click(function( event ) {
			viewMeetingLibrary('<%=meeting.getPath()%>');
		});
                $( "#overviewButton" ).button().click(function( event ) {
			showIt('m_overview');
                });
                $( "#activityPlanButton" ).button().click(function( event ) {
			showIt('m_activities');
                });
	});
</script>
<div class="sectionHeader">Meeting Aids</div>
<%
	String aidTags = meetingInfo.getAidTags();
	aidTags= aidTags==null ? "" : aidTags.trim().toLowerCase();
%>
<p>AidTags:<%=aidTags %></p>
<%
	TagManager tagManager = (TagManager)resourceResolver.adaptTo(TagManager.class);
	java.util.StringTokenizer t= new java.util.StringTokenizer( aidTags, ",");
	while( t.hasMoreElements()){
		com.day.cq.tagging.TagManager.FindResults x = tagManager.findByTitle(t.nextToken());
		java.util.Iterator r = x.resources ;
		while( r.hasNext() ){
			Resource res = (Resource) r.next();
%>
	<li> <a href="<%=res.getPath().replace("/jcr:content/metadata", "")%>"> <%=res.getName()%></a></li>
<%
		  }
	}
	java.util.List<Asset> assets = meeting.getAssets();
	if (assets != null) {
		for(int i=0;i<assets.size();i++) {
%>
			<%=assets.get(i).getPath() %>
<%
		}
	}
%>
<div class="sectionHeader">Meeting Agenda</div>
<a href="javascript:void(0)" onclick="revertAgenda('<%=meeting.getPath()%>')">Revert to Original Agenda</a>
<p>Select an item to view details, edit duration, or delete. Drag and drop items to reorder them.</p>
<ul id="sortable" >
<%
	java.util.Calendar activSched= java.util.Calendar.getInstance();
	activSched.setTime( searchDate);
	int duration =0;
	for(int ii=0;ii< _activities.size();ii++){
		Activity _activity = _activities.get(ii);
%>
	<li value="<%=(ii+1)%>">
		<table>
			<tr>
				<td><%if( user.getYearPlan().getSchedule()!=null ){ out.println(fmtHr.format(activSched.getTime())); }%></td>
				<td><a href="javascript:void(0)" onclick="editAgenda('<%=ii %>')"><%=_activity.getName() %></a></td>
				<td><%=_activity.getDuration() %></td>
			</tr>
		</table>
	</li>
<% 
		if( user.getYearPlan().getSchedule()!=null )
			activSched.add(java.util.Calendar.MINUTE, _activity.getDuration() );
		duration+= _activity.getDuration();
	}
%>
</ul>
<table>
	<tr>
		<td></td>
		<td><b>End</b></td>
		<td>
			<b> 
				<%int min= duration%60;%>
				<%=duration /60 >0 ? duration /60 +"hr" : ""%>
				<%= min<10 ? "0"+min : min%>min 	
			</b>
		</td>
	</tr>
</table>
<%
	for(int ii=0;ii< _activities.size();ii++){ 
		Activity _activity = _activities.get(ii);
%>
<%@include file="editActivity.jsp" %> 
<%
	}
%>
<input type="button" name="" value="Add Agenda Items" onclick="addCustAgenda()"/>
<div id="newMeetingAgenda" style="display:none;">
<% if( user.getYearPlan().getSchedule() !=null){ %>
       <h1>Add New Agenda Item</h1> 
	
	Enter Agenda Item Name:<br/>
	<input type="text" id="newCustAgendaName" value=""/>
	
	<br/>Time Allotment: = 
	<select id="newCustAgendaDuration">
		<option value="5">5</option>
		<option value="10">10</option>
		<option value="20">20</option>
		<option value="30">30</option>
	</select>
	
	 + (<%= activSched.getTime()%>)
	
	<br/>Description:<textarea id="newCustAgendaTxt"></textarea>
	<br/><br/>
	<input type="button" value="save" onclick="createCustAgendaItem1('<%=searchDate.getTime()%>', '<%=activSched.getTime().getTime()%>', '<%=meeting.getPath()%>')"/>
<%}else{ out.println("VIEW MODE"); } %>
</div>

<br/><br/>
 
<div id="meetingLibraryView">


<% if( user.getYearPlan().getSchedule()!=null ) { %>
	<div class="tmp" id="popup" style="background-color:#EEEEEE;">
<%@include file="email/meetingReminder.jsp" %>
	</div>
<%} %>
</div>
<script>
	$("#sortable").sortable({
		update:  function (event, ui) {
			repositionActivity('<%=meeting.getRefId()%>');
		}
	});
</script>
