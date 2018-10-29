<script type="text/javascript" src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery-te-1.4.0.min.js"></script>
<script src="//code.jquery.com/ui/1.11.2/jquery-ui.js"></script>

<!-- apps/girlscouts-vtk/components/vtk/include/modals/modal_meeting_reminder.jsp -->
<div id="modal-meeting-reminder" class="reveal-modal" data-reveal>
  <div class="header clearfix">
    <h3 class="columns large-22">Reminder Email</h3>
    <a class="close-reveal-modal columns large-2" href="#"><i class="icon-button-circle-cross"></i></a>
  </div>
  <div class="scroll">
  <% if( (planView.getYearPlanComponent().getType() ==  YearPlanComponentType.ACTIVITY) ){%>
  <%@include file="/apps/girlscouts-vtk/components/vtk/include/email/activityReminder.jsp" %>  
  <% }else {%>
  <%@include file="/apps/girlscouts-vtk/components/vtk/include/email/meetingReminder.jsp" %>  
  <%} %>
  </div>
  <!-- /scroll -->
</div>




