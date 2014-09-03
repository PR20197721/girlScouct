
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../include/session.jsp"%>

<%@include file="../admin/toolbar.jsp"%>

<script>

function rmUser(){
	$.ajax({
		url: "/content/girlscouts-vtk/controllers/vtk.rmme.html",
		cache: false
	}).done(function( html ) {
		location.reload();
	});
}
</script>


<h1>VTK Admin Tools</h1>
<b>Welcome, admin</b>
<br/><br/>
<div style="background-color:#efefef;">


<div class="grid-system nopadding section" >
<ul class="large-block-grid-2 medium-block-grid-2 small-block-grid-1 ">

<li style="padding:25px;"><div class="text parbase nopadding section">
<table border="0" cellpadding="0" cellspacing="0"
style="border: none;">
<tbody><tr><td>

<a href="/content/girlscouts-vtk/en/vtk.admin.plan.html" >
<input type="submit" value="Build Your Own" />
</a>
</td>
</tr><tr style="background-color: white;border: none;"><td>Go here to build you own action-packed year
 of activities. Share with 
Daisy, Brownie, or Junior troop leaders.
</td></tr>
<tr><td>
<%if(user.getYearPlan()!=null && user.getYearPlan().getMeetingEvents()!=null && user.getYearPlan().getMeetingEvents().size()>0){%>
	Existing Plans:<br/>
	<a href="/content/girlscouts-vtk/en/vtk.admin.plan.html">::<%=user.getYearPlan().getName() %></a> <a href="javascript:void(0)" onclick="rmUser()" style="color:red;">remove</a>
<%}%>
</div>

</td>
</tr></tbody></table>
</div>
</li>


<li style="padding:25px;"><div class="text parbase nopadding section">
<table border="0" cellpadding="0" cellspacing="0"
style="border: none;">
<tbody><tr><td>
<a href="/content/girlscouts-vtk/en/vtk.admin.previewImportMeeting.html">
<input type="submit" value="Year Plan File Preview" />
</a>
</td>
</tr><tr style="background-color: white;border: none;"><td>
Do you want to preview meeting file uploads?
<br/>Upload file and preview.</td>
</tr></tbody></table>
</div>
</li>



<li style="padding:25px;"><div class="text parbase nopadding section">
<table border="0" cellpadding="0" cellspacing="0"
style="border: none;">
<tbody><tr><td>

<a href="/content/girlscouts-vtk/en/vtk.admin.milestones.html" >
<input type="submit" value="Milestones" />
</a>
</td>
</tr><tr style="background-color: white;border: none;"><td>Go here to change milestones
</td></tr>
</tbody></table>
</div>
</li>



<li style="padding:25px;"><div class="text parbase nopadding section">
<table border="0" cellpadding="0" cellspacing="0"
style="border: none;">
<tbody><tr><td>
<a href="/content/girlscouts-vtk/en/vtk.admin.councilRpt.html">
<input type="submit" value="Report" />
</a>
</td>
</tr><tr style="background-color: white;border: none;"><td>
Council Report</td>
</tr></tbody></table>
</div>
</li>


<div style="clear:both"></div>
</ul></div>

<br/><br/><br/><br/>
</div>

<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>