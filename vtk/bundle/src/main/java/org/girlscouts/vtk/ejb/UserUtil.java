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
		
		System.err.println( "Checking permissions: "+ permissionId +" : "+ myPermissionTokens);
		if (myPermissionTokens != null
				&& myPermissionTokens.contains(permissionId))
			return true;
		
		System.err.println( "No permission");
		return false;
	}

	public boolean hasPermission(Troop troop, int permissionId) {
		if (!hasPermission(troop.getTroop().getPermissionTokens(), permissionId))
			return false;
		return true;
	}

	public boolean hasAccess(Troop troop, String mySessionId, int permissionId) {
		System.err.println("HAS ACCESS?? checking perm...");
		if (!hasPermission(troop, permissionId))
			return false;

		System.err.print("OK. checking lock...");
		if (!isCurrentTroopId(troop, mySessionId))
			return false;

		System.err.print("ok");
		return true;
	}
	
	public boolean isCurrentTroopId(Troop troop, String sId) {
		
		java.util.Date lastUpdate = yearPlanDAO.getLastModifByOthers(troop, sId);
		if (lastUpdate != null && troop.getRetrieveTime()!=null && troop.getRetrieveTime().before(lastUpdate)) {
			troop.setRefresh(true);
			return false;
		}
		
		return true;
	}

	
	
}
