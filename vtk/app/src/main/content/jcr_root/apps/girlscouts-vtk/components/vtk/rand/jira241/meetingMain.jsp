<%

  String activeTab = "planView";

    boolean showVtkNav = true;

%>

<%@include file="include/tab_navigation.jsp"%>

<!--page wrapper-->

<div id="panelWrapper" class="row content meeting-detail">

  <div class="hide-for-small hide-for-print crumbs">

    <div class="column large-20 medium-24 large-centered medium-centered">

      <div class="row">

        <div class="columns large-20">

          <ul id="sub-nav" class="inline-list hide-for-print">

            <li><a href="javascript:loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingLibrary.html?mpath='+thisMeetingPath+'&xx=<%=planView.getSearchDate().getTime()%>', false, null, true)" title="replace this meeting">Replace this meeting</a></li>

          </ul>

        </div>

        <div class="columns large-4">

          <ul class="inline-list" id="util-links">

            <li><a class="icon" onclick="self.location = 'c'" title="download plan"><i class="icon-download"></i></a></li>

            <li><a class="icon" onclick="javascript:window.print()" title="print plan"><i class="icon-printer"></i></a></li>

            <li><a class="icon" onclick="javascript:window.print()" title="help"><i class="icon-questions-answers"></i></a></li>

          </ul>

        </div>

      </div>

    </div>

  </div><!--/print, download, breadcrumb links-->

 

  <!--  %@include file="include/meeting_maininfo.jsp"% -->

  <!--%@include file="include/meeting_planning.jsp"% -->

  <!--%@include file="include/meeting_communication.jsp"% -->

  <!--%@include file="include/meeting_aids.jsp"% -->

  <!--%@include file="include/meeting_agenda.jsp"% -->

</div>