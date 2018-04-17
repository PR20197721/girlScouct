package org.girlscouts.web.permissions.dto;

import org.girlscouts.web.permissions.CounselFolder;

public class FolderDTO {

	private String path, reviewerName, authorName;
	
	private boolean exists, correctAuthorPermissions, correctReviewerPermissions;

	public FolderDTO() {
		// Override GSON stuff if needed.
	}

	public FolderDTO(CounselFolder folder, String reviewerName, String authorName) {
		
		// Basic attributes
		setPath(folder.getPath());
		setReviewerName(reviewerName);
		setAuthorName(authorName);
		setExists(folder.exists());
		
		// Permissions
		setCorrectAuthorPermissions(folder.hasAuthorPermissions());
		setCorrectReviewerPermissions(folder.hasReviewerPermissions());
		
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
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

	public boolean isExists() {
		return exists;
	}

	public void setExists(boolean exists) {
		this.exists = exists;
	}

	public boolean isCorrectAuthorPermissions() {
		return correctAuthorPermissions;
	}

	public void setCorrectAuthorPermissions(boolean correctAuthorPermissions) {
		this.correctAuthorPermissions = correctAuthorPermissions;
	}

	public boolean isCorrectReviewerPermissions() {
		return correctReviewerPermissions;
	}

	public void setCorrectReviewerPermissions(boolean correctReviewerPermissions) {
		this.correctReviewerPermissions = correctReviewerPermissions;
	}
	
}
