package org.girlscouts.vtk.utils;


import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.dao.ActivityDAO;
import org.girlscouts.vtk.dao.TroopDAO;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Troop;

@Component
@Service(value = TroopUtil.class)
public class TroopUtil {

	@Reference
	TroopDAO troopDAO;

	@Reference
	ActivityDAO activityDAO;

	public Troop getTroop(String councilId, String troopId) {

		Troop troop = null;

		troop = troopDAO.getTroop(councilId, troopId);

		if (troop != null && troop.getYearPlan() != null
				&& troop.getYearPlan().getMeetingEvents() != null) {

			Comparator<MeetingE> comp = new BeanComparator("id");
			Collections.sort(troop.getYearPlan().getMeetingEvents(), comp);
		}

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
	
	
}
