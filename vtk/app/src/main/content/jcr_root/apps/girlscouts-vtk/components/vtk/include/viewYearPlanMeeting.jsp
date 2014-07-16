<!-- apps/girlscouts-vtk/components/vtk/include/viewYearPlanMeeting.jsp -->
<%
	MeetingE meeting = (MeetingE) _comp;
	Meeting meetingInfo = meetingDAO.getMeeting(  meeting.getRefId() );
	java.util.List <Activity> _activities = meetingInfo.getActivities();
	java.util.Map<String, JcrCollectionHoldString> meetingInfoItems=  meetingInfo.getMeetingInfo();
	
	
	boolean isLocked=false;
	if(searchDate.before( new java.util.Date() ) && user.getYearPlan().getSchedule()!=null ) isLocked= true;
	
        boolean isCanceled =false;
        if( meeting.getCancelled()!=null && meeting.getCancelled().equals("true")){
		isCanceled  = true;
	}
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
<%
		if( user.getYearPlan().getSchedule()!=null ) {
%>
			<div class="count"><%= meetingCount %></div>
<%
		}
                if (isCanceled) {
%>
			<div class="cancelled"><div class="cross">X</div></div>
<%
                }
%>
			<div class="date">
        <%if( user.getYearPlan().getSchedule()!=null ) {%>
				<div class="cal"><span class="month"><%= FORMAT_MONTH.format(searchDate)%><br/></span><span class="day"><%= FORMAT_DAY_OF_MONTH.format(searchDate)%><br/></span><span class="time hide-for-small"><%= FORMAT_hhmm_AMPM.format(searchDate)%></span></div>
        <%} else {%>
                                <div class="cal"><span class="month">Meeting<br/></span><span class="day hide-for-small"><%=meetingCount%></span></div>
        <%}%>
			</div>
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
		<span class="alert">(Cancelled)</span>
<%
	}
%>
	</div>
        <div class="small-4 columns ">
		  <!--  <a id="viewMeetingButton" href="#" class="mLocked">change this meeting</a>  -->
		 <!--  <a id="viewMeetingButton" href="/content/girlscouts-vtk/en/vtk.meetingLibrary.html?mpath=<%=meeting.getPath()%>&xx=<%=searchDate.getTime()%>" class="mLocked">change this meeting</a> -->	
		<a href="javascript:void(0)" class="mLocked" onclick="mm('/content/girlscouts-vtk/controllers/vtk.meetingLibrary.html?mpath=<%=meeting.getPath()%>&xx=<%=searchDate.getTime()%>')">change this meeting</a>
	
	
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
        <div class="small-8 columns"><a id="overviewButtonX" href="javascript:void(0)" onclick="openClose1('m_overview', 'm_activities')">overview</a></div>
        <div class="small-8 columns"><a id="activityPlanButtonX" href="javascript:void(0)" onclick="openClose1('m_activities', 'm_overview' )">activity plan</a></div>
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
		/*
		$( "#viewMeetingButton" ).button().click(function( event ) {
			viewMeetingLibrary('<%=meeting.getPath()%>', '<%=searchDate.getTime()%>');
		});
		*/
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

List<Asset> _aidTags = meeting.getAssets();

java.util.Date sysAssetLastLoad =  sling.getService(org.girlscouts.vtk.helpers.DataImportTimestamper.class).getTimestamp(); //SYSTEM QUERY

//out.println("GlobalFileUpdate "+ sysAssetLastLoad);
if(meeting.getLastAssetUpdate()==null || meeting.getLastAssetUpdate().before(sysAssetLastLoad) ){
	//out.println("FRESH");
	
	_aidTags = _aidTags ==null ? new java.util.ArrayList() : _aidTags;
	
	
	
	//rm cachables
	java.util.List aidToRm= new java.util.ArrayList();
	for(int i=0;i<_aidTags.size();i++){
		if( _aidTags.get(i).getIsCachable() )
			aidToRm.add( _aidTags.get(i));
	}
	
	for(int i=0;i<aidToRm.size();i++)
		_aidTags.remove( aidToRm.get(i));
	
	
	//query aids cachables
	 java.util.List __aidTags =  meetingDAO.getAids( meetingInfo.getAidTags(), meetingInfo.getId(), meeting.getUid());
	
	//merge lists aids
	_aidTags.addAll( __aidTags );
	
	
	
	//query resources cachables
	java.util.List __resources =  meetingDAO.getResources( meetingInfo.getResources(), meetingInfo.getId(), meeting.getUid());
		
	//merge lists resources
	_aidTags.addAll( __resources );
	
	
	
	meeting.setLastAssetUpdate( new java.util.Date() );
	meeting.setAssets( _aidTags);
	userDAO.updateUser(user);
}


