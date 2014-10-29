<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
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



<% 
javax.jcr.Session jcr_session = (javax.jcr.Session)resourceResolver.adaptTo(javax.jcr.Session.class);

String sql= "select * from nt:unstructured where jcr:path like '/vtk/%/users/' and ocm_classname ='org.girlscouts.vtk.models.JcrNode'";
javax.jcr.query.QueryManager qm = jcr_session.getWorkspace().getQueryManager();
javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL);
javax.jcr.query.QueryResult result = q.execute();	
for (javax.jcr.query.RowIterator it = result.getRows(); it.hasNext();) {
	javax.jcr.query.Row r = it.nextRow();
	javax.jcr.Value excerpt = r.getValue("jcr:path");
	//paths.add( excerpt.getString() );
	//System.err.println("Adding path: "+ excerpt.getString());
	String p = excerpt.getString();
	
	javax.jcr.Node t =  jcr_session.getNode( p );
	javax.jcr.NodeIterator itr= t.getNodes();
	if( t.getNodes().getSize()>1 ){
		
		out.println("<hr/>");
	 while( itr.hasNext()){
			Node n = (javax.jcr.Node) itr.next();
			
			String lastMod = null;
			try{ lastMod = n.getProperty("jcr:lastModified").getValue().getString(); }catch(Exception e){}
			%>
				<br/><%=n.getPath() %> - <%=lastMod %>
			<%
		}
	}
}
%>