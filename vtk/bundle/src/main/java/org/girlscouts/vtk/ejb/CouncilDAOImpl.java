package org.girlscouts.vtk.ejb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import javax.jcr.Session;

import org.apache.commons.beanutils.BeanComparator;
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
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.models.Council;
import org.girlscouts.vtk.models.JcrNode;
import org.girlscouts.vtk.models.Location;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.MilestonesCollection;


@Component
@Service(value = CouncilDAO.class)
public class CouncilDAOImpl implements CouncilDAO {

	@Reference
	private SessionFactory sessionFactory;

	@Reference
	private UserUtil userUtil;

	@Reference
	org.girlscouts.vtk.helpers.CouncilMapper councilMapper;


	@Activate
	void activate() {
	}

	public Council findCouncil(User user, String councilId)
			throws IllegalAccessException {
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
			classes.add(Asset.class);
			classes.add(JcrNode.class);
			classes.add(Milestone.class);
			classes.add(Troop.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);

			council = (Council) ocm.getObject("/vtk/" + councilId);

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
			throws IllegalAccessException {

		if (user != null
				&& !userUtil.hasPermission(user.getPermissions(),
						Permission.PERMISSION_LOGIN_ID))
			throw new IllegalAccessException();

		Session session = null;
		Council council = null;
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
			classes.add(JcrNode.class);
			classes.add(Milestone.class);
			classes.add(Troop.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);

			council = new Council("/vtk/" + councilId);
			ocm.insert(council);
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
		return council;

	}

	public Council getOrCreateCouncil(User user, String councilId)
			throws IllegalAccessException {

		if (user != null
				&& !userUtil.hasPermission(user.getPermissions(),
						Permission.PERMISSION_LOGIN_ID))
			throw new IllegalAccessException();

		Council council = findCouncil(user, councilId);
		if (council == null)
			council = createCouncil(user, councilId);

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

		MilestonesCollection list = getMilestonesCollection(councilCode);
		java.util.List<Milestone> milestones = list.getMilestones();
		Comparator<Milestone> comp = new BeanComparator("date");
		Collections.sort(milestones, comp);

		return milestones;
	}

	private MilestonesCollection getMilestonesCollection(String councilCode) {

		//		String councilStr = councilMapper.getCouncilBranch(councilCode);
		//		councilStr = councilStr.replace("/content/", "");

		//		if (user != null
		//				&& !userUtil.hasPermission(user.getPermissions(),
		//						Permission.PERMISSION_LOGIN_ID))
		//			throw new IllegalAccessException();

		Session session = null;
		MilestonesCollection list = null;
		try {
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Milestone.class);
			classes.add(MilestonesCollection.class);
			classes.add(Council.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(MilestonesCollection.class);
			Query query = queryManager.createQuery(filter);

			String path = "/vtk/" + councilCode + "/milestones";
			if(session.itemExists(path)){
				list = (MilestonesCollection)ocm.getObject(path);

			}else{
				if(!session.itemExists("/vtk/" + councilCode)){
					//create council, need user permission
				}
				list = new MilestonesCollection(path); 
				//				milestones = new ArrayList<Milestone>();
				//				milestones.add(new Milestone("Cookie Sales Start",true,null));
				//				milestones.add(new Milestone("Cookie Sales End",true,null));
				//				milestones.add(new Milestone("Re-Enroll Girls",true,null));
				List<Milestone> milestones = getAllMilestones(councilCode);
				//				Comparator<Milestone> comp = new BeanComparator("uid");
				//				Collections.sort(milestones, comp);
				list.setMilestones(milestones);
				ocm.insert(list);
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
		return list;
	}

	public void updateCouncilMilestones(java.util.List<Milestone> milestones, String cid) {

		//		if (user != null
		//				&& !userUtil.hasPermission(user.getPermissions(),
		//						Permission.PERMISSION_LOGIN_ID))
		//			throw new IllegalAccessException();
		Session session = null;
		try {
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Milestone.class);
			classes.add(MilestonesCollection.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);

			MilestonesCollection list = getMilestonesCollection(cid);
			java.util.List<Milestone> oldMilestones = list.getMilestones();
			Comparator<Milestone> comp = new BeanComparator("date");
			Collections.sort(oldMilestones, comp);


			//int removed =0;
			int i=0;
			for (; i < oldMilestones.size(); i++) {
				if(oldMilestones.get(i).getBlurb().equals(milestones.get(i).getBlurb())){
					oldMilestones.set(i,milestones.get(i));
					//ocm.update(oldMilestones.get(i));
					//removed++;
					break;
				}else{
					oldMilestones.remove(i);
					i--;
				}
				//					for(int j=0; j<=i-removed ; j++){
				//						if(oldMilestones.get(i).getBlurb().equals(milestones.get(j).getBlurb())){
				//							oldMilestones.set(i,milestones.get(j));
				//							ocm.update(oldMilestones.get(i));
				//							milestones.remove(j);
				//							removed++;
				//							break;
				//						}
				//					}
				//					removed++;
				//					ocm.remove(oldMilestones.get(i));
			}
			for(int k=i;k<milestones.size(); k++){
				//					milestones.get(k).setPath(path+"/"+milestones.get(k).getUid());
				//					ocm.insert(milestones.get(k));
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
		java.util.List<Milestone> milestones = null;
		Session session = null;
		try {
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Milestone.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(Milestone.class);
			filter.setScope(councilPath+"/en/milestones//");
			//filter.setScope("/content/girlscouts-vtk//");
			Query query = queryManager.createQuery(filter);
			milestones = (List<Milestone>)ocm.getObjects(query);
			Comparator<Milestone> comp = new BeanComparator("date");
			Collections.sort(milestones, comp);
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


}
