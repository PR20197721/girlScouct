<div className="column large-20 medium-20 large-centered medium-centered small-24">
  <%
    if (pageContext.getAttribute("DETAIL_TYPE") != null && "activity".equals((String) pageContext.getAttribute("DETAIL_TYPE"))) {
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
      	<% if(planView.getSearchDate()!=null && planView.getSearchDate().after( new java.util.Date("1/1/1977") )) { %>
        <span className="month">{this.props.meetingModMONTH}</span>
        <span className="day">{this.props.meetingModDAY}</span>
        <span className="hour">{this.props.meetingModHOUR}</span>
      	<% } %>
      	<!-- need end date -->
      	<% Date endDate;
      	 if(endDate!=null && endDate.after(startDate)) { %>
      		-
      		<%SimpleDateFormat fm= new SimpleDateFormat("yyyyMMdd");
      		if(!fm.format(endDate).equals(fm.format(startDate))) { %>
				    <span><%= endDateMonth %></span>
        		<span><%= endDateDay%></span>      		
        	<% } %>     
        		<span><%= endDateTime %></span>
      	<% } %>
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
