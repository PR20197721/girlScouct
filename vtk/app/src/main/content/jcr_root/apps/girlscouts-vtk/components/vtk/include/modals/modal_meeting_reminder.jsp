<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<cq:defineObjects/>
<script type="text/javascript" src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery-te-1.4.0.min.js"></script>
<%@include file="../session.jsp"%>

<!-- apps/girlscouts-vtk/components/vtk/include/modals/modal_meeting_reminder.jsp -->

<!--  
<div id="modal_meeting_reminder" class="reveal-modal" data-reveal>
-->
  <div class="header clearfix">
    <h3 class="columns large-22">Reminder Email</h3>
    <a class="close-reveal-modal columns large-2" href="#"><i class="icon-button-circle-cross"></i></a>
  </div>
  <div class="scroll">
   <%@include file="../email/meetingReminder.jsp" %> 
  </div><!--/scroll-->
<!--  
</div> 
-->
