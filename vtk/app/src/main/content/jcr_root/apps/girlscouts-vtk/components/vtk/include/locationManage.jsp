  
  <%@ page import="org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
  
   <%@include file="/include/headerDev.jsp" %>
<script>

	function rmLocation(locationName){
		
		 $("#locMsg1").load("/VTK/include/controller.jsp?rmLocation="+locationName);
		 
	}
	
	function applyLocToAllMeetings(locationPath){
		
		 $("#locMsg1").load("/VTK/include/controller.jsp?setLocationToAllMeetings="+locationPath);
		 
	}
	
	
	function updLocations(locationPath,idName){
		console.log( idName )
		   var av =  document.getElementsByName(idName);
		
		
		var addon="|";
		for (e=0;e<av.length;e++) {
			  if (av[e].checked==true) {
			   addon+=av[e].value+"|";
			   }
			  }
		
		//$("#locMsg1").load("/VTK/include/controller.jsp?chnLocation="+addon+"&newLocPath="+locationPath);
		 
		
		 var x =$.ajax({ // ajax call starts
	          url: "/VTK/include/controller.jsp?chnLocation="+addon+"&newLocPath="+locationPath, // JQuery loads serverside.php
	          data: '', // Send value of the clicked button
	          dataType: 'html', // Choosing a JSON datatype
	          success: function (data) { 
	              
	              console.log("OK");
	             
	          },
	   			error: function (data) { 
	   				console.log("BAD");
	   				//052114reloadMeeting();
	   			}
	      });
	}
</script>

<div id="locMsg1"></div>
<div>


<%
User user= (User)session.getValue("VTK_user");
java.util.List <Location> locations = user.getYearPlan().getLocations();
if( locations==null ){ out.println("No locations");return;}
System.out.println("Location size: "+ (locations==null) );
for(int i=0;i<locations.size();i++){

	Location location = locations.get(i);
	
	%>
	
		<div style="border:4px solid yellow;">
			Name: <%=location.getName() %>
			<br/>Address: <%=location.getAddress() %>
			<br/>City: <%=location.getCity() %>
			<br/>State: <%=location.getState() %>
			<br/>Zip: <%=location.getZip() %>
			
			<a href="javascript:void(0)" onclick="rmLocation('<%=location.getName()%>'); location.reload();">remove</a> ||
			<a href="javascript:void(0)" onclick="applyLocToAllMeetings('<%=location.getPath()%>')">Apply to All meetings</a>
		
		
			<div style="padding:30px; background-color:gray;">
				
				<% 
				java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(user.getYearPlan());
				java.util.Iterator itr=  sched.keySet().iterator();
				while( itr.hasNext()){
					java.util.Date date = (java.util.Date) itr.next();
					%>
						<li><input type="checkbox" name="<%=location.getName() %>" value="<%=date%>"/><%=fmtDate.format(date) %></li>
					<% 
					
				}
				%>
				
				<input type="button" value="assign locations" onclick="updLocations('<%=location.getPath()%>', '<%=location.getName()%>')"/>
			</div>
		</div>
	
	<% 

}
%>

</div>



<%!
java.text.SimpleDateFormat fmtDate= new java.text.SimpleDateFormat("MM/dd/yyyy");
%>