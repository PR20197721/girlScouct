package org.girlscouts.web.permissions;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.jcr.RepositoryException;

public class CounselDTO {
	
	private String name, reviewerName, authorName;
	
	private List<FolderDTO> folders;
	
	public CounselDTO(Counsel counsel) {
		
		// Basic attributes
		setName(counsel.getName());
		
		// Set group names if they exist.
		setReviewerName(counsel.getReviewersGroup().map(reviewerGroup -> {
			try {
				return reviewerGroup.getPrincipal();
			} catch (RepositoryException e) {
				return null;
			}
		}).map(Principal::getName).orElse("UNKNOWN"));

		setAuthorName(counsel.getAuthorsGroup().map(authorGroup -> {
			try {
				return authorGroup.getPrincipal();
			} catch (RepositoryException e) {
				return null;
			}
		}).map(Principal::getName).orElse("UNKNOWN"));
		
		// Set folders
		folders = new ArrayList<FolderDTO>(counsel.getCounselFolders().size());		
		counsel.getCounselFolders()
				.stream()
				.sorted(Comparator.comparing(CounselFolder::getPath))
				.map(fldr -> new FolderDTO(fldr, getReviewerName(), getAuthorName()))
				.forEachOrdered(folders::add);
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReviewerName() {
		return reviewerName;
	}

	public void setReviewerName(String reviewerName) {
		this.reviewerName = reviewerName;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public List<FolderDTO> getFolders() {
		return folders;
	}

	public void setFolders(List<FolderDTO> folders) {
		this.folders = folders;
	}
	
}
