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
	
	for(String ageGroup : ageGroups){
		java.util.List<CouncilRptBean> brownies= councilRpt.getCollection_byAgeGroup( container, ageGroup);
	    Set<String> yearPlanNames = councilRpt.getDistinctPlanNames( brownies );
	    
  %>
    <div class="row">
      <dl class="accordion" data-accordion="">
        <dt data-target="panel1"><h3 class="on"><%=ageGroup %></h3></dt>
        <dd class="accordion-navigation">
          <div class="content active" id="panel1">
            <div class="row">
              <div class="column large-23 large-centered">
                  <div class="row titles">
                    <span class="name column large-9 text-center">Year Plan</span>
                    <span class="name column large-4 text-center"># of Troops Adopted</span>
                    <span class="name column large-5 text-center"># of Plans Customized</span>
                    <span class="name column large-5 text-center end">Plans with Added Activities</span>
                  </div>
                  <% 
                  for(String yearPlanName: yearPlanNames){
                	  java.util.List<CouncilRptBean> yearPlanNameBeans = councilRpt.getCollection_byYearPlanName( brownies, yearPlanName );
                	  int countAltered = councilRpt.countAltered(yearPlanNameBeans);
                	  int countActivity= councilRpt.countActivity(yearPlanNameBeans);
                    %>
                  <div class="row">
                    <dl class="accordion-inner clearfix" data-accordion="">
                      <dt data-target="panel1b" class="clearfix">
                        <span class="name column large-9"><%=yearPlanName %></span>
                        <span class="column large-4 text-center"><%=(yearPlanNameBeans.size()- countAltered) %></span>
                        <span class="column large-4 text-center"><%=countAltered %></span>
                        <span class="column large-4 text-center"><%=countActivity %></span>
                      </dt>
                      <dd class="accordion-navigation">
                        <div id="panel1b" class="content">
                          <div class="clearfix">
                            <span class="column large-4 text-center large-push-9">
                              <a href="#" title="Troop 245" data-reveal-id="modal_report_detail">Troop245</a>
                            </span>
                            <p class="check column large-4 text-center large-push-9"></p>
                            <p class="check column large-4 text-center"></p>

                          </div>
                          <div class="clearfix">
                            <span class="column large-4 text-center large-push-9">
                              <a href="#" title="Troop 245" data-reveal-id="modal_report_detail">Troop245</a>
                            </span>
                            <p class="check column large-4 text-center large-push-9"></p>
                              <!-- <input type="checkbox" name="ch_3" id="ch_3" /><label for="ch_3"></label> -->
                            <p class="check column large-4 text-center"></p>
                              <!-- <input type="checkbox" name="ch_4" id="ch_4" /><label for="ch_4"></label> -->
                          </div>
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
<%@include file="include/modals/modal_report_detail.jsp" %>