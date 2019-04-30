<%@ page
        import="com.day.cq.i18n.I18n, com.day.cq.search.Predicate,  com.day.cq.search.PredicateGroup,com.day.cq.search.Query,com.day.cq.search.QueryBuilder" %>
<%@ page import="com.day.cq.search.eval.FulltextPredicateEvaluator,
                 com.day.cq.search.eval.JcrPropertyPredicateEvaluator,
                 com.day.cq.search.result.Hit,
                 com.day.cq.search.result.SearchResult,
                 com.day.cq.tagging.TagManager,
                 com.day.cq.wcm.foundation.Search,
                 org.girlscouts.common.search.DocHit,
                 javax.jcr.Node" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../include/session.jsp" %>
<%@include file="../admin/toolbar.jsp" %>


<form action="/content/girlscouts-vtk/en/vtk.admin.test4.html">
    <input type="text" name="xx44" value=""/>
    <input type="submit" value="User id"/>
</form>

<%
    try {
    } catch (Exception e) {
        e.printStackTrace();
    }
%>
done 