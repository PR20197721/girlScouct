package org.girlscouts.vtk.ejb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.jcr.Session;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;
import org.apache.jackrabbit.ocm.query.Filter;
import org.apache.jackrabbit.ocm.query.Query;
import org.apache.jackrabbit.ocm.query.QueryManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.girlscouts.vtk.dao.YearPlanDAO;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.utils.VtkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Service(value = YearPlanDAO.class)
public class YearPlanDAOImpl implements YearPlanDAO {
	private final Logger log = LoggerFactory.getLogger("vtk");
	@Reference
	private SessionFactory sessionFactory;

    @Reference
    private org.apache.sling.api.resource.ResourceResolverFactory resolverFactory;
    
	@Activate
	void activate() {
	}

	public List<YearPlan> getAllYearPlans(User user, String ageLevel) {
		java.util.List<YearPlan> yearPlans = null;
		Session session = null;
		try {
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(YearPlan.class);
			classes.add(Meeting.class);
			classes.add(Cal.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);

			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(YearPlan.class);
			java.util.Calendar today = java.util.Calendar.getInstance();
			filter.setScope("/content/girlscouts-vtk/yearPlanTemplates/yearplan"
					+ VtkUtil.getCurrentGSYear() + "/" + ageLevel + "/");

			Query query = queryManager.createQuery(filter);
			yearPlans = (List<YearPlan>) ocm.getObjects(query);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return yearPlans;
	}

	public YearPlan getYearPlan(String path) {
		YearPlan yearPlan = null;
		Session session = null;
		try {
			List<Class> classes = new ArrayList<Class>();
			classes.add(YearPlan.class);
			classes.add(Meeting.class);
			classes.add(Cal.class);

			session = sessionFactory.getSession();
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);

			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(YearPlan.class);

			Query query = queryManager.createQuery(filter);

			yearPlan = (YearPlan) ocm.getObject(path);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return yearPlan;
	}

	public java.util.Date getLastModif(Troop troop) {
		Session session = null;
		java.util.Date toRet = null;
		try {
			session = sessionFactory.getSession();
			String sql = "select jcr:lastModified from nt:base where jcr:path = '"
					+ troop.getPath() + "' and jcr:lastModified is not null";
			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();
			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.SQL);
			QueryResult result = q.execute();
			for (RowIterator it = result.getRows(); it.hasNext();) {
				Row r = it.nextRow();
				toRet = new java.util.Date(r.getValue("jcr:lastModified")
						.getLong());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return toRet;
	}

	public java.util.Date getLastModifByOthers(Troop troop, String sessionId) {
		Session session = null;
		java.util.Date toRet = null;
		try {
			session = sessionFactory.getSession();
			String sql = "select jcr:lastModified from nt:base where jcr:path = '"
					+ troop.getPath() + "' and jcr:lastModified is not null";

			if (sessionId != null)
				sql += " and currentTroop <>'" + sessionId + "'";
			log.debug("SQL " + sql);
			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();
			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.SQL);
			QueryResult result = q.execute();
			for (RowIterator it = result.getRows(); it.hasNext();) {
				Row r = it.nextRow();
				toRet = new java.util.Date(r.getValue("jcr:lastModified")
						.getLong());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return toRet;
	}
	
	public YearPlan getYearPlanJson( String yearPlanPath ){
	
		YearPlan yearPlan = null;
		Session session = null;
		try {
			 session = sessionFactory.getSession();
			 ResourceResolver resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);
			
			 //year plan info
			 Resource yearPlanResource = resourceResolver.getResource( yearPlanPath );
			 if( yearPlanResource ==null ) return null;
			 ValueMap yearValueMap = yearPlanResource.getValueMap();
			 if(yearValueMap==null) return null;
			 
			 
			 yearPlan = new YearPlan();
			 yearPlan.setName( yearValueMap.get("name") ==null ? "" :  yearValueMap.get("name").toString());
			 yearPlan.setDesc( yearValueMap.get("desc") ==null ? "" :  yearValueMap.get("desc").toString());
			 
			 java.util.List<MeetingE> meetingInfos = new java.util.ArrayList();
             Resource myResource = yearPlanResource.getChild("meetings");
             if( myResource != null ){
            	 Iterable<Resource> meetings = myResource.getChildren();
            	 for (Resource meeting : meetings) {
            		 String refId = meeting.getValueMap().get("refId").toString();
            		 int position = Integer.parseInt( meeting.getValueMap().get("id").toString() );
            		 Resource meetingResource = resourceResolver.getResource( refId );
            		 if( meetingResource==null ) continue;
            		 ValueMap valueMap = meetingResource.getValueMap();
            		 if( valueMap==null) continue;
            		 Meeting meetingInfo = new Meeting();
            		 meetingInfo.setName( valueMap.get("name").toString());
            		 meetingInfo.setBlurb(valueMap.get("blurb").toString());
            		 meetingInfo.setPosition( position );
            		 meetingInfo.setId(valueMap.get("id").toString());
            		 meetingInfo.setCat(valueMap.get("cat").toString());
            		 
            		 //get activities
            		 Iterable<Resource> meetingActivitiesResource = meetingResource.getChild( "activities" ).getChildren();
            		 forActivities:for (Resource r_activities : meetingActivitiesResource) {
            			 ValueMap activityValueMap = r_activities.getValueMap();
            			 if( activityValueMap ==null ) continue;
                		 String isOutdoorAvailable = activityValueMap.get("isOutdoorAvailable") ==null ? null : activityValueMap.get("isOutdoorAvailable").toString();
                		 if( isOutdoorAvailable!=null && isOutdoorAvailable.equals("true") ) {
                			Activity activity = new Activity();
                			activity.setIsOutdoorAvailable(true);
                			java.util.List<Activity> activities = new java.util.ArrayList();
                			activities.add(activity);
                			meetingInfo.setActivities( activities );
                			break forActivities;
                		 }		 
            		 }
            		 MeetingE masterMeeting = new MeetingE();
            		 masterMeeting.setMeetingInfo(meetingInfo);
            		 meetingInfos.add( masterMeeting );
            	 }
             }
             
             Comparator<MeetingE> comp = new Comparator<MeetingE>() {
     			public int compare(MeetingE m1, MeetingE m2) {
     					return m1.getMeetingInfo().getPosition().compareTo(m2.getMeetingInfo().getPosition());
     				}
     		 };
     		 Collections.sort(meetingInfos, comp);
           
     		 for(int i=0;i<meetingInfos.size();i++){
     			 System.err.println("test sort: "+ meetingInfos.get(i).getMeetingInfo().getPosition());
     		 }
     		 
             yearPlan.setMeetingEvents(meetingInfos);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return yearPlan;
	}
}
