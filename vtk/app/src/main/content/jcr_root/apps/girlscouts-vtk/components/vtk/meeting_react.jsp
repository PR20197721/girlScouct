<%@ page
  import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />

<%@include file="include/session.jsp"%>
<%
  String activeTab = "planView";
    boolean showVtkNav = true;
%>
<%@include file="include/tab_navigation.jsp"%>
<!--page wrapper-->
<div id="panelWrapper" class="row content">

    <div class="hide-for-small hide-for-print crumbs">
      <div class="column large-20 medium-24 large-centered medium-centered">
        <div class="row">
          <div class="columns large-19">
            <ul id="vtkSubNav" class="inline-list hide-for-print">
              <li><a href="#" onclick="loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingLibrary.html?mpath=/vtk/603/troops/701G0000000uQzTIAU/yearPlan/meetingEvents/M1415643279874_0.6336507935028649&xx=1418266800000', false, null, true)" title="replace this meeting">Replace this meeting</a></li>
            </ul>
          </div>
          <div class="large-5 columns">
            <ul class="inline-list">
              <li><a class="icon" onclick="self.location = 'c'" title="download plan"><i class="icon-download"></i></a></li>
              <li><a class="icon" onclick="javascript:window.print()" title="print plan"><i class="icon-printer"></i></a></li>
              <li><a class="icon" onclick="javascript:window.print()" title="help"><i class="icon-questions-answers"></i></a></li>
            </ul>
          </div>
        </div>
      </div>
    </div><!--/print, download, breadcrumb links-->
    <div class="column large-20 medium-20 large-centered medium-centered">
      <div class="meeting_navigation row">
        <p class="column">
          <a class="direction prev" href="/content/girlscouts-vtk/en/vtk.planView.html?elem=1418266800000"></a>
        </p>
        <div class="column">
          <h3>Meeting:1 inventory part 1</h3>
          <p class="date">
            <span class="month">October</span>
            <span class="day">10</span>
            <span class="hour">10:00 AM</span>
          </p>
        </div>
        <p class="column">
          <a class="direction next" href="/content/girlscouts-vtk/en/vtk.planView.html?elem=1423083600000"></a>
        </p>
      </div>
    </div>
    <div class="column large-20 medium-20 large-centered medium-centered">
      <div class="row">
        <div class="column large-14">
          <p>Would you like an invention that would help tie your shoes faster? Or one to make elevators record your singing while you ride? In this badge, find out how inventors make stuff and become an inventor yourself.</p>
        </div>
        <div class="column large-6">
          <img src="" alt="badge" />
        </div>
      </div>
    </div>
</div>