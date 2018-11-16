<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<!--  %@include file="include/session.jsp"% -->
<%
    String activeTab = "reports";
%>
<%
  String sectionClassDefinition = "reports";
%>
<%@include file="include/bodyTop.jsp" %>
  <div class="column medium-23 medium-centered">
  <%
  HttpSession session = request.getSession();
  User user = ((org.girlscouts.vtk.models.User) session
          .getAttribute(org.girlscouts.vtk.models.User.class
                  .getName()));

  //security concern.
  String cid = user.getApiConfig().getUser().getAdminCouncilId() +"";

  if( !(user.getApiConfig().getUser().isAdmin() && user.getApiConfig().getUser().getAdminCouncilId()>0)){

		    %>  <div class="columns medium-20 medium-centered">
		                <p>
		                Sorry! You currently don't have permission to view this tab. For questions, click Contact Us at the top of the page.
		                </p>
		      </div>
		      </div>
		     </div> <!-- end panelWrapper -->
		     <script>loadNav('reports')</script>
		    <%
		    return;

  }else{
	final CouncilRpt councilRpt = sling.getService(CouncilRpt.class);
	java.util.List<String> ageGroups = new java.util.ArrayList<String>();
	ageGroups.add("brownie");
	ageGroups.add("daisy");
	ageGroups.add("junior");
	ageGroups.add("multi-level");
	ageGroups.add("cadette");
	ageGroups.add("senior");
	ageGroups.add("ambassador");

	if ( request.getParameter("cid") != null) {
		cid =  (String)request.getParameter("cid");
	}
	java.util.List<CouncilRptBean> container = councilRpt.getRpt(cid);
	int count=0;
	for(String ageGroup : ageGroups){
		java.util.List<CouncilRptBean> brownies= councilRpt.getCollection_byAgeGroup( container, ageGroup);
	    Map<String, String> yearPlanNames = councilRpt.getDistinctPlanByName(brownies);

	    count++;
  %>
    <div class="row">
      <dl class="accordion" data-accordion>
        <dt data-target="panel<%=count%>"><h3 class="on"><%=ageGroup %></h3></dt>
        <dd class="accordion-navigation">
          <div class="content active" id="panel<%=count%>">
            <div class="row">
              <div class="column medium-24">
                  <div class="row titles">
                    <span class="name column medium-8 medium-text-center">Year Plan</span>
                    <span class="name column medium-5 medium-text-center"># of Troops Adopted</span>
                    <span class="name column medium-5 medium-text-center"># of Plans Customized</span>
                    <span class="name column medium-6 medium-text-center">Plans with Added Activities</span>
                  </div>
                  <%
                  int y=0;
                  java.util.Iterator itr = yearPlanNames.keySet().iterator();
                  while( itr.hasNext()){

                	  String yearPlanName = (String)itr.next();

                	  String yearPlanPath= yearPlanNames.get(yearPlanName); //yearPlanNames.get(yearPlanPath);
 //out.println(yearPlanPath +" : "+ yearPlanName);
                	  java.util.List<CouncilRptBean> yearPlanNameBeans = councilRpt.getCollection_byYearPlanName( brownies, yearPlanName );
                	// java.util.List<CouncilRptBean> yearPlanNameBeans = councilRpt.getCollection_byYearPlanPath( brownies, yearPlanPath );

                	 int countAltered = councilRpt.countAltered(yearPlanNameBeans);
                	  int countActivity= councilRpt.countActivity(yearPlanNameBeans);

                	  y++;
                    %>
                  <div class="row">
                    <dl class="accordion-inner clearfix" data-accordion="">
                      <dt data-target="panel<%=count %>_<%=y %>b" class="clearfix">
                        <span class="name column medium-8" onclick="councilRpt('<%=yearPlanPath %>', '<%=cid%>')"><%=yearPlanName %></span>
                        <span class="column medium-5 medium-text-center"><%=yearPlanNameBeans.size() %></span>
                        <span class="column medium-5 medium-text-center"><%=countAltered %></span>
                        <span class="column medium-6 medium-text-center"><%=countActivity %></span>
                      </dt>
                      <dd class="accordion-navigation">
                        <div id="panel<%=count %>_<%=y %>b" class="content">

                        <%for(CouncilRptBean crb : yearPlanNameBeans ) {%>
                          <div class="clearfix">
                            <span class="column medium-5 medium-text-center medium-push-8">
                              <a title="<%=crb.getTroopId() %>" data-reveal-id="modal_report_detail" data-reveal-ajax="true" href="/content/girlscouts-vtk/controllers/vtk.include.modals.modal_report_detail.html?cid=<%=cid%>&tid=<%=crb.getTroopId()%>"><span id="<%=crb.getTroopId()%>"><%=(crb.getTroopName()!=null && !crb.getTroopName().equals("")) ? crb.getTroopName() : crb.getTroopId() %></span></span></a>
                            </span>
                            <p class="<%=crb.isAltered() ? "check " : "" %> column medium-5 medium-text-center medium-push-8"></p>
                            <p class="<%=crb.isActivity() ? "check " : "" %> column medium-6 medium-text-center"></p>
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

    <%}}%>
  </div>
<%@include file="include/bodyBottom.jsp" %>
<script>loadNav('reports')</script>
<div id="modal_report_detail"  class="reveal-modal" data-reveal></div>
