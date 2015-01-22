<section className="column large-20 medium-20 large-centered medium-centered">
  <h6>manage communications</h6>
  <ul className="large-block-grid-2 medium-block-grid-2 small-block-grid-2">
  <% if( (planView.getYearPlanComponent().getType() ==  YearPlanComponentType.ACTIVITY) ){%>
	   <li><a href="#" >Meeting Reminder Email</a>
  <%}else{ %>
   		<li><a <%if(planView.getSearchDate()!=null &&
   planView.getSearchDate().after( new java.util.Date("1/1/1977") )) {%>
   
   		href="#" data-reveal-id="modal_meeting_reminder" 
   		
   		<%} else{%>
   
   		href="javascript:alert('You have not yet scheduled your meeting calendar.\nPlease select a year plan and schedule your meetings by clicking on the MEETING DATES AND LOCATION link.')"
   
   		<%} %> 
   		title="Meeting Reminder Email">Meeting Reminder Email</a></li>
  <% }%> 
   <li>(2 sent - <a href="" title="view" className="view">view</a>)</li>
   <li>        
    <a data-reveal-id="modal_popup" data-reveal-ajax="true" href={"/content/girlscouts-vtk/controllers/vtk.include.modals.modal_attendance.html?mid=<%=planView.getYearPlanComponent().getUid() %>"}>Record Attendance &amp; Achievements</a>
    </li>
   <li>(5 of 6 present, no achievements - <a href="#nogo" title="view" className="view">view</a>)</li>
   <li><a href="" title="Upload Photo">Upload Photo</a></li>
   <li>(none) </li>
  </ul>
</section>
