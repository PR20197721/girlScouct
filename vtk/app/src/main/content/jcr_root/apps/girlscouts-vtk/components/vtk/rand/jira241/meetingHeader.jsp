

<div>

<a className="mLocked button linkButton" href="javascript:loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingLibrary.html?mpath='+thisMeetingPath+'&xx=<%=planView.getSearchDate().getTime()%>', false, null, true)">replace this meeting</a>
 <div class="column large-20 medium-20 large-centered medium-centered">

    <div class="meeting-navigation row">

      <p class="column">

        <a class="direction prev" href="/content/girlscouts-vtk/en/vtk.rand.<%=myProjLoc %>.meeting.html?elem=<%=planView.getPrevDate()%>"></a>

      </p>

      <div class="column">

        <h3>Meeting {this.props.id} : {this.props.meetingTitle}</h3>

        <p class="date">

          <span class="month">{this.props.meetingModMONTH}</span>

          <span class="day">{this.props.meetingModDAY}</span>

          <span class="hour">{this.props.meetingModHOUR}</span>

        </p>

      </div>

      <p class="column">

        <a class="direction next" href="/content/girlscouts-vtk/en/vtk.<%=myProjLoc %>.html?elem=1423083600000"></a>

      </p>

    </div>

  </div>
  
  


   
    
    
    
    
    
    <div>
    
    
    <section className="column large-20 medium-20 large-centered medium-centered" id="main-info">
  <div class="row">

    <div className="column large-17">

      <p>{this.props.blurb}</p>

      <section>

        <p>Location:</p>

        <p>{this.props.location}</p>

        </section>

      <section>

        <p>Category:</p>

        <p>XXX</p>

      </section>

    </div>

    <div className="column large-7">

		
      <img src={this.props.meetingGlobalId} height="211" width="236" alt="badge" />
	
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