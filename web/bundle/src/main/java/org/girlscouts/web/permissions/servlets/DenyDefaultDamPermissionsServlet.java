package org.girlscouts.web.permissions.servlets;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.girlscouts.web.permissions.CounselFolder;
import org.girlscouts.web.permissions.CounselPermissionUpdate;

import com.day.cq.search.QueryBuilder;

@SuppressWarnings("serial")
@SlingServlet(paths="/bin/utils/counsel_permission_update_deny_default_dam_permissions", methods="GET")
public class DenyDefaultDamPermissionsServlet extends CounselPermissionActionServlet {
	
	@Reference
	private QueryBuilder queryBuilder;

	@Override
	public List<CounselPermissionUpdate> getUpdates(){
		List<CounselPermissionUpdate> returner = new ArrayList<>(4);
		returner.add((dto, tool) -> tool.denyRights(dto.getRequestedFolders()[0], CounselFolder.Role.AUTHOR, CounselFolder.Permission.READ));
		returner.add((dto, tool) -> tool.denyRights(dto.getRequestedFolders()[0], CounselFolder.Role.REVIEWER, CounselFolder.Permission.READ));
		returner.add((dto, tool) -> {
			if(dto.getAllowRoot()) {
				tool.addRights(dto.getRequestedFolders()[0], CounselFolder.Role.REVIEWER, CounselFolder.Permission.READ_SELF);
			}
		});
		returner.add((dto, tool) -> {
			if(dto.getAllowRoot()) {
				tool.addRights(dto.getRequestedFolders()[0], CounselFolder.Role.AUTHOR, CounselFolder.Permission.READ_SELF);
			}
		});
		return returner;
	}

	@Override
	public QueryBuilder getInjectedQueryBuilder() {
		return queryBuilder;
	}
	
}
