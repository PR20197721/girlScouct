package org.girlscouts.web.permissions.servlets;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.girlscouts.web.permissions.CounselPermissionUpdate;

import com.day.cq.search.QueryBuilder;

@SuppressWarnings("serial")
@SlingServlet(paths="/bin/utils/counsel_permission_update_add_missing_rights", methods="GET")
public class AddMissingRightsServlet extends CounselPermissionActionServlet {

	@Reference
	private QueryBuilder queryBuilder;
	

	@Override
	public List<CounselPermissionUpdate> getUpdates(){
		List<CounselPermissionUpdate> returner = new ArrayList<>(1);
		returner.add((dto, tool) -> tool.addMissingRights(dto.getPathOverride(), dto.getRequestedFolders()[0], dto.getCounselName()));
		return returner;
	}

	@Override
	public QueryBuilder getInjectedQueryBuilder() {
		return queryBuilder;
	}
	
}
