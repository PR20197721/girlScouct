package org.girlscouts.vtk.ejb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.jcr.Session;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;
import org.apache.jackrabbit.ocm.query.Query;
import org.apache.jackrabbit.ocm.query.Filter;
import org.apache.jackrabbit.ocm.query.QueryManager;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.dao.CouncilDAO;
import org.girlscouts.vtk.models.Achievement;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.models.Attendance;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.models.Council;
import org.girlscouts.vtk.models.JcrNode;
import org.girlscouts.vtk.models.Location;
import org.girlscouts.vtk.models.MeetingCanceled;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.SentEmail;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.CouncilInfo;
import org.girlscouts.vtk.utils.ModifyNodePermissions;
import org.girlscouts.vtk.utils.VtkException;
import org.girlscouts.vtk.utils.VtkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Service(value = CouncilDAO.class)
public class CouncilDAOImpl implements CouncilDAO {
	private final Logger log = LoggerFactory.getLogger("vtk");

	@Reference
	private SessionFactory sessionFactory;

	@Reference
	private UserUtil userUtil;

	@Reference
	org.girlscouts.vtk.helpers.CouncilMapper councilMapper;

	@Reference
	private ModifyNodePermissions permiss;
	
	@Activate
	void activate() {
	}

	public Council findCouncil(User user, String councilId)
			throws IllegalAccessException, VtkException {
		Council council = null;
		Session session = null;
		try {

			if (user != null
					&& !userUtil.hasPermission(user.getPermissions(),
							Permission.PERMISSION_LOGIN_ID))
				throw new IllegalAccessException();
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Council.class);
			classes.add(YearPlan.class);
			classes.add(MeetingE.class);
			classes.add(Location.class);
			classes.add(Cal.class);
			classes.add(Activity.class);
			classes.add(SentEmail.class);
			classes.add(Asset.class);
			classes.add(JcrNode.class);
			classes.add(Milestone.class);
			classes.add(Troop.class);
			classes.add(Attendance.class);
			classes.add(Achievement.class);
			classes.add(org.girlscouts.vtk.models.MeetingCanceled.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
			council = (Council) ocm.getObject(VtkUtil.getYearPlanBase(user, null) + councilId);
	
		} catch (org.apache.jackrabbit.ocm.exception.IncorrectPersistentClassException ec ){
			throw new VtkException("Could not complete intended action due to a server error. Code: "+ new java.util.Date().getTime());
		
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
		return council;
	}

	public Council createCouncil(User user, String councilId)
			throws IllegalAccessException, VtkException {

		if (user != null
				&& !userUtil.hasPermission(user.getPermissions(),
						Permission.PERMISSION_LOGIN_ID))
			throw new IllegalAccessException();
		Session session = null;
		Council council = null;
		try {
			List<Class> classes = new ArrayList<Class>();
			classes.add(Council.class);
			classes.add(YearPlan.class);
			classes.add(MeetingE.class);
			classes.add(Location.class);
			classes.add(Cal.class);
			classes.add(Activity.class);
			classes.add(Asset.class);
			classes.add(SentEmail.class);
			classes.add(JcrNode.class);
			classes.add(Milestone.class);
			classes.add(Troop.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			
			String vtkBase = VtkUtil.getYearPlanBase(user, null);
			session = sessionFactory.getSession();
			if (!session.itemExists(vtkBase)) {
				//create vtk base
				//ocm.insert(new JcrNode(vtkBase.substring(0, vtkBase.length()-1)));
				permiss.modifyNodePermissions(vtkBase, "vtk");
				permiss.modifyNodePermissions("/content/dam/girlscouts-vtk/troop-data"+VtkUtil.getCurrentGSYear()+"/", "vtk");
			}
			
			
			//council = new Council("/vtk/" + councilId);
			council = new Council(vtkBase + councilId);
		
			if (!session.itemExists(vtkBase + councilId)) {
				ObjectContentManager ocm = new ObjectContentManagerImpl(session,
						mapper);
				ocm.insert(council);
				ocm.save();
			} else {
				log.debug(">>>>>>>>>>> INFO : CouncilDAOImpl.createCouncil skipped because council already exists: "+  councilId); 
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
		return council;

	}

	public Council getOrCreateCouncil(User user, String councilId)
			throws IllegalAccessException, VtkException {

		if (user != null
				&& !userUtil.hasPermission(user.getPermissions(),
						Permission.PERMISSION_LOGIN_ID))
			throw new IllegalAccessException();
System.err.println("tata1");
		Council council = findCouncil(user, councilId);
System.err.println("tata2");		
		if (council == null){			
			council = createCouncil(user, councilId);
		}
		return council;
	}

	public void updateCouncil(User user, Council council)
			throws IllegalAccessException {
		if (user != null
				&& !userUtil.hasPermission(user.getPermissions(),
						Permission.PERMISSION_LOGIN_ID))
			throw new IllegalAccessException();
		Session session = null;
		try {
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Council.class);
			classes.add(YearPlan.class);
			classes.add(MeetingE.class);
			classes.add(Location.class);
			classes.add(Cal.class);
			classes.add(Activity.class);
			classes.add(Asset.class);
			classes.add(SentEmail.class);
			classes.add(JcrNode.class);
			classes.add(Milestone.class);
			classes.add(Troop.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);

			ocm.update(council);
			ocm.save();

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
	}

	public java.util.List<Milestone> getCouncilMilestones(String councilCode) {

		CouncilInfo list = getCouncilInfo(councilCode);
		java.util.List<Milestone> milestones = list.getMilestones();
		sortMilestonesByDate(milestones);
		return milestones;
	}

	private CouncilInfo getCouncilInfo(String councilCode) {
		Session session = null;
		CouncilInfo cinfo = null;
		try {
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Milestone.class);
			classes.add(CouncilInfo.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(CouncilInfo.class);
			Query query = queryManager.createQuery(filter);

			String path = VtkUtil.getYearPlanBase(null, null) + councilCode + "/councilInfo";
			if (session.itemExists(path)) {
				cinfo = (CouncilInfo) ocm.getObject(path);
				if (cinfo == null)
					log.error("OCM failed to retrieve "+ VtkUtil.getYearPlanBase(null, null) + councilCode
							+ "/councilInfo !!!");
				if (cinfo.getMilestones() == null) {
					List<Milestone> milestones = getAllMilestones(councilCode);
					cinfo.setMilestones(milestones);
					ocm.update(cinfo);
					ocm.save();
				}
			} else {
				if (!session.itemExists(VtkUtil.getYearPlanBase(null, null) + councilCode)) {
					// create council, need user permission
					log.error(VtkUtil.getYearPlanBase(null, null) + councilCode + "does NOT exist!!!");
				}
				cinfo = new CouncilInfo(path);
				List<Milestone> milestones = getAllMilestones(councilCode);
				cinfo.setMilestones(milestones);
				ocm.insert(cinfo);
				ocm.save();
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
		return cinfo;
	}

	public void updateCouncilMilestones(java.util.List<Milestone> milestones,
			String cid) {
		Session session = null;
		try {
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Milestone.class);
			classes.add(CouncilInfo.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
			CouncilInfo list = getCouncilInfo(cid);
			java.util.List<Milestone> oldMilestones = list.getMilestones();
			sortMilestonesByDate(oldMilestones);
			int i = 0;
			for (; i < oldMilestones.size(); i++) {
				if (milestones.size() > i
						&& oldMilestones.get(i).getBlurb()
								.equals(milestones.get(i).getBlurb())) {
					oldMilestones.get(i).setDate(milestones.get(i).getDate());
					oldMilestones.get(i).setShow(milestones.get(i).getShow());
					continue;
				} else {
					oldMilestones.remove(i);
					i--;
				}
			}
			for (int k = i; k < milestones.size(); k++) {
				oldMilestones.add(milestones.get(k));
			}
			list.setMilestones(oldMilestones);
			ocm.update(list);
			ocm.save();
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
	}

	public java.util.List<Milestone> getAllMilestones(String councilCode) {
		String councilPath = councilMapper.getCouncilBranch(councilCode);
		java.util.List<Milestone> milestones = new ArrayList<Milestone>();
		Session session = null;
		try {
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Milestone.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
			if (session.itemExists(councilPath + "/en/milestones")) {
				QueryManager queryManager = ocm.getQueryManager();
				Filter filter = queryManager.createFilter(Milestone.class);
				filter.setScope(councilPath + "/en/milestones//");
				Query query = queryManager.createQuery(filter);
				milestones = (List<Milestone>) ocm.getObjects(query);
				sortMilestonesByDate(milestones);
			} else {
				log.error(councilPath + "/en/milestones does NOT exist!");
				return milestones;
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
		return milestones;
	}

	private void sortMilestonesByDate(List<Milestone> milestones) {
		Comparator<Milestone> comp = new Comparator<Milestone>() {
			public int compare(Milestone m1, Milestone m2) {
				if (m1.getDate() != null) {
					if (m2.getDate() != null) {
						return m1.getDate().compareTo(m2.getDate());
					} else {
						return -1;
					}
				} else if (m2.getDate() != null) {
					return 1;
				} else {
					return m1.getBlurb().compareTo(m2.getBlurb());
				}
			}

		};
		Collections.sort(milestones, comp);
	}
}
