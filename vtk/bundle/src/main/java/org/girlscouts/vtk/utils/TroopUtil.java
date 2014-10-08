package org.girlscouts.vtk.utils;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;

import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.dao.ActivityDAO;
import org.girlscouts.vtk.dao.CouncilDAO;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.dao.TroopDAO;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Council;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Troop;

@Component
@Service(value = TroopUtil.class)
public class TroopUtil {

	@Reference
	TroopDAO troopDAO;

	@Reference
	ActivityDAO activityDAO;
	
	@Reference
	MeetingDAO meetingDAO;

	@Reference
	CouncilDAO councilDAO;
	
	public Troop getTroop(String councilId, String troopId) {

		Troop troop = null;
		troop = troopDAO.getTroop(councilId, troopId);
		if( troop ==null ) return troop;		
		if (troop != null && troop.getYearPlan() != null
				&& troop.getYearPlan().getMeetingEvents() != null) {

			Comparator<MeetingE> comp = new BeanComparator("id");
			Collections.sort(troop.getYearPlan().getMeetingEvents(), comp);
		}

		if (troop.getYearPlan() == null) return troop;
		java.util.List<Activity> activities = troop.getYearPlan()
				.getActivities();
		if (activities != null)
			for (int i = 0; i < activities.size(); i++) {
				if ((activities.get(i).getCancelled() == null || activities
						.get(i).getCancelled().equals("false"))
						&& !activities.get(i).getIsEditable()
						&& activities.get(i).getRefUid() != null) {

					Activity a = activityDAO.findActivity(activities.get(i)
							.getRefUid());
					if (a == null)
						activities.get(i).setCancelled("true");
					else
						activities.set(i, a);

				}
			}
		return troop;
	}
	
	
	public boolean isUpdated(Troop troop){			
			java.util.Date lastUpdate =meetingDAO.getLastModif(troop);
			if( lastUpdate !=null && troop.getRetrieveTime().before(lastUpdate) ){
				troop.setRefresh(true);
				return true;
			}					
			return false;	
	}
	
	public void autoLogin(HttpSession session){
        org.girlscouts.vtk.auth.models.ApiConfig config = new org.girlscouts.vtk.auth.models.ApiConfig();
        config.setId("test");
        config.setAccessToken("test");
        config.setInstanceUrl("test");
        config.setUserId("test_user");
        config.setUser(new org.girlscouts.vtk.auth.models.User() );

        java.util.List <org.girlscouts.vtk.salesforce.Troop > troops= new java.util.ArrayList();
        org.girlscouts.vtk.salesforce.Troop troop = new org.girlscouts.vtk.salesforce.Troop();
        troop.setCouncilCode(1);
        troop.setGradeLevel("1-Brownie");
        troop.setTroopId("test_troop_id");
        troop.setCouncilId("123");
        troop.setTroopName("test");
        

        troops.add(troop);
        config.setTroops(troops);

        session.setAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName(), config);
}
	
public Troop createTroop(String councilId, String troopId) {
		
		
		Troop troop = null;
		Council council = councilDAO.getOrCreateCouncil(councilId);
		if( council==null)
			return null;
		
		java.util.List<Troop> troops = council.getTroops();
		if (troops == null)
			troops = new java.util.ArrayList<Troop>();
		troop= new Troop(troopId);
		troops.add(troop);
		council.setTroops(troops);
		
		councilDAO.updateCouncil(council);
		return troop;
	}
	
}
