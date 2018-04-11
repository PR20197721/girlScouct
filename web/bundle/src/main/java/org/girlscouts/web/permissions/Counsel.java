package org.girlscouts.web.permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

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
	
	private UserManager userManager = null;
	private List<CounselFolder> counselFolders;
	private Optional<String> overrideDamDirectory;
	private Optional<Authorizable> reviewersGroup, authorsGroup;
	
	public Counsel(Session session, String counselName, Optional<List<CounselFolder>> folderList) {

		this.adminSession = session;
		this.name = counselName;
		
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
		setCounselFolders(folderList.orElseGet(() -> getDefaultFolderList(session)));
		
	}
	
	public Counsel(Node potentialNode, Session session) {
		this(session, getNodeName(potentialNode), Optional.empty());
	}
	
	private List<CounselFolder> getDefaultFolderList(Session session){
		List<CounselFolder> folders = new ArrayList<CounselFolder>(7);
		folders.add(new CounselFolder(DirectoryConfig.CONTENT, getContentDirectoryPath(), session, reviewersGroup, authorsGroup));
		folders.add(new CounselFolder(DirectoryConfig.DAM, getDamDirectoryPath(), session, reviewersGroup, authorsGroup));
		folders.add(new CounselFolder(DirectoryConfig.GROUP, getGroupDirectoryPath(), session, reviewersGroup, authorsGroup));
		folders.add(new CounselFolder(DirectoryConfig.DESIGNS, getDesignsDirectoryPath(), session, reviewersGroup, authorsGroup));
		folders.add(new CounselFolder(DirectoryConfig.SCAFFOLDING, getScaffoldingDirectoryPath(), session, reviewersGroup, authorsGroup));
		folders.add(new CounselFolder(DirectoryConfig.TAGS, getTagsDirectoryPath(), session, reviewersGroup, authorsGroup));
		folders.add(new CounselFolder(DirectoryConfig.VTK_RESOURCES, getVtkResourcesDirectoryPath(), session, reviewersGroup, authorsGroup));
		folders.add(new CounselFolder(DirectoryConfig.VTK_DAM, getVtkDamDirectoryPath(), session, reviewersGroup, authorsGroup));
		return folders;
	}
	
	private static String getNodeName(Node potentialNode) {
		try {
			return potentialNode.getName();
		} catch (RepositoryException e) {
			return "unknown";
		}
	}
	
	public String getVtkResourcesDirectoryPath() {
		return "/content/vtk-resources2/" + getName();
	}
	
	public String getVtkDamDirectoryPath() {
		return "/content/dam-resources2/girlscouts-" + getName();
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
	
	public String getGroupDirectoryPath() {
		return "/home/groups/" + getName();
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

	public Optional<String> getOverrideDamDirectory() {
		return overrideDamDirectory;
	}

	public void setOverrideDamDirectory(Optional<String> overrideDamDirectory) {
		this.overrideDamDirectory = overrideDamDirectory;
	}
	
	public void setCounselFolders(List<CounselFolder> counselFolders) {
		this.counselFolders = counselFolders;
	}
		
}
