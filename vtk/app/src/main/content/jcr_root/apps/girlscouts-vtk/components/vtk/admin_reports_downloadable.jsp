<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
"AGE GROUP", "Year Plan", "# of Troops Adopted", "# of Plans Customized", "Plans with Added Activities"
<% 
  HttpSession session = request.getSession();
  User user = ((org.girlscouts.vtk.models.User) session
          .getAttribute(org.girlscouts.vtk.models.User.class
                  .getName()));
  String cid = user.getApiConfig().getUser().getAdminCouncilId() +"";//"603";//troop.getSfCouncil();
 
  if(user.getApiConfig().getUser().getAdminCouncilId()>0){//hasPermission(troop, Permission.PERMISSION_VIEW_REPORT_ID) ){ 
    final CouncilRpt councilRpt = sling.getService(CouncilRpt.class);
    java.util.List<String> ageGroups = new java.util.ArrayList<String>();
    ageGroups.add("brownie");
    ageGroups.add("daisy");
    ageGroups.add("junior");
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
			        Map<String, String> yearPlanNames = councilRpt.getDistinctPlanNamesPath(brownies);		        
			        count++;
                  int y=0;
                  java.util.Iterator itr = yearPlanNames.keySet().iterator();
                  while( itr.hasNext()){
                      String yearPlanPath = (String)itr.next();
                      String yearPlanName= yearPlanNames.get(yearPlanPath);
                      java.util.List<CouncilRptBean> yearPlanNameBeans = councilRpt.getCollection_byYearPlanPath( brownies, yearPlanPath );
                      int countAltered = councilRpt.countAltered(yearPlanNameBeans);
                      int countActivity= councilRpt.countActivity(yearPlanNameBeans);
                      y++;
 %>"<%=ageGroup %>", "XXX<%=yearPlanName.replaceAll(",","") %>","<%=yearPlanNameBeans.size() %>","<%=countAltered %>","<%=countActivity %>",<%         
                 }//edn for
    }
    }
    %>