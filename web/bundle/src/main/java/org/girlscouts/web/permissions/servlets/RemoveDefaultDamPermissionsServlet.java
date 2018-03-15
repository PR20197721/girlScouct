package org.girlscouts.web.permissions.servlets;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.girlscouts.web.permissions.CounselFolder;
import org.girlscouts.web.permissions.CounselPermissionUpdate;

import com.day.cq.search.QueryBuilder;

@SuppressWarnings("serial")
@SlingServlet(paths="/bin/utils/counsel_permission_remove_default_dam_permissions", methods="GET")
public class RemoveDefaultDamPermissionsServlet extends CounselPermissionActionServlet {

	@Reference
	private QueryBuilder queryBuilder;
	

	@Override
	public List<CounselPermissionUpdate> getUpdates(){
		List<CounselPermissionUpdate> returner = new ArrayList<>(2);
		returner.add((dto, tool) -> tool.removeRights(dto.getRequestedFolders()[0], CounselFolder.Role.AUTHOR, CounselFolder.Permission.READ));
		returner.add((dto, tool) -> tool.removeRights(dto.getRequestedFolders()[0], CounselFolder.Role.REVIEWER, CounselFolder.Permission.READ));
		return returner;
	}

	@Override
	public QueryBuilder getInjectedQueryBuilder() {
		return queryBuilder;
	}
	
}
