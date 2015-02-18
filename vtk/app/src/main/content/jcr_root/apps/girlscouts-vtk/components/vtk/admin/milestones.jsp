<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../include/session.jsp"%>

<script type="text/javascript" src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.datepicker.validation.js"></script>

<p>edit milestones, add dates, create new milestones, and set to show in plans.</p>

<div>
<form action="/content/girlscouts-vtk/controllers/vtk.controller.html" method="POST" style="display:none;" id="MileStoneForm">

<table id="MileStoneTable">
	<th></th>
	<th>Message</th>
	<th>Date</th>
	<th>Show in Plans</th>
<%
String councilId= request.getParameter("cid");
int t=0;
java.util.List<Milestone> milestones = yearPlanUtil.getCouncilMilestones(councilId) ;
for(int i=0;i<milestones.size();i++,t++){
%>
<tr id="entry<%=t %>">
 <td><a onclick="rmvEntry()"><i class="icon-button-circle-cross" style="color: green"></i></a></td>
 <td><input type="text" id="blurb<%=t %>" name="ms_blurb[]<%=t %>" value="<%=milestones.get(i).getBlurb()%>"/></td>
 <td><input type="text" id="date<%=t %>" name="ms_date[]<%=t %>"  placeholder="  /  /    " onchange="doChkSubmitValid()" value="<%=milestones.get(i).getDate()==null?"":FORMAT_MMddYYYY.format(milestones.get(i).getDate())%>" onchange="doChkSubmitValid()"/></td>
 <td><input type="checkbox" id="show<%=t %>" name="ms_show[]<%=t %>" <%=milestones.get(i).getShow()?"checked":"unchecked"%>/></td>
</tr>
<%} %>

<tr id="entry<%=t++ %>">
 <td><a onclick="rmvEntry()"><i class="icon-button-circle-cross" style="color: green"></i></a></td>
 <td><input type="text" id="blurb<%=t %>" name="ms_blurb[]" placeholder="Enter a Milestone"/></td>
 <td><input type="text" id="date<%=t %>" name="ms_date[]" placeholder="  /  /    "/></td>
 <td><input type="checkbox" id="show<%=t %>" name="ms_show[]" unchecked/></td>
</tr>

<tr id="entry<%=t++ %>">

 <td><a onclick="rmvEntry()"><i class="icon-button-circle-cross" style="color: green"></i></a></td>
 <td><input type="text" id="blurb<%=t %>" name="ms_blurb[]" placeholder="Enter a Milestone"/></td>
 <td><input type="text" id="date<%=t %>" name="ms_date[]" placeholder="  /  /    "/>
 <td><input type="checkbox" id="show<%=t %>" name="ms_show[] value="checked"/></td>
</tr>
 

</table>
<div class="right clearfix">
	<input type="submit" name="saveCouncilMilestones" value="Save To Plans" class="button btn"/>
	<!--  <input class="button btn" value="Send email" type="button" onclick="sendMeetingReminderEmail()"/>-->
</div>
</form>
<div id="add_entry">
<a  onclick = "newEntry()"><i>icon-plus</i></a>Add a MileStone
</div>


<script>

/*    $(function() {
	  $("input[name='ms_date[]']").datepicker();  
  });  */
  function rmvEntry(){
	  $(this).next().remove();
  };
  function newEntry(){
	  var n = $('#MileStoneTable tr').length-1;

	  $('#MileStoneTable tr:last').after('<tr id="entry'+n+'"></tr>');

<%-- 	 // $("#MileStoneTable").append('<tr id="entry<%=t%>"><a onclick = "rmvEntry(t)"<i class="icon-button-circle-cross" style="color: green"></i></a><tr>');
 --%> $("#entry"+n).append("<td><a onclick='rmvEntry()'><i class='icon-button-circle-cross' style='color: green'></i></a></td>");
      $("#entry"+n).append("<td><input type='text' id='blurb"+n+"' name='ms_blurb[]' placeholder='Enter a Milestone'/></td>");
	  $("#entry"+n).append("<td><input type='text' id='date"+n+"' name='ms_date[]' placeholder='  /  /    '/></td>");
	  $("#entry"+n).append("<td><input type='checkbox' id='show"+n+"' name='ms_show[]' value='unchecked'/></td></tr>");
	  

  }; 
  
</script>




<%-- 

<%
String councilId= request.getParameter("cid");


%>


<h1>Milestones council <%= councilId %></h1>

<div style="border: 5px solid gray;">
	<form action="/content/girlscouts-vtk/en/vtk.admin.milestones.html" method="get" >
	Change council:
	<input type="text" name="cid" value="<%=councilId%>"/>
	<input type="submit" value="Change council"/>
	</form>
</div>

<form action="/content/girlscouts-vtk/controllers/vtk.controller.html" method="POST" >
<input type="hidden" name="cid" value="<%=councilId%>"/>
<table>

<tr>
	<th>Num</th> 
	<th>Date</th>
	<th>Name</th>
	<th></th>
</tr>

<%

java.util.List<Milestone> milestones = yearPlanUtil.getCouncilMilestones(councilId ) ;
for(int i=0;i<milestones.size();i++){

%>
<tr>
	<td><%= (i+1) %></td> 
	<td><input type="text" id="date<%=i %>" name="date<%=i %>" value="<%=FORMAT_MMddYYYY.format(milestones.get(i).getDate())%>"/></td>
	<td><input type="text" name="blurb<%=i %>" value="<%=milestones.get(i).getBlurb()%>"/></td>
	<td>
	<!-- 
		<a href="/content/girlscouts-vtk/controllers/vtk.controller.html?removeCouncilMilestones=<%=milestones.get(i).getUid() %>" style="color:red;">remove</a></td>
	-->
</tr>
	
	<script>
  $(function() {
    $( "#date<%=i%>" ).datepicker();
  
  });
  </script>
<%} %>






<tr>
	<td colspan="4" style="text-align:center;"><input type="submit" name="updateCouncilMilestones" value="Edit"/></td>
</tr>

</table>
</form>


<script>
  $(function() {
    $( "#date" ).datepicker();
  
  });
  </script>
  
  
<form action="/content/girlscouts-vtk/controllers/vtk.controller.html" method="POST" style="display:none;" >
<input type="hidden" name="cid" value="<%=councilId%>"/>
<a href="javascript:void(0)" onclick="document.getElementById('newMil').style.display='block'">create new </a>
<div style="display:none;" id="newMil">
<table>
<tr>
	<td></td> 
	<td><input type="text" id="date" name="date" value=""/></td>
	<td><input type="text" name="blurb" value=""/></td>
	<td></td>
</tr>
<tr>
	<td colspan="4" style="text-align:center;">
	<input type="submit" name="createCouncilMilestones" value="Create"/>
	<input type="button" value="Close" onclick="document.getElementById('newMil').style.display='none'"/>
	</td>
	
</tr>
</table>
</form>
</div> --%>