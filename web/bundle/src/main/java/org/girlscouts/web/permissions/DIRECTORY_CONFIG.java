package org.girlscouts.web.permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.girlscouts.web.permissions.CounselFolder.PERMISSION;
import org.girlscouts.web.permissions.CounselFolder.ROLE;

public enum DIRECTORY_CONFIG {
	
	CONTENT (
		new PERMISSION[] {PERMISSION.READ, PERMISSION.MODIFY, PERMISSION.CREATE, PERMISSION.DELETE},
		new PERMISSION[] {PERMISSION.READ, PERMISSION.MODIFY, PERMISSION.CREATE, PERMISSION.DELETE, PERMISSION.REPLICATE}
	), 
		
	DAM (
		new PERMISSION[] {PERMISSION.READ, PERMISSION.MODIFY, PERMISSION.CREATE, PERMISSION.DELETE},
		new PERMISSION[] {PERMISSION.READ, PERMISSION.MODIFY, PERMISSION.CREATE, PERMISSION.DELETE, PERMISSION.REPLICATE}
	), 
	
	DESIGNS(
		new PERMISSION[] {PERMISSION.READ},
		new PERMISSION[] {PERMISSION.READ}
	), 
	
	SCAFFOLDING(
		new PERMISSION[] {PERMISSION.READ},
		new PERMISSION[] {PERMISSION.READ}
	), 
	
	TAGS(
		new PERMISSION[] {PERMISSION.READ},
		new PERMISSION[] {PERMISSION.READ}
	), 
	
	GS_COMMON_FOLDERS(
		new PERMISSION[] {PERMISSION.READ},
		new PERMISSION[] {PERMISSION.READ}
	);
	
	public final static List<String> GS_COMMON_FOLDER_LIST = Stream.of(
			"/content/dam/girlscouts-shared",
			"/etc/designs/girlscouts-usa-green",
			"/etc/designs/girlscouts-usa-blue"
		).collect(Collectors.toList());
	
	PERMISSION[] reviewerRights, authorRights;
	
	DIRECTORY_CONFIG(PERMISSION[] authorRights, PERMISSION[] reviewerRights){
		this.reviewerRights = reviewerRights;
		this.authorRights = authorRights;
	}
	
	public List<PERMISSION> getRights(ROLE role){
		switch (role){
			case AUTHOR: return getAuthorRights();
			case REVIEWER: return getReviewerRights();
			default: return new ArrayList<PERMISSION>(0);
		}
	}
	
	public List<PERMISSION> getAuthorRights(){
		return Arrays.asList(authorRights);
	}
	
	public List<PERMISSION> getReviewerRights(){
		return Arrays.asList(reviewerRights);
	}
	
}