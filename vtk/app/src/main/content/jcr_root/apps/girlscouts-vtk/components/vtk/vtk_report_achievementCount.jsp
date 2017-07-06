<%@ page import="com.google.common.collect.*,org.apache.commons.lang3.StringEscapeUtils,java.util.stream.Collectors,java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
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
		response.setHeader("Content-Disposition","attachment; filename=AchievementReport.csv");
		javax.jcr.Session s= (slingRequest.getResourceResolver().adaptTo(Session.class));
		String sql="select id,name, level from nt:unstructured where jcr:path like '/content/girlscouts-vtk/meetings/myyearplan"+user.getCurrentYear()+"/%' and ocm_classname='org.girlscouts.vtk.models.Meeting'";
		javax.jcr.query.QueryManager qm = s.getWorkspace().getQueryManager();
		javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL); 
		avax.jcr.query.QueryResult result = q.execute();
		java.util.Map<String, String[]> meetingInfos = new java.util.TreeMap();
		for (javax.jcr.query.RowIterator it = result.getRows(); it.hasNext(); ) {
			javax.jcr.query.Row r = it.nextRow();
			try{ 
				String name= r.getValue("name").getString();
				String id= r.getValue("id").getString();
				String level= r.getValue("level").getString();
				String meetingInfo[] = {name, level};
				meetingInfos.put( id, meetingInfo);
			}catch(Exception e){e.printStackTrace();}          
		}
		
		sql="select parent.refId, child.users from [nt:base] as parent INNER JOIN [nt:base] as child ON ISCHILDNODE(child, parent) where  (isdescendantnode (parent, ["+ VtkUtil.getYearPlanBase(null,null) +"])) and child.ocm_classname='org.girlscouts.vtk.models.Achievement' and child.users <> ''";
		q = qm.createQuery(sql, javax.jcr.query.Query.JCR_SQL2); 
		result = q.execute();
		Multimap<String, String> meetingIds = ArrayListMultimap.create();
		for (javax.jcr.query.RowIterator it = result.getRows(); it.hasNext(); ) {
			javax.jcr.query.Row r = it.nextRow();
			try{
				String refId= r.getValue("parent.refId").getString();
				String meetingId= refId.substring( refId.lastIndexOf("/")+1);
				meetingId= meetingId.contains("_") ? meetingId.substring(0,meetingId.indexOf("_")) : meetingId;
				meetingIds.put( meetingId, r.getValue("child.users").getString());
			}catch(Exception e){e.printStackTrace();}          
		}
		
		java.util.Iterator itr= meetingIds.keySet().iterator();
		while( itr.hasNext() ){
			String meetingId= (String) itr.next();
			java.util.Collection achv = meetingIds.get(meetingId);
			int countAchv = 0;
			java.util.Iterator _itr = achv.iterator();
			while( _itr.hasNext() ){
				String girls = (String)_itr.next();
				StringTokenizer t = new StringTokenizer( girls, ",");
				countAchv += t.countTokens();
			}
			String meetingInfo[] = meetingInfos.get( meetingId );
			out.println( "\n"+ meetingId +","+ achv.size()+","+countAchv+","+
					StringEscapeUtils.escapeCsv( meetingInfo==null ? "" : meetingInfo[0]) +","+ StringEscapeUtils.escapeCsv( meetingInfo==null ? "" : meetingInfo[1]) 
					);
	}
	%>