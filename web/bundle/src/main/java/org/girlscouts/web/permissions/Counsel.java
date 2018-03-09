package org.girlscouts.web.permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;

/*
 * Group names are generated based on the naming convention:
 * 	{{name}}-reviewers
 * 	{{name}}-authors
 * 
 */
public class Counsel {

	private final String name;
	private final Session adminSession;
	private final Node node;
	
	private UserManager userManager = null;
	private final List<CounselFolder> counselFolders;
	private Optional<String> overrideDamDirectory;
	
	private Optional<Authorizable> reviewersGroup, authorsGroup;
	
	/*
	 * AccessControlList acl = AccessControlUtils.getAccessControlList(adminSession, BASE_COUNSEL_PATH)
	 */
	
	public Counsel(Node potentialNode, Session session) {
		// Base Info
		String counselName;
		try {
			counselName = potentialNode.getName();
		} catch (RepositoryException e) {
			counselName = "unknown";
		}
		this.adminSession = session;
		this.name = counselName;
		this.node = potentialNode;
		
		// Override DAM Directory
		try {
			Node counselConfigNode = session.getNode("/etc/counsel-config/" + getName());
			if(counselConfigNode != null && counselConfigNode.hasProperty("overrideDamDirectory")) {
				setOverrideDamDirectory(Optional.of(counselConfigNode.getProperty("overrideDamDirectory").getString()));
			}
		} catch (RepositoryException e1) {
			setOverrideDamDirectory(Optional.empty());
		}
		
		// Authorizable Groups
		try {
			reviewersGroup = Optional.ofNullable(getUserManager().getAuthorizable(getReviewersGroupName()));
		} catch (RepositoryException e) {
			reviewersGroup = Optional.empty();
		}
		try {
			authorsGroup = Optional.ofNullable(getUserManager().getAuthorizable(getAuthorsGroupName()));
		} catch (RepositoryException e) {
			authorsGroup = Optional.empty();
		}
		
		// Counsel Folders (with permission rules)
		List<CounselFolder> folders = new ArrayList<CounselFolder>(4);
		folders.add(new CounselFolder(DIRECTORY_CONFIG.CONTENT, getContentDirectoryPath(), session, reviewersGroup, authorsGroup));
		folders.add(new CounselFolder(DIRECTORY_CONFIG.DAM, getDamDirectoryPath(), session, reviewersGroup, authorsGroup));
		folders.add(new CounselFolder(DIRECTORY_CONFIG.DESIGNS, getDesignsDirectoryPath(), session, reviewersGroup, authorsGroup));
		folders.add(new CounselFolder(DIRECTORY_CONFIG.SCAFFOLDING, getScaffoldingDirectoryPath(), session, reviewersGroup, authorsGroup));
		folders.add(new CounselFolder(DIRECTORY_CONFIG.TAGS, getTagsDirectoryPath(), session, reviewersGroup, authorsGroup));
		
		// Add the common folders.
		DIRECTORY_CONFIG.GS_COMMON_FOLDER_LIST
			.stream()
			.map(cmnFldr -> new CounselFolder(DIRECTORY_CONFIG.GS_COMMON_FOLDERS, cmnFldr, session, reviewersGroup, authorsGroup))
			.forEach(folders::add);
		
		
		this.counselFolders = folders;
	}
	
	public boolean hasDamDirectory() {
		return hasDirectory(getDamDirectoryPath());
	}
	
	public boolean hasDesignsDirectory() {
		return hasDirectory(getDesignsDirectoryPath());
	}
	
	public boolean hasScaffoldingDirectory() {
		return hasDirectory(getScaffoldingDirectoryPath());
	}
	
	public boolean hasTagsDirectory() {
		return hasDirectory(getTagsDirectoryPath());
	}
	
	public boolean hasDirectory(String directory) {
		try {
			Node node = adminSession.getNode(directory);
			return node != null && node.getPrimaryNodeType().toString().equals(NodeType.NT_FOLDER);
		} catch (RepositoryException e) {
			return false;
		}
	}
	
	public String getContentDirectoryPath() {
		return "/content/" + getName();
	}
	
	public String getDamDirectoryPath() {
		return getOverrideDamDirectory().orElseGet(() -> "/content/dam/girlscouts-" + getName());
	}
	
	public String getDesignsDirectoryPath() {
		return "/etc/designs/girlscouts-" + getName();
	}
	
	public String getScaffoldingDirectoryPath() {
		return "/etc/scaffolding/" + getName();
	}
	
	public String getTagsDirectoryPath() {
		return "/etc/tags/" + getName();
	}
	
	public Optional<Authorizable> getReviewersGroup() {
		return reviewersGroup;
	}
	
	public Optional<Authorizable> getAuthorsGroup(){
		return authorsGroup;
	}
	
	private UserManager getUserManager() {
		if(userManager == null) {
			try {
				userManager = ((JackrabbitSession) adminSession).getUserManager();
			} catch (RepositoryException e) {
				return null;
			}
		}
		return userManager;
	}
	
	public String getName() {
		return name;
	}
	
	public String getReviewersGroupName() {
		return new StringBuilder(this.name).append("-reviewers").toString();
	}
	
	public String getAuthorsGroupName() {
		return new StringBuilder(this.name).append("-authors").toString();
	}

	public List<CounselFolder> getCounselFolders() {
		return counselFolders;
	}

	public Node getNode() {
		return node;
	}

	public Optional<String> getOverrideDamDirectory() {
		return overrideDamDirectory;
	}

	public void setOverrideDamDirectory(Optional<String> overrideDamDirectory) {
		this.overrideDamDirectory = overrideDamDirectory;
	}
		
}
