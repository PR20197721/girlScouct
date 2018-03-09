package org.girlscouts.web.permissions;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.Privilege;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.commons.jackrabbit.authorization.AccessControlUtils;

public class CounselFolder {

	public enum ROLE { AUTHOR, REVIEWER }
	
	public enum PERMISSION { 
		READ("jcr:read"), 
		MODIFY("rep:write"), 
		CREATE("jcr:addChildNodes"), 
		DELETE("jcr:removeNode"), 
		REPLICATE("crx:replicate");
	
		private String jcrPrivilege;
		
		public String getJcrPrivilege() {
			return jcrPrivilege;
		}
		
		PERMISSION(String jcrPrivilege) {
			this.jcrPrivilege = jcrPrivilege;
		}
	
	}
	
	private final String path;
	private final DIRECTORY_CONFIG config;
	private final Session adminSession;
	private final Optional<Authorizable> reviewerGroup;
	private final Optional<Authorizable> authorGroup;
	
	public CounselFolder(DIRECTORY_CONFIG config, String path, Session adminSession, Optional<Authorizable> reviewerGroup, Optional<Authorizable> authorGroup) {
		this.config = config;
		this.path = path;
		this.adminSession = adminSession;
		this.reviewerGroup = reviewerGroup;		
		this.authorGroup = authorGroup;
	}
	
	public boolean hasAuthorPermissions() {
		for(PERMISSION permission : config.getAuthorRights()) {
			if(missingRights(ROLE.AUTHOR, permission)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean hasReviewerPermissions() {
		for(PERMISSION permission : config.getReviewerRights()) {
			if(missingRights(ROLE.REVIEWER, permission)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean missingRights(ROLE role, PERMISSION permission) {
		List<PERMISSION> requiredRights = config.getRights(role);
		
		// Check if right is required.
		if(!requiredRights.contains(permission)) {
			return false;
		}
		
		// Check if we have permission already.
		Optional<Authorizable> authorizable;
		if(role == ROLE.AUTHOR) {
			authorizable = authorGroup;
		}else if(role == ROLE.REVIEWER){
			authorizable = reviewerGroup;
		}else {
			// Don't even have the role?
			return true;
		}
		
		if(!authorizable.isPresent()) {
			// Role hasn't been created.
			return true;
		}
		
		try {
			String authorizableName = authorizable.get().getPrincipal().getName();
			AccessControlList acl = AccessControlUtils.getAccessControlList(adminSession, getPath());
			for(AccessControlEntry ace : acl.getAccessControlEntries()) {
				if(authorizableName.equals(ace.getPrincipal().getName())){
					for(Privilege privilege : ace.getPrivileges()) {
						// Check the privilege is assigned.
						if(privilege.getName().equals(permission.getJcrPrivilege())) {
							return false;
						}
						
						// Check if any of the aggregate privileges are assigned.
						if(Stream.of(privilege.getAggregatePrivileges()).map(Privilege::getName).anyMatch(permission.getJcrPrivilege()::equals)) {
							return false;
						}
					}
				}
			}
		} catch (RepositoryException e) {
			return true;
		}

		return true;
	}
	
	public boolean exists() {
		try {
			Node node = getAdminSession().getNode(getPath());
			return node != null && node.isNode();
		} catch (RepositoryException e) {
			return false;
		}
	}

	public String getPath() {
		return path;
	}

	public DIRECTORY_CONFIG getConfig() {
		return config;
	}

	public Session getAdminSession() {
		return adminSession;
	}

	public Optional<Authorizable> getReviewerGroup() {
		return reviewerGroup;
	}

	public Optional<Authorizable> getAuthorGroup() {
		return authorGroup;
	}
	
}
