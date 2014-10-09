package org.girlscouts.vtk.ejb;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.dao.YearPlanDAO;
import org.girlscouts.vtk.models.Troop;
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

	public boolean hasPermission(Troop user, int permissionId) {
		if (!hasPermission(user.getTroop().getPermissionTokens(), permissionId))
			return false;
		return true;
	}

	public boolean hasAccess(Troop user, String mySessionId, int permissionId) {
		if (!hasPermission(user, permissionId))
			return false;

		if (!isCurrentTroopId(user, mySessionId))
			return false;

		return true;
	}
	
	public boolean isCurrentTroopId(Troop troop, String sId) {
		
		java.util.Date lastUpdate = yearPlanDAO.getLastModif(troop);
		if (lastUpdate != null && troop.getRetrieveTime().before(lastUpdate)) {
			troop.setRefresh(true);
			return false;
		}
		
		return true;
	}

}
