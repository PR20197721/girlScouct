<div class="sectionHeader">Manage Communications</div>
<table>
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
	<tr>
		<td><a href="javascript:void(0)" onclick="doPic()">Upload Photo</a></td>
		<td>(none)</td>
	</tr>
	<tr>
		<td>Edit/Send Activity Recap Email</td>
		<td>(not sent)</td>
	</tr>
</table>


<div id="picModal"></div>
<script>

function doPic(){
	link='/content/girlscouts-vtk/controllers/vtk.uploadPhoto.html?refId=<%=_comp.getUid()%>';
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