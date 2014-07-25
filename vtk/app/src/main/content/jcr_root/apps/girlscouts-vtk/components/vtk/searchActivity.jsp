<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<%
java.util.List <org.girlscouts.vtk.models.Activity> activities =  (java.util.List <org.girlscouts.vtk.models.Activity>)session.getValue("vtk_search_activity");
%><h2 class="searchResults">Found <%=activities.size()%> Activities</h2>
<ul>
<% 
	for(int i=0;i<activities.size();i++){
		boolean isExists=false;
		if( user.getYearPlan().getActivities() !=null) {
			for(int y=0;y<user.getYearPlan().getActivities().size();y++) {
				if( user.getYearPlan().getActivities().get(y).getName().equals( activities.get(i).getName())) {
					isExists=true;
					break;
				}
			}
		}
%>
		<li class="searchResultsItem" > 
			<%=activities.get(i).getName()%>
			<br/>Start: <%=activities.get(i).getDate() %>
			<br/>End <%=activities.get(i).getEndDate() %>
			<br/>Location:<%=activities.get(i).getLocationAddress()==null ? "Currently no location" : activities.get(i).getLocationAddress() %>
			<p> <%=activities.get(i).getContent() %> </p>
			<%if(activities.get(i).getDate()==null){ %>
				<i>Unable to add this item due to missing start date.</i>
			<% }else if( isExists ){ %>
					<i>This Activity is currently is already selected </i>
			<%}else{ %>
				<a href="#" class="reserved button linkButton" onclick="addActiv3('<%=activities.get(i).getUid()%>')">Select Activity</a>
			<%} %>
			<div id="cust_activ_<%=activities.get(i).getUid()%>"></div>
		</li> 
<%
	}
%>
<script>
function addActiv3(id){
	$.ajax({
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
		type: 'POST',
		data: { 
			newCustActivityBean:id,
			a:Date.now()
		},
		success: function(result) {
			//document.getElementById("cust_activ_"+id).innerHTML='Added';
			location.reload();
		}
	});
}
</script>
