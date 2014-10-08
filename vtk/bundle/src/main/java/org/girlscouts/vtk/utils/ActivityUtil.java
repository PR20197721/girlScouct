package org.girlscouts.vtk.utils;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.dao.ActivityDAO;
import org.girlscouts.vtk.dao.TroopDAO;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Troop;

@Component
@Service(value = ActivityUtil.class)
public class ActivityUtil {

	@Reference
	TroopDAO troopDAO;

	@Reference
	ActivityDAO activityDAO;

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

}
