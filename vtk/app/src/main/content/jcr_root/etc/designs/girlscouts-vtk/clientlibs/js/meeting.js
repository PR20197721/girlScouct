
function addCustAgenda(){
	$(function() {
		$( "#newMeetingAgenda" ).dialog({
			width:920,
			modal:true,
			dialogClass:"modalWrap"
		});
	});
}

function createCustAgendaItem(mPath, time){
	var newCustAgendaName = document.getElementById("newCustAgendaName").value;
	var newCustAgendaDuration= document.getElementById("newCustAgendaDuration").value;
	var urlPath =mPath +"&duration="+newCustAgendaDuration+"&name="+ newCustAgendaName+"&startTime="+time ;

	$.ajax({
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
		type: 'POST',
		data: { 
			newCustAgendaName:mPath,
			duration:newCustAgendaDuration,
			name: newCustAgendaName,
			startTime:time,
			a:Date.now()
		}
	}).done(function( html ) {
		document.location="/content/girlscouts-vtk/en/vtk.planView.html?mid="+ mPath;
	});
}
