package org.girlscouts.vtk.salesforce;

public class Campaign {

	private String name, Id, jobCode, parentCampaignId;

	public String getParentCampaignId() {
		return parentCampaignId;
	}

	public void setParentCampaignId(String parentCampaignId) {
		this.parentCampaignId = parentCampaignId;
	}

	public String getJobCode() {
		return jobCode;
	}

	public void setJobCode(String jobCode) {
		this.jobCode = jobCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

}
