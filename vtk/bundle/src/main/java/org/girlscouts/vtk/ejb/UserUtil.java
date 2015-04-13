package org.girlscouts.vtk.ejb;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.dao.YearPlanDAO;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;

@Component
@Service(value = UserUtil.class)
public class UserUtil {

	@Reference
	YearPlanDAO yearPlanDAO;

	public boolean hasPermission(java.util.Set<Integer> myPermissionTokens,
			int permissionId) {

		if (myPermissionTokens != null
				&& myPermissionTokens.contains(permissionId))
			return true;

		return false;
	}

	public boolean hasPermission(Troop troop, int permissionId) {
		//if (true)
		//	return true;
		if (!hasPermission(troop.getTroop().getPermissionTokens(), permissionId))
			return false;
		return true;
	}

	public boolean isCurrentTroopId(Troop troop, String sId) {

		if (true)
			return true;

		java.util.Date lastUpdate = yearPlanDAO
				.getLastModifByOthers(troop, sId);
		if (lastUpdate != null && troop.getRetrieveTime() != null
				&& troop.getRetrieveTime().before(lastUpdate)) {
			troop.setRefresh(true);
			return false;
		}
		return true;
	}

	public boolean isCurrentTroopId_NoRefresh(Troop troop, String sId) {

		java.util.Date lastUpdate = yearPlanDAO
				.getLastModifByOthers(troop, sId);
		if (lastUpdate != null && troop.getRetrieveTime() != null
				&& troop.getRetrieveTime().before(lastUpdate)) {

			return false;
		}

		return true;
	}

}
