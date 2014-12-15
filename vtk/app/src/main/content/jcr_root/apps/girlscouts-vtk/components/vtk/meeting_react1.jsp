<%@ page
  import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />

<%@include file="include/session.jsp"%>
<%
  String activeTab = "planView";
    boolean showVtkNav = true;
    
	org.girlscouts.vtk.models.PlanView planView = meetingUtil.planView1(user, troop, request);
	String mid = planView.getYearPlanComponent().getUid();
	java.util.Date searchDate = new java.util.Date(planView.getSearchDate().getTime());


%>


<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
<script src="http://fb.me/react-0.12.1.js"></script>
<script src="http://fb.me/JSXTransformer-0.12.1.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script>


<%@include file="include/tab_navigation.jsp"%>
<!--page wrapper-->
<div id="panelWrapper" class="row content meeting-detail">
  <!--/utility links and download, print, help links-->
  <%@include file="include/utility_nav.jsp"%>
  <!--/meeting title and navigation-->
  <%@include file="include/meeting_navigator.jsp"%>

  <%@include file="include/meeting_maininfo.jsp"%>
  <%@include file="include/meeting_planning.jsp"%>
  <%@include file="include/meeting_communication.jsp"%>
  <%@include file="include/meeting_aids.jsp"%>
  <%@include file="include/meeting_agenda.jsp"%>
  <!--/TODO this is for text only-->
  <%@include file="include/modal_1.jsp"%>

</div>

