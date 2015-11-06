<li  className={ <%if( !VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_YEARPLAN_ID) ){%> true || <%} %> (moment(comment) < moment( new Date()) && (moment(comment).get('year') >2000)) ? 'row meeting ui-state-default ui-state-disabled' : 'row meeting ui-state-default'} key={obj[comment].id} id={obj[comment].id+1}>      
	<div className="column large-20 medium-20 large-centered medium-centered">
		<img className={(moment(comment) < moment( new Date()) && (moment(comment).get('year') >2000)) ? "touchscroll hide" : "touchscroll <%= VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_YEARPLAN_ID) ? "" : " hide" %>"} src="/etc/designs/girlscouts-vtk/clientlibs/css/images/throbber.png"/> 
		<div className="large-3 medium-3 small-4 columns"><DateBox comment= {comment} obj={obj}/></div>
		<div className="large-22 medium-22 small-24 columns">
			<p className="subtitle"><ViewMeeting date={moment(comment).toDate()} name={obj[comment].meetingInfo.name}/></p>
			<p className="category">{obj[comment].meetingInfo.cat}</p>
			<p className="blurb">{obj[comment].meetingInfo.blurb}</p>
		</div>
		<div className="large-2 medium-2 columns hide-for-small">
			<MeetingImg mid={obj[comment].meetingInfo.id}/>
		</div>
	</div>
</li>
