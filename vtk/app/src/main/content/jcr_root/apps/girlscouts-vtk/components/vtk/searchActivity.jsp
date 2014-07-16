
<%

java.util.List <org.girlscouts.vtk.models.Activity> activities =  (java.util.List <org.girlscouts.vtk.models.Activity>)session.getValue("vtk_search_activity");
	
%><h2>Found <%=activities.size()%> Activites </h2><% 
for(int i=0;i<activities.size();i++){
		%>
		
		<div style="border: 3px solid red;"> 
			<%=activities.get(i).getName()%>
			<br/>Start: <%=activities.get(i).getDate() %>
			<br/>End <%=activities.get(i).getEndDate() %>
			<br/>Location:<%=activities.get(i).getLocationAddress() %>
			<p> <%=activities.get(i).getContent() %> </p>
			
			<input type="button" value="Add" onclick="addActiv('<%=activities.get(i).getUid()%>')"/>
			<div id="cust_activ_<%=activities.get(i).getUid()%>"></div>
		</div> 
		
		<%
	}
%>


<script>
function addActiv(id){

	
	$.ajax({
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
		type: 'POST',
		data: { 
			newCustActivityBean:id,
			
			a:Date.now()
		},
		success: function(result) {
			document.getElementById("cust_activ_"+id).innerHTML='Added';
		}
	});
}
	</script>