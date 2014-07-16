<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>



<%

SearchTag search = meetingDAO.searchA();
java.util.Map<String, String> levels = search.getLevels();
java.util.Map<String, String> categories =search.getCategories();

%>
<form id="schFrm">
<input type="text" id="sch_keyword" value=""/>


<fieldset>

<br/>From Date<input type="text" id="sch_startDate"  value=""/>
<br/>To Date<input type="text" id="sch_endDate"  value=""/>
</fieldset>


<h2>Categories</h2>
<% java.util.Iterator itr= categories.keySet().iterator();

	while( itr.hasNext() ){
		String str=(String) itr.next();
%>
	<%= str %><input type="checkbox" name="sch_cats" value="<%= str %>"/>
<% } %>


<h2>Levels</h2>
<% java.util.Iterator itr1= levels.keySet().iterator();
while( itr1.hasNext() ){
String str=(String) itr1.next();
%>
	<%= str %><input type="checkbox" name="sch_lvl" value="<%= str %>"/>
<% } %>

<input type="button" value="Search" onclick='src11()'/>
</form>


<script>

$('#sch_startDate').datepicker({minDate: 0});
$('#sch_endDate').datepicker({minDate: 0});

function checkAll(x) {
	
		var container ="";
	   var arrMarkMail = document.getElementsByName(x);
	   for (var i = 0; i < arrMarkMail.length; i++) {
	     if(arrMarkMail[i].checked)
	    	 container += arrMarkMail[i].value +"|";
	   }
	   return container;
	   
	}
	
	
	
function src11(){
	
	var  keywrd = document.getElementById("sch_keyword").value;
	var lvl=  checkAll('sch_lvl');
	var cat=  checkAll('sch_cats');
	var startDate = document.getElementById("sch_startDate").value;
	var endDate = document.getElementById("sch_endDate").value;
	
	$.ajax({
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
		type: 'POST',
		data: { 
			srch:true,
			keywrd:keywrd,
			tags:lvl+"|"+ cat,
			startDate:startDate,
			endDate:endDate,
			a:Date.now()
		},
		success: function(result) {
			$("#srch_reslts").load('/content/girlscouts-vtk/controllers/vtk.searchActivity.html');
		}
	});
	
}
</script>



<div style="background-color:yellow" id="srch_reslts"></div>