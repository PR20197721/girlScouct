package org.girlscouts.vtk.ejb;

import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.dao.ActivityDAO;
import org.girlscouts.vtk.dao.TroopDAO;
import org.girlscouts.vtk.dao.YearPlanDAO;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.YearPlan;

@Component
@Service(value = YearPlanUtil.class)
public class YearPlanUtil {

	@Reference
	TroopDAO troopDAO;

	@Reference
	ActivityDAO activityDAO;
	
	@Reference
	YearPlanDAO yearPlanDAO;

	public void createActivity(Troop user, Activity activity) throws java.lang.IllegalAccessException{

		activityDAO.createActivity(user, activity);
		user.getYearPlan().setAltered("true");
		troopDAO.updateTroop(user);
	}

	public void checkCanceledActivity(Troop user) throws java.lang.IllegalAccessException{

		if (user == null || user.getYearPlan() == null
				|| user.getYearPlan().getActivities() == null
				|| user.getYearPlan().getActivities().size() == 0)
			return;

		java.util.List<Activity> activity2Cancel = new java.util.ArrayList<Activity>();

		java.util.List<Activity> activities = user.getYearPlan()
				.getActivities();
		for (int i = 0; i < activities.size(); i++) {

			if (!(activities.get(i).getCancelled() != null && activities.get(i)
					.getCancelled().equals("true")))
				if (!activityDAO
						.isActivityByPath(activities.get(i).getRefUid())) {
					activities.get(i).setCancelled("true"); // org
					activity2Cancel.add(activities.get(i));
					troopDAO.updateTroop(user);
				}
		}

		for (Activity a : activity2Cancel)
			if (activities.contains(a))
				activities.remove(a);
	}

	
	public List<YearPlan> getAllYearPlans(String ageLevel){
		return yearPlanDAO.getAllYearPlans(ageLevel);
	}
	
	public YearPlan getYearPlan(String path){
		return  getYearPlan(path);
	}
}
