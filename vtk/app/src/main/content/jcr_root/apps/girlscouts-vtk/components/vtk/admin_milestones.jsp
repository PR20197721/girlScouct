<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<% 
    String activeTab = "admin_milestones";
    java.util.List<Milestone> milestones = yearPlanUtil.getCouncilMilestones("603") ;//TODO MUST ADD COUNSIL ID THIS IS FOR TESTING ONLY
%>
<%@include file="include/tab_navigation.jsp"%>
<div id="panelWrapper" class="row content milestones meeting-detail">
  <%@include file="include/utility_nav.jsp"%>
  <div class="columns small-20 small-centered">
    <p>Edit milestones, add dates, create new milestones, and set to show in plans.</p>
    <div class="row">
     <p class="column large-7 large-push-2">Message</p>
     <p class="column large-4 large-push-3">Date</p>
     <p class="column large-4 large-pull-4">Show in Plans</p>
    </div>
    <form>
    <% for( int i=0; i<milestones.size(); i++ ) { %>
      <section class="row">
        <div class="column large-1">
          <a href="" title="remove"><i class="icon-button-circle-cross"></i></a>
        </div>
        <div class="column large-7 large-push-1">
          <input type="text" placeholder="<%=milestones.get(i).getBlurb()%>" />
        </div>
        <div class="column large-4 large-push-2">
         <input type="text" id="date<%=i %>" class="datepicker" name="date_<%=i %>" value="<%=FORMAT_MMddYYYY.format(milestones.get(i).getDate())%>"/>
        </div>
        <div class="column large-1 large-push-2">
          <label for="date<%=i %>"><a class="icon-calendar"></a></label>
        </div>
        <div class="column large-2 large-pull-5">
          <input type="checkbox" name="ch_<%=i %>" id="ch_<%=i %>" value="" / >
          <label for="ch_<%=i %>"></label>
        </div>
      </section>
    <%}%>
    <
    </form>
  </div>
</div>
<script>
  $(function() {
    $( ".datepicker" ).datepicker();
  });
  </script>