<%@ page
        import="com.day.cq.i18n.I18n, com.day.cq.search.Predicate,  com.day.cq.search.PredicateGroup,com.day.cq.search.Query,com.day.cq.search.QueryBuilder" %>
<%@ page import="com.day.cq.search.eval.FulltextPredicateEvaluator,
                 com.day.cq.search.eval.JcrPropertyPredicateEvaluator,
                 com.day.cq.search.result.Hit,
                 com.day.cq.search.result.SearchResult,
                 com.day.cq.tagging.TagManager,
                 com.day.cq.wcm.foundation.Search,
                 org.girlscouts.web.search.DocHit,
                 javax.jcr.Node" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp" %>
<%

    javax.jcr.Session s = (slingRequest.getResourceResolver().adaptTo(Session.class));
    java.util.Map<String, String> attendances = new java.util.TreeMap();
    List<String> councils = VtkUtil.getCouncils();

    String sql = "select * from [nt:unstructured] where isdescendantnode('/vtk2015') and ocm_classname='org.girlscouts.vtk.models.Activity'";
    javax.jcr.query.QueryManager qm = s.getWorkspace().getQueryManager();
    javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.JCR_SQL2);
    javax.jcr.query.QueryResult result = q.execute();
    for (javax.jcr.query.RowIterator it = result.getRows(); it.hasNext(); ) {
        javax.jcr.query.Row r = it.nextRow();
        String path = r.getValue("jcr:path").getString();

    }


%>
end of report