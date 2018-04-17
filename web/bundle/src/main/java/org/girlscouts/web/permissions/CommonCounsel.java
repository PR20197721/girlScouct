package org.girlscouts.web.permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.jcr.Session;

public class CommonCounsel extends Counsel {
	
	public static final String REVIEWERS_NAME = "gs-reviewers";
	public static final String AUTHORS_NAME = "gs-authors";
	public static final String COUNSEL_NAME = "common-counsel";

	public CommonCounsel(Session session) {
		super(session, COUNSEL_NAME, Optional.of(new ArrayList<CounselFolder>()));
		
		// Set folders to the common list with the special groups.
		List<CounselFolder> commonCounselFolders = DirectoryConfig.GS_COMMON_FOLDER_LIST
				.stream()
				.map(cmnFldr -> new CounselFolder(DirectoryConfig.GS_COMMON_FOLDERS, cmnFldr, session, getReviewersGroup(), getAuthorsGroup()))
				.collect(Collectors.toList());
		
		// Group Folders
		commonCounselFolders.add(new CounselFolder(DirectoryConfig.GS_COMMON_AUTHORS, DirectoryConfig.GS_COMMON_AUTHOR_GROUP_FOLDER, session, getReviewersGroup(), getAuthorsGroup()));
		commonCounselFolders.add(new CounselFolder(DirectoryConfig.GS_COMMON_REVIEWERS, DirectoryConfig.GS_COMMON_REVIEWER_GROUP_FOLDER, session, getReviewersGroup(), getAuthorsGroup()));
		
		this.setCounselFolders(commonCounselFolders);
	}
	
	@Override
	public String getReviewersGroupName() {
		return REVIEWERS_NAME;
	}

	@Override
	public String getAuthorsGroupName() {
		return AUTHORS_NAME;
	}
	
}
