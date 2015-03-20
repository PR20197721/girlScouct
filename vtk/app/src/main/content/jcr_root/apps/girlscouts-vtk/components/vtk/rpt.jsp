<%@ page
  import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<!-- PAGEID :: ./app/src/main/content/jcr_root/apps/girlscouts-vtk/components/vtk/plan.jsp -->
<cq:defineObjects />
<%@include file="include/session.jsp"%>

<% 
final CouncilRpt councilRpt = sling.getService(CouncilRpt.class);

java.util.List<String> ageGroups = new java.util.ArrayList<String>();
ageGroups.add("brownie");
ageGroups.add("daisy");
ageGroups.add("junior");


java.util.List<CouncilRptBean> container = councilRpt.getRpt( request.getParameter("cid"));

for(String ageGroup : ageGroups){
	
	out.println("<h1>" + ageGroup + "</h1>");
	java.util.List<CouncilRptBean> brownies= councilRpt.getCollection_byAgeGroup( container, ageGroup);
	Set<String> yearPlanNames = councilRpt.getDistinctPlanNames( brownies );
	for(String yearPlanName: yearPlanNames){
		java.util.List<CouncilRptBean> yearPlanNameBeans = councilRpt.getCollection_byYearPlanName( brownies, yearPlanName );
		int countAltered = councilRpt.countAltered(yearPlanNameBeans);
		int countActivity= councilRpt.countActivity(yearPlanNameBeans);
		out.println( "<br/><span style='color:green;'>"+ yearPlanName + "</span> count: "+yearPlanNameBeans.size() +" Adopted: "+ (yearPlanNameBeans.size()- countAltered)+" Cust: "+ countAltered);
		
	}
}

%>


