


<%@ page import="org.apache.commons.lang3.StringEscapeUtils,java.util.stream.Collectors,java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@ page import="com.day.cq.wcm.foundation.Search,
org.girlscouts.web.search.DocHit,java.io.*,
com.day.cq.search.eval.JcrPropertyPredicateEvaluator,com.day.cq.search.eval.FulltextPredicateEvaluator,
com.day.cq.tagging.TagManager,com.google.common.collect.*,
java.util.Locale,com.day.cq.search.QueryBuilder,javax.jcr.Node,
java.util.ResourceBundle,com.day.cq.search.PredicateGroup,
com.day.cq.search.Predicate,com.day.cq.search.result.Hit,
com.day.cq.i18n.I18n,com.day.cq.search.Query,com.day.cq.search.result.SearchResult,org.apache.commons.beanutils.*,
java.util.Map,java.util.HashMap,java.util.List" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<%
   		String rptForYear = user.getCurrentYear();
		if( request.getParameter("rptForYear")!=null)  
			rptForYear= request.getParameter("rptForYear");

		response.setContentType("application/csv");
		response.setHeader("Content-Disposition","attachment; filename=MeetingCountReport.csv");
		out.println("\nMeeting ID,# of Troops, # of Girls, Meeting Name, Grade-Level");
		javax.jcr.Session s= (slingRequest.getResourceResolver().adaptTo(Session.class));
		javax.jcr.query.QueryManager qm = s.getWorkspace().getQueryManager();
		javax.jcr.query.Query q=null;
		javax.jcr.query.QueryResult result=null;
		List<String> councils= VtkUtil.getCouncils();
		java.util.Map<String, String[]> meetingInfos = new java.util.TreeMap();
		String sql=null;
		java.util.List<String> mids= new java.util.ArrayList();
		Multimap<String, String> meetingIds = ArrayListMultimap.create();
		try{
			for(String council: councils ){

				sql="select users from [nt:unstructured] where isdescendantnode( [/vtk"+ rptForYear +"/" + council+"/])  and [ocm_classname]='org.girlscouts.vtk.models.Achievement' and [users] <> '' ";
                q = qm.createQuery(sql, javax.jcr.query.Query.SQL); 
					result = q.execute();
                    for (javax.jcr.query.RowIterator it = result.getRows(); it.hasNext(); ) {
                            javax.jcr.query.Row r = it.nextRow();
                            try{
                                String refId = r.getNode().getParent().getProperty("refId").getString() ;
                                String meetingId= refId.substring( refId.lastIndexOf("/")+1);
                                meetingId= meetingId.contains("_") ? meetingId.substring(0,meetingId.indexOf("_")) : meetingId;
                                meetingIds.put( meetingId, r.getValue("users").getString());
                            }catch(Exception e){log.debug("Found error in vtk_report_achievementCount.jsp while traversing Achievements  ");}  

					}//edn for
			}//end for		
		}catch(Exception e){log.debug("Found error in vtk_report_achievementCount.jsp while exec SQL Achievement");}	



		sql="select id,name, level from [nt:unstructured] where isdescendantnode( '/content/girlscouts-vtk/meetings/myyearplan"+rptForYear+"/') and [ocm_classname]='org.girlscouts.vtk.models.Meeting'";
		q = qm.createQuery(sql, javax.jcr.query.Query.JCR_SQL2); 
		result = q.execute();

		for (javax.jcr.query.RowIterator it = result.getRows(); it.hasNext(); ) {
			javax.jcr.query.Row r = it.nextRow();
			try{ 

				String name= r.getValue("name").getString();
				String id= r.getValue("id").getString();
				String level= r.getValue("level").getString();      
                java.util.Collection achv = meetingIds.get(id);
  				if(achv==null || achv.size()<=0) continue;              
                int countAchv = 0;
                java.util.Iterator _itr = achv.iterator();
                while( _itr.hasNext() ){
                    String girls = (String) _itr.next();
                    StringTokenizer t = new StringTokenizer( girls, ",");
                    countAchv += t.countTokens();
                }

               out.println( "\n"+ id +","+ achv.size()+","+countAchv+","+
                        StringEscapeUtils.escapeCsv( name) +","+ StringEscapeUtils.escapeCsv( level) 
                        );

            }catch(Exception e){log.debug("Found error in vtk_report_achievementCount.jsp while traversing lib meetings");}
        }//edn for





%>


