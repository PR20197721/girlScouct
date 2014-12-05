<li data-id={i} key={i}  
			draggable="true"
            onDragEnd={this.dragEnd}
            onDragStart={this.dragStart} >

	
<img className="touchscroll" src="/etc/designs/girlscouts-vtk/clientlibs/css/images/touchscroll-small.png" width="21" height="34"/>
	
		
			<a href="javascript:void(0)"  className="mLocked" onclick="loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingMisc.html?mid='+{comment.uid}+'&isAgenda={comment.activityNumber}', true, 'Agenda')">{comment.name}</a>
		
{comment.duration}
	</li>