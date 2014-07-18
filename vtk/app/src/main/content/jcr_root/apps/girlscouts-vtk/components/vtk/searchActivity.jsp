<%
java.util.List <org.girlscouts.vtk.models.Activity> activities =  (java.util.List <org.girlscouts.vtk.models.Activity>)session.getValue("vtk_search_activity");
%><h2 class="searchResult">Found <%=activities.size()%> Activites </h2>
<ul>
<% 
for(int i=0;i<activities.size();i++){
%>
		<li class="searchResultsItem" > 
			<%=activities.get(i).getName()%>
			<br/>Start: <%=activities.get(i).getDate() %>
			<br/>End <%=activities.get(i).getEndDate() %>
			<br/>Location:<%=activities.get(i).getLocationAddress() %>
			<p> <%=activities.get(i).getContent() %> </p>
			<%if(activities.get(i).getDate()==null){ %>
				<i>Unable to add this item due to missing start date.</i>
			<%}else{ %>
				<a href="#" onclick="addActiv3('<%=activities.get(i).getUid()%>')">Select Activity</a>
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
