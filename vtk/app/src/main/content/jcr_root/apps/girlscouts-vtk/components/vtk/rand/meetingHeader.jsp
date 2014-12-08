<div>
     <div className="meetingHeader">
		<h1>{this.props.meetingTitle}</h1>
		<div className="error">Location:{this.props.location}</div>
		<div>Blurb:{this.props.blurb}</div>
		<div>Meeting# {this.props.meetingId}</div>
		
		<div><a className="mLocked button linkButton" href="javascript:loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingLibrary.html?mpath='+thisMeetingPath+'&xx=<%=planView.getSearchDate().getTime()%>', false, null, true)">replace this meeting</a>
		</div>
			
        {this.props.children}
        
        
     <a href="http://localhost:4503/content/girlscouts-vtk/en/vtk.rand.meeting.html?elem=<%=planView.getPrevDate()%>">NEXT</a>
        || <a href="http://localhost:4503/content/girlscouts-vtk/en/vtk.rand.meeting.html?elem=<%=planView.getNextDate()%>">NEXT</a>
     
      
      <hr/>
        <a href="http://localhost:4503/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=planView.getPrevDate()%>">NEXT</a>
        || <a href="http://localhost:4503/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=planView.getNextDate()%>">NEXT</a>
     
      </div>
      
 
      <div className="row meetingDetailDescription linkButtonWrapper">
        <div className="small-8 columns"><a className="button linkButton tight" id="overviewButtonX" href="javascript:loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingMisc.html?mid=<%=mid %>&isOverview=true', true, 'Overview', false, true)">overview</a></div>
        <div className="small-8 columns"><a className="button linkButton tight" id="activityPlanButtonX" href="javascript:loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingMisc.html?mid=<%=mid %>&isActivity=true', true, 'Activity', false, true)">activity plan</a></div>
        <div className="small-8 columns"><a className="button linkButton tight" id="materialsListButton" href="javascript:loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingMisc.html?mid=<%=mid %>&isMaterials=true', true, 'Materials', false, true)">materials list</a></div>
     </div>
</div>