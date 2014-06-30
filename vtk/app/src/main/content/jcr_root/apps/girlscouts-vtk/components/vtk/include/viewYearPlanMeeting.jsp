<%@ page import="java.util.*, org.apache.sling.api.resource.*, org.apache.sling.jcr.api .*,java.lang.ref.*, com.day.cq.tagging.*, com.day.cq.tagging.*, org.apache.jackrabbit.commons.JcrUtils, org.apache.sling.api.resource.*"%>
<cq:defineObjects/>


<%


	MeetingDAO meetingDAO = sling.getService(MeetingDAO.class);
	MeetingE meeting = (MeetingE) _comp;
	Meeting meetingInfo = meetingDAO.getMeeting(  meeting.getRefId() );
	java.util.List <Activity> _activities = meetingInfo.getActivities();
	java.util.Map<String, JcrCollectionHoldString> meetingInfoItems=  meetingInfo.getMeetingInfo();
	
	
	boolean isLocked=false;
	if(searchDate.before( new java.util.Date() )) isLocked= true;
	
	//System.err.println("test");
	
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
		
		
		#<%=(currInd+1 )%>
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
        <div class="small-4 columns ">
		<a id="viewMeetingButton" href="#" class="mLocked">change this meeting</a>
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
        <div class="small-8 columns"><a id="overviewButtonX" href="javascript:void(0)" onclick="openClose('m_overview')">overview</a></div>
        <div class="small-8 columns"><a id="activityPlanButtonX" href="javascript:void(0)" onclick="openClose('m_activities')">activity plan</a></div>
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

	
	
	out.println(meetingInfo.getId() );
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
	
%>

       <div style="background-color:orange;">
        	<h4>Upload File</h4>
        	
              <form action="/vtk/<%=user.getTroop().getCouncilCode()%>/<%=user.getTroop().getTroopName() %>/assets/" method="post"
                        enctype="multipart/form-data">
                       
               <input type="hidden" name="refId" value="<%=meeting.getUid()%>"/>      
               <input type="hidden" name="owner" value="<%=user.getId()%>"/>
               <input type="hidden" name="createTime" value="<%=new java.util.Date()%>"/>         
			   <input type="file" name="custasset" size="50" />
               <br />
                <input type="submit" value="Upload File" />
         </form>
         
         
         <div style="background-color:red">CAMERA
         
         <div id="example" style="height:300px;"></div>
			<div id="gallery" style=""></div>
			
			
        <script> 
         container = document.getElementById( "example" );
         gallery = document.getElementById( "gallery" );

         myPhotobooth = new Photobooth( container );

         myPhotobooth.onImage = function( dataUrl ){
         	var myImage = document.createElement( "img" );
         	myImage.src = dataUrl;
         	gallery.appendChild( myImage );
         	
         		
         	var x =$.ajax({ // ajax call starts
         		url: '/vtk/<%=user.getTroop().getCouncilCode()%>/<%=user.getTroop().getTroopName() %>/assets/', // JQuery loads serverside.php
         		
         		data: {
         			"./jcr:data":myImage.src,
         			"./jcr:mimeType":"image/png"
         			
         		},
         		
         		type: 'POST',
         		success: function (data) { 
         		},
         		error: function (data) { 
         		}
         	});
         	
         };
         
         
         
         
        </script>
        
           
        
        
        
        
         </div>
        </div>
        
        
        
        
        
        
        
        
        
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
				<td><%if( user.getYearPlan().getSchedule()!=null ){ out.println(fmtHr.format(activSched.getTime())); }%></td>
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
<% if( user.getYearPlan().getSchedule() !=null){ %>
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
<% if( user.getYearPlan().getSchedule()!=null ) { %>
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