package org.girlscouts.web.permissions.dto;

public class CounselPermissionUpdateDTO {

	private Boolean pathOverride;
	private String reviewerGroupName, authorGroupName, counselName;
	private String[] requestedFolders;
	private Boolean allowRoot;
	
	public Boolean getPathOverride() {
		return pathOverride;
	}
	
	public void setPathOverride(Boolean pathOverride) {
		this.pathOverride = pathOverride;
	}
	
	public String getReviewerGroupName() {
		return reviewerGroupName;
	}
	
	public void setReviewerGroupName(String reviewerGroupName) {
		this.reviewerGroupName = reviewerGroupName;
	}
	
	public String getAuthorGroupName() {
		return authorGroupName;
	}
	
	public void setAuthorGroupName(String authorGroupName) {
		this.authorGroupName = authorGroupName;
	}
	
	public String getCounselName() {
		return counselName;
	}
	
	public void setCounselName(String counselName) {
		this.counselName = counselName;
	}
	
	public String[] getRequestedFolders() {
		return requestedFolders;
	}
	
	public void setRequestedFolders(String[] requestedFolders) {
		this.requestedFolders = requestedFolders;
	}

	public Boolean getAllowRoot() {
		return allowRoot;
	}

	public void setAllowRoot(Boolean allowRoot) {
		this.allowRoot = allowRoot;
	}

}
