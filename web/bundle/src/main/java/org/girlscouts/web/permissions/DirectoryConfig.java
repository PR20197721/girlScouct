package org.girlscouts.web.permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.girlscouts.web.permissions.CounselFolder.Permission;
import org.girlscouts.web.permissions.CounselFolder.Role;

public enum DirectoryConfig {
	
	CONTENT (
		new Permission[] {Permission.READ, Permission.LOCK, Permission.MODIFY, Permission.MODIFY_SELF, Permission.CREATE, Permission.DELETE, Permission.READ_ACL},
		new Permission[] {Permission.READ, Permission.LOCK, Permission.MODIFY, Permission.MODIFY_SELF, Permission.CREATE, Permission.DELETE, Permission.READ_ACL, Permission.REPLICATE}
	), 
	
	VTK_RESOURCES(
		new Permission[] {Permission.READ, Permission.LOCK, Permission.MODIFY, Permission.MODIFY_SELF, Permission.CREATE, Permission.DELETE, Permission.READ_ACL},
		new Permission[] {Permission.READ, Permission.LOCK, Permission.MODIFY, Permission.MODIFY_SELF, Permission.CREATE, Permission.DELETE, Permission.READ_ACL, Permission.REPLICATE}
	), 
	
	VTK_DAM(
		new Permission[] {Permission.READ, Permission.LOCK, Permission.MODIFY, Permission.MODIFY_SELF, Permission.CREATE, Permission.DELETE, Permission.REPLICATE},
		new Permission[] {Permission.READ, Permission.LOCK, Permission.MODIFY, Permission.MODIFY_SELF, Permission.CREATE, Permission.DELETE, Permission.REPLICATE}
	),
	
	DAM (
		new Permission[] {Permission.READ, Permission.LOCK, Permission.MODIFY, Permission.MODIFY_SELF, Permission.CREATE, Permission.DELETE, Permission.REPLICATE},
		new Permission[] {Permission.READ, Permission.LOCK, Permission.MODIFY, Permission.MODIFY_SELF, Permission.CREATE, Permission.DELETE, Permission.REPLICATE}
	), 
	
	DESIGNS(
		new Permission[] {Permission.READ},
		new Permission[] {Permission.READ}
	), 
	
	SCAFFOLDING(
		new Permission[] {Permission.READ},
		new Permission[] {Permission.READ}
	), 
	
	TAGS(
		new Permission[] {Permission.READ},
		new Permission[] {Permission.READ, Permission.LOCK, Permission.MODIFY, Permission.MODIFY_SELF, Permission.CREATE, Permission.DELETE, Permission.READ_ACL}
	), 
	
	GROUP(
		new Permission[] {Permission.READ},
		new Permission[] {Permission.READ}
	),
		
	GS_COMMON_FOLDERS(
		new Permission[] {Permission.READ},
		new Permission[] {Permission.READ}
	),
	
	GS_COMMON_AUTHORS(
		new Permission[] {Permission.READ},
		new Permission[] {}
	),
	
	GS_COMMON_REVIEWERS(
		new Permission[] {},
		new Permission[] {Permission.READ}
	);
	
	public final static List<String> GS_COMMON_FOLDER_LIST = Stream.of(
			"/content/dam/girlscouts-shared",
			"/content/dam/all_icons",
			"/etc/designs/girlscouts-usa-green",
			"/etc/designs/girlscouts-usa-blue",
			"/etc/designs/girlscouts",
			"/etc/workflow/models",
			
			// VTK
			"/content/vtkcontent/en/resources2"
			"/content/dam-resources2/girlscouts-vtkcontent"
			
		).collect(Collectors.toList());
	
	public final static String GS_COMMON_AUTHOR_GROUP_FOLDER = "/home/groups/girlscouts-usa/gs-authors";
	public final static String GS_COMMON_REVIEWER_GROUP_FOLDER = "/home/groups/girlscouts-usa/gs-reviewers";
	
	Permission[] reviewerRights, authorRights;
	
	DirectoryConfig(Permission[] authorRights, Permission[] reviewerRights){
		this.reviewerRights = reviewerRights;
		this.authorRights = authorRights;
	}
	
	public List<Permission> getRights(Role role){
		switch (role){
			case AUTHOR: return getAuthorRights();
			case REVIEWER: return getReviewerRights();
			default: return new ArrayList<Permission>(0);
		}
	}
	
	public List<Permission> getAuthorRights(){
		return Arrays.asList(authorRights);
	}
	
	public List<Permission> getReviewerRights(){
		return Arrays.asList(reviewerRights);
	}
	
}