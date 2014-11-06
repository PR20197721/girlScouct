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
		if (!hasPermission(troop.getTroop().getPermissionTokens(), permissionId))
			return false;
		return true;
	}

	/*
	public boolean hasAccess(Troop troop, String mySessionId, int permissionId) {
		
		if (!hasPermission(troop, permissionId))
			return false;

	
		if (!isCurrentTroopId(troop, mySessionId))
			return false;

		
		return true;
	}
	*/
	public boolean isCurrentTroopId(Troop troop, String sId) {
		
		//System.err.println("Checking yp modif...."+ sId);
		java.util.Date lastUpdate = yearPlanDAO.getLastModifByOthers(troop, sId);
		//System.err.println("LastModif db: "+ lastUpdate);
		//System.err.println("Yptime troop: "+troop.getRetrieveTime());
		if (lastUpdate != null && troop.getRetrieveTime()!=null && troop.getRetrieveTime().before(lastUpdate)) {
			troop.setRefresh(true);
	//System.err.println("YP UPDATED : YES");		
			return false;
		}
	//System.err.println("YP UPDATED : NO");	
		return true;
	}
	
public boolean isCurrentTroopId_NoRefresh(Troop troop, String sId) {
		
		//System.err.println("Checking yp modif...."+ sId);
		java.util.Date lastUpdate = yearPlanDAO.getLastModifByOthers(troop, sId);
		//.err.println("LastModif db: "+ lastUpdate);
		//System.err.println("Yptime troop: "+troop.getRetrieveTime());
		if (lastUpdate != null && troop.getRetrieveTime()!=null && troop.getRetrieveTime().before(lastUpdate)) {
			//troop.setRefresh(true);
	//System.err.println("YP UPDATED : YES");		
			return false;
		}
	//System.err.println("YP UPDATED : NO");	
		return true;
	}

	
	
}
