<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@ page import="com.day.cq.wcm.foundation.Search,
org.girlscouts.web.search.DocHit,java.io.*,
com.day.cq.search.eval.JcrPropertyPredicateEvaluator,com.day.cq.search.eval.FulltextPredicateEvaluator,
com.day.cq.tagging.TagManager,
java.util.Locale,com.day.cq.search.QueryBuilder,javax.jcr.Node,
java.util.ResourceBundle,com.day.cq.search.PredicateGroup,
com.day.cq.search.Predicate,com.day.cq.search.result.Hit,
com.day.cq.i18n.I18n,com.day.cq.search.Query,com.day.cq.search.result.SearchResult,org.apache.commons.beanutils.*,
java.util.Map,java.util.HashMap,java.util.List" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<%
        boolean isHtml= true;
		if(request.getParameter("download")!=null){
		    response.setContentType("application/csv");
		    isHtml = false;
		}else{
			%>
	        <a href="?download=true">download report</a>
	        || <a href="/content/girlscouts-vtk/controllers/vtk.ReportDetails.html">detailed report</a>
	        <br/><br/>
	        <% 
        }
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat format1 = new SimpleDateFormat("MM-dd-yyyy");
		StringBuffer buffer = new StringBuffer("Council Report generated on " + format1.format(new java.util.Date())+ " \nCouncil, Troop, Junior, Brownie, Daisy, Total ");
		java.util.Map<String, String> cTrans = new java.util.TreeMap();		
		cTrans.put("597", "Girl Scouts of Northeast Texas"); 
		cTrans.put("477", "Girl Scouts of Minnesota and Wisconsin River Valleys, Inc.");
		cTrans.put("465", "Girl Scouts of Southeastern Michigan"); 
		cTrans.put("367", "Girl Scouts - North Carolina Coastal Pines, Inc.");
		cTrans.put("320", "Girl Scouts of West Central Florida, Inc.");
		cTrans.put("388", "Girl Scout Council of the Southern Appalachians, Inc.");
		cTrans.put("313", "Girl Scouts of Gateway Council, Inc.");
		cTrans.put("664", "Oregon and SW Washington");
		cTrans.put("234", "North East Ohio");
		cTrans.put("661", "Sierra Nevada");
		cTrans.put("664", "Oregon & SW Wash");
		cTrans.put("240", "Western Ohio");
		cTrans.put("607", "Arizona Cactus Pine");
		cTrans.put("536", "Kansas Heartland");
		cTrans.put("563", "Western Oklahoma");
		cTrans.put("564", "Eastern Oklahoma");
		cTrans.put("591", "San Jacinto");
		cTrans.put("636", "Northern CA");
		cTrans.put("512", "Colorado");
		cTrans.put("313", "Gateway");
		cTrans.put("212", "Kentucky Wilderness Road");
		cTrans.put("623", "San Diego");
		cTrans.put("131", "Central & Southern NJ");
		cTrans.put("263", "Western PA");
		cTrans.put("467", "Wisconsin Badgerland");
		cTrans.put("116", "Central & Western Mass");
		cTrans.put("622", "Orange County");
		cTrans.put("660", "Southern Nevada");
		cTrans.put("514", "Eastern IA & Western IL");
		cTrans.put("524", "Greater Iowa");
		cTrans.put("430", "Greater Chicago and NW  Indiana");
		
	    java.util.HashSet<String> ageGroups = new java.util.HashSet<String>();
		javax.jcr.Session s= (slingRequest.getResourceResolver().adaptTo(Session.class));
		//String sql="select  sfTroopName,sfTroopAge,jcr:path, sfTroopId,sfCouncil,excerpt(.) from nt:base where jcr:path like '"+VtkUtil.getYearPlanBase(user, troop)+"%' and contains(*, 'org.girlscouts.vtk.models.Troop ') ";
		String sql="select  sfTroopName,sfTroopAge,jcr:path, sfTroopId,sfCouncil,excerpt(.) from nt:base where jcr:path like '"+VtkUtil.getYearPlanBase(user, troop)+"%' and ocm_classname= 'org.girlscouts.vtk.models.Troop'";
        
		javax.jcr.query.QueryManager qm = s.getWorkspace().getQueryManager();
		javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL); 
		int count=0 ;
		
		java.util.HashSet councilIds = new java.util.HashSet<String>();
		java.util.List <org.girlscouts.vtk.models.YearPlanRpt> yprs = new java.util.ArrayList<org.girlscouts.vtk.models.YearPlanRpt>();
		javax.jcr.query.QueryResult result = q.execute();
		for (javax.jcr.query.RowIterator it = result.getRows(); it.hasNext(); ) {
			javax.jcr.query.Row r = it.nextRow();
			String path = r.getValue("jcr:path").getString() ;
			String sfCouncil = null, sfTroopAge=null;
			try{ sfCouncil =r.getValue("sfCouncil").getString() ;}catch(Exception e){}			
		    try{sfTroopAge= r.getValue("sfTroopAge").getString();}catch(Exception e){}

		    org.girlscouts.vtk.models.YearPlanRpt ypr = new org.girlscouts.vtk.models.YearPlanRpt();
		    ypr.setCouncil(sfCouncil);
		    ypr.setTroop( r.getValue("sfTroopId").getString() );
		    ypr.setTroopName( r.getValue("sfTroopName").getString() );
		    ypr.setTroopAge(sfTroopAge);
		    yprs.add(ypr);

		    councilIds.add(sfCouncil);
		    ageGroups.add(ypr.getTroopAge());
		    count++;
		}
		out.println("Report Generated on "+ format1.format( new java.util.Date() ) +" ,total results found: "+count +" ,Total council(s): "+ councilIds.size());
	    java.util.Iterator itr= councilIds.iterator();
	    while( itr.hasNext() ){
		   final String councilId= (String) itr.next();
		   java.util.Iterator ageGroupIter = ageGroups.iterator();
		   while(ageGroupIter.hasNext()){
			  final String ageGroup = (String)ageGroupIter.next();
			  
		      java.util.List<org.girlscouts.vtk.models.YearPlanRpt> container = (java.util.List<org.girlscouts.vtk.models.YearPlanRpt>) org.apache.commons.collections4.CollectionUtils
		                   .select(yprs, new  org.apache.commons.collections4.Predicate<org.girlscouts.vtk.models.YearPlanRpt>() {
		                         public boolean evaluate(org.girlscouts.vtk.models.YearPlanRpt o) {
		                             return 
		                            		 o.getTroopAge().equals(ageGroup) && 
		                            		 o.getCouncil().equals( councilId);
		                         }
		         });
			  
		    out.println( (isHtml ? "<br/>" : "\n") + councilId +"," + ageGroup+ "," + container.size() );
		   }
	   }	        
	%>