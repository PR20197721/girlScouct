<%@ page import="java.util.stream.Collectors,java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
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
		response.setHeader("Content-Disposition","attachment; filename=rpt.csv");
        javax.jcr.Session s= (slingRequest.getResourceResolver().adaptTo(Session.class));
        String sql="select refId from nt:unstructured where jcr:path like '"+ VtkUtil.getYearPlanBase(null,null) +"%' and ocm_classname ='org.girlscouts.vtk.models.MeetingE' order by [jcr:score]";
        javax.jcr.query.QueryManager qm = s.getWorkspace().getQueryManager();
        javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL); 
        javax.jcr.query.QueryResult result = q.execute();
		java.util.List<String> container = new java.util.ArrayList();
		for (javax.jcr.query.RowIterator it = result.getRows(); it.hasNext(); ) {
            javax.jcr.query.Row r = it.nextRow();
            try{
				container.add(r.getValue("refId").getString());
            }catch(Exception e){}          
        }
		Map<String, Long> counts =VtkUtil.countUniq(container);
        java.util.Iterator itr= counts.keySet().iterator();
        while( itr.hasNext() ){
            String meetingRef = (String) itr.next();
            Node node = s.getNode(meetingRef);
            long count= counts.get(meetingRef);
            out.println( "\n"+node.getProperty("name").getString() +","+ count );
        }
    %>