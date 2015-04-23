<div className="column large-20 medium-20 large-centered medium-centered small-24">
  <div className="meeting-navigation<%= (planView.getYearPlanComponent().getType() ==  YearPlanComponentType.ACTIVITY) ? " activity-navigation " : "" %> row collapse">
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
 	
      	<% switch( planView.getYearPlanComponent().getType() ) {
    	  		case ACTIVITY:
  					java.util.Date endDate = ( (Activity) planView.getYearPlanComponent() ).getEndDate();
					out.println("-");
					if(planView.getSearchDate().getMonth() !=  endDate.getMonth() ){%><%= VtkUtil.formatDate(VtkUtil.FORMAT_MMMM_dd_hhmm_AMPM, endDate)%><% }
					else if(planView.getSearchDate().getDay() !=  endDate.getDay() ){%><%=VtkUtil.formatDate(VtkUtil.FORMAT_MMMM_dd_hhmm_AMPM, endDate)%><% }				
					else{%><%= VtkUtil.formatDate(VtkUtil.FORMAT_hhmm_AMP, endDate) %> <% }
					break;
             	case MEETING:
	            	Calendar meetingDate = null;
	        	    meetingDate=Calendar.getInstance();
	        		meetingDate.setTime(planView.getSearchDate());
	        		meetingDate.add(Calendar.MINUTE, planView.getMeetingLength());
	        		//Date meetingEnd = null;
	        		Date meetingEnd = meetingDate.getTime();%>
					-<span><%=VtkUtil.formatDate(VtkUtil.FORMAT_hhmm_AMPM, meetingEnd)%></span><% 
            		break; 
          }

       } %> 
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
