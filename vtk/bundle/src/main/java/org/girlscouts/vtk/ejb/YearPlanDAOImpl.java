package org.girlscouts.vtk.ejb;

import java.util.ArrayList;
import java.util.List;
import javax.jcr.Session;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;
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
import org.girlscouts.vtk.dao.YearPlanDAO;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.models.Meeting;
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
System.err.println("tata YP: /content/girlscouts-vtk/yearPlanTemplates/yearplan"
		+ VtkUtil.getCurrentGSYear() + "/" + ageLevel + "/");
			Query query = queryManager.createQuery(filter);
			yearPlans = (List<YearPlan>) ocm.getObjects(query);
System.err.println("tata YP found: "+ (yearPlans==null));
System.err.println("tata YP found: "+ (yearPlans.size()) );
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
}
