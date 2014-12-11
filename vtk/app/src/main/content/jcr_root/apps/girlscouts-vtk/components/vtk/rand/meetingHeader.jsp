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
    
    
    
    
    
    <div>
    
    
    <section className="column large-20 medium-20 large-centered medium-centered" id="main-info">
  <div class="row">

    <div className="column large-17">

      <p>Would you like an invention that would help tie your shoes faster? Or one to make elevators record your singing while you ride? In this badge, find out how inventors make stuff and become an inventor yourself.</p>

      <section>

        <p>Location:</p>

        <p>Hall Neighborhood House, 334 East Main Street</p>

        </section>

      <section>

        <p>Category:</p>

        <p>Science, technology, engineering and math</p>

      </section>

    </div>

    <div className="column large-7">

      <img src="/jcr_root/etc/designs/girlscouts-vtk/images/badge.png" height="211" width="236" alt="badge" />

    </div>

  </div>

</section>
    </div>  
    
    
 
      <div className="row meetingDetailDescription linkButtonWrapper">
    
   
<section className="column large-20 medium-20 large-centered medium-centered">

  <h6>planning materials</h6>

  <ul>

   <li><a  id="overviewButtonX" href="javascript:loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingMisc.html?mid=<%=mid %>&isOverview=true', true, 'Overview', false, true)" title="Meeting Overview">Meeting Overview</a></li>

   <li><a id="activityPlanButtonX" href="javascript:loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingMisc.html?mid=<%=mid %>&isActivity=true', true, 'Activity', false, true)" title="Activity Plan">Activity Plan</a></li>

   <li><a id="materialsListButton" href="javascript:loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingMisc.html?mid=<%=mid %>&isMaterials=true', true, 'Materials', false, true)" title="Materials List">Materials List</a></li>

  </ul>

</section>
    
     </div>
</div>