if( _aidTags!=null )
 for(int i=0;i<_aidTags.size();i++){
	%><li>
	<!--  POP NEW WINDOW  <a href="<%=_aidTags.get(i).getRefId()%>"  target="_blank"><%=_aidTags.get(i).getDescription()%></a> -->
	 <%=_aidTags.get(i).getType() %> :: <a href="#modal" id="<%=_aidTags.get(i).getUid() %>" onclick="x12('<%=_aidTags.get(i).getRefId()%>', '<%=java.net.URLEncoder.encode(_aidTags.get(i).getDescription())%>', '<%=_aidTags.get(i).getUid() %>')"><%=_aidTags.get(i).getDescription()%></a>
	
	 </li><% 
 }








	//out.println(meetingInfo.getId() +" : " + meeting.getUid() );
	/*
	List<org.girlscouts.vtk.models.Search> _aidTags =  meetingDAO.getAidTag( meetingInfo.getAidTags(), meetingInfo.getId());
	for(int i=0;i<_aidTags.size();i++){
		%><li> <%=_aidTags.get(i).getType()%> <a href="<%=_aidTags.get(i).getPath()%>"><%=_aidTags.get(i).getDesc()%></a> </li><% 
	}
	
	java.util.List<Asset> assets = meeting.getAssets();
	if( assets!=null)
	 for(int i=0;i< assets.size(); i++){
		%><li style="background-color:lightblue;"><%=assets.get(i).getType()%>: <a href="<%=assets.get(i).getRefId() %>"><%=assets.get(i).getRefId() %></a></li><a href="javascript:void(0)" onclick="rmAsset('<%=meeting.getUid()%>', '<%=assets.get(i).getUid()%>')" style="background-color:red;">remove</a><% 
	 }
	
	List<org.girlscouts.vtk.models.Search> custassets = meetingDAO.getAidTag_custasset(meeting.getUid());
	for(int i=0;i<custassets.size();i++){
		%> <div style="background-color:yellow;">custasset:<a href="<%=custassets.get(i).getPath() %>"><%=custassets.get(i).getPath() %></a></div><%
	}
	*/
%>
    <!-- GOOD : moved to manageAssets 
       <div style="background-color:orange;">
        	<h4>Upload File**</h4>
        		<%String assetId = new java.util.Date().getTime() +"_"+ Math.random(); %>
    
              <form action="/content/girlscouts-vtk/controllers/auth.asset.html" method="post"  
              			onsubmit="return bindAssetToYPC( '/vtk/<%=user.getTroop().getCouncilCode()%>/<%=user.getTroop().getTroopName() %>/assets/<%=assetId %>', '<%=meeting.getUid() %>' )"  enctype="multipart/form-data">
              
                       <input type="hidden" name="loc" value="/vtk/<%=user.getTroop().getCouncilCode()%>/<%=user.getTroop().getTroopName() %>/assets"/>
              Asset Name: <input type="text" id="assetDesc" name="assetDesc" value="" />
               <input type="hidden" name="id" value="<%=assetId%>"/>     
                <input type="hidden" name="me" value="<%=searchDate.getTime()%>"/>      
               <input type="hidden" name="owner" value="<%=user.getId()%>"/>
               <input type="hidden" name="createTime" value="<%=new java.util.Date()%>"/>         
			   <input type="file" id="custasset" name="custasset" size="50" />
               <br />
                <input type="submit" value="Upload File" />
         </form>
      
        </div>
    
        -->
        
        
        
        
        
        
<div class="sectionHeader">Meeting Agenda</div>

	<a href="javascript:void(0)" onclick="revertAgenda('<%=meeting.getPath()%>')"  class="mLocked">Revert to Original Agenda</a>


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
				<td><%if( user.getYearPlan().getSchedule()!=null ){ out.println(FORMAT_hhmm_AMPM.format(activSched.getTime())); }%></td>
				<td>
				
					<%if( !isLocked) {%>
						<a href="javascript:void(0)"  class="mLocked" onclick="editAgenda('<%=ii %>')"><%=_activity.getName() %></a>
					<%}else{ %>
						<%=_activity.getName() %>
					<%} %>
					
				</td>
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
<input type="button" name="" value="Add Agenda Items" onclick="addCustAgenda()"  class="mLocked"/>
<div id="newMeetingAgenda" style="display:none;">
<% if(true){// user.getYearPlan().getSchedule() !=null){ %>
       <h1>Add New Agenda Item</h1> 
	
	Enter Agenda Item Name:<br/>
	<input type="text" id="newCustAgendaName" value=""/>
	
	<br/>Time Allotment:
	<select id="newCustAgendaDuration">
		<option value="5">5</option>
		<option value="10">10</option>
                <option value="15">15</option>
		<option value="20">20</option>
                <option value="25">25</option>
		<option value="30">30</option>
	</select>
	
	 + (<%= activSched.getTime()%>)
	
	<br/>Description:<textarea id="newCustAgendaTxt"></textarea>
	<br/><br/>
	<input type="button" value="save" onclick="createCustAgendaItem1('<%=searchDate.getTime()%>', '<%=activSched.getTime().getTime()%>', '<%=meeting.getPath()%>')"/>
<%}else{ out.println("VIEW MODE"); } %>
</div>

<br/><br/>
 


<%if( !isLocked ){ %>

<style>
.mLocked{ }
.mLocked a{  }
</style>

<div id="meetingLibraryView">
<% if( false) {//user.getYearPlan().getSchedule()!=null ) { %>
	<div class="tmp" id="popup123" style="background-color:#EEEEEE;">
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
	<%@include file="../include/manageCommunications.jsp" %>
<%}else{ %>	
	
	
	<style>
.mLocked{ display:none;}
.mLocked a{ display:none; }
</style>


<% }//ednelse %>
