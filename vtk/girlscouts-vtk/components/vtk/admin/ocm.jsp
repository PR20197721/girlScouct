<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@ page import="com.day.cq.wcm.foundation.Search,
org.girlscouts.web.search.DocHit,
com.day.cq.search.eval.JcrPropertyPredicateEvaluator,com.day.cq.search.eval.FulltextPredicateEvaluator,
com.day.cq.tagging.TagManager,
java.util.Locale,com.day.cq.search.QueryBuilder,javax.jcr.Node,
java.util.ResourceBundle,com.day.cq.search.PredicateGroup,
com.day.cq.search.Predicate,com.day.cq.search.result.Hit,
com.day.cq.i18n.I18n,com.day.cq.search.Query,com.day.cq.search.result.SearchResult,
java.util.Map,java.util.HashMap,java.util.List" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../include/session.jsp"%>
<%@include file="../admin/toolbar.jsp"%>
<h1>UUID search</h1>
<form action="/content/girlscouts-vtk/en/vtk.admin.ocm.html">
<input type="text" value="" name="uuid"/>
<input type="submit">
</form>


<%


javax.jcr.Session s= (slingRequest.getResourceResolver().adaptTo(Session.class));

if( request.getParameter("uuid")!=null && !"".equals(request.getParameter("uuid"))){
	out.println("searching for uuid: "+ request.getParameter("uuid"));
 try{
	//out.println( s.getNodeByUUID("4248f12c-59b4-4770-bf8d-2aede5a19229") );
	out.println("<br/>found: "+ s.getNodeByIdentifier(request.getParameter("uuid")));
 }catch(Exception e){e.printStackTrace();}
 //if(true)return;
}

out.println("<hr/>");


/*

String sql="select  excerpt(.) from nt:base where jcr:path like '/vtk/%' ";


javax.jcr.query.QueryManager qm = s.getWorkspace().getQueryManager();
javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL); 
	




javax.jcr.query.QueryResult result = q.execute();
for (javax.jcr.query.RowIterator it = result.getRows(); it.hasNext(); ) {
	javax.jcr.query.Row r = it.nextRow();
	//String uuid = r.getValue("jcr:uuid").getString() ;
	//out.println("<br/>"+ uuid);
	Node node = r.getNode();
	out.println( "<br/>"+node.getIdentifier() );
	
}

*/
%>
