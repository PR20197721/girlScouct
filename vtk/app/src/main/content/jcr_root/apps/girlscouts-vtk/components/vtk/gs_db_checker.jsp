
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

        Set<String> allowedReportUsers = new HashSet<String>();
        allowedReportUsers.add("005g0000002apMT");
        allowedReportUsers.add("005G0000006oEkZ");
        allowedReportUsers.add("005G0000006oBVG");
        allowedReportUsers.add("005g0000002G004");

        if( !allowedReportUsers.contains(user.getApiConfig().getUserId()) ){
            out.println("You do not have no access to this page [" + user.getApiConfig().getUserId() + "].");
            return;
        }

		out.println("Inconsistancy report started on "+ new java.util.Date() +". Wait for completed time");

		List <String> sqls = new ArrayList();

		//check all meetings
		sqls.add("select child.* from [nt:unstructured] as parent  INNER JOIN [nt:unstructured] as child on ISCHILDNODE( child, parent)  where ISDESCENDANTNODE (parent, '"+VtkUtil.getYearPlanBase(user, troop) +"') and NAME(parent)='meetingEvents' and child.ocm_classname is null");
		
		//check all activities
		sqls.add("select child.* from [nt:unstructured] as parent  INNER JOIN [nt:unstructured] as child on ISCHILDNODE( child, parent)  where ISDESCENDANTNODE (parent, '"+VtkUtil.getYearPlanBase(user, troop) +"') and NAME(parent)='activities' and child.ocm_classname is null");
		
		//sched
		sqls.add("select child.* from [nt:unstructured] as parent  INNER JOIN [nt:unstructured] as child on ISCHILDNODE( child, parent)  where ISDESCENDANTNODE (parent, '"+VtkUtil.getYearPlanBase(user, troop) +"') and NAME(child)='schedule' and  (child.dates is null or child.dates ='')");

		javax.jcr.Session s= null;
		try{ 
            s =(slingRequest.getResourceResolver().adaptTo(Session.class));
            javax.jcr.query.QueryManager qm = s.getWorkspace().getQueryManager();
            javax.jcr.query.Query q = null;
            for(String sql : sqls){
                try{
                    out.println( "<hr/>" + sql);
                    q= qm.createQuery(sql, javax.jcr.query.Query.JCR_SQL2); 
                    java.util.Map container= new java.util.TreeMap();
                    javax.jcr.query.QueryResult result = q.execute();
                    for (javax.jcr.query.RowIterator it = result.getRows(); it.hasNext(); ) {
                        javax.jcr.query.Row r = it.nextRow();
                        String path = r.getPath() ;
                        out.println( "<div style='color:red; padding-top:25px;'>"+ path +"</div>");
                    }//end for
                }catch(Exception e){out.println("<br/><br/>FOUND ERROR"); e.printStackTrace();}
            }
        }catch(Exception eMain){
			eMain.printStackTrace();
        } finally {
            if (s != null && s.isLive()) {
				//TODO close connection or return to connection pool
                //s.logout();
            }
        }


        
out.println("<br/><br/>Inconsistancy report completed on "+ new java.util.Date() );
%>