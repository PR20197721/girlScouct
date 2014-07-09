<div class="sectionHeader">Manage Communications</div>
<table>
<!-- 
	<tr>
		<td> Edit/Send RequestVolunteers Email</td>
		<td>view</td>
	</tr>
	<tr>
		<td>Edit/Send Invitation with Dues &  Forms</td>
		<td>view</td>
	</tr>
	
	<tr>
		<td>Edit/Send Activity Reminder Email </td>
		<td>View</td>
	</tr>
	-->
	<tr>
		<td><a href="javascript:void(0)" onclick="doPic(0)">Upload Photo</a></td>
		<td></td>
	</tr>
	
	<tr>
		<td><a href="javascript:void(0)" onclick="doPic(1)">Upload File</a></td>
		<td></td>
	</tr>
	<!--  
	<tr>
		<td>Edit/Send Activity Recap Email</td>
		<td>(not sent)</td>
	</tr>
	-->
</table>


<div id="picModal"></div>
<script>

function doPic(isFile){
	link='/content/girlscouts-vtk/controllers/vtk.uploadPhoto.html?isFile='+isFile+'&refId=<%=_comp.getUid()%>&myId=<%=searchDate.getTime()%>';
    $( "#picModal" ).load(link, function( response, status, xhr ) {
        if ( status == "error" ) {
                var msg = "Sorry but there was an error: ";
                $( "#error" ).html( msg + xhr.status + " " + xhr.statusText );
        }else{
	$( "#picModal" ).dialog({
		width:920,
		modal:true,
		dialogClass:"modalWrap"
	});
	$(".ui-dialog-titlebar").hide();
        }
});
}
</script>