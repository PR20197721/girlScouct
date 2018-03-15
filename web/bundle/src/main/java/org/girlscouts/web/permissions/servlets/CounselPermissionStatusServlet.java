package org.girlscouts.web.permissions.servlets;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.girlscouts.web.permissions.CounselPermissionUpdate;

import com.day.cq.search.QueryBuilder;

@SlingServlet(paths="/bin/utils/counsel_permission_status", methods="GET")
@SuppressWarnings("serial")
public class CounselPermissionStatusServlet extends CounselPermissionActionServlet {
	
	@Reference
	private QueryBuilder queryBuilder;
	
	/*
	 * Empty update list as we're just getting the data back.
	 */
	@Override
	public List<CounselPermissionUpdate> getUpdates() {
		return new ArrayList<>();
	}

	@Override
	public QueryBuilder getInjectedQueryBuilder() {
		return queryBuilder;
	}
	
}
