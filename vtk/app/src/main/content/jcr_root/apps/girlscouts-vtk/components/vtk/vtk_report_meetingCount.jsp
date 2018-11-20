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
		if( request.getParameter("rptForYear")!=null) //overwrite current year 
			rptForYear= request.getParameter("rptForYear");

		response.setContentType("application/csv");
		response.setHeader("Content-Disposition","attachment; filename=MeetingCountReport.csv");
		javax.jcr.Session s= (slingRequest.getResourceResolver().adaptTo(Session.class));
		javax.jcr.query.QueryManager qm = s.getWorkspace().getQueryManager();
		javax.jcr.query.Query q=null;
		javax.jcr.query.QueryResult result=null;
		List<String> councils= VtkUtil.getCouncils();
		java.util.Map<String, String[]> meetingInfos = new java.util.TreeMap();
		String sql=null;
		java.util.List<String> mids= new java.util.ArrayList();
		try{
			for(String council: councils ){

					sql="select refId from nt:unstructured where isdescendantnode( '/vtk"+ rptForYear +"/" + council+"/')  and ocm_classname ='org.girlscouts.vtk.models.MeetingE'  and jcr:path not like '%/SHARED_%'";
    				q = qm.createQuery(sql, javax.jcr.query.Query.SQL); 
					result = q.execute();
                    for (javax.jcr.query.RowIterator it = result.getRows(); it.hasNext(); ) {
                            javax.jcr.query.Row r = it.nextRow();
                            String refId = r.getValue("refId").getString() ;
                            String meetingId= refId.substring( refId.lastIndexOf("/")+1 );
                            if( meetingId.contains("_") ) meetingId= meetingId.substring( 0, meetingId.indexOf("_") );
                      		 if (meetingId==null) continue;
                        	mids.add( meetingId );
					}//edn for
			}//end for		
		}catch(Exception e){e.printStackTrace();}	


		sql="select id,name, level, catTags, catTagsAlt from nt:unstructured where isdescendantnode( '/content/girlscouts-vtk/meetings/myyearplan"+rptForYear+"/') and ocm_classname='org.girlscouts.vtk.models.Meeting'";
		q = qm.createQuery(sql, javax.jcr.query.Query.SQL); 
		result = q.execute();

		for (javax.jcr.query.RowIterator it = result.getRows(); it.hasNext(); ) {
			javax.jcr.query.Row r = it.nextRow();
			try{ 

				String name= r.getValue("name").getString();
				String id= r.getValue("id").getString();
				String level= r.getValue("level").getString();      
				String cat= "";
				try{ cat= r.getValue("catTags").getString();}catch(Exception e){}
				String catAlt= "";
				try{ catAlt= r.getValue("catTagsAlt").getString();}catch(Exception e){}
                
                int count = Collections.frequency(mids, id);
                out.println("\n"+ id +","+ count + "," + StringEscapeUtils.escapeCsv( name ) +","+ StringEscapeUtils.escapeCsv( level ) +","+ (  (cat+","+catAlt).replaceAll(",,","")));

			}catch(Exception e){e.printStackTrace();}
        }//edn for





%>


