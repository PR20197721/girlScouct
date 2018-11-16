package org.girlscouts.vtk.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MeetingSearch {

	private int year;
	private java.util.List<String> level;
	private java.util.List<String> categoryTags;
	private String keywords;
	private String meetingPlanType;
	
	
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public java.util.List<String> getLevel() {
		return level;
	}
	public void setLevel(java.util.List<String> level) {
		this.level = level;
	}
	public java.util.List<String> getCategoryTags() {
		return categoryTags;
	}
	public void setCategoryTags(java.util.List<String> categoryTags) {
		this.categoryTags = categoryTags;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getMeetingPlanType() {
		return meetingPlanType;
	}
	public void setMeetingPlanType(String meetingPlanType) {
		this.meetingPlanType = meetingPlanType;
	}
	
	
}
