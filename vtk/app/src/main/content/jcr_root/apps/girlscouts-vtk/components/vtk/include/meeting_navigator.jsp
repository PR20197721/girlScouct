<div className="column large-20 medium-20 large-centered medium-centered small-24">
  <div className="meeting-navigation row collapse">
    <p className="column">
    	<span>
      { '<%=planView.getPrevDate()%>' !=0 ? 
        <a className="direction prev" href="/content/girlscouts-vtk/en/vtk.details.html?elem=<%=planView.getPrevDate()%>"></a> : ""
      }
      </span>
    </p>
    <div className="column">
      <h3><%=planView.getYearPlanComponent().getType()%> {this.props.id} : {this.props.meetingTitle}</h3>
      <p className="date">
        <span className="month">{this.props.meetingModMONTH}</span>
        <span className="day">{this.props.meetingModDAY}</span>
        <span className="hour">{this.props.meetingModHOUR}</span>
      </p>
    </div>
    <p className="column">
      <span>
      { '<%=planView.getNextDate()%>' !=0 ? 
     	  <a className="direction next" href="/content/girlscouts-vtk/en/vtk.details.html?elem=<%=planView.getNextDate()%>"></a>
        : ""
    	}
      </span>
    </p>
  </div>
</div>