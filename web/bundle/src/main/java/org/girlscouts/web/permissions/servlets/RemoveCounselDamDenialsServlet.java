package org.girlscouts.web.permissions.servlets;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.girlscouts.web.permissions.CounselFolder;
import org.girlscouts.web.permissions.CounselPermissionUpdate;

import com.day.cq.search.QueryBuilder;

@SuppressWarnings("serial")
@SlingServlet(paths="/bin/utils/counsel_permission_update_remove_counsel_dam_denial_nodes", methods="GET")
public class RemoveCounselDamDenialsServlet extends CounselPermissionActionServlet {

	@Reference
	private QueryBuilder queryBuilder;
	
	@Override
	public List<CounselPermissionUpdate> getUpdates(){
		List<CounselPermissionUpdate> returner = new ArrayList<>(1);
		returner.add((dto, tool) -> {
			for(String counselDamFolder: dto.getRequestedFolders()) {
				tool.removeRights(counselDamFolder, CounselFolder.Role.AUTHOR, CounselFolder.Permission.READ);
				tool.removeRights(counselDamFolder, CounselFolder.Role.REVIEWER, CounselFolder.Permission.READ);
			}
		});
		return returner;
	}

	@Override
	public QueryBuilder getInjectedQueryBuilder() {
		return queryBuilder;
	}
	
}
