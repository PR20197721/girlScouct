package org.girlscouts.vtk.ejb;

import javax.servlet.http.HttpServletRequest;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.dao.YearPlanDAO;
import org.girlscouts.vtk.helpers.CouncilMapper;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.utils.VtkUtil;

@Component
@Service(value = UserUtil.class)
public class UserUtil {

	@Reference
	YearPlanDAO yearPlanDAO;
	
	@Reference
	private CouncilMapper councilMapper;

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
	
	public String getCouncilUrlPath(ApiConfig apiConfig,  HttpServletRequest request){
		String redirectUrl =null;
		try {
			String councilId = Integer.toString(apiConfig.getTroops().get(0).getCouncilCode());
			if( councilId==null || councilId.trim().equals("") )
				redirectUrl = councilMapper.getCouncilUrl(VtkUtil.getCouncilInClient(request));
			else
				redirectUrl = councilMapper.getCouncilUrl(councilId);
		} catch (Exception e) {
		}
		return redirectUrl;
	}

}
