<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<%
java.util.List <org.girlscouts.vtk.models.Activity> activities =  (java.util.List <org.girlscouts.vtk.models.Activity>)session.getValue("vtk_search_activity");
%>

<h3 class="searchResults">Found <%=activities.size()%> Activities</h3>
<ul>
	<% 
	if( activities!=null  ){
		//sort activities
		try {
			meetingUtil.sortActivityByDate( activities);
		}catch(Exception e) {
			e.printStackTrace(); 
		}
	}

	for(int i=0;i<activities.size();i++) {
		boolean isExists=false;
		if( troop.getYearPlan().getActivities() !=null) {
			for(int y=0;y<troop.getYearPlan().getActivities().size();y++) {
				if( troop.getYearPlan().getActivities().get(y).getName().equals( activities.get(i).getName())) {
					isExists=true;
					break;
				}
			}
		}
	%>
		<li class="searchResultsItem" > 
			<h4 class="activityName"><%=activities.get(i).getName()%></h4>
			<p class="activityDate"><strong>Date: <%=activities.get(i).getDate()==null? "" : FORMAT_MEETING_REMINDER.format(activities.get(i).getDate()) %> - 
			<%=activities.get(i).getEndDate()==null ? "" : FORMAT_MEETING_REMINDER.format(activities.get(i).getEndDate()) %></strong></p>
			<p class="activityLocation"><strong>Location:
			  <%=activities.get(i).getLocationName()==null ? "" : activities.get(i).getLocationName() %>
				<%=activities.get(i).getLocationAddress()==null ? "Currently no location" : activities.get(i).getLocationAddress() %>
				
				<%if(activities.get(i).getLocationAddress()!=null && !activities.get(i).getLocationAddress().trim().equals("") ){ %>
					<!-- <input type="button" onclick="showMap('<%=activities.get(i).getLocationAddress().replace("\r","")%>')" value="Map"/> -->
					<a href="javascript:void(0)" class="reserved" onclick="showMap('<%=activities.get(i).getLocationAddress().replace("\r","")%>')" >Map</a>
				<% } %>
			</strong></p>
				<%if(activities.get(i).getContent()!=null) { %>
					<p class="activityContent"><%=activities.get(i).getContent().trim() %></p>
				<% } %>
				<%if(activities.get(i).getDate()==null){ %>
					<i class="activityDisabled">Unable to add this item due to missing start date.</i>
				<% }else if( isExists ){ %>
					<i class="activityDisabled">This Activity has already been selected</i>
				<%}else{ %>
					<%if( activities.get(i).getRegisterUrl()  !=null && !activities.get(i).getRegisterUrl().equals("")){ %>
						<a href="<%=activities.get(i).getRegisterUrl()%>" target="_blank" class="reserved" onclick="addActiv3('<%=activities.get(i).getUid()%>', '<%=activities.get(i).getRegisterUrl()%>')">Select Activity and Register for event</a>
					<%}else{ %>
						<a href="#" class="reserved" onclick="addActiv3('<%=activities.get(i).getUid()%>', null)">Select Activity</a>
					<%} %>
				<%} %>
			<div id="cust_activ_<%=activities.get(i).getUid()%>"></div>
		</li> 
	<% } %>
</ul>
<script>
function addActiv3(id, registerHrefToPop){
	$.ajax({
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
		type: 'POST',
		data: { 
			act:'CreateCustomActivity',
			newCustActivityBean:id,
			a:Date.now()
		},
		success: function(result) {
			//document.getElementById("cust_activ_"+id).innerHTML='Added';
			
			/*
			if(registerHrefToPop!=null && registerHrefToPop!=''){
				window.open(registerHrefToPop, '_blank');
			}
			*/
			location.reload();
		}
	});
}



function showMap(address){
	
	 window.open('/content/girlscouts-vtk/controllers/vtk.map.html?address='+address);
}

</script>


