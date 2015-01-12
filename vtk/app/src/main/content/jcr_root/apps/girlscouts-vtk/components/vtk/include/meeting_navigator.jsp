<div className="column large-20 medium-20 large-centered medium-centered small-24">

<%
//if (pageContext.getAttribute("DETAIL_TYPE") != null && "activity".equals((String) pageContext.getAttribute("DETAIL_TYPE"))) {
if( (planView.getYearPlanComponent().getType() ==  YearPlanComponentType.ACTIVITY) ){
%>
  <div className="meeting-navigation activity-navigation row collapse">
<%
} else {
%>
  <div className="meeting-navigation row collapse">
<%
}
%>
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
      	<%if(planView.getSearchDate()!=null && planView.getSearchDate().after( new java.util.Date("1/1/1977") )){ %>
        <span className="month">{this.props.meetingModMONTH}</span>
        <span className="day">{this.props.meetingModDAY}</span>
        <span className="hour">{this.props.meetingModHOUR}</span>
      	<%} %>
      	
      	
      	
      	<% switch( planView.getYearPlanComponent().getType() ) {
	
	  		case ACTIVITY:
				
					java.util.Date endDate = ( (Activity) planView.getYearPlanComponent() ).getEndDate();
					
						out.println("-");
						if(planView.getSearchDate().getMonth() !=  endDate.getMonth() ){%>FORMAT_MONTH.format(endDate)<% }
						if(planView.getSearchDate().getDay() !=  endDate.getDay() ){%>FORMAT_DAY_OF_MONTH.format(endDate)<% }
						
						%><%=FORMAT_hhmm_AMPM.format(endDate) %><% 
					
				
				break;
		}
      	%>
      	
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
