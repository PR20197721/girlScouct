<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../include/session.jsp"%>

<%@include file="../admin/toolbar.jsp"%>


<%

java.util.List<Milestone> milestones = user.getYearPlan().getMilestones();

%>

<script>
  $(function() {
    $( "#date1" ).datepicker();
    $( "#date2" ).datepicker();
    $( "#date3" ).datepicker();
  });
  </script>

<h1>Milestones</h1>
<form action="/content/girlscouts-vtk/controllers/vtk.controller.html" method="POST" >
<table>

<tr>
	<th>Num</th> 
	<th>Date</th>
	<th>Name</th>
</tr>

<tr>
	<td>1</td> 
	<td><input type="text" id="date1" name="date1" value="<%=FORMAT_MMddYYYY.format(milestones.get(0).getDate())%>"/></td>
	<td><input type="text" name="blurb1" value="<%=milestones.get(0).getBlurb()%>"/></td>
</tr>

<tr>
	<td>2</td> 
	<td><input type="text" id="date2" name="date2" value="<%=FORMAT_MMddYYYY.format(milestones.get(1).getDate())%>"/></td>
	<td><input type="text" name="blurb2" value="<%=milestones.get(1).getBlurb()%>"/></td>
</tr>


<tr>
	<td>3</td> 
	<td><input type="text" id="date3"  name="date3" value="<%=FORMAT_MMddYYYY.format(milestones.get(2).getDate())%>"/></td>
	<td><input type="text" name="blurb3" value="<%=milestones.get(2).getBlurb()%>"/></td>
</tr>




<tr>
	<td colspan="3" style="text-align:center;"><input type="submit" name="updateCouncilMilestones" value="Edit"/></td>
</tr>

</table>
</form>