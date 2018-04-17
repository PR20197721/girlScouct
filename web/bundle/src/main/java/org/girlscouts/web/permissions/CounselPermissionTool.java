package org.girlscouts.web.permissions;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.commons.jackrabbit.authorization.AccessControlUtils;
import org.girlscouts.web.permissions.CounselFolder.Permission;
import org.girlscouts.web.permissions.CounselFolder.Role;
import org.girlscouts.web.permissions.dto.FolderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.QueryBuilder;

public class CounselPermissionTool {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CounselPermissionTool.class);
	
	private final Session adminSession;
	private final ContentCounselRetriever initialRetriever;
	private final Authorizable reviewerGroup, authorGroup;
	private final AccessControlManager accessControlManager;
	private final UserManager userManager;
	
	public CounselPermissionTool(Session adminSession, QueryBuilder queryBuilder, String reviewerGroupName, String authorGroupName) throws CounselPermissionModificationException {
		try {
			this.adminSession = adminSession;
			this.accessControlManager = adminSession.getAccessControlManager();
			this.userManager = ((JackrabbitSession) adminSession).getUserManager();
			this.reviewerGroup = userManager.getAuthorizable(reviewerGroupName);
			this.authorGroup = userManager.getAuthorizable(authorGroupName);
			this.initialRetriever = new ContentCounselRetriever(adminSession, queryBuilder);
		} catch (RepositoryException e) {
			LOGGER.error("Unable to get permission utilities!", e);
			throw new CounselPermissionModificationException("Bad Input");
		}
	}
	
	public void addMissingRights(boolean pathOverride, String requestedFolder, String counselName) throws CounselPermissionModificationException {
		List<Counsel> counselList = initialRetriever.getCounsels();
		CounselFolder targetFolder;
		if(pathOverride) {
			String targetFolderPath = initialRetriever.getAvailableDamFolders().stream().map(FolderDTO::getPath).filter(requestedFolder::equals).findFirst().orElse(null);
			targetFolder = new CounselFolder(DirectoryConfig.DAM, targetFolderPath, adminSession, Optional.of(reviewerGroup), Optional.of(authorGroup));
			
			// Mark the counsel now that the path has been overriden.
			markCounselDamOverridePath(counselList.stream().filter(counsel -> counsel.getName().equals(counselName)).findFirst().orElse(null), targetFolder.getPath(), adminSession);
		}else {
			targetFolder = counselList
					.stream()
					.filter(cnsl -> counselName.equals(cnsl.getName()))
					.map(Counsel::getCounselFolders)
					.flatMap(List::stream)
					.filter(fldr -> requestedFolder.equals(fldr.getPath()))
					.findFirst()
					.orElseThrow(() -> new CounselPermissionModificationException("Cant find requested folder!"));
		}
		
		for(Permission permission : targetFolder.getConfig().getAuthorRights()) {
			if(targetFolder.missingRights(Role.AUTHOR, permission)) {
				addRights(requestedFolder, Role.AUTHOR, permission);
			}
		}
		for(Permission permission : targetFolder.getConfig().getReviewerRights()) {
			if(targetFolder.missingRights(Role.REVIEWER, permission)) {
				addRights(requestedFolder, Role.REVIEWER, permission);
			}
		}
	}
	
	private static void markCounselDamOverridePath(Counsel counsel, String path, Session session) {
		try {
			Node counselConfigRootNode;
			try {
				counselConfigRootNode = session.getNode("/etc/counsel-config");
			} catch (RepositoryException e1) {
				Node etcNode = session.getNode("/etc");
				counselConfigRootNode = etcNode.addNode("counsel-config", "sling:OrderedFolder");
			}
			Node counselConfigNode;
			try {
				counselConfigNode = counselConfigRootNode.getNode(counsel.getName());
			} catch (RepositoryException e1) {
				counselConfigNode = counselConfigRootNode.addNode(counsel.getName(), "nt:unstructured");
			}
			
			counselConfigNode.setProperty("overrideDamDirectory", path);
			session.save();
		} catch (RepositoryException e) {
			LOGGER.error("Unable to set overrideDamDirectory", e);
		}
	}
	
	public void removeRights(String requestedFolder, Role role, Permission permission) throws CounselPermissionModificationException {
		try {
				AccessControlList acl;
				try {
					acl = AccessControlUtils.getAccessControlList(adminSession, requestedFolder);
				} catch (RepositoryException e) {
					acl = (AccessControlList) accessControlManager.getPolicies(requestedFolder)[0];
				}
								
				Principal principal = getPrincipal(role);
				List<AccessControlEntry> acesToRemove = new ArrayList<>();
				for(AccessControlEntry potentialAce : acl.getAccessControlEntries()) {
					if(potentialAce.getPrincipal().getName().equals(principal.getName())) {
						for(Privilege potentialPriv : potentialAce.getPrivileges()) {
							if(permission.getJcrPrivileges().contains(potentialPriv.getName())) {
								acesToRemove.add(potentialAce);
								break;
							}
						}
					}
				}
				for(AccessControlEntry ace : acesToRemove) {
					acl.removeAccessControlEntry(ace);
				}
				
				accessControlManager.setPolicy(requestedFolder, acl);
				adminSession.save();
		} catch (RepositoryException e) {
			throw new CounselPermissionModificationException("Unable to add permissions", e);
		}
	}	
	
	public void denyRights(String requestedFolder, Role role, Permission permission) throws CounselPermissionModificationException {
		try {			
			AccessControlUtils.deny(adminSession.getNode(requestedFolder), getPrincipal(role).getName(), (String[]) permission.getJcrPrivileges().toArray());
			adminSession.save();
		} catch (RepositoryException e) {
			throw new CounselPermissionModificationException("Unable to deny permissions", e);
		}
		
	}
	
	public void addRights(String requestedFolder, Role role, Permission permission) throws CounselPermissionModificationException {
		try {
			JackrabbitAccessControlList acl;
			try {
				acl = AccessControlUtils.getAccessControlList(adminSession, requestedFolder);
			} catch (RepositoryException e) {
				acl = (JackrabbitAccessControlList) accessControlManager.getPolicies(requestedFolder)[0];
			}
			for(String privName : permission.getJcrPrivileges()) {
				Privilege privArr = accessControlManager.privilegeFromName(privName);
				if(permission.getRestrictions() == null) {
					acl.addEntry(getPrincipal(role), new Privilege[] {privArr}, true);
				}else {
					Map<String, Value> privRestrictions = new HashMap<>();
					ValueFactory valFactory = adminSession.getValueFactory();
					for(Entry<String, String> restrictionEntry : permission.getRestrictions().entrySet()) {
						privRestrictions.put(restrictionEntry.getKey(), valFactory.createValue(restrictionEntry.getValue()));
					}
					acl.addEntry(getPrincipal(role), new Privilege[] {privArr}, true, privRestrictions);
				}
			}
			
			accessControlManager.setPolicy(requestedFolder, acl);
			adminSession.save();
		} catch (RepositoryException e) {
			throw new CounselPermissionModificationException("Unable to add permissions", e);
		}
	}
	
		
	public Principal getPrincipal(Role role) throws CounselPermissionModificationException {
		try {
			if(role == Role.AUTHOR) {
				return authorGroup.getPrincipal();
			}else if(role == Role.REVIEWER) {
				return reviewerGroup.getPrincipal();
			}else {
				return null;
			}
		} catch (RepositoryException e) {
			throw new CounselPermissionModificationException("Unable to get principal", e);
		}
	}
	
}
