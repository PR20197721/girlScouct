package org.girlscouts.vtk.ejb;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.Session;

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
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.dao.CouncilDAO;
import org.girlscouts.vtk.models.Achievement;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.models.Attendance;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.models.Council;
import org.girlscouts.vtk.models.CouncilInfo;
import org.girlscouts.vtk.models.JcrNode;
import org.girlscouts.vtk.models.Location;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.models.SentEmail;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.YearPlan;
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
	
	@Reference
	CouncilRpt councilRpt;
	
	@Activate
	void activate() {
	}

	public Council findCouncil(User user, String councilId)
			throws IllegalAccessException, VtkException {
		Council council = null;
		Session session = null;
		try {
			//TODO Permission.PERMISSION_LOGIN_ID

			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Council.class);
			/*
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
			*/
			
			Mapper mapper = new AnnotationMapperImpl(classes);
			
			ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
			
String p= VtkUtil.getYearPlanBase(user, null) + councilId; 

			council = (Council) ocm.getObject(p);
			
			
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

		//TODO Permission.PERMISSION_LOGIN_ID
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
				
				permiss.modifyNodePermissions(vtkBase, "vtk");
				permiss.modifyNodePermissions("/content/dam/girlscouts-vtk/troop-data"+VtkUtil.getCurrentGSYear()+"/", "vtk");
			}
			
			
	
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

		//TODO Permission.PERMISSION_LOGIN_ID
		Council council = findCouncil(user, councilId);
	
		if (council == null){			
			council = createCouncil(user, councilId);
		}
		return council;
	}

	public void updateCouncil(User user, Council council)
			throws IllegalAccessException {
		//TODO Permission.PERMISSION_LOGIN_ID
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

	public java.util.List<Milestone> getCouncilMilestones(User user, String councilCode) 
			throws IllegalAccessException{
		//TODO Permission.PERMISSION_VIEW_MILESTONE_ID
		CouncilInfo list = getCouncilInfo(user, councilCode);
		java.util.List<Milestone> milestones = list.getMilestones();
		sortMilestonesByDate(milestones);
		return milestones;
	}

	// TODO: Alex - deprecate this method after full testing of method removal
    private CouncilInfo getCouncilInfo(String councilCode) {
		return getCouncilInfo(null, councilCode);
	}

	private CouncilInfo getCouncilInfo(User user, String councilCode) {

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

			String path = VtkUtil.getYearPlanBase(user, null) + councilCode + "/councilInfo";
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

	public void updateCouncilMilestones(User user, java.util.List<Milestone> milestones, String cid)
			throws IllegalAccessException{
		
		//TODO: permissions here Permission.PERMISSION_EDIT_MILESTONE_ID
		Session session = null;
		try {
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Milestone.class);
			classes.add(CouncilInfo.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
			CouncilInfo list = getCouncilInfo(user, cid);
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



	private java.util.List<Milestone> getAllMilestones(String councilCode) {
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

				String newGSYearDateString = "";
				try {
    				newGSYearDateString = VtkUtil.getNewGSYearDateString();
    				DateFormat format = new SimpleDateFormat("MMdd");
    				// TODO: use joda time
    				Date newGSYearDate = format.parse(newGSYearDateString);
    				newGSYearDate.setYear(new Date().getYear());

    				Date startDate, endDate;
    				if (new Date().compareTo(newGSYearDate) >= 0) {
    				    // New troop year has started for this calendar year.
    				    // Current school year is this year to next year.
    				    startDate = newGSYearDate;
    				    endDate = new Date(newGSYearDate.getTime());
    				    endDate.setYear(new Date().getYear() + 1);
    				} else {
    				    // New troop year has NOT started for this calendar year.
    				    // Current school year is last year to this year.
    				    endDate = newGSYearDate;
    				    startDate = new Date(newGSYearDate.getTime());
    				    startDate.setYear(new Date().getYear() - 1);
    				}

    				List<Milestone> filteredMilestones = new ArrayList<Milestone>();
				    for (Milestone milestone : milestones) {
				        if (milestone.getDate().before(endDate) && milestone.getDate().compareTo(startDate) >= 0) {
				            filteredMilestones.add(milestone);
				        }
				    }
				    milestones = filteredMilestones;
				} catch (ParseException pe) {
				    log.warn("ParseException MMdd for new year " + newGSYearDateString + "\n" +
				            "No milestones have been filtered.");
				}
				
				sortMilestonesByDate(milestones);
			} else {
				log.error(councilPath + "/en/milestones does NOT exist!");
				return milestones;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null) {
					sessionFactory.closeSession(session);
				}
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
	
	public void GSMonthlyRpt(){
		Session session= null;
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat format1 = new SimpleDateFormat("MM-dd-yyyy");
		Set<String> allowedReportUsers = new HashSet<String>();
		allowedReportUsers.add("005g0000002apMT");
		allowedReportUsers.add("005G0000006oEkZ");
		allowedReportUsers.add("005G0000006oBVG");
		allowedReportUsers.add("005g0000002G004");
		boolean isHtml= false;
		
		 java.util.Map<String, String> cTrans = new java.util.TreeMap();     
	        cTrans.put("597", "Girl Scouts of Northeast Texas"); 
	        cTrans.put("477", "Girl Scouts of Minnesota and Wisconsin River Valleys, Inc.");
	        cTrans.put("465", "Girl Scouts of Southeastern Michigan"); 
	        cTrans.put("367", "Girl Scouts - North Carolina Coastal Pines, Inc.");
	        cTrans.put("320", "Girl Scouts of West Central Florida, Inc.");
	        cTrans.put("388", "Girl Scout Council of the Southern Appalachians, Inc.");
	        cTrans.put("313", "Girl Scouts of Gateway Council, Inc.");
	        cTrans.put("664", "Oregon and SW Washington");
	        cTrans.put("234", "North East Ohio");
	        cTrans.put("661", "Sierra Nevada");
	        cTrans.put("664", "Oregon & SW Wash");
	        cTrans.put("240", "Western Ohio");
	        cTrans.put("607", "Arizona Cactus Pine");
	        cTrans.put("536", "Kansas Heartland");
	        cTrans.put("563", "Western Oklahoma");
	        cTrans.put("564", "Eastern Oklahoma");
	        cTrans.put("591", "San Jacinto");
	        cTrans.put("636", "Northern CA");
	        cTrans.put("512", "Colorado");
	        cTrans.put("313", "Gateway");
	        cTrans.put("212", "Kentucky Wilderness Road");
	        cTrans.put("623", "San Diego");
	        cTrans.put("131", "Central & Southern NJ");
	        cTrans.put("263", "Western PA");
	        cTrans.put("467", "Wisconsin Badgerland");
	        cTrans.put("116", "Central & Western Mass");
	        cTrans.put("622", "Orange County");
	        cTrans.put("660", "Southern Nevada");
	        cTrans.put("514", "Eastern IA & Western IL");
	        cTrans.put("524", "Greater Iowa");
	        cTrans.put("430", "Greater Chicago and NW  Indiana");
	        
	        cTrans.put("578", "Central Texas");
	        cTrans.put("208", "Kentuckiana");
	        cTrans.put("700", "USA Girl Scouts Overseas");
	        cTrans.put("204", "Nation's Capital");
	        cTrans.put("674", "Utah");
	        cTrans.put("258", "Heart of Pennsylvania");
	        cTrans.put("333", "Greater Atlanta");
	        cTrans.put("135", "Heart of New Jersey");
	        cTrans.put("289", "Black Diamond");
	        cTrans.put("155", "Heart of the Hudson");
	        cTrans.put("325", "Historic Georgia");
	        cTrans.put("608", "Southern Arizona");
	        cTrans.put("312", "Citrus");
	        cTrans.put("169", "NYPENN Pathways");
	        cTrans.put("596", "Greater South Texas");
	        cTrans.put("583", "Texas Oklahoma Plains");
	        cTrans.put("688", "Western Washington");
	        cTrans.put("590", "Southwest Texas");
	        cTrans.put("634", "Heart of Central California");
	        cTrans.put("376", "Eastern South Carolina");
	        cTrans.put("346", "Louisiana East");
	        cTrans.put("117", "Eastern Massachusetts");
	        cTrans.put("654", "Montana and Wyoming");
	        cTrans.put("134", "Jersey Shore");
	        cTrans.put("415", "Northern Illinois");
	        cTrans.put("557", "New Mexico Trails");
	        cTrans.put("110", "Maine");
	        cTrans.put("126", "Green and White Mountains");
	        cTrans.put("687", "Eastern Washington and Northern Idaho");
	        cTrans.put("441", "Southwest Indiana");
	        cTrans.put("238", "Ohio's Heartland");
	        
	        
		StringBuffer sb= new StringBuffer();
		try{
			session = sessionFactory.getSession();
		    sb.append("Council Report generated on " + format1.format(new java.util.Date())+ " \n ");
		        String limitRptToCouncil= null;
		        limitRptToCouncil= limitRptToCouncil==null ? "" :  limitRptToCouncil.trim() ;
		      
		        java.util.HashSet<String> ageGroups = new java.util.HashSet<String>();
		      
		        String sql="select  sfTroopName,sfTroopAge,jcr:path, sfTroopId,sfCouncil,excerpt(.) from nt:base where jcr:path like '"+VtkUtil.getYearPlanBase(null, null)+""+ (limitRptToCouncil.equals("") ? "" : (limitRptToCouncil+"/") ) + "%' and ocm_classname= 'org.girlscouts.vtk.models.Troop'";        
		      
		        javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
		        javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL); 
		        java.util.Map container= new java.util.TreeMap();
		        javax.jcr.query.QueryResult result = q.execute();
		        for (javax.jcr.query.RowIterator it = result.getRows(); it.hasNext(); ) {
		            javax.jcr.query.Row r = it.nextRow();
		            String path = r.getValue("jcr:path").getString() ;
		            String sfCouncil = null, sfTroopAge=null;
		            try{ sfCouncil =r.getValue("sfCouncil").getString() ;}catch(Exception e){}          
		            try{
		                sfTroopAge= r.getValue("sfTroopAge").getString(); 
		                if(!sfTroopAge.toUpperCase().equals("7-MULTI-LEVEL") && !sfTroopAge.equals("2-Brownie") && !sfTroopAge.equals("3-Junior") && !sfTroopAge.equals("1-Daisy")){
		                    continue;
		                    }
		            }catch(Exception e){}
		            Integer counter = (Integer)container.get( sfCouncil+"|"+sfTroopAge );
		            if( counter ==null )
		                container.put(sfCouncil+"|"+sfTroopAge , new Integer(0));
		            else
		                container.put(sfCouncil+"|"+sfTroopAge , new Integer( counter.intValue() +1 ) );
		        }
		        
		        java.util.Iterator itr = container.keySet().iterator();
		        while( itr.hasNext() ){
		           String key = (String) itr.next();
		           String councilId= key.substring(0, key.indexOf("|"));
		           String ageGroup = key.substring(key.indexOf("|") +1 );
		           Integer count= (Integer) container.get(key);
		           sb.append( (isHtml ? "<br/>" : "\n") + "\"" +cTrans.get(councilId)+"\","+ councilId +"," + ageGroup+ "," + count );          
		          
		         }
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		try {
			if (session != null) {
				sessionFactory.closeSession(session);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
		//save to db
		String rptId= councilRpt.saveRpt( sb );

		//email rpt
		councilRpt.emailRpt( sb.toString(), "GS Monthly Report" ); //"/vtk"+VtkUtil.getCurrentGSYear()+"/rpt/"+ rptId);
	}
	
	
	public void GSMonthlyDetailedRpt(){
		
				
		Session session= null;
		Set<String> allowedReportUsers = new HashSet<String>();
		allowedReportUsers.add("005g0000002apMT");
		allowedReportUsers.add("005G0000006oEkZ");
		allowedReportUsers.add("005G0000006oBVG");
		allowedReportUsers.add("005g0000002G004");

		allowedReportUsers.add("005G0000006oEjsIAE");
		StringBuffer sb= new StringBuffer();
		
		        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		        SimpleDateFormat format1 = new SimpleDateFormat("MM-dd-yyyy");
		        sb.append("Council Report generated on " + format1.format(new java.util.Date())+ " \n");
		        
		        java.util.Map<String, String> cTrans = new java.util.TreeMap();     
		        cTrans.put("597", "Girl Scouts of Northeast Texas"); 
		        cTrans.put("477", "Girl Scouts of Minnesota and Wisconsin River Valleys Inc.");
		        cTrans.put("465", "Girl Scouts of Southeastern Michigan"); 
		        cTrans.put("367", "Girl Scouts - North Carolina Coastal Pines Inc.");
		        cTrans.put("320", "Girl Scouts of West Central Florida Inc.");
		        cTrans.put("388", "Girl Scout Council of the Southern Appalachians Inc.");
		        cTrans.put("313", "Girl Scouts of Gateway Council Inc.");
		        cTrans.put("664", "Oregon and SW Washington");
		        cTrans.put("234", "North East Ohio");
		        cTrans.put("661", "Sierra Nevada");
		        cTrans.put("664", "Oregon & SW Wash");
		        cTrans.put("240", "Western Ohio");
		        cTrans.put("607", "Arizona Cactus Pine");
		        cTrans.put("536", "Kansas Heartland");
		        cTrans.put("563", "Western Oklahoma");
		        cTrans.put("564", "Eastern Oklahoma");
		        cTrans.put("591", "San Jacinto");
		        cTrans.put("636", "Northern CA");
		        cTrans.put("512", "Colorado");
		        cTrans.put("313", "Gateway");
		        cTrans.put("212", "Kentucky Wilderness Road");
		        cTrans.put("623", "San Diego");
		        cTrans.put("131", "Central & Southern NJ");
		        cTrans.put("263", "Western PA");
		        cTrans.put("467", "Wisconsin Badgerland");
		        cTrans.put("116", "Central & Western Mass");
		        cTrans.put("622", "Orange County");
		        cTrans.put("660", "Southern Nevada");
		        cTrans.put("514", "Eastern IA & Western IL");
		        cTrans.put("524", "Greater Iowa");
		        cTrans.put("430", "Greater Chicago and NW  Indiana");
		        
		        cTrans.put("578", "Central Texas");
		        cTrans.put("208", "Kentuckiana");
		        cTrans.put("700", "USA Girl Scouts Overseas");
		        cTrans.put("204", "Nation's Capital");
		        cTrans.put("674", "Utah");
		        cTrans.put("258", "Heart of Pennsylvania");
		        cTrans.put("333", "Greater Atlanta");
		        cTrans.put("135", "Heart of New Jersey");
		        cTrans.put("289", "Black Diamond");
		        cTrans.put("155", "Heart of the Hudson");
		        cTrans.put("325", "Historic Georgia");
		        cTrans.put("608", "Southern Arizona");
		        cTrans.put("312", "Citrus");
		        cTrans.put("169", "NYPENN Pathways");
		        cTrans.put("596", "Greater South Texas");
		        cTrans.put("583", "Texas Oklahoma Plains");
		        cTrans.put("688", "Western Washington");
		        cTrans.put("590", "Southwest Texas");
		        cTrans.put("634", "Heart of Central California");
		        cTrans.put("376", "Eastern South Carolina");
		        cTrans.put("346", "Louisiana East");
		        cTrans.put("117", "Eastern Massachusetts");
		        cTrans.put("654", "Montana and Wyoming");
		        cTrans.put("134", "Jersey Shore");
		        cTrans.put("415", "Northern Illinois");
		        cTrans.put("557", "New Mexico Trails");
		        cTrans.put("110", "Maine");
		        cTrans.put("126", "Green and White Mountains");
		        cTrans.put("687", "Eastern Washington and Northern Idaho");
		        cTrans.put("441", "Southwest Indiana");
		        cTrans.put("238", "Ohio's Heartland");
		        
		        String limitRptToCouncil= null;//request.getParameter("limitRptToCouncil");
		        limitRptToCouncil= limitRptToCouncil==null ? "" :  limitRptToCouncil.trim() ;
		      
		        java.util.HashSet<String> ageGroups = new java.util.HashSet<String>();
		        //javax.jcr.Session s= (slingRequest.getResourceResolver().adaptTo(Session.class));
		        String sql="select  sfTroopName,sfTroopAge,jcr:path, sfTroopId,sfCouncil,excerpt(.) from nt:base where jcr:path like '"+VtkUtil.getYearPlanBase(null, null)+""+ (limitRptToCouncil.equals("") ? "" : (limitRptToCouncil+"/") ) + "%' and ocm_classname= 'org.girlscouts.vtk.models.Troop'";        
		
		     try{
   		        session = sessionFactory.getSession();
		        javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
		        javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL); 
		        java.util.Map container= new java.util.TreeMap();
		        javax.jcr.query.QueryResult result = q.execute();
		     
		        for (javax.jcr.query.RowIterator it = result.getRows(); it.hasNext(); ) {
		            javax.jcr.query.Row r = it.nextRow();

		            javax.jcr.Node n = r.getNode();
		            
		            javax.jcr.Node n1 = n.getNode("yearPlan");
		            String yearPlanName=n1.getProperty("name").getValue().getString();
		               
		            String path = r.getValue("jcr:path").getString() ;
		            String sfCouncil = null, sfTroopAge=null, sfTroopName=null, sfTroopId=null;
		            
		            try{ sfTroopId   = r.getValue("sfTroopId").getString() ;}catch(Exception e){}
		            try{ sfTroopName = r.getValue("sfTroopName").getString() ;}catch(Exception e){}
		            try{ sfCouncil   = r.getValue("sfCouncil").getString() ;}catch(Exception e){}          
		            try{
		                sfTroopAge= r.getValue("sfTroopAge").getString(); 
		                if(!sfTroopAge.toUpperCase().equals("7-MULTI-LEVEL") && !sfTroopAge.equals("2-Brownie") && !sfTroopAge.equals("3-Junior") && !sfTroopAge.equals("1-Daisy")){
		                    continue;
		                    }
		            }catch(Exception e){}
		            
		            
		            
		          //  out.println( (isHtml ? "<br/>" : "\n") + "\"" +cTrans.get(sfCouncil)+"\","+ sfCouncil +"," + sfTroopAge+ "," + yearPlanName  +","+ sfTroopId + ","+sfTroopName );          
		            sb.append(  "\n \""+cTrans.get(sfCouncil)+"\","+ sfCouncil +"," + sfTroopAge+ ",\"" + yearPlanName  +"\","+ sfTroopId + ","+sfTroopName );          
		           
		            
		           
		            
		           
		        }
		     } catch (Exception e) {
		 		e.printStackTrace();
		 	} finally {
		 		try {
		 			if (session != null) {
		 				sessionFactory.closeSession(session);
		 			}
		 		} catch (Exception ex) {
		 			ex.printStackTrace();
		 		}
		 	}
		
				//email rpt
				councilRpt.emailRpt( sb.toString(), "GS Detailed Report" ); 
			
				
	}
}
