<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>

<%

SearchTag search = yearPlanUtil.searchA(""+troop.getTroop().getCouncilCode());
java.util.Map<String, String> levels = search.getLevels();
java.util.Map<String, String> categories =search.getCategories();
java.util.Map<String, String> region =search.getRegion();

%>
<form id="schFrm">
<input type="text" id="sch_keyword" value=""/>


<fieldset>
<select id="sch_region">
<option value="">Select Region</option>
<% java.util.Iterator itr2= region.keySet().iterator();

	while( itr2.hasNext() ){
		String str=(String) itr2.next();
%>
	<option value="<%= str %>"><%= str %></option>
<% } %>

	
</select>


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
	
	var  keywrd = $.trim(document.getElementById("sch_keyword").value);
	if( keywrd.length>0 && keywrd.length<3  ){alert("Min 3 character search for keyword: "+ keywrd);return false;}
	
	var lvl=  $.trim(checkAll('sch_lvl'));
	var cat=  $.trim(checkAll('sch_cats'));
	var startDate = $.trim(document.getElementById("sch_startDate").value);
	var endDate = $.trim(document.getElementById("sch_endDate").value);
	var region = $.trim(document.getElementById("sch_region").value);
	
	if( startDate != '' && endDate=='' )
		{alert('Missing end date');return false; }
	if( startDate =='' && endDate!='' )
		{alert('Missing start date');return false;}
	
	if( keywrd=='' && lvl=='' && cat =='' && startDate=='' && endDate=='' && region=='' ){
		alert("Please select search criteria.");
		return false;
	}
	
	$.ajax({
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
		type: 'POST',
		data: { 
			act:'Search',
			srch:true,
			keywrd:keywrd,
			lvl:lvl,
			cat:cat,
			startDate:startDate,
			endDate:endDate,
			region:region,
			a:Date.now()
		},
		success: function(result) {
			$("#srch_reslts").load('/content/girlscouts-vtk/controllers/vtk.searchActivity.html');
		}
	});
	
}
</script>

<div id="srch_reslts"></div>
