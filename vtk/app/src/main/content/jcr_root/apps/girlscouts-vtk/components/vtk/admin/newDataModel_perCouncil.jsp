<%@ page
        import="com.day.cq.i18n.I18n, com.day.cq.search.Predicate, com.day.cq.search.PredicateGroup, com.day.cq.search.Query,com.day.cq.search.QueryBuilder,com.day.cq.search.eval.FulltextPredicateEvaluator" %>
<%@ page import="com.day.cq.search.eval.JcrPropertyPredicateEvaluator,
                 com.day.cq.search.result.Hit,
                 com.day.cq.search.result.SearchResult,
                 com.day.cq.tagging.TagManager,
                 com.day.cq.wcm.foundation.Search,
                 org.girlscouts.vtk.models.user.*,
                 org.girlscouts.web.search.DocHit,
                 javax.jcr.Node,
                 java.util.StringTokenizer" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../include/session.jsp" %>
<%@include file="../admin/toolbar.jsp" %>
<table border="1">
    <tr>
        <th>Troop Count
        <th>Council
        <th>Troop
        <th>Path
        <th>lastModified
    </tr>
    <%
        java.util.List L_COUNCIL = new java.util.ArrayList();
        int yp_2plus_withTime = 0;
        javax.jcr.Session jcr_session = (javax.jcr.Session) resourceResolver.adaptTo(javax.jcr.Session.class);
        String sql = "select * from nt:unstructured where jcr:path like '" + VtkUtil.getYearPlanBase(user, selectedTroop) + "%/users/' and ocm_classname ='org.girlscouts.vtk.ocm.JcrNode'";
        javax.jcr.query.QueryManager qm = jcr_session.getWorkspace().getQueryManager();
        javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL);
        javax.jcr.query.QueryResult result = q.execute();
        int count = 0;
        for (javax.jcr.query.RowIterator it = result.getRows(); it.hasNext(); ) {
            javax.jcr.query.Row r = it.nextRow();
            javax.jcr.Value excerpt = r.getValue("jcr:path");
            String p = excerpt.getString();
            javax.jcr.Node t = jcr_session.getNode(p);
            javax.jcr.NodeIterator itr = t.getNodes();
            if (t.getNodes().getSize() > 1) {
                count++;
    %>
    <tr>
        <td>
        <td><%=L_COUNCIL.size() %>
        <td>yp_2plus_withTime=<%=yp_2plus_withTime %>
        <td>
        <td>
                <%
		boolean NoTs=true;
	 while( itr.hasNext()){
			Node n = (javax.jcr.Node) itr.next();
			
			String lastMod = null;
			try{ lastMod = n.getProperty("jcr:lastModified").getValue().getString(); }catch(Exception e){}
			StringTokenizer tt= new StringTokenizer(n.getPath(),"/");
			tt.nextToken();
			String council = tt.nextToken();
			String troopId = tt.nextToken();
			
			if(! L_COUNCIL.contains(council) )
				L_COUNCIL.add(council);
			
			if( lastMod!=null)
				NoTs=false;
			%>
    <tr>
        <td><%=count%>
        <td><%=council%>
        <td><%=troopId%>
        <td>
            <small><%=n.getPath() %>
            </small>
        <td> <%=lastMod==null ? "" : lastMod %>
    </tr>
    <%
                }
                if (NoTs)
                    yp_2plus_withTime++;
            }
        }
    %>
</table>
</br>Total Councils with problem: <%=L_COUNCIL.size() %>
</br>Year Plan with 2+ plans with NO Time: <%=yp_2plus_withTime%>
</br>Total overall troops with 2+ year plans <%=count%> 