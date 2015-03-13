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
import org.girlscouts.vtk.models.SentEmail;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.CouncilInfo;


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
			classes.add(SentEmail.class);
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
			classes.add(SentEmail.class);
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

		//		String councilStr = councilMapper.getCouncilBranch(councilCode);
		//		councilStr = councilStr.replace("/content/", "");

		//		if (user != null
		//				&& !userUtil.hasPermission(user.getPermissions(),
		//						Permission.PERMISSION_LOGIN_ID))
		//			throw new IllegalAccessException();

		Session session = null;
		CouncilInfo list = null;
		try {
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Milestone.class);
			classes.add(CouncilInfo.class);
			//classes.add(Council.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(CouncilInfo.class);
			Query query = queryManager.createQuery(filter);

			String path = "/vtk/" + councilCode + "/councilInfo";
			if(session.itemExists(path)){
				list = (CouncilInfo)ocm.getObject(path);

			}else{
				if(!session.itemExists("/vtk/" + councilCode)){
					//create council, need user permission
				}
				list = new CouncilInfo(path); 
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
			classes.add(CouncilInfo.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);

			CouncilInfo list = getCouncilInfo(cid);
			java.util.List<Milestone> oldMilestones = list.getMilestones();
			sortMilestonesByDate(oldMilestones);

			int i=0;
			for (; i < oldMilestones.size(); i++) {
				if(milestones.size()>i && oldMilestones.get(i).getBlurb().equals(milestones.get(i).getBlurb())){
					oldMilestones.get(i).setDate(milestones.get(i).getDate());
					oldMilestones.get(i).setShow(milestones.get(i).getShow());
					continue;
				}else{
					oldMilestones.remove(i);
					i--;
				}
			}
			for(int k=i;k<milestones.size(); k++){
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
			if(session.itemExists(councilPath+"/en/milestones")){
				QueryManager queryManager = ocm.getQueryManager();
				Filter filter = queryManager.createFilter(Milestone.class);
				filter.setScope(councilPath+"/en/milestones//");
				//filter.setScope("/content/girlscouts-vtk//");
				Query query = queryManager.createQuery(filter);
				milestones = (List<Milestone>)ocm.getObjects(query);
				sortMilestonesByDate(milestones);
			}else{
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
	private void sortMilestonesByDate(List<Milestone> milestones){
		Comparator<Milestone> comp = new Comparator<Milestone>() {
			public int compare(Milestone m1, Milestone m2){
				if(m1.getDate()!=null){
					if(m2.getDate()!=null){
						return m1.getDate().compareTo(m2.getDate());
					}else{
						return -1;
					}
				}else if(m2.getDate()!=null){
					return 1;
				}else{
					return m1.getBlurb().compareTo(m2.getBlurb());
				}
			}
			
		};
		Collections.sort(milestones, comp);
	}


}
