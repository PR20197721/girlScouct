package org.girlscouts.vtk.impl.servlets;

import java.util.stream.Collectors;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.girlscouts.vtk.ejb.SessionFactory;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.utils.VtkUtil;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.girlscouts.web.cq.workflow.service.impl.RolloutTemplatePageServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
@SlingServlet(
	    paths = {"/bin/vtk/v1/meetingSearch"}
	) 

//EX: http://localhost:4503/bin/vtk/v1/meetingSearch?search={%22keywords%22:%22Award%22,%22year%22:2017,%22meetingPlanType%22:%22Journey%22,%22level%22:[%22daisy%22,%22Junior%22],%22categoryTags%22:[%22Its_Your_Story_-_Tell_It,%22]}
public class MeetingSearch extends SlingAllMethodsServlet{

	private static Logger log = LoggerFactory.getLogger(MeetingSearch.class);

	private static final long serialVersionUID = 1L;
	
	@Reference
	private SessionFactory sessionFactory;

	protected void doPost(SlingHttpServletRequest request,
					SlingHttpServletResponse response) {
		
		java.util.List<Meeting> meetings = new java.util.ArrayList();
		Session session = null;
		ResourceResolver rr= null;
		try {
			ObjectMapper mapper =new ObjectMapper();
			mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
			mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
			
			org.girlscouts.vtk.models.MeetingSearch search  = mapper.readValue(VtkUtil.getJsonFromRequest(request).toString(), org.girlscouts.vtk.models.MeetingSearch.class);
			
			rr = sessionFactory.getResourceResolver();
			session = rr.adaptTo(Session.class);
			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();
			
			String sql = "select [id],[name],[position] from [nt:unstructured] where [ocm_classname]='org.girlscouts.vtk.models.Meeting' "
					+ " and isdescendantnode('/content/girlscouts-vtk/meetings/myyearplan"+ search.getYear()+"/')  ";
				 
				if( search.getLevel()!=null && search.getLevel().size()>0 ) 
					sql+=" and  contains([level] ,  '"+ fmtMultiContainsSql(search.getLevel()) +"') " ;
				
				if( search.getCategoryTags()!=null && search.getCategoryTags().size()>0 ) {
					String catTagsFmt = fmtMultiContainsSql(search.getCategoryTags()).replace("'","''");
					sql+=" and (contains([catTags] ,  '" + catTagsFmt + "') ";
					sql+=" or contains([catTagsAlt] ,  '" + catTagsFmt + "') )";
				}
				
				if( search.getMeetingPlanType()!=null &&  !"".equals(search.getMeetingPlanType()) ) {
					String meetingPlanTypeFmt= search.getMeetingPlanType();
					sql+=" and ( meetingPlanType = '" + meetingPlanTypeFmt + "' ";
					sql+=" or  meetingPlanTypeAlt = '" + meetingPlanTypeFmt + "' ) ";
				}
				
				if( search.getKeywords() != null &&  !"".equals( search.getKeywords().trim() ) ){
					String keywords= search.getKeywords().replace(" ", " OR ").replace("'","''");
					if(keywords.length() >= 5){
						sql+= " and contains( *, '*"+ keywords +"*')  ";
					}else{
						sql+= " and contains( *, '"+ keywords +"')  ";
					}
				}
	
						javax.jcr.query.Query q = qm.createQuery(sql,
								javax.jcr.query.Query.JCR_SQL2);
						QueryResult result = q.execute();
						for (RowIterator it = result.getRows(); it.hasNext();) {
							try{
								Row r = it.nextRow();
								Value excerpt = r.getValue("jcr:path");
								String path = excerpt.getString();
								Meeting meeting = new Meeting();
								meeting.setId(r.getValue("id").getString());
								meeting.setName(r.getValue("name").getString() );
								meetings.add( meeting );
							}catch (Exception e){
								log.error("Error parsing search result: ", e);
							}
						}
						
						mapper.setSerializationInclusion(Include.NON_NULL);
						mapper.setSerializationInclusion(Include.NON_EMPTY);
						response.getWriter().write(mapper.writeValueAsString(meetings));
						
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							if( rr!=null )
								sessionFactory.closeResourceResolver( rr );
							if (session != null)
								session.logout();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
	
	private String fmtMultiContainsSql(java.util.List<String> val){
		return  val.stream()
         .map(entry ->  entry )
         .collect(Collectors.joining(" OR "));
	}
	
}
