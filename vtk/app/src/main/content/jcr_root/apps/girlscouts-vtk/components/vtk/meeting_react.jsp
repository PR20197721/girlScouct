<%@ page
  import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<!-- <cq:defineObjects /> -->

<!--%@include file="include/session.jsp"%-->
 
 <%
  //String activeTab = "planView";
   // boolean showVtkNav = true;
%>
<!--%@include file="include/tab_navigation.jsp"%-->
 <!--page wrapper-->
 <div id="panelWrapper" class="row content meeting-detail">
  <!--/utility links and download, print, help links-->
<!-- %@include file="include/utility_nav.jsp"%-->
 <!--/meeting title and navigation-->
  <!-- %@include file="include/meeting_navigator.jsp"%-->

<!-- %@include file="include/meeting_maininfo.jsp"%-->
  <!-- %@include file="include/meeting_planning.jsp"%-->
  <!-- %@include file="include/meeting_communication.jsp"%-->
  <!-- %@include file="include/meeting_aids.jsp"%-->
  <!-- %@include file="include/meeting_agenda.jsp"%-->
  <!--/TODO this is for text only-->
  <a data-reveal-id="modal_meeting">Replace this meeting</a>
  <%@include file="include/modals/modal_meeting_location.jsp"%>

  <a data-reveal-id="modal_agenda"><i className="icon-button-circle-plus"></i> Add Agenda Item</a>
  <%@include file="include/modals/modal_agenda.jsp"%>
  
</div>
