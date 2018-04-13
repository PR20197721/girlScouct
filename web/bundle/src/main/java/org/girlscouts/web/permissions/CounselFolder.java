package org.girlscouts.web.permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.Privilege;

import org.apache.jackrabbit.api.security.JackrabbitAccessControlEntry;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.commons.jackrabbit.authorization.AccessControlUtils;

public class CounselFolder {

	public enum Role { AUTHOR, REVIEWER }
	
	public enum Permission { 
		READ(new String[] {"jcr:read"}), 
		READ_SELF(new String[] {"jcr:read"}, getRestrictionMap("rep:glob", "\"\"")),
		LOCK(new String[] {"jcr:lockManagement"}), 
		MODIFY(new String[] {"jcr:lockManagement", "jcr:modifyProperties", "jcr:versionManagement"}), 
		MODIFY_SELF(new String[] {"jcr:addChildNodes", "jcr:nodeTypeManagement", "jcr:removeChildNodes", "jcr:removeNode"}, getRestrictionMap("rep:glob", "*/jcr:content*")), 
		CREATE(new String[] {"jcr:addChildNodes", "jcr:nodeTypeManagement"}), 
		DELETE(new String[] {"jcr:removeChildNodes", "jcr:removeNode"}), 
		READ_ACL(new String[] {"jcr:readAccessControl"}), 
		REPLICATE(new String[] {"crx:replicate"});
	
		private List<String> jcrPrivileges;
		private Map<String, String> restrictions = null;
		private static Map<String, String> getRestrictionMap(String name, String val){
			Map<String, String> returner = new HashMap<>(1);
			returner.put(name, val);
			return returner;
		}
		
		public List<String> getJcrPrivileges() {
			return jcrPrivileges;
		}
		
		Permission(String[] jcrPrivileges) {
			this.jcrPrivileges = Arrays.asList(jcrPrivileges);
		}
		
		Permission(String[] jcrPrivileges, Map<String, String> restrictions) {
			this(jcrPrivileges);
			this.restrictions = restrictions;
			
		}

		public Map<String, String> getRestrictions() {
			return restrictions;
		}
	
	}
	
	private final String path;
	private final DirectoryConfig config;
	private final Session adminSession;
	private final Optional<Authorizable> reviewerGroup;
	private final Optional<Authorizable> authorGroup;
	
	public CounselFolder(DirectoryConfig config, String path, Session adminSession, Optional<Authorizable> reviewerGroup, Optional<Authorizable> authorGroup) {
		this.config = config;
		this.path = path;
		this.adminSession = adminSession;
		this.reviewerGroup = reviewerGroup;		
		this.authorGroup = authorGroup;
	}
	
	public boolean hasAuthorPermissions() {
		for(Permission permission : config.getAuthorRights()) {
			if(missingRights(Role.AUTHOR, permission)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean hasReviewerPermissions() {
		for(Permission permission : config.getReviewerRights()) {
			if(missingRights(Role.REVIEWER, permission)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean missingRights(Role role, Permission permission) {
		List<Permission> requiredRights = config.getRights(role);
		
		// Check if right is required.
		if(!requiredRights.contains(permission)) {
			return false;
		}
		
		// Check if we have permission already.
		Optional<Authorizable> authorizable;
		if(role == Role.AUTHOR) {
			authorizable = authorGroup;
		}else if(role == Role.REVIEWER){
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
			JackrabbitAccessControlList acl = AccessControlUtils.getAccessControlList(adminSession, getPath());
			
			for(String privilegeName : permission.getJcrPrivileges()) {
				boolean privilegeMet = false;
				for(AccessControlEntry ace : acl.getAccessControlEntries()) {
					if(authorizableName.equals(ace.getPrincipal().getName())){
						if(isPermissionMet(privilegeName, permission, (JackrabbitAccessControlEntry) ace)) {
							privilegeMet = true;
							break;
						}
					}
				}
				if(!privilegeMet) {
					return true;
				}
			}
			return false;
			
		} catch (RepositoryException e) {
			return true;
		}
	}
	
	private boolean isPermissionMet(String privName, Permission permission, JackrabbitAccessControlEntry ace) throws RepositoryException {
		// Make sure all the restrictions are present.
		// Check if restriction names are the same.
		List<String> aceRestrictionNames = Optional.ofNullable(ace.getRestrictionNames()).map(Arrays::asList).orElseGet(ArrayList::new);
		List<String> permissionRestrictionNames = Optional.ofNullable(permission.getRestrictions()).map(Map::keySet).map(ArrayList::new).orElseGet(ArrayList::new);
		if(permissionRestrictionNames.size() != aceRestrictionNames.size()) {
			return false;
		}
		permissionRestrictionNames.removeAll(aceRestrictionNames);
		if(permissionRestrictionNames.size() > 0) {
			return false;
		}
		
		// Check the restriction values.
		if(permission.getRestrictions() != null) {
			for(Entry<String, String> restrictionMap : permission.getRestrictions().entrySet()) {
				Value restrictionValue = ace.getRestriction(restrictionMap.getKey());
				if(restrictionValue == null) {
					return false;
				}
				if(!restrictionMap.getValue().equals(restrictionValue.getString())) {
					return false;
				}
			}
		}

		// Flatten the privilege list and check to make sure they're all met.
		return Stream.of(ace.getPrivileges())
				.flatMap(priv -> getAggregatePriviliges(priv, new ArrayList<Privilege>()).stream())
				.map(Privilege::getName)
				.anyMatch(privName::equals);
	}
	
	// recursively aggregate privs
	private List<Privilege> getAggregatePriviliges(Privilege base, List<Privilege> collector){
		collector.add(base);
		List<Privilege> aggs = Optional.ofNullable(base.getAggregatePrivileges()).map(Stream::of).orElseGet(Stream::empty).collect(Collectors.toList());
		for(Privilege agg : aggs) {
			getAggregatePriviliges(agg, collector);
		}
		return collector;
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

	public DirectoryConfig getConfig() {
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
