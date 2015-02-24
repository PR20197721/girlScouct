<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<% 
    String activeTab = "reports";
%>
<%@include file="include/tab_navigation.jsp"%>
<div id="panelWrapper" class="row content reports meeting-detail">
  <%@include file="include/utility_nav.jsp"%>


  <div class="column large-23 large-centered">       
  <% 
	final CouncilRpt councilRpt = sling.getService(CouncilRpt.class);
	java.util.List<String> ageGroups = new java.util.ArrayList<String>();
	ageGroups.add("brownie");
	ageGroups.add("daisy");
	ageGroups.add("junior");

	java.util.List<CouncilRptBean> container = councilRpt.getRpt( request.getParameter("cid"));
	int count=0;
	for(String ageGroup : ageGroups){
		java.util.List<CouncilRptBean> brownies= councilRpt.getCollection_byAgeGroup( container, ageGroup);
	    Map<String, String> yearPlanNames = councilRpt.getDistinctPlanNamesPath(brownies);
	    count++;
  %>
    <div class="row">
      <dl class="accordion" data-accordion>
        <dt data-target="panel<%=count%>"><h3 class="on"><%=ageGroup %></h3></dt>
        <dd class="accordion-navigation">
          <div class="content active" id="panel<%=count%>">
            <div class="row">
              <div class="column large-23 large-centered">
                  <div class="row titles">
                    <span class="name column large-9 text-center">Year Plan</span>
                    <span class="name column large-4 text-center"># of Troops Adopted</span>
                    <span class="name column large-5 text-center"># of Plans Customized</span>
                    <span class="name column large-5 text-center end">Plans with Added Activities</span>
                  </div>
                  <% 
                  int y=0;
                  java.util.Iterator itr = yearPlanNames.keySet().iterator();
                  while( itr.hasNext()){
                	  
                	  String yearPlanPath = (String)itr.next();
                	  String yearPlanName= yearPlanNames.get(yearPlanPath);
                	  java.util.List<CouncilRptBean> yearPlanNameBeans = councilRpt.getCollection_byYearPlanName( brownies, yearPlanName );
                	  int countAltered = councilRpt.countAltered(yearPlanNameBeans);
                	  int countActivity= councilRpt.countActivity(yearPlanNameBeans);
                	  y++;
                    %>
                  <div class="row">
                    <dl class="accordion-inner clearfix" data-accordion="">
                      <dt data-target="panel<%=count %>_<%=y %>b" class="clearfix">
                        <span class="name column large-9" onclick="councilRpt('<%=yearPlanPath %>', '<%=request.getParameter("cid")%>')"><%=yearPlanName %></span>
                        <span class="column large-4 text-center"><%=(yearPlanNameBeans.size()- countAltered) %></span>
                        <span class="column large-4 text-center"><%=countAltered %></span>
                        <span class="column large-4 text-center"><%=countActivity %></span>
                      </dt>
                      <dd class="accordion-navigation">
                        <div id="panel<%=count %>_<%=y %>b" class="content">
                        
                        <%for(CouncilRptBean crb : yearPlanNameBeans ) {%>
                          <div class="clearfix">
                            <span class="column large-4 text-center large-push-9">
                              <a title="Troop 245" data-reveal-id="modal_report_detail" data-reveal-ajax="true" href="/content/girlscouts-vtk/controllers/vtk.include.modals.modal_report_detail.html?cid=<%=request.getParameter("cid")%>&tid=<%=crb.getTroopId()%>"><span id="<%=crb.getTroopId()%>"><%=crb.getTroopId() %></span></span></a>
                            </span>
                            <p class="<%=crb.isAltered() ? "check " : "" %> column large-4 text-center large-push-9"></p>
                            <p class="<%=crb.isActivity() ? "check " : "" %> column large-4 text-center"></p>
                          </div>
                         <%} %>
                         
                        </div>
                      </dd>
                    </dl>
                  </div>
                  
                 <%}//edn for %>
              </div>
            </div>
          </div>
        </dd>
      </dl>
    </div><!-- /row -->
    
    <%}//edn for %>
    
   
  </div>
</div>

<div id="modal_report_detail"  class="reveal-modal" data-reveal></div>
