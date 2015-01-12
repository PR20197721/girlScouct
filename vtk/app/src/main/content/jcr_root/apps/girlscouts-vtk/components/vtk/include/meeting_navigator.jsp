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
      	<%if(planView.getSearchDate()!=null && planView.getSearchDate().after( new java.util.Date("1/1/1977") )){ %>
        <span className="month">{this.props.meetingModMONTH}</span>
        <span className="day">{this.props.meetingModDAY}</span>
        <span className="hour">{this.props.meetingModHOUR}</span>
      	<%} 
      	if(new Date("07/28/2015 17:00:00")!=null && new Date("07/28/2015 17:00:00").after(planView.getSearchDate())){%>
      		-
      		<%SimpleDateFormat fm= new SimpleDateFormat("yyyyMMdd");
      		if(!fm.format(new Date("07/28/2015 17:00:00")).equals(fm.format(planView.getSearchDate()))){%>
				<span><%= new SimpleDateFormat("MMMM").format(new Date("07/28/2015 17:00:00")) %></span>
        		<span><%= new SimpleDateFormat("dd").format(new Date("07/28/2015 17:00:00"))%></span>      		
        	<%}%>     
        		<span><%= new SimpleDateFormat("hh:mm a").format(new Date("07/28/2015 17:00:00")) %></span>
      	<%}%>
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
