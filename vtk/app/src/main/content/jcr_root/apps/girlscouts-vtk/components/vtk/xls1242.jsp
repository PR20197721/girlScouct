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
    	response.setContentType("application/csv");
    	response.setHeader("Content-Disposition","attachment; filename=MeetingRpt.csv");

        Set<String> allowedReportUsers = new HashSet<String>();
        allowedReportUsers.add("005g0000002apMT");
        allowedReportUsers.add("005G0000006oEkZ");
        allowedReportUsers.add("005G0000006oBVG");
        allowedReportUsers.add("005g0000002G004");
        allowedReportUsers.add("005G0000006oEjsIAE");

        String year =request.getParameter("year");
        if( year==null || "".equals(year))
        	year= VtkUtil.getYearPlanBase(null, null);
        else
        	year = "/vtk"+ year+ "/";
        	
        
        if( !allowedReportUsers.contains(user.getApiConfig().getUserId()) ){
            out.println("You do not have access to this page [" + user.getApiConfig().getUserId() + "].");
            return;
        } 

        out.println("Meeting Report generated on " + new java.util.Date()+ " \nTroop,Meeting, Achievement record for the Troop & Meeting pair  ");

        javax.jcr.Session s= (slingRequest.getResourceResolver().adaptTo(Session.class));
		java.util.Map <String, String> attendances = new java.util.TreeMap();
		List<String> councils= VtkUtil.getCouncils();
		
        String sql ="select users from nt:unstructured where isdescendantnode('"+ year  +"') and ocm_classname ='org.girlscouts.vtk.models.Achievement'";
		javax.jcr.query.QueryManager qm = s.getWorkspace().getQueryManager();
        javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL); 
        javax.jcr.query.QueryResult result = q.execute();
        for (javax.jcr.query.RowIterator it = result.getRows(); it.hasNext(); ) {
            javax.jcr.query.Row r = it.nextRow();               
            String path = r.getValue("jcr:path").getString() ; 
            if( path.indexOf("/SHARED_") !=-1) continue;
            String count= null;
            try{
				String achievement_users = r.getValue("users").getString();
				if( achievement_users!=null && achievement_users.contains(",") )
					count= ""+ achievement_users.split(",").length;
            }catch(Exception e){e.printStackTrace();}

            attendances.put( path , count);
        }
        
        for(String council: councils){
	        sql ="select refId from nt:unstructured where ocm_classname='org.girlscouts.vtk.models.MeetingE' and isdescendantnode('"+ year + council +"/') ";
	        q = qm.createQuery(sql, javax.jcr.query.Query.SQL); 
	        result = q.execute();
	        for (javax.jcr.query.RowIterator it = result.getRows(); it.hasNext(); ) {
	            javax.jcr.query.Row r = it.nextRow();               
	            String path = r.getValue("jcr:path").getString() ;
	            if( path.indexOf("/SHARED_") !=-1) continue;
	            String refId= r.getValue("refId").getString();
	            String meetingId= refId.substring( refId.lastIndexOf("/")+1 );
	            if( meetingId.contains("_") ) meetingId= meetingId.substring( 0, meetingId.indexOf("_") );
	            out.println(path.split("/")[4] + "," +meetingId +","+ (attendances.get(path +"/achievement")) );
	        }
        }//edn for
    %>
end of report